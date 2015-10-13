package tr.gov.tubitak.course.service;

import java.io.Serializable;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import tr.gov.tubitak.course.dao.UrunDao;
import tr.gov.tubitak.course.entity.Urun;
import tr.gov.tubitak.course.util.GenericDAO;
import tr.gov.tubitak.course.util.GenericService;

@Service
public class UrunService extends GenericService<Urun> implements Serializable{

	private static final long serialVersionUID = 6661732649378391055L;
	
	@Autowired UrunDao dao;

	@Override
	public GenericDAO<Urun> getDao() {
		return dao;
	}
	

}
