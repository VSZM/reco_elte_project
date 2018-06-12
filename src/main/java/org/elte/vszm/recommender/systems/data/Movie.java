package org.elte.vszm.recommender.systems.data;

import java.util.List;

import lombok.*;

@Getter
@Builder
@AllArgsConstructor
@NoArgsConstructor
@Setter
@ToString
public class Movie {

	private Integer number;
	private String title;
	private List<String> categories;
}
