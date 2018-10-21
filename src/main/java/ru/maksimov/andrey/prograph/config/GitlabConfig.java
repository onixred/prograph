package ru.maksimov.andrey.prograph.config;

import java.util.Set;

import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Configuration;

import lombok.Data;
import ru.maksimov.andrey.prograph.component.Utility;

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
            this.listPath = Utility.string2SetString(paths);
        }

        public void setPrefixFilters(String prefixFilters) {
            this.prefixFilters = prefixFilters;
            this.listPrefixFilter = Utility.string2SetString(prefixFilters);
        }
    }

}
