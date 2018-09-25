package ru.maksimov.andrey.prograph.service.impl;

import java.io.FilenameFilter;
import java.net.URL;
import java.util.HashSet;
import java.util.Iterator;
import java.util.Map;
import java.util.Set;


import lombok.RequiredArgsConstructor;
import org.apache.commons.configuration2.Configuration;
import org.apache.commons.configuration2.FileBasedConfiguration;
import org.apache.commons.configuration2.PropertiesConfiguration;
import org.apache.commons.configuration2.builder.FileBasedConfigurationBuilder;
import org.apache.commons.configuration2.builder.fluent.Parameters;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import ru.maksimov.andrey.prograph.component.PropertyType;
import ru.maksimov.andrey.prograph.component.Utility;
import ru.maksimov.andrey.prograph.config.PropertiesConfig;
import ru.maksimov.andrey.prograph.model.File;
import ru.maksimov.andrey.prograph.model.Property;
import ru.maksimov.andrey.prograph.service.PropertieService;

/**
 * Реализация сервиса по работе со свойствами
 * 
 * @author <a href="mailto:onixbed@gmail.com">amaksimov</a>
 */
@Service
@RequiredArgsConstructor(onConstructor = @__(@Autowired))
public class PropertyServiceImpl implements PropertieService {

	private static final Logger LOG = LogManager.getLogger(PropertyServiceImpl.class);

	/*private String propertiesPath;
	private String[] propertyNames;
	private String propertiesFilter;
	private Map<String, Integer> propertyNames2group;*/
	private final PropertiesConfig propertiesConfig;

	protected PropertyType getPropertyType(String propertyName) {
        for (Map.Entry<PropertyType, String> entry : propertiesConfig.getTypes().entrySet()) {
            if (Utility.checkWithRegExp(propertyName, entry.getValue())) {
            	return entry.getKey();
			}
        }

		return PropertyType.UNDEFINED;
	}

	@Override
	public Set<File> loadFiles() {
		Set<File> propertyFiles = new HashSet<>();
		try {
			URL url = PropertyServiceImpl.class.getResource(propertiesConfig.getPath());
			java.io.File file = new java.io.File(url.toURI());
			java.io.File[] files = file.listFiles(new FilenameFilter() {
				public boolean accept(java.io.File dir, String filename) {
					return filename.endsWith(propertiesConfig.getFilter());
				}
			});

			for (java.io.File propertyFile : files) {
				propertyFiles.add(loadConfig(propertyFile));
			}

		} catch (Exception e) {
			LOG.warn("FATAL: can't scan properties path: " + propertiesConfig.getPath());
		}

		return propertyFiles;
	}

	/**
	 * Загрузка конфига
	 */
	private File loadConfig(java.io.File propertyFile) {
		try {
			Parameters params = new Parameters();
			FileBasedConfigurationBuilder<FileBasedConfiguration> builder = new FileBasedConfigurationBuilder<FileBasedConfiguration>(
					PropertiesConfiguration.class).configure(params.properties().setFile(propertyFile));
			builder.setAutoSave(true);
			Configuration configuration = builder.getConfiguration();
			String fileName = getFileName(propertyFile.getName());
			File file = new File(fileName, PropertyType.NTK_SERVICE);
			initFile(configuration, file);
			return file;
		} catch (Exception e) {
			throw new RuntimeException("FATAL: can't load config from: " + propertyFile.getPath(), e);
		}
	}

	private String getFileName(String fileName) {
		String name = Utility.tailCut(fileName);
		return name.replace('-', '.');
	}

	private void initFile(Configuration configuration, File file) {
		Set<Property> properties = new HashSet<>();
		// Поиск ключей которые удовлетворяют герексам
		Iterator<String> allKeys = configuration.getKeys();
		while (allKeys.hasNext()) {
			String key = allKeys.next();
			//TODO restore
			/*for (String propertyName : propertyNames) {
				if (Utility.checkWithRegExp(key, propertyName)) {
					int group = propertyNames2group.get(propertyName);
					Property property = new Property(key, group);
					properties.add(property);
					break;
				}
			}*/
		}
		file.getProperties().addAll(properties);
	}
}
