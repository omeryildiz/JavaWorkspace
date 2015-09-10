package tr.gov.tubitak.oneylul.usingenum;

public enum DayOfWeek {
	monday("Pazartesi"),
	tuesday("Salı"),
	wednesday("Çarşamba"),
	thursday("Perşembe"),
	friday("Cuma"),
	saturday("Ctesi"),
	sunday("Pazar");
	private String turkisMean;
	private DayOfWeek(String mean) {
		turkisMean = mean;
	}
	
	public String getTurkish(){
		return turkisMean;
	}
	
	

}
