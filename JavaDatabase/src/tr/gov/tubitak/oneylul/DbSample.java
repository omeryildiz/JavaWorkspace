package tr.gov.tubitak.oneylul;

import java.util.List;

public class DbSample {
	public static void main(String[] args) throws Exception {
		OgrenciDao dao = new OgrenciDaoJdbcImpl();
		//transaction için kullanýlan metod
		 dao.insertWithCommit(2, "omer");
		 printList(dao.getAll());
		//Ogrenci x = dao.find(1);
		//System.out.println(x);
		//
		// dao.update(1, "kamil");
		// printList(dao.getAll());
		//
		// dao.delete(2);
		// printList(dao.getAll());

	}

	static void printList(List<Ogrenci> list) {
		for (Ogrenci ogrenci : list) {
			// sysout içinde tostring metodu yazýlmasa bile otomatik olarak
			// çaðrýlýr.
			System.out.println(ogrenci);
		}
	}

}
