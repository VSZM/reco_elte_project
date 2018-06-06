package org.elte.vszm.recommender.systems.communication;

public class AddRelation extends Message {

	public AddRelation(Task task) {
		super("addRelation", task);
	}
}
