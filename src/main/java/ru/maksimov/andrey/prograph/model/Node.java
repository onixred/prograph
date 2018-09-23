package ru.maksimov.andrey.prograph.model;

import java.awt.Color;
import java.util.Objects;

public class Node {
	private String id;
	private String label;
	private String color;

	public Node(String moduleName, Color color) {
		this.id = moduleName;
		this.label = moduleName;
		String hex = String.format("#%02x%02x%02x", color.getRed(), color.getGreen(), color.getBlue());
		this.color = hex;
	}

	public String getId() {
		return id;
	}

	public void setId(String id) {
		this.id = id;
	}

	public String getLabel() {
		return label;
	}

	public void setLabel(String label) {
		this.label = label;
	}

	public String getColor() {
		return color;
	}

	public void setColor(String color) {
		this.color = color;
	}

	@Override
	public boolean equals(Object o) {
		if (this == o)
			return true;
		if (o == null || getClass() != o.getClass())
			return false;
		Node node = (Node) o;
		return Objects.equals(id, node.id) && Objects.equals(label, node.label);
	}

	@Override
	public int hashCode() {

		return Objects.hash(id, label);
	}
}