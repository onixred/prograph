package ru.maksimov.andrey.prograph.service.impl;

import org.junit.Before;
import org.junit.Test;
import ru.maksimov.andrey.prograph.component.PropertyType;
import ru.maksimov.andrey.prograph.config.PropertiesConfig;

import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.Map;

import static org.junit.Assert.*;

public class PropertyServiceImplTest {
    private PropertyServiceImpl propertyService;

    @Before
    public void setUp() {
        PropertiesConfig propertiesConfig = new PropertiesConfig();
        Map<PropertyType, String> types = propertiesConfig.getTypes();
        types.put(PropertyType.DB, "db\\-.+\\.host$");
        types.put(PropertyType.NTK_SERVICE, "ntk.+\\.host$|^ntk.+\\.url$");
        types.put(PropertyType.THIRD_PARTY_SERVICE, "^.+\\.host$|^.+\\.url$");

        propertiesConfig.setTypes(types);
        propertyService = new PropertyServiceImpl(propertiesConfig);
    }

    @Test
    public void getPropertyType() {
        assertEquals(PropertyType.DB, propertyService.getPropertyType("db-net.host"));
        assertEquals(PropertyType.NTK_SERVICE, propertyService.getPropertyType("ntk.our.service.host"));
        assertEquals(PropertyType.NTK_SERVICE, propertyService.getPropertyType("ntk.our.service.url"));
        assertEquals(PropertyType.THIRD_PARTY_SERVICE, propertyService.getPropertyType("our.service.host"));
        assertEquals(PropertyType.THIRD_PARTY_SERVICE, propertyService.getPropertyType("our.service.url"));
        assertEquals(PropertyType.UNDEFINED, propertyService.getPropertyType("other"));
    }
}