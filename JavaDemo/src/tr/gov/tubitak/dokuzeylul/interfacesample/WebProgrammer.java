package tr.gov.tubitak.dokuzeylul.interfacesample;

public class WebProgrammer {

	public WebProgrammer() {
		//interface metodlar� soyutlam�� oldu.
		//y�ntemler de�i�se bile metod isimleri ve ald��� parametreler ayn� kald�.
		TestDao dao = new TestDaoImp();
		TestDao daon = new TestDaoWsCallImpl();
		((Updatetable) daon).update("");
		daon.read();
		dao.insert("�mer");
		System.out.println(dao.read());
	}

}
