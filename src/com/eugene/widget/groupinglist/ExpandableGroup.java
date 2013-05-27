package com.eugene.widget.groupinglist;

public class ExpandableGroup implements ExpandableItem {
	
	private final String name;
	
	public ExpandableGroup(String name) {
		this.name = name;
	}

	@Override
	public String getName() {
		return this.name;
	}

}
