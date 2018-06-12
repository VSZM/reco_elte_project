package org.elte.vszm.recommender.systems.communication;

import lombok.*;

@Getter
@Builder
@AllArgsConstructor
@NoArgsConstructor
@ToString
public class MovieRecommendation {

	private int id;
	private String type = "Movie";
	private double score;
	private String title;
	private int externalId;
}
