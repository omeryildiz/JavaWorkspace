package tr.gov.tubitak.course.service;

import javax.ejb.Stateful;
import javax.inject.Inject;

import tr.gov.tubitak.course.dao.StreetDAO;
import tr.gov.tubitak.course.entity.Street;
import tr.gov.tubitak.course.util.GenericDAO;
import tr.gov.tubitak.course.util.GenericService;

@Stateful
public class StreetService extends GenericService<Street> {

	private static final long serialVersionUID = -90139513439501720L;
	@Inject
	StreetDAO dao;

	@Override
	public GenericDAO<Street> getDao() {
		return dao;
	}

}
