package tr.gov.tubitak.course.web;

import javax.enterprise.context.SessionScoped;
import javax.inject.Inject;
import javax.inject.Named;

import tr.gov.tubitak.course.entity.Street;
import tr.gov.tubitak.course.service.StreetService;
import tr.gov.tubitak.course.util.GenericService;

@Named
@SessionScoped
public class StreetBean extends GenericBean<Street> {

	private static final long serialVersionUID = 3353131744348503730L;
	@Inject
	StreetService service;

	@Override
	public GenericService<Street> getService() {
		return service;
	}


}
