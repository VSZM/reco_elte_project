package org.elte.vszm.recommender.systems.communication;

import lombok.*;

@Getter
@RequiredArgsConstructor
@ToString
public class Message {

	private final String method;
	private final Task task;

}
