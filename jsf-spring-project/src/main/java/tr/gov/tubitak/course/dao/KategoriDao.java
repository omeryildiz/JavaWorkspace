package tr.gov.tubitak.course.dao;

import java.io.Serializable;

import org.springframework.stereotype.Repository;

import tr.gov.tubitak.course.entity.Kategori;
import tr.gov.tubitak.course.util.GenericDAO;

@Repository
public class KategoriDao extends GenericDAO<Kategori>implements Serializable {

	private static final long serialVersionUID = -2399817255343702492L;

}
