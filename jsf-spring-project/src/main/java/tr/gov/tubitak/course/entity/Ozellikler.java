package tr.gov.tubitak.course.entity;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.ManyToMany;

@Entity
public class Ozellikler implements Serializable {

	private static final long serialVersionUID = -7766088249178879233L;
	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private Long id;
	private String name;

	@ManyToMany( mappedBy = "ozelliklers")
	private List<Urun> urun = new ArrayList<Urun>();

	public Long getId() {
		return id;
	}

	public void setId(Long id) {
		this.id = id;
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public List<Urun> getUrun() {
		return urun;
	}

	public void setUrun(List<Urun> urun) {
		this.urun = urun;
	}

}
