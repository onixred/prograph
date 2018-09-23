package ru.maksimov.andrey.prograph.service;

import java.util.Map;
import java.util.Set;

/**
 * Сервис по работе с  графом
 * 
 * @author <a href="mailto:onixbed@gmail.com">amaksimov</a>
 */
public interface GraphService {

	/**
	 * Найти все узлы и ребра 
	 * 
	 * @return карта где ключ - название, а значение это набор из вершин или ребер
	 */
	Map<String, Set<?>> fillNodeAndEdges();
}
