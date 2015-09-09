package tr.gov.tubitak.dokuzeylul.interfacesample;

public class WebProgrammer {

	public WebProgrammer() {
		//interface metodlarý soyutlamýþ oldu.
		//yöntemler deðiþse bile metod isimleri ve aldýðý parametreler ayný kaldý.
		TestDao dao = new TestDaoImp();
		TestDao daon = new TestDaoWsCallImpl();
		((Updatetable) daon).update("");
		daon.read();
		dao.insert("ömer");
		System.out.println(dao.read());
	}

}
