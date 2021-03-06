package tr.gov.tubitak.demo;

public class Student {
	private int id;
	private String name;
	private int age;
	public School school;
	
	public Student() {
		System.out.println("�lk constructor �al��t�");
	}
	
	public Student(String name, int age)
	{
		
		this(name);
		//this.name = name; zaten tan�mland��� i�in this(name) kullanabiliriz. Bu �ekilde artan bir �ekilde constructor kullan�labilir.
		System.out.println("�kinci constructor �al��t�");
		this.age = age;
	}
	public Student(String name)
	{
		System.out.println("���nc� constructor �al��t�");
		this.name = name;
	}

	public Student(Builder builder) {
		this.id = builder.id;
		this.age = builder.age;
		this.name = builder.name;
	}

	public void setName(String stringName) {
		name = stringName;
	}

	public String getName() {
		return name;
	}

	public void setAge(int intAge) {
		age = intAge;
	}

	public int getAge() {
		return age;
	}

	public void doHomework() {
		System.out.println(name + " �devini yap�yor.");
	}

	public void doHomework(int counter) {
		for (int i = 0; i < counter; i++) {
			System.out.println(name + " �devini yap�yor.");
		}

	}

	public Boolean isAdult() {
		return age >= 18 ? true : false;
	}

	public void Sum(int... candidate) {
		int result = 0;
		for (int i = 0; i < candidate.length; i++)
			result = result + candidate[i];
		System.out.println(result);
	}

	public int mult(int... candidate) {
		int result = 1;
		for (int i = 0; i < candidate.length; i++)
			result = result * candidate[i];

		return result;
	}

	public int getId() {
		return id;
	}

	public void setId(int id) {
		this.id = id;
	}

	public static class Builder {
		private int id;
		private String name;
		private int age;
		
		public Builder id(int intNumber){this.id = intNumber; return this;};
		public Builder name(String stringName){this.name = stringName; return this;};
		public Builder age(int intAge){this.age = intAge; return this;};
		public Student Build(){
			return new Student(this);
		}
		
		
	}

	// Bu �zellik istenildi�i kadar parametreyi toplamay� sa�lar
	// public int sum(int... candidates)
	// {
	// int result = 0;
	// for (int i : candidates) {
	// result = result + candidates[i];
	// }
	// return result;
	// }

}
