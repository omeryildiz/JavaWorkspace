package tr.gov.tubitak.dokuzeylul.poly;

public class PolySample {

	public static void main(String[] args) {
//		Cokgen kare = new Kare();
//		Cokgen ucgen = new Ucgen();
//		kare.printKenarSayisi();
//		ucgen.printKenarSayisi();
		
		Kare kare = new Kare();
		Ucgen ucgen = new Ucgen();
		kenarSayisiYaz(kare);
		kenarSayisiYaz(ucgen);
		
		
		Cokgen altigen = new Cokgen(6);
		kenarSayisiYaz(altigen);
		altigen = kare;

		kenarSayisiYaz(altigen);
		
		
	}
	
	static void kenarSayisiYaz(Cokgen cokgen) {
		cokgen.printKenarSayisi();
	}

}
