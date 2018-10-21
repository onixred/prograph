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

    public static final String REF_NAME = "master";

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
                if (graphConfig.getFile().getListPath().isEmpty()) {
                    findDfs(project, repositoryApi);
                } else {
                    findPath(project, repositoryApi);
                }

            }
        } catch (GitLabApiException e) {
            log.error("Unable get git projects " + e.getMessage(), e);
        }

    }

    private void findPath(Project project, RepositoryApi repositoryApi) {
        boolean isFind = false;
        for (String filePath : graphConfig.getFile().getListPath()) {
            try {
                List<TreeItem> tree = repositoryApi.getTree(project.getId(), filePath + project.getName(), REF_NAME);
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
                            // если нашли хотя бы один файл то помечаем поиск
                            // успехом
                            isFind = true;
                            // загрузка файла
                            copeFile(repositoryApi, project, item.getId(), item.getName());
                        }
                    }
                }
            } catch (GitLabApiException e) {
                log.error("Unable get tree for projects " + project.getName() + " " + e.getMessage(), e);
            }
        }
        // если не нашли то запустить поиск DFS
        if (!isFind) {
            findDfs(project, repositoryApi);
        }

    }

    private void findDfs(Project project, RepositoryApi repositoryApi) {
        // Dfs (Поиск в глубину)
        log.info("find dfs for " + project.getName());
        searchFileByDeepness(repositoryApi, project, "");

    }

    private boolean searchFileByDeepness(RepositoryApi repositoryApi, Project project, String path) {
        try {
            List<TreeItem> tree = repositoryApi.getTree(project.getId(), path, REF_NAME);
            if (tree.isEmpty()) {
                return false;
            }
            for (TreeItem item : tree) {
                if (item.getType().equals(TreeItem.Type.BLOB)) {
                    if (item.getName().contains(propertiesConfig.getFilter())) {
                        // нашли то что искали
                        copeFile(repositoryApi, project, item.getId(),
                                project.getName() + propertiesConfig.getFilter());
                        return true;
                    }
                } else if (item.getType().equals(TreeItem.Type.TREE)) {
                    boolean isSearchFile = searchFileByDeepness(repositoryApi, project, item.getPath());
                    if (isSearchFile) {
                        return isSearchFile;
                    }
                }
            }
        } catch (GitLabApiException e) {
            log.error("Unable get tree for projects " + project.getName() + " " + e.getMessage(), e);
        }
        return false;
    }

    private void copeFile(RepositoryApi repositoryApi, Project project, String itemId, String name) {
        // загрузка файла
        try {
            InputStream initialStream = repositoryApi.getRawBlobContent(project.getId(), itemId);
            URL url = PropertyServiceImpl.class.getResource(propertiesConfig.getPath());

            File targetFile = new File(url.getPath() + name);
            java.nio.file.Files.copy(initialStream, targetFile.toPath(), StandardCopyOption.REPLACE_EXISTING);
            IOUtils.closeQuietly(initialStream);
            try {
                Thread.sleep(REPOSITORY_SLEEP_TIME_IN_MILLS);
            } catch (InterruptedException e1) {
                log.error("Unable find file for project " + project.getName(), e1);
            }
        } catch (IOException e) {
            log.error("Unable save file " + name + " " + e.getMessage(), e);
        } catch (GitLabApiException e) {
            log.error("Unable load file " + name + " " + e.getMessage(), e);
        }
    }

    private GitLabApi getGitLabApi() {
        if (gitLabApi == null) {
            gitLabApi = new GitLabApi(graphConfig.getHostUrl(), graphConfig.getPrivateToken());
        }
        return gitLabApi;
    }

}
