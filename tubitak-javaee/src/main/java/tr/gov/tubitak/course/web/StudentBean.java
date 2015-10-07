package tr.gov.tubitak.course.web;

import java.io.Serializable;
import java.util.List;

import javax.annotation.PostConstruct;
import javax.ejb.Stateful;
import javax.ejb.TransactionAttribute;
import javax.ejb.TransactionAttributeType;
import javax.enterprise.context.SessionScoped;
import javax.inject.Named;
import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import javax.persistence.PersistenceContextType;

import tr.gov.tubitak.course.entity.Faculty;
import tr.gov.tubitak.course.entity.Lesson;
import tr.gov.tubitak.course.entity.Student;

@Named
@SessionScoped
@Stateful
public class StudentBean implements Serializable{
 
	private Student student = new Student(); 
	
	@PersistenceContext(type = PersistenceContextType.EXTENDED, unitName="primary")
	EntityManager entityManager;
	private List<Student> studentList;
	private List<Faculty> facultyList;
	private List<Lesson> lessonList;
	
//	@Inject
//	UserTransaction utx;
	
	@PostConstruct
	public void init(){
//		entityManager.joinTransaction();
		loadStudentList();
		this.facultyList = entityManager.createQuery("from Faculty").getResultList();
		this.lessonList = entityManager.createQuery("from Lesson").getResultList();
	}


	private void loadStudentList() {
		this.studentList = entityManager.createQuery("from Student").getResultList();
	}
	

	@TransactionAttribute(TransactionAttributeType.REQUIRED)
	public void save(){
//		student.setGender(Gender.Men);
		
//		entityManager.persist(student.getStudentAddress());
		
		entityManager.persist(student);
		loadStudentList();
		
		this.student = new Student();
	}
	
	public void remove(Student selectedStudent) throws Exception{
		selectedStudent = entityManager.merge(selectedStudent);
		entityManager.remove(selectedStudent);
		loadStudentList();
	}
	
	public void update() throws Exception{
		entityManager.merge(this.student);
		this.student = new Student();
		loadStudentList();
	}
	
	public void select(Student selectedStudent){
		this.student = selectedStudent;
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


	public List<Faculty> getFacultyList() {
		return facultyList;
	}


	public void setFacultyList(List<Faculty> facultyList) {
		this.facultyList = facultyList;
	}


	public List<Lesson> getLessonList() {
		return lessonList;
	}


	public void setLessonList(List<Lesson> lessonList) {
		this.lessonList = lessonList;
	}


}
