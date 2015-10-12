package tr.gov.tubitak.course.service;

import java.io.Serializable;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import tr.gov.tubitak.course.dao.OgrenciDao;
import tr.gov.tubitak.course.entity.Ogrenci;

@Service
public class OgrenciService implements Serializable {

	private static final long serialVersionUID = -5850305585076898668L;
	@Autowired OgrenciDao ogrenciDao;
	
	public void setOgrenciDao(OgrenciDao ogrenciDao) {
		this.ogrenciDao = ogrenciDao;
	}


	@Transactional
	public void save(Ogrenci ogrenci) {
		ogrenciDao.save(ogrenci);
	}
	

	@Transactional
	public void remove(Ogrenci ogrenci) {
		ogrenciDao.remove(ogrenci);
	}
	

	@Transactional
	public void update(Ogrenci ogrenci) {
		ogrenciDao.update(ogrenci);
	}

	@Transactional
	public List<Ogrenci> list() {
		return ogrenciDao.list();
	}
}
