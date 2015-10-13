package tr.gov.tubitak.course.service;

import java.io.Serializable;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import tr.gov.tubitak.course.dao.KategoriDao;
import tr.gov.tubitak.course.entity.Kategori;
import tr.gov.tubitak.course.util.GenericDAO;
import tr.gov.tubitak.course.util.GenericService;

@Service
public class KategoriService extends GenericService<Kategori>implements Serializable {

	private static final long serialVersionUID = -770820706491427113L;
	
	@Autowired KategoriDao dao;

	@Override
	public GenericDAO<Kategori> getDao() {
		return dao;
	}

}
