package ru.maksimov.andrey.prograph.model;

import java.util.HashSet;
import java.util.Set;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * Модель данных граф
 * 
 * @author <a href="mailto:onixbed@gmail.com">amaksimov</a>
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
public class Graph {

    private Set<Edge> edges = new HashSet<>();
    private Set<Node> nodes = new HashSet<>();
}
