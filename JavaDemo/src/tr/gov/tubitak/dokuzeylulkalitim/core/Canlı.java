package tr.gov.tubitak.dokuzeylulkalitim.core;

//e�er s�n�f�n ba��na final eklersem extend edilemez bir s�n�f olu�turulmu� olur.
public class Canl� {
	private String type = "Canli";
	
	public Canl�(String type) {
		this.type = type;
		System.out.println("canli olustu");
	}

	// e�er nefesAl metodunun de�i�memesini garanti etmek istiyorsak final keywordun� kullanabilirim.
	public void nefesAl() {
		System.out.println(type+" nefes aldi");
	}

	public void setType(String type) {
		this.type = type;
	}
	
	public class Kol{
		
	}

}
