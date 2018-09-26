package ru.maksimov.andrey.prograph.config;

import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Configuration;
import ru.maksimov.andrey.prograph.component.PropertyType;

import java.util.LinkedHashMap;
import java.util.Map;

@Data
@Configuration
@ConfigurationProperties(prefix = "properties")
public class PropertiesConfig {
    private String path;

    private String filter;

    private Map<PropertyType, String> types = new LinkedHashMap<>();
}
