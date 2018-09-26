package ru.maksimov.andrey.prograph.service.impl;

import lombok.extern.slf4j.Slf4j;
import org.apache.tomcat.util.http.fileupload.IOUtils;
import org.gitlab4j.api.GitLabApi;
import org.gitlab4j.api.GitLabApiException;
import org.gitlab4j.api.RepositoryApi;
import org.gitlab4j.api.models.Project;
import org.gitlab4j.api.models.TreeItem;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
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
public class GitDataSourceServiceImpl implements DataSourceService {
    public static final long REPOSITORY_SLEEP_TIME_IN_MILLS = 300;

	private GitLabApi gitLabApi;
	private String[] filePaths;
	private String[] prefixFilters;
	private String propertiesPath;

	public GitDataSourceServiceImpl(@Value("${github.host.url}") String hostUrl,
			@Value("${github.private.token}") String privateToken,
			@Value("#{'${github.file.paths:}'.split(',')}") final String[] filePaths,
			@Value("#{'${github.file.prefix.filters:}'.split(',')}") final String[] prefixFilters,
			@Value("${properties.path}") final String propertiesPath) {
		this.filePaths = filePaths;
		this.prefixFilters = prefixFilters;
		this.propertiesPath = propertiesPath;
		gitLabApi = new GitLabApi(hostUrl, privateToken);
	}

	@Override
	public void loadFile() {
		try {
			List<Project> projects = gitLabApi.getProjectApi().getProjects();
			RepositoryApi repositoryApi = gitLabApi.getRepositoryApi();
			for (Project project : projects) {
				log.info(project.getName());
				if (project.getDefaultBranch() == null) {
					continue;
				}
				for (String filePath : filePaths) {
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
							for (String prefixFilter : prefixFilters) {
								if (item.getName().contains(prefixFilter)) {
									log.info(item.getName());
									// загрузка файла
									try {
										InputStream initialStream = repositoryApi.getRawBlobContent(project.getId(),
												item.getId());
										URL url = PropertyServiceImpl.class.getResource(propertiesPath);

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
						log.error("Unable get tree for projects " +project.getName() + " "+ e.getMessage(), e);
					}
				}
			}
		} catch (GitLabApiException e) {
			log.error("Unable get git projects " + e.getMessage(), e);
		}

	}

}
