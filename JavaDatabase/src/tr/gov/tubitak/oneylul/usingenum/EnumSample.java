package tr.gov.tubitak.oneylul.usingenum;

public class EnumSample {
	
	public static void main(String[] args) {
		System.out.println(convertTurkish(Gender.female));
		
		for (DayOfWeek day : DayOfWeek.values()) {
			System.out.println(day.getTurkish()+" "+day.name());
		}
	}
	
	static String convertTurkish(Gender gender) {
		//str null gelirse null pointer exception alabiliriz bu nedenle 
//		if(str.equals("m")) {
//			return "Erkek";
//		}
//		else {
//			return "Bayan";
//		}
		
		if(Gender.male == gender) {
			return "Erkek";
		}
		else {
			return "Bayan";
		}
	}

}
