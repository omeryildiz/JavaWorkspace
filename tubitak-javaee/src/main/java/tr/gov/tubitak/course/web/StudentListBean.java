package tr.gov.tubitak.course.web;

import java.io.Serializable;
import java.util.List;

import javax.annotation.PostConstruct;
import javax.ejb.Stateful;
import javax.enterprise.context.RequestScoped;
import javax.inject.Named;
import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import javax.persistence.PersistenceContextType;

import tr.gov.tubitak.course.entity.Student;

@Named
@RequestScoped
@Stateful
public class StudentListBean implements Serializable {

	private List<Student> studentList;

	@PersistenceContext(type = PersistenceContextType.EXTENDED, unitName ="primary")
	EntityManager entityManager;

	@PostConstruct
	public void init(){
		this.studentList = entityManager.createQuery("from Student").getResultList();
	}
	
	
	public void findStudent(){
		System.out.println("is open : " + entityManager.isOpen());
		Student student = entityManager.find(Student.class, 11L);
		System.out.println(student.getName());
		System.out.println(student.getFaculty().getName());
	}
	
	public List<Student> getStudentList() {
		return studentList;
	}

	public void setStudentList(List<Student> studentList) {
		this.studentList = studentList;
	}

}
