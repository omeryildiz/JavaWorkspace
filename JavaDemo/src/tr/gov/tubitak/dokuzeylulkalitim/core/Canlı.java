package tr.gov.tubitak.dokuzeylulkalitim.core;

//eðer sýnýfýn baþýna final eklersem extend edilemez bir sýnýf oluþturulmuþ olur.
public class Canlý {
	private String type = "Canli";
	
	public Canlý(String type) {
		this.type = type;
		System.out.println("canli olustu");
	}

	// eðer nefesAl metodunun deðiþmemesini garanti etmek istiyorsak final keywordunü kullanabilirim.
	public void nefesAl() {
		System.out.println(type+" nefes aldi");
	}

	public void setType(String type) {
		this.type = type;
	}
	
	public class Kol{
		
	}

}
