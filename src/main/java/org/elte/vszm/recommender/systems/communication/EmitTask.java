package org.elte.vszm.recommender.systems.communication;

import lombok.*;

@ToString
@Getter
@Builder
@AllArgsConstructor
public class EmitTask {

	private String channel;
	private String configName;

}
