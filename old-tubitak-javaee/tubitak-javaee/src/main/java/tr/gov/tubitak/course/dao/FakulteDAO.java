package tr.gov.tubitak.course.dao;

import java.io.Serializable;

import javax.ejb.Stateful;

import tr.gov.tubitak.course.entity.Fakulte;
import tr.gov.tubitak.course.util.GenericDAO;

@Stateful
public class FakulteDAO extends GenericDAO<Fakulte>implements Serializable {

	private static final long serialVersionUID = -5917287323582650494L;

}
