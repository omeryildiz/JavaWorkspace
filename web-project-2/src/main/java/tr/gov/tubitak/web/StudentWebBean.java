package tr.gov.tubitak.web;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

import javax.faces.bean.ManagedBean;
import javax.faces.bean.SessionScoped;

import tr.gov.tubitak.entity.Student;

@ManagedBean
@SessionScoped
public class StudentWebBean implements Serializable{

	private Student student = new Student();
	private List<Student> studentList = new ArrayList<Student>();
	
	public void save(){
		studentList.add(student);
		student = new Student();
	}

	public Student getStudent() {
		return student;
	}

	public void setStudent(Student student) {
		this.student = student;
	}

	public List<Student> getStudentList() {
		return studentList;
	}

	public void setStudentList(List<Student> studentList) {
		this.studentList = studentList;
	}
	
}
