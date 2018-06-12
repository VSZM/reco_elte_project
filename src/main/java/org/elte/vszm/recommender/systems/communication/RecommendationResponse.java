package org.elte.vszm.recommender.systems.communication;

import java.util.List;

import lombok.*;

@Builder
@Getter
@Setter
@ToString
@NoArgsConstructor
@AllArgsConstructor
public class RecommendationResponse {
	private int itemCount;
	private List<MovieRecommendation> itemList;

}
