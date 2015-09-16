package tr.gov.tubitak.web;

import java.io.Serializable;

import javax.annotation.PostConstruct;
import javax.annotation.PreDestroy;
import javax.faces.bean.ApplicationScoped;
import javax.faces.bean.ManagedBean;
import javax.faces.bean.RequestScoped;
import javax.faces.bean.SessionScoped;
import javax.faces.bean.ViewScoped;

@ManagedBean(eager = true)
@ApplicationScoped
public class CounterBean implements Serializable{

	@PostConstruct
	public void baslangic(){
		System.out.println("Nesne context icerisine alindi...");
	}
	
	@PreDestroy
	public void end(){
		System.out.println("Nesne context icerisinden cikarildi...");
	}
	
	private Integer count = 0;
	
	public void next(){
		count ++;
	}

	public Integer getCount() {
		return count;
	}

	public void setCount(Integer count) {
		this.count = count;
	}
	
	
}
