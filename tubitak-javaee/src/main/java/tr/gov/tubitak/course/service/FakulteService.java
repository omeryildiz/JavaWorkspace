package tr.gov.tubitak.course.service;

import java.io.Serializable;

import javax.inject.Inject;

import tr.gov.tubitak.course.dao.FakulteDAO;
import tr.gov.tubitak.course.entity.Fakulte;
import tr.gov.tubitak.course.util.GenericDAO;
import tr.gov.tubitak.course.util.GenericService;

public class FakulteService extends GenericService<Fakulte> implements Serializable{
@Inject FakulteDAO fakulteDao;

@Override
public GenericDAO<Fakulte> getDao() {
	// TODO Auto-generated method stub
	return fakulteDao;
}
}
