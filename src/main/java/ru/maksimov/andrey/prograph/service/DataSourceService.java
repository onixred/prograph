package ru.maksimov.andrey.prograph.service;

import java.util.Set;

/**
 * Сервис по работе с хранилищем
 * 
 * @author <a href="mailto:onixbed@gmail.com">amaksimov</a>
 */
public interface DataSourceService {

    Set<ru.maksimov.andrey.prograph.model.File> loadFile();

}
