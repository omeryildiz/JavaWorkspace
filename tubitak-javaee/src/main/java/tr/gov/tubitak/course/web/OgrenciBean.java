package tr.gov.tubitak.course.web;

import javax.annotation.PostConstruct;
import javax.enterprise.context.SessionScoped;
import javax.inject.Inject;
import javax.inject.Named;

import tr.gov.tubitak.course.entity.Ogrenci;
import tr.gov.tubitak.course.service.OgrenciService;
import tr.gov.tubitak.course.util.GenericService;

@Named
@SessionScoped
public class OgrenciBean extends GenericBean<Ogrenci> {

	private static final long serialVersionUID = -6140175991234758351L;
	@Inject
	OgrenciService ogrenciService;

	@Override
	public GenericService<Ogrenci> getService() {
		return ogrenciService;
	}

	@PostConstruct
	@Override
	public void init() {
		super.init();
	}

}
