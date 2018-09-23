package ru.maksimov.andrey.prograph.model;

import ru.maksimov.andrey.prograph.component.Utility;

public class Property {

	private String name;
	private int group;

	public Property(String name, int group) {
		this.name = name;
		this.group = group;
	}

	public String getName() {
		return name;
	}

	public String getShotName() {
		return Utility.tailCut(name);
	}

	public int getGroup() {
		return group;
	}

	public void setName(String name) {
		this.name = name;
	}

	public void setGroup(int group) {
		this.group = group;
	}

}
