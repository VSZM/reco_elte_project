package org.elte.vszm.recommender.systems.data;


import lombok.*;

@Getter
@Builder
@AllArgsConstructor
@ToString
public class User {

	private Integer number;
	private char gender;
	private AgeCategory ageCategory;
	private Occupation occupation;
	private String zipCode;
}
