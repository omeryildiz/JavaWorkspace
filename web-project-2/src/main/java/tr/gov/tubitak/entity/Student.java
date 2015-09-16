package tr.gov.tubitak.entity;

import java.util.Date;

public class Student {

	private String name;
	private String lastname;
	private Integer age;
	private Date birthday;
	private Double scholarship;

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

	public Integer getAge() {
		return age;
	}

	public void setAge(Integer age) {
		this.age = age;
	}

	public Date getBirthday() {
		return birthday;
	}

	public void setBirthday(Date birthday) {
		this.birthday = birthday;
	}

	public Double getScholarship() {
		return scholarship;
	}

	public void setScholarship(Double scholarship) {
		this.scholarship = scholarship;
	}
}
