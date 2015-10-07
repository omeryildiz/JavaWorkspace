package tr.gov.tubitak.course.service;

import javax.ejb.Stateful;
import javax.inject.Inject;

import tr.gov.tubitak.course.dao.OgrenciDAO;
import tr.gov.tubitak.course.entity.Ogrenci;
import tr.gov.tubitak.course.util.GenericDAO;
import tr.gov.tubitak.course.util.GenericService;

@Stateful
public class OgrenciService extends GenericService<Ogrenci> {
	
	@Inject OgrenciDAO ogrenciDao;

	@Override
	public GenericDAO<Ogrenci> getDao() {
		
		return ogrenciDao;
	}
	

}
