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
		printInfo(new Kare());
		
		
	}
	
	static void kenarSayisiYaz(Cokgen cokgen) {
		cokgen.printKenarSayisi();
	}
	
	static void printInfo(Cokgen cokgen) {
		if (cokgen instanceof Kare) {
			System.out.println(((Kare) cokgen).getKenarSayisi());
		}else if (cokgen instanceof Ucgen) {
			((Ucgen) cokgen).printKenarSayisi();
		}else{
			cokgen.printKenarSayisi();
		}

	}

}
