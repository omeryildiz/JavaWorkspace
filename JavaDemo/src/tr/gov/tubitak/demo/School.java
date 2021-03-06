package tr.gov.tubitak.demo;

import java.util.ArrayList;
import java.util.List;

public class School {
	private String name;
	public List<Classroom> classrooms;

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public School() {
		System.out.println("Okul nesnesi oluşturuldu.");

	}

	public School(String schoolName, int classRoomCount) {
		this();
		name = schoolName;
		classrooms = new ArrayList<>();
		for (int i = 0; i < classRoomCount; i++) {

			classrooms.add(new Classroom(i, this));
			
		}
		System.out.println(name+"adında nesne oluşturuldu");
	}
	
	public class Classroom{
		public int id;
		public School school;
		
		public Classroom(int id, School school){
			this.id = id;
			this.school = school;
		}
	}

}
