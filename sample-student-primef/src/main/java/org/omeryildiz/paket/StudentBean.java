package org.omeryildiz.paket;

import java.util.ArrayList;
import java.util.List;

import javax.faces.bean.ManagedBean;
import javax.faces.bean.SessionScoped;

@ManagedBean
@SessionScoped
public class StudentBean {
	private List<Student> students = new ArrayList<Student>();
	private Student student = new Student();

//	public void init() { @postconstruct annotation'ı çalışmıyor ayrı bir dependency eklemek gerekiyor olmalı
//		Student stud = new Student();
//		stud.setId(1);
//		stud.setName("Ahmet");
//		stud.setSurname("YILDIZ");
//		this.students.add(stud);
//	}
	
	public void select(Student student) {
		this.student = student;
	}

	public void create() {
		this.students.add(getStudent());
		this.student = new Student();
	}

	public void delete(Student student) {
		students.remove(student);
	}

	public void update(Student student) {
		this.student = new Student();
	}

	public List<Student> getStudents() {
		return students;
	}

	public void setStudents(List<Student> students) {
		this.students = students;
	}

	public Student getStudent() {
		return student;
	}

	public void setStudent(Student student) {
		this.student = student;
	}

}
