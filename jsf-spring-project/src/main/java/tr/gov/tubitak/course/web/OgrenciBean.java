package tr.gov.tubitak.course.web;

import java.io.Serializable;
import java.util.List;

import javax.annotation.PostConstruct;
import javax.faces.bean.RequestScoped;
import javax.inject.Named;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Scope;

import tr.gov.tubitak.course.entity.Ogrenci;
import tr.gov.tubitak.course.service.OgrenciService;

//@Controller("ogrenciBean")
@Scope("session")
@Named
public class OgrenciBean implements Serializable{
	
	private static final long serialVersionUID = 7352289225012061778L;
	private Ogrenci ogrenci = new Ogrenci();
	private List<Ogrenci> list;
	
	@Autowired OgrenciService ogrenciService;
	
	
	@PostConstruct
	public void init(){
		this.list = ogrenciService.list();
	}
	
	public void save(){
		ogrenciService.save(ogrenci);
		ogrenci = new Ogrenci();
		init();
	}
	
	public void update(){
		ogrenciService.update(ogrenci);
		ogrenci = new Ogrenci();
		init();
	}
	
	public void remove(Ogrenci ogrenci) {
		ogrenciService.remove(ogrenci);
		init();
	}
	
	public void select(Ogrenci ogrenci){
		this.ogrenci = ogrenci;
	}

	public Ogrenci getOgrenci() {
		return ogrenci;
	}

	public void setOgrenci(Ogrenci ogrenci) {
		this.ogrenci = ogrenci;
	}

	public List<Ogrenci> getList() {
		return list;
	}

	public void setList(List<Ogrenci> list) {
		this.list = list;
	}
}
