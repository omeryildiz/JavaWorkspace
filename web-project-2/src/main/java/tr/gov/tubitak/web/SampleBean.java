package tr.gov.tubitak.web;

import java.io.Serializable;

import javax.faces.bean.ManagedBean;
import javax.faces.bean.RequestScoped;

@ManagedBean
@RequestScoped
public class SampleBean implements Serializable{

	private String ad;
	private String soyad;
	
	
	public void kaydet(){
		System.out.println("ad : " + ad);
		System.out.println("soyad : " + soyad);
		System.out.println("Kayit yapildi...");
	}


	public String getAd() {
		return ad;
	}


	public void setAd(String ad) {
		this.ad = ad;
	}


	public String getSoyad() {
		return soyad;
	}


	public void setSoyad(String soyad) {
		this.soyad = soyad;
	}
}
