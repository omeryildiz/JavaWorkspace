package tr.gov.tubitak.course.entity;

import java.io.Serializable;
import java.util.List;

import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.ManyToMany;
import javax.persistence.ManyToOne;

@Entity
public class Urun implements Serializable {

	private static final long serialVersionUID = -7249345903888819822L;

	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private Long id;
	private String name;
	private Double fiyat;
	private Float stok;
	@ManyToOne
	private Kategori kategori;

	@ManyToMany
	private List<Ozellikler> ozellikler;

	public Long getId() {
		return id;
	}

	public void setId(Long id) {
		this.id = id;
	}

	public Double getFiyat() {
		return fiyat;
	}

	public void setFiyat(Double fiyat) {
		this.fiyat = fiyat;
	}

	public Float getStok() {
		return stok;
	}

	public void setStok(Float stok) {
		this.stok = stok;
	}

	public Kategori getKategori() {
		return kategori;
	}

	public void setKategori(Kategori kategori) {
		this.kategori = kategori;
	}

	public List<Ozellikler> getOzellikler() {
		return ozellikler;
	}

	public void setOzellikler(List<Ozellikler> ozellikler) {
		this.ozellikler = ozellikler;
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

}
