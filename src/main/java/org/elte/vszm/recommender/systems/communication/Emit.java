package org.elte.vszm.recommender.systems.communication;

import lombok.Getter;

@Getter
public class Emit extends Message {

	public Emit(String channelName, Integer itemCount) {
		super("emit", Task.builder().channel(channelName).itemCount(itemCount).build());
	}
}
