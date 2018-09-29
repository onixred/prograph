package ru.maksimov.andrey.prograph.model;

import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
public class Edge {
    private final String from;
    private final String to;
}