package tr.gov.tubitak.web;

import java.io.Serializable;

import javax.faces.bean.ManagedBean;
import javax.faces.bean.ViewScoped;

@ManagedBean
@ViewScoped
public class OrnekBean implements Serializable{

	private Integer sayi = 0;
	
	public String kaydet(){
		System.out.println("sayi : " + sayi);
		return "/ornek2.xhtml?faces-redirect=true";
	}

	public Integer getSayi() {
		return sayi;
	}

	public void setSayi(Integer sayi) {
		this.sayi = sayi;
	}
}
