package tr.gov.tubitak.oneylul.annotation;

import java.util.Date;

public class AnnotationSample {
	
	private int id;
	private String name;
	private Date kayitTarihi;

	public int getId() {
		return id;
	}

	public String getName() {
		return name;
	}
	
	@LogAnnotation(name = "deneme2", value = "iki deðerli")
	public void setName(String name) {
		this.name = name;
	}

	public Date getKayitTarihi() {
		return kayitTarihi;
	}

	public void setKayitTarihi(Date kayitTarihi) {
		this.kayitTarihi = kayitTarihi;
	}

}
