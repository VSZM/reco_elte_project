package org.elte.vszm.recommender.systems.communication;

import lombok.Getter;

@Getter
public class Emit {

	private final String method;
	private final EmitTask task;

	public Emit(String responceChannel) {
		this.method = "emit";
		this.task = EmitTask.builder().channel(responceChannel).build();
	}
}
