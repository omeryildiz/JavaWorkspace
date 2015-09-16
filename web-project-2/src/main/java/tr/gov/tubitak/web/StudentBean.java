package tr.gov.tubitak.web;

import java.io.Serializable;

import javax.faces.bean.ManagedProperty;
import javax.faces.bean.RequestScoped;

import javax.faces.bean.ManagedBean;

@ManagedBean
@RequestScoped
public class StudentBean implements Serializable{

	private String name;
	private String lastname;
	
	@ManagedProperty("#{userBean}")
	private UserBean userBean;
	
	@ManagedProperty("#{userBean.username}")
	private String username;
	
	public void save(){
		System.out.println(name);
		System.out.println(lastname);
		System.out.println("Kaydeden kullanici : " + userBean.getUsername());
		System.out.println("Kaydeden kullanici : " + username);
	}
	
	public void setUserBean(UserBean userBean) {
		this.userBean = userBean;
	}
	
	public void setUsername(String username) {
		this.username = username;
	}
	
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
	
}
