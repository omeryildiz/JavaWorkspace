package tr.gov.tubitak.dokuzeylul.abstractsample;

public abstract class AbstractYazici {
	
	public final void yazdir(String str) {
		System.out.println(str+ " "+ getPrintType() +" olarak yazdirildi");
	}
	
	public abstract String getPrintType();
}
