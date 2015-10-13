package tr.gov.tubitak.course.service;

import java.io.Serializable;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import tr.gov.tubitak.course.dao.OzelliklerDao;
import tr.gov.tubitak.course.entity.Ozellikler;
import tr.gov.tubitak.course.util.GenericDAO;
import tr.gov.tubitak.course.util.GenericService;

@Service
public class OzelliklerService extends GenericService<Ozellikler>implements Serializable {

	private static final long serialVersionUID = 2995103577280308446L;
	
	@Autowired OzelliklerDao dao;

	@Override
	public GenericDAO<Ozellikler> getDao() {
		return dao;
	}

}
