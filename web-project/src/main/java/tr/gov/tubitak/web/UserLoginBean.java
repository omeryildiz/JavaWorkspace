package tr.gov.tubitak.web;

import javax.faces.bean.ManagedBean;
import javax.faces.bean.RequestScoped;

@ManagedBean
@RequestScoped
public class UserLoginBean {
	private String[] secilenIlgiAlanlari;
	private String username;
	private String password;
	private String mesaj;
	
	public void yazdir(String[] secilenIlgiAlanari) 
	{
		for (String string : secilenIlgiAlanari) {
			System.out.println(string);
		}
	}
	
	public boolean beniHatirla() 
	{
		return true;
	}
	
	public boolean login() {
		if(username.equals("admin") && password.equals("123456")){
			System.out.println("baþarýlý giriþ yaptýnýz");
			mesaj = "Baþarýlý bir giriþ yaptýnýz";
			return true;
		}
		else {
			System.out.println("hatalý giriþ yaptýnýz");
			mesaj = "Baþarýsýz giriþ";
			return false;
			
		}
	}
	
	public String getUsername() {
		return username;
	}
	public void setUsername(String username) {
		this.username = username;
	}
	public String getPassword() {
		return password;
	}
	public void setPassword(String password) {
		this.password = password;
	}

	public String getMesaj() {
		return mesaj;
	}

	public void setMesaj(String mesaj) {
		this.mesaj = mesaj;
	}

	public String[] getSecilenIlgiAlanlari() {
		return secilenIlgiAlanlari;
	}

	public void setSecilenIlgiAlanlari(String[] secilenIlgiAlanlari) {
		this.secilenIlgiAlanlari = secilenIlgiAlanlari;
	}

}
