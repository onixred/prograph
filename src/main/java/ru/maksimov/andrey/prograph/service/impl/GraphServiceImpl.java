package ru.maksimov.andrey.prograph.service.impl;

import java.awt.Color;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import ru.maksimov.andrey.prograph.component.Utility;
import ru.maksimov.andrey.prograph.model.Edge;
import ru.maksimov.andrey.prograph.model.File;
import ru.maksimov.andrey.prograph.model.Node;
import ru.maksimov.andrey.prograph.model.Property;
import ru.maksimov.andrey.prograph.service.DataSourceService;
import ru.maksimov.andrey.prograph.service.GraphService;
import ru.maksimov.andrey.prograph.service.PropertieService;

/**
 * Реализация сервиса по работе с графом
 * 
 * @author <a href="mailto:onixbed@gmail.com">amaksimov</a>
 */
@Slf4j
@Service
@RequiredArgsConstructor(onConstructor = @__(@Autowired))
public class GraphServiceImpl implements GraphService {
    private final PropertieService propertieService;
    private final DataSourceService dataSourceService;

    @Override
    public Map<String, Set<?>> fillNodeAndEdges() {
       
        dataSourceService.loadFile();
        Set<Edge> edges = new HashSet<>();
        Map<String, Node> key2Node = new HashMap<>();
        Set<File> files = propertieService.loadFiles();
        for (File file : files) {
            Color color = Utility.getColor(file.getType());
            Node node = new Node(file.getName(), color);
            key2Node.put(file.getName(), node);
        }

        for (File file : files) {
            for (Property property : file.getGroupProperties()) {
                String existKey = null;
                if (existKey == null) {
                    existKey = property.getShortName();
                }
                Node node = key2Node.get(existKey);
                if (node == null) {
                    Color color = Utility.getColor(property.getType());
                    node = new Node(existKey, color);
                    key2Node.put(existKey, node);
                }
                // сделать связь
                Edge edge = new Edge(file.getName(), existKey);
                edges.add(edge);
            }
        }

        Map<String, Set<?>> name2Set = new HashMap<>();
        for (Node node : key2Node.values()) {
            log.info("node" + node.getLabel() + " Color " + node.getColor());
        }
        for (Edge edge : edges) {
            log.info("edge" + edge.getFrom() + "->" + edge.getTo());
        }
        name2Set.put("nodes", new HashSet<>(key2Node.values()));
        name2Set.put("edges", edges);

        return name2Set;
    }

}
