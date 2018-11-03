package ru.maksimov.andrey.prograph.contoller;

import lombok.RequiredArgsConstructor;
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
@RequiredArgsConstructor(onConstructor = @__(@Autowired))
public class GraphController {
	private final GraphService graphService;

	@GetMapping("/modules")
	public Map<String, Set<?>> moduleInfo() {
		return graphService.fillNodeAndEdges();
	}

	@GetMapping("/dependencies")
	public Map<String, Set<String>> dependencies() {
		return graphService.findDependencies();
	}
}