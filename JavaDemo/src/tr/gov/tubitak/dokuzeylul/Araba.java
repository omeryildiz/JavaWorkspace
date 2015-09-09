package tr.gov.tubitak.dokuzeylul;

public class Araba {
	public static String type = writeName("sedan");
	private String name = writeName("Mercedes");
	
	public Araba() {
		System.out.println("Araba oluþturuldu.");
	}
	
	public static String writeName(String str)
	{
		System.out.println(str);
		return str;
	}
	
public static void main(String[] args) {
	new Araba();
}
}
