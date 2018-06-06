package org.elte.vszm.recommender.systems.data;

import lombok.Getter;

@Getter
public class GraphEdgeDirected extends GraphObject {

	private final String fromId;
	private final String toId;
	private final Double rating;

	public GraphEdgeDirected(String id, String name, String type, String fromId, String toId, Double rating) {
		super(id, name, type);
		this.fromId = fromId;
		this.toId = toId;
		this.rating = rating;
	}
}
