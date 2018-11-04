package ru.maksimov.andrey.prograph.service.impl;

import java.awt.Color;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

import lombok.NonNull;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import ru.maksimov.andrey.prograph.component.Utility;
import ru.maksimov.andrey.prograph.model.Edge;
import ru.maksimov.andrey.prograph.model.File;
import ru.maksimov.andrey.prograph.model.Graph;
import ru.maksimov.andrey.prograph.model.Node;
import ru.maksimov.andrey.prograph.model.Property;
import ru.maksimov.andrey.prograph.service.DataSourceService;
import ru.maksimov.andrey.prograph.service.GraphService;

/**
 * Реализация сервиса по работе с графом
 * 
 * @author <a href="mailto:onixbed@gmail.com">amaksimov</a>
 */
@Slf4j
@Service
@RequiredArgsConstructor(onConstructor = @__(@Autowired))
public class GraphServiceImpl implements GraphService {

    @NonNull
    private final DataSourceService dataSourceService;

    @Override
    public Map<String, Set<?>> fillNodeAndEdges() {
        Graph graph = getGraph(false);

        if(log.isDebugEnabled()) {
            for (Node node : graph.getNodes()) {
                log.debug(NODES + node.getLabel() + " Color " + node.getColor());
            }
            for (Edge edge :  graph.getEdges()) {
                log.debug(EDGES + edge.getFrom() + "->" + edge.getTo());
            }
        }
        Map<String, Set<?>> name2Set = new HashMap<>();
        name2Set.put(NODES, new HashSet<>(graph.getNodes()));
        name2Set.put(EDGES, graph.getEdges());
        return name2Set;
    }

    @Override
    public Map<String, Set<String>> findDependencies() {
        Graph graph = getGraph(true);
        Map<String, Set<String>> result = new HashMap<>();
        for (Edge edge : graph.getEdges()) {
            Set<String> strings = result.computeIfAbsent(edge.getFrom(), k -> new HashSet<>());
            strings.add(edge.getTo());
        }
        return result;
    }

    private Graph getGraph(boolean isNotFillNodes) {
        Graph graph = new Graph();
        Set<File> files = dataSourceService.loadFile();

        Map<String, Node> key2Node = new HashMap<>();
        for (File file : files) {
            Color color = Utility.getColor(file.getType());
            Node node = new Node(file.getName(), color);
            if(!isNotFillNodes) {
                key2Node.put(file.getName(), node);
            }
            
        }

        for (File file : files) {
            for (Property property : file.getGroupProperties()) {
                String existKey = property.getShortName();
                if(!isNotFillNodes) {
                    Node node = key2Node.get(existKey);
                    if (node == null) {
                        Color color = Utility.getColor(property.getType());
                        node = new Node(existKey, color);
                        key2Node.put(existKey, node);
                    }
                }
                // сделать связь
                Edge edge = new Edge(file.getName(), existKey);
                graph.getEdges().add(edge);
            }
        }
        if(!isNotFillNodes) {
            graph.getNodes().addAll(key2Node.values());
        }
        return graph;
    }
}
