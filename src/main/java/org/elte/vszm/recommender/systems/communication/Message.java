package org.elte.vszm.recommender.systems.communication;

import lombok.*;

@Getter
@NoArgsConstructor
@AllArgsConstructor
@ToString
public class Message {

	private String method;
	private Task task;

}
