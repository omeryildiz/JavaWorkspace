package tr.gov.tubitak.course.entity;

import java.io.Serializable;

import javax.persistence.Entity;
import javax.persistence.ManyToOne;

import tr.gov.tubitak.course.util.BaseEntity;

@Entity
public class Ogrenci extends BaseEntity implements Serializable {

	private static final long serialVersionUID = 4331079198315914029L;

	private String surname;

	@ManyToOne
	private Fakulte fakulte;

	public String getSurname() {
		return surname;
	}

	public void setSurname(String surname) {
		this.surname = surname;
	}

	public Fakulte getFakulte() {
		return fakulte;
	}

	public void setFakulte(Fakulte fakulte) {
		this.fakulte = fakulte;
	}

}
