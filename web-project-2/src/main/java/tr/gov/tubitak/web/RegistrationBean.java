package tr.gov.tubitak.web;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

import javax.faces.bean.ManagedBean;
import javax.faces.bean.RequestScoped;

@ManagedBean
@RequestScoped
public class RegistrationBean implements Serializable{

	private String[] secilenIlgiAlanlari;
	private List<String> ilgiAlaniListesi;
	
	public RegistrationBean() {
		ilgiAlaniListesi = new ArrayList<>();
		ilgiAlaniListesi.add("Muzik");
		ilgiAlaniListesi.add("Moda");
		ilgiAlaniListesi.add("Oyun");
	}
	
	public void kaydet(){
		for (String ilgiAlani : secilenIlgiAlanlari) {
			System.out.println(ilgiAlani);
		}
	}

	public String[] getSecilenIlgiAlanlari() {
		return secilenIlgiAlanlari;
	}

	public void setSecilenIlgiAlanlari(String[] secilenIlgiAlanlari) {
		this.secilenIlgiAlanlari = secilenIlgiAlanlari;
	}

	public List<String> getIlgiAlaniListesi() {
		return ilgiAlaniListesi;
	}

	public void setIlgiAlaniListesi(List<String> ilgiAlaniListesi) {
		this.ilgiAlaniListesi = ilgiAlaniListesi;
	}
	
	
}
