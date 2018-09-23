package ru.maksimov.andrey.prograph.service.impl;

import java.awt.Color;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import ru.maksimov.andrey.prograph.component.Utility;
import ru.maksimov.andrey.prograph.model.Edge;
import ru.maksimov.andrey.prograph.model.File;
import ru.maksimov.andrey.prograph.model.Node;
import ru.maksimov.andrey.prograph.model.Property;
import ru.maksimov.andrey.prograph.service.GraphService;
import ru.maksimov.andrey.prograph.service.PropertieService;

/**
 * Реализация сервиса по работе с графом
 * 
 * @author <a href="mailto:onixbed@gmail.com">amaksimov</a>
 */
@Service
public class GraphServiceImpl implements GraphService {

	private PropertieService propertieService;

	GraphServiceImpl(@Autowired PropertieService propertieService) {
		this.propertieService = propertieService;
	}

	@Override
	public Map<String, Set<?>> fillNodeAndEdges() {

		Set<Edge> edges = new HashSet<Edge>();
		Map<String, Node> key2Node = new HashMap<>();
		Set<File> files = propertieService.loadFiles();
		for (File file : files) {
			Color color = Utility.getColor(file.getGroup());
			Node node = new Node(file.getName(), color);
			key2Node.put(file.getName(), node);
		}

		for (File file : files) {
			for (Property property : file.getProperties()) {
				Node node = key2Node.get(property.getShotName());
				if (node == null) {
					Color color = Utility.getColor(property.getGroup());
					node = new Node(property.getShotName(), color);
					key2Node.put(property.getShotName(), node);
				}
				// сделать связь
				Edge edge = new Edge(file.getName(), property.getShotName());
				edges.add(edge);
			}
		}

		Map<String, Set<?>> name2Set = new HashMap<>();
		name2Set.put("nodes", new HashSet<>(key2Node.values()));
		name2Set.put("edges", edges);

		return name2Set;
	}

}
