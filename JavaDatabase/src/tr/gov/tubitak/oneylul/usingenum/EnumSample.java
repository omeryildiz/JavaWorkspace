package tr.gov.tubitak.oneylul.usingenum;

public class EnumSample {
	
	public static void main(String[] args) {
		System.out.println(convertTurkish("m"));
	}
	
	static String convertTurkish(String str) {
		//str null gelirse null pointer exception alabiliriz bu nedenle 
//		if(str.equals("m")) {
//			return "Erkek";
//		}
//		else {
//			return "Bayan";
//		}
		
		if("m".equals(str)) {
			return "Erkek";
		}
		else {
			return "Bayan";
		}
	}

}
