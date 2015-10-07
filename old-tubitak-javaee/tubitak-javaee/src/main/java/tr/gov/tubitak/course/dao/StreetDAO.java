package tr.gov.tubitak.course.dao;

import javax.ejb.Stateful;

import tr.gov.tubitak.course.entity.Street;
import tr.gov.tubitak.course.util.GenericDAO;

@Stateful
public class StreetDAO extends GenericDAO<Street> {

	private static final long serialVersionUID = -5172691925140367974L;

}
