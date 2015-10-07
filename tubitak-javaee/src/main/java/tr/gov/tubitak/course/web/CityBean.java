package tr.gov.tubitak.course.web;

import java.io.Serializable;

import javax.annotation.PostConstruct;
import javax.enterprise.context.SessionScoped;
import javax.inject.Inject;
import javax.inject.Named;

import tr.gov.tubitak.course.entity.City;
import tr.gov.tubitak.course.service.CityService;
import tr.gov.tubitak.course.util.GenericService;

@Named
@SessionScoped
public class CityBean extends GenericBean<City> implements Serializable{

	@Inject CityService cityService;

	@Override
	public GenericService<City> getService() {
		return cityService;
	}
	
	@PostConstruct
	@Override
	public void init() {
		super.init();
	}
}
