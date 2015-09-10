package tr.gov.tubitak.oneylul.usingenum;

public enum DayOfWeek {
	monday("Pazartesi"),
	tuesday("Sal�"),
	wednesday("�ar�amba"),
	thursday("Per�embe"),
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
