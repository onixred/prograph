package ru.maksimov.andrey.prograph.config;

import lombok.Data;

import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Configuration;
import ru.maksimov.andrey.prograph.component.PropertyType;
import ru.maksimov.andrey.prograph.component.Utility;

import java.util.*;

/**
 * Конфиг свойств properties
 * 
 * @author <a href="mailto:onixbed@gmail.com">amaksimov</a>
 */
@Data
@Configuration
@ConfigurationProperties(prefix = "properties")
public class PropertiesConfig {
    private String path;

    private String filter;

    private String ntkServices;

    private Set<String> whiteListNtkServices = new HashSet<>();

    private Map<PropertyType, String> types = new LinkedHashMap<>();

    public void setNtkServices(String ntkServices) {
        this.ntkServices = ntkServices;
        this.whiteListNtkServices = Utility.string2SetString(ntkServices);
    }
}
