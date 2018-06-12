package org.elte.vszm.recommender.systems.data;

public enum AgeCategory {

	ONE(1, "Under 18"), EIGHTEEN(18, "18-24"), TWENTYFIVE(25, "25-34"), THIRTYFIVE(35,
		"35-44"), FORTYFIVE(45, "45-49"), FIFTY(50, "50-55"), FIFTYSIX(56,
		("56+"));

	private final int category;
	private final String representation;

	AgeCategory(int category, String representation) {
		this.category = category;
		this.representation = representation;
	}

	public static AgeCategory fromString(String str){
		int value = Integer.valueOf(str);

		for(AgeCategory ageCategory : AgeCategory.values()){
			if(value == ageCategory.category)
				return ageCategory;
		}

		return null;
	}

	@Override
	public String toString() {
		return representation;
	}
}
