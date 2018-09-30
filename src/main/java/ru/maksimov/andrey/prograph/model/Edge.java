package ru.maksimov.andrey.prograph.model;

/**
 * Модель данных ребро
 * 
 * @author <a href="mailto:onixbed@gmail.com">amaksimov</a>
 */
import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
public class Edge {
    private final String from;
    private final String to;
}