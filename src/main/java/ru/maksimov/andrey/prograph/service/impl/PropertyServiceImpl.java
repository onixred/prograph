package ru.maksimov.andrey.prograph.service.impl;

import lombok.NonNull;
import lombok.RequiredArgsConstructor;

import org.apache.commons.lang3.NotImplementedException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import ru.maksimov.andrey.prograph.component.PropertyType;
import ru.maksimov.andrey.prograph.component.Utility;
import ru.maksimov.andrey.prograph.config.PropertiesConfig;
import ru.maksimov.andrey.prograph.model.File;
import ru.maksimov.andrey.prograph.model.GroupProperty;
import ru.maksimov.andrey.prograph.model.Property;
import ru.maksimov.andrey.prograph.service.PropertieService;

import java.io.FileReader;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.Map;
import java.util.Properties;

/**
 * Реализация сервиса по работе со свойствами
 * 
 * @author <a href="mailto:onixbed@gmail.com">amaksimov</a>
 */
@Service
@RequiredArgsConstructor(onConstructor = @__(@Autowired))
public class PropertyServiceImpl implements PropertieService {

    @NonNull
    private final PropertiesConfig propertiesConfig;

    @Override
    public void merge(File file1, java.io.File file2) {
        loadConfig(file1, file2);
    }

    @Override
    public void merge(File file1, File file2) {
        throw new NotImplementedException("merge for arg file1, file2  not implemented.");
    }

    /**
     * Загрузка конфига
     */
    private File loadConfig(File file, java.io.File propertyFile) {
        try {
            Properties properties = new Properties();
            properties.load(new FileReader(propertyFile));
            if (file == null) {
                file = new File(Utility.getFileName(propertyFile.getName()), PropertyType.NTK_SERVICE);
            }
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
        // TODO уйти от частных случаев
        /*
         * 1) все БД делать с префиксом db- 2) все службы и другие сервисы не
         * должны содержать знаков '-' 3) использовать префикс для определения
         * наших служб 4) Использовать постфикс для фильтрации ключей 5)
         * Использовать список наших служб (не рекомендуется это временное
         * решение)
         */
        if ("db.url".equals(propertyName) || "db.host".equals(propertyName)) {
            int index1 = propertyValue.indexOf('/') + 1;
            int index2 = propertyValue.indexOf('?');

            if (index1 > 1 && index2 > 0 && index2 > index1) {
                String dbName = propertyValue.substring(index1, index2).toLowerCase();
                return "db-" + dbName + ".host";
            } else {
                return "db-unknown.host";
            }
        } else if (propertyName.startsWith("db.")
                && (propertyName.endsWith(".host") || propertyName.endsWith(".url"))) {
            return "db-" + propertyName.substring(3);
        } else if (propertyName.startsWith("ntk-")
                && (propertyName.endsWith(".host") || propertyName.endsWith(".url"))) {
            return "ntk." + propertyName.substring(4);
        } else if (propertyName.endsWith("host") || propertyName.endsWith("url")) {
            String trimName = propertyName.replace(".host", "").replace(".url", "");
            if (propertiesConfig.getWhiteListNtkServices().contains(trimName)
                    || propertiesConfig.getWhiteListNtkServices().contains(propertyName + ".host")) {
                return "ntk." + trimName + ".host";
            }
        }

        return propertyName;
    }

    private void initFile(Properties properties, File file) {
        // Поиск ключей которые удовлетворяют regexp-ам
        Map<String, Property> key2Property = new HashMap<>();
        for (String propertyName : properties.stringPropertyNames()) {
            String propertyValue = properties.getProperty(propertyName);
            String propertyNamePreProcess = preProcessProperty(propertyName, propertyValue);
            PropertyType propertyType = getPropertyType(propertyNamePreProcess);
            if (propertyType != PropertyType.UNDEFINED) {
                Property property = new Property(propertyNamePreProcess, propertyType, propertyValue);
                key2Property.put(property.getShortName(), property);
            }
        }
        // TODO Использовать для связи не только ключ, добавиь связь через vale
        // обработка файла находим минимальный ключ и объединяем
        ArrayList<String> listKey = new ArrayList<>(key2Property.keySet());
        Collections.sort(listKey);

        // если есть GroupProperties то учесть их.
        Map<String, GroupProperty> key2GroupProperty = new HashMap<>();
        for (GroupProperty groupProperty : file.getGroupProperties()) {
            key2GroupProperty.put(groupProperty.getShortName(), groupProperty);
        }

        for (String key : listKey) {
            boolean isAdd = false;
            for (String key2 : key2GroupProperty.keySet()) {
               //Делаем сравнение по ключам (вхождение ключей)
                if (key.length() > key2.length()) {
                    if (key.contains(key2)) {
                        isAdd = true;
                        // оставляем ключ и добавляем свойство
                        GroupProperty groupProperty = key2GroupProperty.get(key2);
                        groupProperty.addLikeProperty(key2Property.get(key));
                        break;
                    }
                } else {
                    if (key2.contains(key)) {
                        isAdd = true;
                        // меняем ключ на меньший и добавляем свойство
                        GroupProperty groupProperty = key2GroupProperty.remove(key2);
                        groupProperty.addLikeProperty(key2Property.get(key));
                        key2GroupProperty.put(key, groupProperty);
                        break;
                    }
                }
                //Делаем сравнение по значениям
                if (propertiesConfig.isCompareValue()) {
                    Property property = key2Property.get(key);
                    if (property.getType() != PropertyType.DB
                            && propertiesConfig.getMinSizeCorrectValue() <= property.getValue().length()) {
                        boolean isEqualsValue = false;
                        GroupProperty groupProperty = key2GroupProperty.get(key2);
                        for (Property property2 : groupProperty.getLikeProperties()) {
                            if (property2.getValue().equalsIgnoreCase(property.getValue())) {
                                isEqualsValue = true;
                                break;
                            }
                        }
                        if (isEqualsValue) {
                            isAdd = true;
                            groupProperty.addLikeProperty(property);
                        }
                    }
                }
            }
            if (!isAdd) {
                // если не удалось сгруппировать то создаем новую группу
                key2GroupProperty.put(key, new GroupProperty(key2Property.get(key)));
            }
        }
        // удальить все что было
        file.getGroupProperties().clear();
        // добавить новую группировку
        file.getGroupProperties().addAll(key2GroupProperty.values());
    }
}
