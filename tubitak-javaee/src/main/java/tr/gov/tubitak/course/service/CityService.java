package tr.gov.tubitak.course.service;

import javax.inject.Inject;

import tr.gov.tubitak.course.dao.CityDAO;
import tr.gov.tubitak.course.entity.City;
import tr.gov.tubitak.course.util.GenericDAO;
import tr.gov.tubitak.course.util.GenericService;

public class CityService extends GenericService<City>{

	@Inject CityDAO cityDao; 
	
	@Override
	public GenericDAO<City> getDao() {
		// TODO Auto-generated method stub
		return cityDao;
	}

}
