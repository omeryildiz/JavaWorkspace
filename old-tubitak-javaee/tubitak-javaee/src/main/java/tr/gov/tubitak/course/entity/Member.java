package tr.gov.tubitak.course.entity;

import java.io.Serializable;

import javax.persistence.Column;
import javax.persistence.EmbeddedId;
import javax.persistence.Entity;

@Entity
public class Member implements Serializable {

	@EmbeddedId
	private MemberId id;
	private String name;
	@Column(columnDefinition = "Text")
	private String lastname;

	public MemberId getId() {
		return id;
	}

	public void setId(MemberId id) {
		this.id = id;
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public String getLastname() {
		return lastname;
	}

	public void setLastname(String lastname) {
		this.lastname = lastname;
	}

}
