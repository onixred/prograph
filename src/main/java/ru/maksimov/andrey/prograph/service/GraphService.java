package ru.maksimov.andrey.prograph.service;

import java.util.Map;
import java.util.Set;

/**
 * Сервис по работе с графом
 * 
 * @author <a href="mailto:onixbed@gmail.com">amaksimov</a>
 */
public interface GraphService {

    public static final String NODES = "nodes";
    public static final String EDGES = "edges";

    /**
     * Найти все узлы и ребра
     * 
     * @return карта где ключ - название ({@value GraphService#NODES} или
     *         {@value GraphService#EDGES}), а значение это набор из вершин или
     *         ребер
     */
    Map<String, Set<?>> fillNodeAndEdges();

    /**
     * Найти все зависимости для вершин (службы, БД или другое)
     * 
     * @return карта где ключ - название вершины, а значение это набор из вершин
     */
    Map<String, Set<String>> findDependencies();
}
