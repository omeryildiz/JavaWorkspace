package tr.gov.tubitak.course.entity;

import java.io.Serializable;

import javax.persistence.Entity;

import tr.gov.tubitak.course.util.BaseEntity;

@Entity
public class Customer extends BaseEntity implements Serializable {

	private String lastname;

	public String getLastname() {
		return lastname;
	}

	public void setLastname(String lastname) {
		this.lastname = lastname;
	}
}
