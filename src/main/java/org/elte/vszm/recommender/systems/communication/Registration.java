package org.elte.vszm.recommender.systems.communication;

import lombok.*;

@Getter
@ToString
public class Registration extends Message {


	public Registration() {
		super("register", Task.builder().methodName("recommendations_by_vszm").developerEmail("wayasam@gmail.com")
			.developerName("Szegedi Gábor").developerNeptun("D071PO").build());
	}
}
