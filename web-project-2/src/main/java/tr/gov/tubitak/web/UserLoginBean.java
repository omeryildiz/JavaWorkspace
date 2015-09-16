package tr.gov.tubitak.web;

import java.io.Serializable;

import javax.faces.bean.RequestScoped;

import javax.faces.bean.ManagedBean;

@ManagedBean
@RequestScoped
public class UserLoginBean implements Serializable{

	private static final long serialVersionUID = 1L;
	private String username;
	private String password;
	
	private String mesaj = "";
	
	private Boolean beniHatirla;
	
	public boolean login(){
		if(username.equals("admin") && password.equals("123456")){
			setMesaj("Basarili bir sekilde giris yaptiniz...");
			return true;
		}
		else{
			setMesaj("Hatali giris yaptiniz...");
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


	public Boolean getBeniHatirla() {
		return beniHatirla;
	}


	public void setBeniHatirla(Boolean beniHatirla) {
		this.beniHatirla = beniHatirla;
	}
	
}
