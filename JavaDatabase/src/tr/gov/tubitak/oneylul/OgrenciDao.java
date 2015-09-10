package tr.gov.tubitak.oneylul;

import java.util.List;

public interface OgrenciDao {
	void insert(int id, String ad) throws Exception;

	void update(int id, String ad) throws Exception;

	void delete(int id) throws Exception;

	//List<Ogrenci> getAll() throws Exception;
	List<Ogrenci> getAll();

	Ogrenci find(int id) throws Exception;
	//transaction örneði
	public void insertWithCommit(int id, String ad) throws Exception;

}
