package tr.gov.tubitak.dokuzeylul.interfacesample;

public class TestDaoImp implements TestDao {
	private String data = null;
	
	@Override
	public void insert(String data) {
		this.data = data;
		
	}

	@Override
	public void delete(String data) {
		this.data = null;
		
	}

	@Override
	public String read() {
		
		return data;
	}
	
	public void update() {
		
	}


}
