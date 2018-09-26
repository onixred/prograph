package ru.maksimov.andrey.prograph.service.impl;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import ru.maksimov.andrey.prograph.component.PropertyType;
import ru.maksimov.andrey.prograph.component.Utility;
import ru.maksimov.andrey.prograph.config.PropertiesConfig;
import ru.maksimov.andrey.prograph.model.File;
import ru.maksimov.andrey.prograph.model.Property;
import ru.maksimov.andrey.prograph.service.PropertieService;

import java.io.FileReader;
import java.net.URL;
import java.util.HashSet;
import java.util.Map;
import java.util.Properties;
import java.util.Set;

/**
 * Реализация сервиса по работе со свойствами
 * 
 * @author <a href="mailto:onixbed@gmail.com">amaksimov</a>
 */
@Slf4j
@Service
@RequiredArgsConstructor(onConstructor = @__(@Autowired))
public class PropertyServiceImpl implements PropertieService {
	private final PropertiesConfig propertiesConfig;

	@Override
	public Set<File> loadFiles() {
		Set<File> propertyFiles = new HashSet<>();
		try {
			URL url = PropertyServiceImpl.class.getResource(propertiesConfig.getPath());
			java.io.File file = new java.io.File(url.toURI());
			java.io.File[] files = file.listFiles((dir, filename) ->
                    filename.endsWith(propertiesConfig.getFilter()));

			if (files != null) {
                for (java.io.File propertyFile : files) {
                    propertyFiles.add(loadConfig(propertyFile));
                }
            } else {
			    log.warn("FATAL: files not found {} ", propertiesConfig.getPath());
            }
		} catch (Exception e) {
			log.warn("FATAL: can't scan properties path: " + propertiesConfig.getPath());
		}

		return propertyFiles;
	}

	/**
	 * Загрузка конфига
	 */
	private File loadConfig(java.io.File propertyFile) {
		try {
            Properties properties = new Properties();
            properties.load(new FileReader(propertyFile));


			File file = new File(getFileName(propertyFile.getName()), PropertyType.NTK_SERVICE);
			initFile(properties, file);

			return file;
		} catch (Exception e) {
			throw new RuntimeException("FATAL: can't load config from: " + propertyFile.getPath(), e);
		}
	}

    protected PropertyType getPropertyType(String propertyName) {
        for (Map.Entry<PropertyType, String> entry : propertiesConfig.getTypes().entrySet()) {
            if (Utility.checkWithRegExp(propertyName, entry.getValue())) {
                return entry.getKey();
            }
        }

        return PropertyType.UNDEFINED;
    }

    private String preProcessProperty(String propertyName, String propertyValue) {
		if ("db.url".equals(propertyName)) {
			int index1 = propertyValue.indexOf('/') + 1;
			int index2 = propertyValue.indexOf('?');

			if (index1 > 1 && index2 > 0 && index2 > index1) {
				String dbName = propertyValue.substring(index1, index2).toLowerCase();
				return "db-" + dbName + ".host";
			} else {
				return "db-unknown.host";
			}
		} else if (propertyName.startsWith("db.") && propertyName.endsWith(".host")) {
			return "db-" + propertyName.substring(3);
		} else if (propertyName.endsWith("host") || propertyName.endsWith("url")) {
		    String trimName = propertyName.replace(".host", "").replace(".url", "");
		    if (propertiesConfig.getWhiteListNtkServices().contains(propertyName + ".host") ||
                    propertiesConfig.getWhiteListNtkServices().contains(trimName)) {
		        return "ntk-" + trimName + ".host";
            }
        }

		return propertyName;
	}


    private String getFileName(String fileName) {
        String name = Utility.tailCut(fileName);

        return name.replace('-', '.');
    }

	private void initFile(Properties properties, File file) {
		Set<Property> propertiesSet = new HashSet<>();
		// Поиск ключей которые удовлетворяют regexp-ам
        for (String propertyName : properties.stringPropertyNames()) {
        	String propertyNamePreProcess = preProcessProperty(propertyName, properties.getProperty(propertyName));
            PropertyType propertyType = getPropertyType(propertyNamePreProcess);
            if (propertyType != PropertyType.UNDEFINED) {
                Property property = new Property(propertyNamePreProcess, propertyType);
                propertiesSet.add(property);
            }
        }

		file.getProperties().addAll(propertiesSet);
	}
}
