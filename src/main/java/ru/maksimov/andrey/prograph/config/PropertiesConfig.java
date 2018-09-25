package ru.maksimov.andrey.prograph.config;

import lombok.Getter;
import lombok.Setter;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Configuration;
import ru.maksimov.andrey.prograph.component.PropertyType;

import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.Map;

@Getter
@Setter
@Configuration
@ConfigurationProperties(prefix = "properties")
public class PropertiesConfig {
    private String path;
    private String filter;
    private Map<PropertyType, String> types = new LinkedHashMap<>();


}
