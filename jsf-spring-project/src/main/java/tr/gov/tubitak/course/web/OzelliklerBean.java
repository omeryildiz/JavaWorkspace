package tr.gov.tubitak.course.web;

import java.io.Serializable;

import javax.annotation.PostConstruct;
import javax.inject.Named;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Scope;

import tr.gov.tubitak.course.entity.Ozellikler;
import tr.gov.tubitak.course.service.OzelliklerService;
import tr.gov.tubitak.course.util.GenericBean;
import tr.gov.tubitak.course.util.GenericService;

@Scope("session")
@Named
public class OzelliklerBean extends GenericBean<Ozellikler> implements Serializable {

	private static final long serialVersionUID = 5064179697530870279L;
    @Autowired
    OzelliklerService service;

	@Override
	public GenericService<Ozellikler> getService() {
		return service;
	}
	
	@PostConstruct
	@Override
	public void init(){
		super.init();
	}

}
