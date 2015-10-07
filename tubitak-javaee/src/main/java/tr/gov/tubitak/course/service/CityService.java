package tr.gov.tubitak.course.service;

import java.io.Serializable;

import javax.ejb.Stateful;
import javax.inject.Inject;

import tr.gov.tubitak.course.dao.CityDAO;
import tr.gov.tubitak.course.entity.City;
import tr.gov.tubitak.course.util.GenericDAO;
import tr.gov.tubitak.course.util.GenericService;

@Stateful
public class CityService extends GenericService<City> implements Serializable{

	@Inject CityDAO cityDao; 
	
	@Override
	public GenericDAO<City> getDao() {
		// TODO Auto-generated method stub
		return cityDao;
	}

}
