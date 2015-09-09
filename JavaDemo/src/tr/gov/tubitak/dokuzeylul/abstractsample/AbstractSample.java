package tr.gov.tubitak.dokuzeylul.abstractsample;

public class AbstractSample {

	public static void main(String[] args) {
		AbstractYazici yazici;
		yazici = new RenkliYazici();
		yazici.yazdir("deneme");
		yazici = new SiyahBeyazYazici();
		yazici.yazdir("merhaba dünya");

	}

}
