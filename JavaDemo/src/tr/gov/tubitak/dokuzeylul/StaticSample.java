package tr.gov.tubitak.dokuzeylul;

public class StaticSample {
	public static void main(String[] args) {
		Pencere pencere = Pencere.getInstance("�ift Cam");
		System.out.println(pencere.getType());
	}

}