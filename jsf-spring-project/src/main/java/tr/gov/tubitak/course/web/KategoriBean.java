package tr.gov.tubitak.course.web;

import java.io.Serializable;

import javax.annotation.PostConstruct;
import javax.inject.Named;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Scope;

import tr.gov.tubitak.course.entity.Kategori;
import tr.gov.tubitak.course.service.KategoriService;
import tr.gov.tubitak.course.util.GenericBean;
import tr.gov.tubitak.course.util.GenericService;

@Scope("session")
@Named
public class KategoriBean extends GenericBean<Kategori>implements Serializable {

	private static final long serialVersionUID = 2826532775186949733L;
	@Autowired
	KategoriService service;

	@Override
	public GenericService<Kategori> getService() {
		return service;
	}
	
	@PostConstruct
	@Override
	public void init(){
		super.init();
	}

}
