package tr.gov.tubitak.dokuzeylulkalitim.sample1;

public class Employer {
	private String name;
	private String surname;
	private String no;
	

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public String getSurname() {
		return surname;
	}

	public void setSurname(String surname) {
		this.surname = surname;
	}

	public String getNo() {
		return no;
	}

	public void setNo(String no) {
		this.no = no;
	}
	
	public void work() {
		System.out.println("Çalýþýyor");
	}
	
	public void getPayment() {
		System.out.println("Maaþ alindi");
	}

}
