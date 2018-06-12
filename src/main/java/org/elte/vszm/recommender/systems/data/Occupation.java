package org.elte.vszm.recommender.systems.data;


/*

 *  0:  "other" or not specified
 *  1:  "academic/educator"
 *  2:  "artist"
 *  3:  "clerical/admin"
 *  4:  "college/grad student"
 *  5:  "customer service"
 *  6:  "doctor/health care"
 *  7:  "executive/managerial"
 *  8:  "farmer"
 *  9:  "homemaker"
 * 10:  "K-12 student"
 * 11:  "lawyer"
 * 12:  "programmer"
 * 13:  "retired"
 * 14:  "sales/marketing"
 * 15:  "scientist"
 * 16:  "self-employed"
 * 17:  "technician/engineer"
 * 18:  "tradesman/craftsman"
 * 19:  "unemployed"
 * 20:  "writer"s

 */
public enum Occupation {

	ZERO(0, "other"), ONE(1, "academic/educator"), TWO(2, "artist"), THREE(3, "clerical/admin"),
	FOUR(4, "college/grad student"), FIVE(5, "customer service"), SIX(6, "doctor/health care"),
	SEVEN(7, "executive/managerial"), EIGHT(8, "farmer"), NINE(9, "homemaker"), TEN(10, "K-12 student"),
	ELEVEN(11, "lawyer"), TWELVE(12, "programmer"), THIRTEEN(13, "retired"), FOURTEEN(14, "sales/marketing"),
	FIFTEEN(15, "scientist"), SIXTEEN(16, "self-employed"), SEVENTEEN(17, "technician/engineer"),
	EIGHTEEN(18, "tradesman/craftsman"), NINETEEN(19, "unemployed"), TWENTY(20, "writer");

	private final int category;
	private final String representation;

	Occupation(int category, String representation) {
		this.category = category;
		this.representation = representation;
	}

	public static Occupation fromString(String str) {
		int value = Integer.valueOf(str);

		for (Occupation occupation : Occupation.values()) {
			if (value == occupation.category) {
				return occupation;
			}
		}

		return null;
	}

	@Override
	public String toString() {
		return representation;
	}

}
