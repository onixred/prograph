package ru.maksimov.andrey.prograph.contoller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

import ru.maksimov.andrey.prograph.service.GraphService;
import java.util.Map;
import java.util.Set;

/**
 * Контроллер для отрисовки графа
 * 
 * @author <a href="mailto:onixbed@gmail.com">amaksimov</a>
 */
@RestController
public class GraphController {

	private GraphService graphService;

	GraphController(@Autowired GraphService graphService) {
		this.graphService = graphService;
	}

	@GetMapping("/modules")
	public Map<String, Set<?>> moduleInfo() {
		return graphService.fillNodeAndEdges();
	}
}