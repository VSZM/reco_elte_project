package org.elte.vszm.recommender.systems.communication;

import lombok.*;

@ToString
@Getter
@Builder
@AllArgsConstructor
public class Task {

	private String methodName;
	private String developerEmail;
	private String developerName;
	private String developerNeptun;
	private String channel;
	private String configName;
	private String title;
	private String type;
	private String externalId;
	private Boolean recommendable;
	private String fromExternalId;
	private String toExternalId;
	private Double rating;


}
