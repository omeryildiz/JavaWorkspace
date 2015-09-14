package tr.gov.tubitak.ondorteylul;

import java.util.Calendar;

import javax.faces.bean.ManagedBean;
import javax.faces.bean.RequestScoped;

@ManagedBean
@RequestScoped
public class Person {
	private String name;
	private String surname;
	private int birthdayYear;
	private String mesaj;

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public String getSurname() {
		return surname;
	}

	public void setSurname(String surname) {
		this.surname = surname;
	}

	public int getBirthdayYear() {
		return birthdayYear;
	}

	public void setBirthdayYear(int birthdayYear) {
		this.birthdayYear = birthdayYear;
	}

	public void onceKontrolEt() {
		int year = Calendar.getInstance().get(Calendar.YEAR);
		if (year - this.birthdayYear >= 18) {
			kaydet();

		} else {
			System.out.println("Yaş küçük olduğundan dolayı kayıt yapılamadı");

		}

	}

	public void kaydet() {

	}

	public String getMesaj() {
		return mesaj;
	}

	public void setMesaj(String mesaj) {
		this.mesaj = mesaj;
	}


}
