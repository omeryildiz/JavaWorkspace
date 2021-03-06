package tr.gov.tubitak.demo;

import java.util.LinkedHashMap;
import java.util.Locale;
import java.util.Map;

import tr.gov.tubitak.demo.Student.Builder;
import tr.gov.tubitak.sekizeylul.CollectionFrameworkSamples;

public class Run {

	public static void main(String[] args) {
//		Student st = new Student();
//		st.setName("�mer YILDIZ");
//		st.setAge(27);
//		st.doHomework(3);
//		System.out.println(st.isAdult());
//		if(!st.isAdult())
//		{
//			st.doHomework();
//		}
//		StudentPrinter sp = new StudentPrinter();
//		sp.print(st);
//		System.out.println("Hesap Makinesi");
//		for (int i = 0; i < 10; i++) {
//			System.out.print("-");	
//		}
//		System.out.println("");
//		Calculate c = new Calculate();
//		c.Sum(4+5+6+7);
//		System.out.println(c.mult(3, 4, 2));
//		
//		for (int i = 0; i < 10; i++) {
//			System.out.print("-");	
//		}
//		System.out.println("");
		CollectionFrameworkSamples.cizgiCek();
		Student st = new Student();
		st.setId(12);
		st.setName("�mer yildiz");
		st.setAge(27);
		
		Student newSt = new Student
				.Builder()
				.name("�mer YILDIZ")
				.age(27)
				.id(1)
				.Build();
		
		
		Map<Integer,Student> student = new LinkedHashMap<Integer,Student>();
		student.put(st.getId(), st);
		
		for (Map.Entry<Integer, Student> entry : student.entrySet()) {
			System.out.println(entry.getValue().getId()+""+entry.getValue().getName());
		}
		
		CollectionFrameworkSamples.cizgiCek();
		String text = "�akir a�lamasayd� �ok g�lerdi.".toUpperCase(new Locale("tr", "TR"));
		System.out.println(text);
		System.out.println(text.substring(1, 5));
		
		Student omer = new Student("�mer YILDIZ",25);
		Student yunus = new Student("Yunus Se�kin");
		
		School school = new School("Ankara",5);
		System.out.println(school.classrooms.get(3).id);

	}

}
