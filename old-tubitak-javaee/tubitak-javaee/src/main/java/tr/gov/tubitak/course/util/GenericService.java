package tr.gov.tubitak.course.util;

import java.io.Serializable;
import java.util.List;

import javax.ejb.TransactionAttribute;
import javax.ejb.TransactionAttributeType;

public abstract class GenericService<E> implements Serializable{

	public abstract GenericDAO<E> getDao();

	@TransactionAttribute(TransactionAttributeType.NOT_SUPPORTED)
	@SuppressWarnings("unchecked")
	public List<E> getList() {
		return getDao().getList();
	}

	@TransactionAttribute(TransactionAttributeType.REQUIRED)
	public void save(E e) {
		getDao().save(e);
	}

	@TransactionAttribute(TransactionAttributeType.REQUIRED)
	public void remove(E e) {
		getDao().remove(e);
	}

	@TransactionAttribute(TransactionAttributeType.REQUIRED)
	public void update(E e) {
		getDao().update(e);
	}
}
