package tr.gov.tubitak.web;

import java.io.Serializable;

import javax.faces.bean.ManagedBean;
import javax.faces.bean.RequestScoped;

import org.hibernate.validator.constraints.Email;

@ManagedBean
@RequestScoped
public class KullaniciKayitBean implements Serializable {
	private String ad;
	@Email
	private String email;
	public String getAd() {
		return ad;
	}
	public void setAd(String ad) {
		this.ad = ad;
	}
	public String getEmail() {
		return email;
	}
	public void setEmail(String email) {
		this.email = email;
	}

}
