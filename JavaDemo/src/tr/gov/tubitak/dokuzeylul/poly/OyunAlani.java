package tr.gov.tubitak.dokuzeylul.poly;

public class OyunAlani {

	public static void main(String[] args) {
		Sporcu futbolcu = new Futbolcu();
		futbolcu.setName("�mer");
		futbolcu.setSurname("Y�ld�z");
		futbolcu.setAge(27);
		((Futbolcu) futbolcu).at();
		
	}

}
