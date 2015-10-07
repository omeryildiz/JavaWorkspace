package tr.gov.tubitak.course.service;

import javax.ejb.Stateful;
import javax.inject.Inject;

import tr.gov.tubitak.course.dao.MahalleDAO;
import tr.gov.tubitak.course.entity.Mahalle;
import tr.gov.tubitak.course.util.GenericDAO;
import tr.gov.tubitak.course.util.GenericService;

@Stateful
public class MahalleService extends GenericService<Mahalle>{

	@Inject MahalleDAO mahalledDAO;
	
	@Override
	public GenericDAO<Mahalle> getDao() {
		return mahalledDAO;
	}

}
