package tr.gov.tubitak.course.dao;

import java.io.Serializable;

import org.springframework.stereotype.Repository;

import tr.gov.tubitak.course.entity.Ozellikler;
import tr.gov.tubitak.course.util.GenericDAO;

@Repository
public class OzelliklerDao extends GenericDAO<Ozellikler>implements Serializable {

	private static final long serialVersionUID = 8561667053408967820L;

}
