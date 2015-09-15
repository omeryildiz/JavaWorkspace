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
			System.out.println("ba�ar�l� giri� yapt�n�z");
			mesaj = "Ba�ar�l� bir giri� yapt�n�z";
			return true;
		}
		else {
			System.out.println("hatal� giri� yapt�n�z");
			mesaj = "Ba�ar�s�z giri�";
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
