package ru.maksimov.andrey.prograph.service.impl;

import lombok.NonNull;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.tomcat.util.http.fileupload.IOUtils;
import org.gitlab4j.api.GitLabApi;
import org.gitlab4j.api.GitLabApiException;
import org.gitlab4j.api.RepositoryApi;
import org.gitlab4j.api.models.Project;
import org.gitlab4j.api.models.TreeItem;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import ru.maksimov.andrey.prograph.config.GitlabConfig;
import ru.maksimov.andrey.prograph.config.PropertiesConfig;
import ru.maksimov.andrey.prograph.service.DataSourceService;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.net.URL;
import java.nio.file.StandardCopyOption;
import java.util.List;

/**
 * Реализация сервиса по работе с git-хранилищем
 * 
 * @author <a href="mailto:onixbed@gmail.com">amaksimov</a>
 */
@Slf4j
@Service
@RequiredArgsConstructor(onConstructor = @__(@Autowired))
public class GitLabDataSourceServiceImpl implements DataSourceService {
    public static final long REPOSITORY_SLEEP_TIME_IN_MILLS = 300;
    @NonNull
    private final GitlabConfig graphConfig;
    @NonNull
    private final PropertiesConfig propertiesConfig;
    private GitLabApi gitLabApi;

    @Override
    public void loadFile() {
        if (!graphConfig.getLoad()) {
            return;
        }
        try {
            List<Project> projects = getGitLabApi().getProjectApi().getProjects();
            RepositoryApi repositoryApi = gitLabApi.getRepositoryApi();
            for (Project project : projects) {
                log.info(project.getName());
                if (project.getDefaultBranch() == null) {
                    continue;
                }

                for (String filePath : graphConfig.getFile().getListPath()) {
                    try {
                        List<TreeItem> tree = repositoryApi.getTree(project.getId(), filePath + project.getName(),
                                "master");
                        try {
                            Thread.sleep(REPOSITORY_SLEEP_TIME_IN_MILLS);
                        } catch (InterruptedException e1) {
                            log.error("Unable find file for project " + project.getName(), e1);
                        }
                        if (tree.isEmpty()) {
                            continue;
                        }
                        for (TreeItem item : tree) {
                            for (String prefixFilter : graphConfig.getFile().getListPrefixFilter()) {
                                if (item.getName().contains(prefixFilter)) {
                                    log.info(item.getName());
                                    // загрузка файла
                                    try {
                                        InputStream initialStream = repositoryApi.getRawBlobContent(project.getId(),
                                                item.getId());
                                        URL url = PropertyServiceImpl.class.getResource(propertiesConfig.getPath());

                                        File targetFile = new File(url.getPath() + item.getName());
                                        java.nio.file.Files.copy(initialStream, targetFile.toPath(),
                                                StandardCopyOption.REPLACE_EXISTING);
                                        IOUtils.closeQuietly(initialStream);
                                        try {
                                            Thread.sleep(REPOSITORY_SLEEP_TIME_IN_MILLS);
                                        } catch (InterruptedException e1) {
                                            log.error("Unable find file for project " + project.getName(), e1);
                                        }
                                    } catch (IOException e) {
                                        log.error("Unable save file " + item.getName() + " " + e.getMessage(), e);
                                    } catch (GitLabApiException e) {
                                        log.error("Unable load file " + item.getName() + " " + e.getMessage(), e);
                                    }
                                }
                            }
                        }
                    } catch (GitLabApiException e) {
                        log.error("Unable get tree for projects " + project.getName() + " " + e.getMessage(), e);
                    }
                }
            }
        } catch (GitLabApiException e) {
            log.error("Unable get git projects " + e.getMessage(), e);
        }

    }

    private GitLabApi getGitLabApi() {
        if (gitLabApi == null) {
            gitLabApi = new GitLabApi(graphConfig.getHostUrl(), graphConfig.getPrivateToken());
        }
        return gitLabApi;
    }

}
