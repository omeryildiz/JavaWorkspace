package tr.gov.tubitak.oneylul;

import java.util.Date;

public class Ogrenci {
	private int id;
	private String name;
	private Date kayitTarihi;

	public int getId() {
		return id;
	}

	public void setId(int id) {
		this.id = id;
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public Date getKayitTarihi() {
		return kayitTarihi;
	}

	public void setKayitTarihi(Date kayitTarihi) {
		this.kayitTarihi = kayitTarihi;
	}

	@Override
	public String toString() {
		System.out.println("toString Metodu çaðrýldý");
		return "Ogrenci [id=" + id + ", name=" + name + ", kayitTarihi=" + kayitTarihi + "]";
	}
	

	

}
