package tr.gov.tubitak.dokuzeylul;

public class StaticSample {
	static int getPencereId() {
		return Pencere.getId();
	}
	public static void main(String[] args) {
		Pencere pencere = Pencere.getInstance("Çift Cam");
		Pencere pencere2 = Pencere.getInstance("Default");
		System.out.println(pencere.getType());
		System.out.println(pencere.getId());
		System.out.println(pencere2.getId());

	}

}
