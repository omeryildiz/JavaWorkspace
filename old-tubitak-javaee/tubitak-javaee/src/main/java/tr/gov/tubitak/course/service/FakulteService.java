package tr.gov.tubitak.course.service;

import java.io.Serializable;

import javax.ejb.Stateful;
import javax.inject.Inject;

import tr.gov.tubitak.course.dao.FakulteDAO;
import tr.gov.tubitak.course.entity.Fakulte;
import tr.gov.tubitak.course.util.GenericDAO;
import tr.gov.tubitak.course.util.GenericService;

@Stateful
public class FakulteService extends GenericService<Fakulte> implements Serializable {

	@Inject FakulteDAO dao;

	@Override
	public GenericDAO<Fakulte> getDao() {
		return dao;
	}

}
