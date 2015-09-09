package tr.gov.tubitak.dokuzeylul.poly;

public class OyunAlani {

	public static void main(String[] args) {
		Sporcu futbolcu = new Futbolcu();
		futbolcu.setName("Ömer");
		futbolcu.setSurname("Yýldýz");
		futbolcu.setAge(27);
		((Futbolcu) futbolcu).at();
		
	}

}
