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

}
