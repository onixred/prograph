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

import ru.maksimov.andrey.prograph.component.PropertyType;
import ru.maksimov.andrey.prograph.component.Utility;
import ru.maksimov.andrey.prograph.config.GitlabConfig;
import ru.maksimov.andrey.prograph.config.PropertiesConfig;
import ru.maksimov.andrey.prograph.service.DataSourceService;
import ru.maksimov.andrey.prograph.service.PropertieService;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.Collections;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

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
    @NonNull
    private PropertieService propertieService;
    private GitLabApi gitLabApi;

    @Override
    public Set<ru.maksimov.andrey.prograph.model.File> loadFile() {
        if (!graphConfig.getLoad()) {
            return Collections.emptySet();
        }
        try {
            Set<ru.maksimov.andrey.prograph.model.File> files = new HashSet<>();
            List<Project> projects = getGitLabApi().getProjectApi().getProjects();
            RepositoryApi repositoryApi = getGitLabApi().getRepositoryApi();
            for (Project project : projects) {
                log.info(project.getName());
                if (project.getDefaultBranch() == null) {
                    continue;
                }
                ru.maksimov.andrey.prograph.model.File file = new ru.maksimov.andrey.prograph.model.File(
                        Utility.getFileName(project.getName()), PropertyType.NTK_SERVICE);
                if (graphConfig.getFile().getListPath().isEmpty()) {
                    findDfs(file, project, repositoryApi);
                } else {
                    findPath(file, project, repositoryApi);
                }
                if (!file.getGroupProperties().isEmpty()) {
                    files.add(file);
                }
            }
            return files;
        } catch (GitLabApiException e) {
            log.error("Unable get git projects " + e.getMessage(), e);
        }
        return Collections.emptySet();

    }

    private void findPath(ru.maksimov.andrey.prograph.model.File file, Project project, RepositoryApi repositoryApi) {
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
                            // copeFile(repositoryApi, project, item.getId(),
                            // item.getName());
                            File tmpFile = loadFile(repositoryApi, project, item.getId(), item.getName());
                            if (tmpFile != null) {
                                recognizeFile(file, tmpFile);
                            }
                        }
                    }
                }
            } catch (GitLabApiException e) {
                log.error("Unable get tree for projects " + project.getName() + " " + e.getMessage(), e);
            }
        }
        // если не нашли то запустить поиск DFS
        if (!isFind) {
            findDfs(file, project, repositoryApi);
        }

    }

    private void findDfs(ru.maksimov.andrey.prograph.model.File file, Project project, RepositoryApi repositoryApi) {
        // Dfs (Поиск в глубину)
        log.debug("find dfs for " + project.getName());
        searchFileByDeepness(file, repositoryApi, project, "");
    }

    private boolean searchFileByDeepness(ru.maksimov.andrey.prograph.model.File file, RepositoryApi repositoryApi,
            Project project, String path) {
        try {
            List<TreeItem> tree = repositoryApi.getTree(project.getId(), path, REF_NAME);
            if (tree.isEmpty()) {
                return false;
            }
            for (TreeItem item : tree) {
                if (item.getType().equals(TreeItem.Type.BLOB)) {
                    if (item.getName().contains(propertiesConfig.getFilter())) {
                        // нашли то что искали
                        log.debug(item.getName());
                        File tmpFile = loadFile(repositoryApi, project, item.getId(),
                                project.getName() + propertiesConfig.getFilter());
                        if (tmpFile != null) {
                            recognizeFile(file, tmpFile);
                        }
                    }
                } else if (item.getType().equals(TreeItem.Type.TREE)) {
                    searchFileByDeepness(file, repositoryApi, project, item.getPath());
                }
            }
        } catch (GitLabApiException e) {
            log.error("Unable get tree for projects " + project.getName() + " " + e.getMessage(), e);
        }
        return false;
    }

    private void recognizeFile(ru.maksimov.andrey.prograph.model.File file, File tmpFile) {
        if (tmpFile.getName().contains(propertiesConfig.getFilter())) {
            propertieService.merge(file, tmpFile);
        } else {
            log.warn("Skip load file " + tmpFile.getName() + " no implementation of the bootloader.");
        }
    }

    private File loadFile(RepositoryApi repositoryApi, Project project, String itemId, String name) {
        // загрузка файла
        try {
            InputStream initialStream = repositoryApi.getRawBlobContent(project.getId(), itemId);
            File tempFile = File.createTempFile(name, null);
            tempFile.deleteOnExit();
            FileOutputStream out = new FileOutputStream(tempFile);
            IOUtils.copy(initialStream, out);
            return tempFile;
        } catch (IOException e) {
            log.error("Unable save file " + name + " " + e.getMessage(), e);
        } catch (GitLabApiException e) {
            log.error("Unable load file " + name + " " + e.getMessage(), e);
        }
        return null;
    }

    private GitLabApi getGitLabApi() {
        if (gitLabApi == null) {
            gitLabApi = new GitLabApi(graphConfig.getHostUrl(), graphConfig.getPrivateToken());
        }
        return gitLabApi;
    }

}
