package org.elte.vszm.recommender.systems.communication;

public class AddNode extends Message {

	public AddNode(Task task) {
		super("addNode", task);
	}
}
