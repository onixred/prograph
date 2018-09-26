package ru.maksimov.andrey.prograph.config;

import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Configuration;
import ru.maksimov.andrey.prograph.component.PropertyType;

import java.util.*;

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
        this.whiteListNtkServices = new HashSet<>(Arrays.asList(ntkServices.split(", ")));
    }
}
