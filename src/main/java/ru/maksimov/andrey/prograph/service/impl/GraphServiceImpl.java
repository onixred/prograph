package ru.maksimov.andrey.prograph.service.impl;

import java.awt.Color;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
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
@Service
public class GraphServiceImpl implements GraphService {

	private static final Logger LOG = LogManager.getLogger(GraphServiceImpl.class);

	private PropertieService propertieService;
	private DataSourceService dataSourceService;

	GraphServiceImpl(@Autowired PropertieService propertieService, DataSourceService dataSourceService) {
		this.propertieService = propertieService;
		this.dataSourceService = dataSourceService;
	}

	@Override
	public Map<String, Set<?>> fillNodeAndEdges() {
		//dataSourceService.loadFile();
		Set<Edge> edges = new HashSet<>();
		Map<String, Node> key2Node = new HashMap<>();
		Set<File> files = propertieService.loadFiles();
		for (File file : files) {
			Color color = Utility.getColor(file.getType());
			Node node = new Node(file.getName(), color);
			key2Node.put(file.getName(), node);
		}

		for (File file : files) {
			for (Property property : file.getProperties()) {
				String existKey = null;
				for(String key: key2Node.keySet()) {
					if(property.getShotName().contains(key)) {
						existKey = key;
						break;
					}
				}
				if(existKey == null) {
					existKey = property.getShotName();
				} 
				Node node = key2Node.get(existKey);
				if (node == null) {
					Color color = Utility.getColor(property.getGroup());
					node = new Node(existKey, color);
					key2Node.put(existKey, node);
				}
				// сделать связь
				Edge edge = new Edge(file.getName(), existKey);
				edges.add(edge);
			}
		}

		Map<String, Set<?>> name2Set = new HashMap<>();
		for(Node node: key2Node.values()) {
			LOG.info("node" +node.getLabel() + " Color " + node.getColor());
		}
		for(Edge edge: edges) {
			LOG.info("edge" +edge.getFrom() +"->" +edge.getTo());
		}
		name2Set.put("nodes", new HashSet<>(key2Node.values()));
		name2Set.put("edges", edges);

		return name2Set;
	}

}
