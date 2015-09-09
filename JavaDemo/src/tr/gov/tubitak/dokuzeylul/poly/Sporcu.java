package tr.gov.tubitak.dokuzeylul.poly;


public class Sporcu {
	
	private String name;
	private String surname;
	private int age;
	
	public Sporcu() {
		
	}
	

	public Sporcu(Builder builder) {
		this.name = builder.name;
		this.surname = builder.surname;
		this.age = builder.age;
	}

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
	
	public void isinmaHareketleriYap() {
		System.out.println(name+ " " + surname + "isiniyor");
	}
	
	public int getAge() {
		return age;
	}

	public void setAge(int age) {
		this.age = age;
	}

	public static class Builder {
		private String name;
		private String surname;
		private int age;
		
		public Builder name(String stringName){this.name = stringName; return this;};
		public Builder age(int intAge){this.age = intAge; return this;};
		public Sporcu Build(){
			return new Sporcu(this);
		}
		
		
	}
	

}
