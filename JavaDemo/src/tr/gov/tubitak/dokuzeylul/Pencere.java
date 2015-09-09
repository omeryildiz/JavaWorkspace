package tr.gov.tubitak.dokuzeylul;

public class Pencere {
	private static int id = 0;
	private String type;


	// bu �ekilde new ile �a��rmay� engellemi� olduk, pencereden yeni bir obje
	// olu�turmamak i�in private yapt�k constructoru
	private Pencere() {
		id++;
	}

	// static metodlar� overwrite edemeyiz
	public static Pencere getInstance(String type) {
		Pencere pencere = new Pencere();
		pencere.type = type;
		return pencere;
	}

	// Singleton uygulmas�

	public String getType() {
		return type;
	}

	public static int getId() {
		return id;
	}
	
	public static void main(String[] args) {
		System.out.println(Pencere.getId());
		Pencere pencere = Pencere.getInstance("tek cam");
		Pencere.getInstance("tek cam");
		Pencere.getInstance("tek cam");
		System.out.println(Pencere.getId());
		System.out.println(StaticSample.getPencereId());
	}

	// public void setType(String type) {
	// this.type = type;
	// }

}
