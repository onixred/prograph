package ru.maksimov.andrey.prograph.service.impl;

import java.io.FilenameFilter;
import java.net.URL;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.Map;
import java.util.Set;


import org.apache.commons.configuration2.Configuration;
import org.apache.commons.configuration2.FileBasedConfiguration;
import org.apache.commons.configuration2.PropertiesConfiguration;
import org.apache.commons.configuration2.builder.FileBasedConfigurationBuilder;
import org.apache.commons.configuration2.builder.fluent.Parameters;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import ru.maksimov.andrey.prograph.component.Utility;
import ru.maksimov.andrey.prograph.model.File;
import ru.maksimov.andrey.prograph.model.Property;
import ru.maksimov.andrey.prograph.service.PropertieService;

/**
 * Реализация сервиса по работе со свойствами
 * 
 * @author <a href="mailto:onixbed@gmail.com">amaksimov</a>
 */
@Service
public class PropertieServiceImpl implements PropertieService {

	private static final Logger LOG = LogManager.getLogger(PropertieServiceImpl.class);

	private String propertiesPath;
	private String[] propertyNames;
	private String propertiesFilter;
	private Map<String, Integer> propertyNames2group;

	PropertieServiceImpl(@Value("${properties.path}") final String propertiesPath,
			@Value("#{'${property.names:}'.split(',')}") final String[] propertyNames,
			@Value("${properties.filter}") final String propertiesFilter) {
		this.propertiesPath = propertiesPath;
		this.propertyNames = propertyNames;
		this.propertiesFilter = propertiesFilter;

		propertyNames2group = new HashMap<>();
		for (int index = 0; index < propertyNames.length; index++) {
			propertyNames2group.put(propertyNames[index], getGroup(index));
		}
	}

	@Override
	public Set<File> loadFiles() {
		Set<File> propertyFiles = new HashSet<>();
		try {
			URL url = PropertieServiceImpl.class.getResource(propertiesPath);
			java.io.File file = new java.io.File(url.toURI());
			java.io.File[] files = file.listFiles(new FilenameFilter() {
				public boolean accept(java.io.File dir, String filename) {
					return filename.endsWith(propertiesFilter);
				}
			});

			for (java.io.File propertyFile : files) {
				propertyFiles.add(loadConfig(propertyFile));
			}

		} catch (Exception e) {
			LOG.warn("FATAL: can't scan properties path: " + propertiesPath);
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
			File file = new File(fileName, getGroup(propertyNames.length + 1));
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
			for (String propertyName : propertyNames) {
				if (Utility.checkWithRegExp(key, propertyName)) {
					int group = propertyNames2group.get(propertyName);
					Property property = new Property(key, group);
					properties.add(property);
					break;
				}
			}
		}
		file.getProperties().addAll(properties);
	}

	public static int getGroup(int value) {
		return value * 57 /3 ;
	}
}
