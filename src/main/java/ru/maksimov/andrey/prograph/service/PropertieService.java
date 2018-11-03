package ru.maksimov.andrey.prograph.service;

import java.util.Set;

import ru.maksimov.andrey.prograph.model.File;

/**
 * Сервис по работе со свойствами
 * 
 * @author <a href="mailto:onixbed@gmail.com">amaksimov</a>
 */
public interface PropertieService {

    /**
     * Загрузить все файлы
     * 
     * @return набор файлов со свойствами
     */
    Set<File> loadFiles();

    /**
     * Обеденить файлы, взять все свойства из file2 и добапвить из в file1
     * 
     * @param file1 файл к кототорому нужно добавить свойства из другого файла
     * @param file2 времменый файл из которого нужно взять свойства 
     */
    void merge(ru.maksimov.andrey.prograph.model.File file1, java.io.File file2);

    /**
     * Обеденить файлы, взять все свойства из file2 и добапвить из в file1
     * 
     * @param file1 файл к кототорому нужно добавить свойства из другого файла
     * @param file2 времменый файл из которого нужно взять свойства 
     */
    void merge(File file1, File file2);

}
