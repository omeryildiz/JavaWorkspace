package tr.gov.tubitak.dokuzeylul.poly;

public class OyunAlani {

	public static void main(String[] args) {
		Sporcu sporcu = new Sporcu.Builder().age(25).name("Ahmet YILDIZ").Build();
		Sporcu futbolcu = new Futbolcu();
		futbolcu.setName("Ömer");
		futbolcu.setSurname("Yýldýz");
		futbolcu.setAge(27);
		((Futbolcu) futbolcu).at();
		
	}

}
