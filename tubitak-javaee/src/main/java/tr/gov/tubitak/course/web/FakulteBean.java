package tr.gov.tubitak.course.web;

import java.io.Serializable;

import javax.annotation.PostConstruct;
import javax.enterprise.context.SessionScoped;
import javax.inject.Inject;
import javax.inject.Named;

import tr.gov.tubitak.course.entity.Fakulte;
import tr.gov.tubitak.course.service.FakulteService;
import tr.gov.tubitak.course.util.GenericService;

@Named
@SessionScoped
public class FakulteBean extends GenericBean<Fakulte> implements Serializable {

	private static final long serialVersionUID = 7018971744945257268L;
	@Inject
	FakulteService fakulteService;

	@Override
	public GenericService<Fakulte> getService() {
		return fakulteService;
	}
	
	@PostConstruct
	public void init(){
		super.init();
	}

}
