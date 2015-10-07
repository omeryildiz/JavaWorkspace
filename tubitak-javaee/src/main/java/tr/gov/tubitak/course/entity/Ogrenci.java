package tr.gov.tubitak.course.entity;

import java.io.Serializable;

import javax.persistence.Entity;
import javax.persistence.ManyToOne;

import tr.gov.tubitak.course.util.BaseEntity;

@Entity
public class Ogrenci extends BaseEntity implements Serializable {
	private String soyad;
	
	@ManyToOne
	private Fakulte fakulte;

	public String getSoyad() {
		return soyad;
	}

	public void setSoyad(String soyad) {
		this.soyad = soyad;
	}

	public Fakulte getFakulte() {
		return fakulte;
	}

	public void setFakulte(Fakulte fakulte) {
		this.fakulte = fakulte;
	}
}
