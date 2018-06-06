package org.elte.vszm.recommender.systems.data;

import lombok.Getter;


@Getter
public class GraphNode extends GraphObject {

	private final boolean recommendable;


	public GraphNode(String id, String name, String type, boolean recommendable) {
		super(id, name, type);
		this.recommendable = recommendable;
	}
}
