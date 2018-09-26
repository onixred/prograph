package ru.maksimov.andrey.prograph.model;

import lombok.Data;

import java.awt.Color;
import java.util.Objects;

@Data
public class Node {
	private final String id;
	private final String label;
	private final String color;

	public Node(String moduleName, Color color) {
		this.id = moduleName;
		this.label = moduleName;
		this.color = String.format("#%02x%02x%02x", color.getRed(), color.getGreen(), color.getBlue());
	}
}