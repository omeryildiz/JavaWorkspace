package tr.gov.tubitak.dokuzeylul.interfacesample;

public class TestDaoWsCallImpl implements TestDao, Updatetable {

	@Override
	public void insert(String data) {
		// TODO Auto-generated method stub
		System.out.println("web service insert");

	}

	@Override
	public void delete(String data) {
		// TODO Auto-generated method stub
		System.out.println("web service delete");

	}

	@Override
	public String read() {
		// TODO Auto-generated method stub
		System.out.println("web service read");
		return null;
	}

	@Override
	public int update(String str) {
		System.out.println("update i�lemi ger�ekle�tirildi");
		return 1;
	}


}
