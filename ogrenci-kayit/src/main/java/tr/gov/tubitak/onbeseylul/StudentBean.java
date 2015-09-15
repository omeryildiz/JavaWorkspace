package tr.gov.tubitak.onbeseylul;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

import javax.annotation.PostConstruct;
import javax.faces.bean.ManagedBean;
import javax.faces.bean.SessionScoped;

@ManagedBean
@SessionScoped
public class StudentBean implements Serializable{
	
	private List<Student> students = new ArrayList<Student>();
	private Student student = new Student();
	private List<String> cities = new ArrayList<>();
	
	@PostConstruct
	public void init() {
		cities.add("İstanbul");
		cities.add("Ankara");
		cities.add("Edirne");
		cities.add("Şanlıurfa");
	}
	
	public void saveStudent() {
		students.add(getStudent());
		this.setStudent(new Student());
	}
	
	public void removeStudent(Student student) 
	{
		this.students.remove(student);
	}
	
	public void selectStudent(Student student) 
	{
		this.student = student;
	}
	
	public void updateStudent() {
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

	public List<String> getCities() {
		return cities;
	}

	public void setCities(List<String> cities) {
		this.cities = cities;
	}
	

}
