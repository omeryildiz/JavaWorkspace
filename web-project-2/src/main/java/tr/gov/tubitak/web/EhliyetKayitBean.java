package tr.gov.tubitak.web;

import java.io.Serializable;

import javax.faces.application.FacesMessage;
import javax.faces.bean.ManagedBean;
import javax.faces.bean.RequestScoped;
import javax.faces.component.html.HtmlCommandButton;
import javax.faces.context.FacesContext;

@ManagedBean
@RequestScoped
public class EhliyetKayitBean implements Serializable {

	private String ad;
	private String soyad;
	private Integer yas;

	private String mesaj;
	
	private HtmlCommandButton butonunKendisi;

	public void kaydet() {
		if (yas < 18) {
			setMesaj("18 yasindan kucukler ehliyet kaydi yapamaz...");
		} else {
			setMesaj("Kaydiniz basari ile yapildi...");
			FacesContext.getCurrentInstance().addMessage(null, new FacesMessage(mesaj));
		}

		butonunKendisi.setRendered(false);
	}

	public HtmlCommandButton getButonunKendisi() {
		return butonunKendisi;
	}

	public void setButonunKendisi(HtmlCommandButton butonunKendisi) {
		this.butonunKendisi = butonunKendisi;
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

	public Integer getYas() {
		return yas;
	}

	public void setYas(Integer yas) {
		this.yas = yas;
	}

	public String getMesaj() {
		return mesaj;
	}

	public void setMesaj(String mesaj) {
		this.mesaj = mesaj;
	}

}
