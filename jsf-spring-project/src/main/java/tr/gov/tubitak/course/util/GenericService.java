package tr.gov.tubitak.course.util;

import java.io.Serializable;
import java.util.List;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public abstract class GenericService<E> implements Serializable {

	public abstract GenericDAO<E> getDao();

	@Transactional
	public List<E> getList() {
		return getDao().getList();
	}

	@Transactional
	public void save(E e) {
		getDao().save(e);
	}

	@Transactional
	public void remove(E e) {
		getDao().remove(e);
	}

	@Transactional
	public void update(E e) {
		getDao().update(e);
	}
}
