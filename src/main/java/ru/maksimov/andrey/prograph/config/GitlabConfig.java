package ru.maksimov.andrey.prograph.config;

import java.util.Arrays;
import java.util.HashSet;
import java.util.Set;

import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Configuration;

import lombok.Data;

/**
 * Конфиг свойств gitlab
 * 
 * @author <a href="mailto:onixbed@gmail.com">amaksimov</a>
 */
@Data
@Configuration
@ConfigurationProperties(prefix = "gitlab")
public class GitlabConfig {
    private Boolean load;
    private String hostUrl;
    private String privateToken;
    private File file;

    @Data
    public static class File {
        private String paths;
        private Set<String> listPath;

        private String prefixFilters;
        private Set<String> listPrefixFilter;

        public void setPaths(String paths) {
            this.paths = paths;
            this.listPath = new HashSet<>(Arrays.asList(paths.split("\\s*,\\s*")));
        }

        public void setPrefixFilters(String prefixFilters) {
            this.prefixFilters = prefixFilters;
            this.listPrefixFilter = new HashSet<>(Arrays.asList(prefixFilters.split("\\s*,\\s*")));
        }
    }

}
