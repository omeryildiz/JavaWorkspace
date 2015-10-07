package tr.gov.tubitak.course.entity;

import java.io.Serializable;
import java.util.List;

import javax.persistence.CascadeType;
import javax.persistence.CollectionTable;
import javax.persistence.Column;
import javax.persistence.ElementCollection;
import javax.persistence.Entity;
import javax.persistence.EnumType;
import javax.persistence.Enumerated;
import javax.persistence.FetchType;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.ManyToMany;
import javax.persistence.ManyToOne;
import javax.persistence.OneToOne;
import javax.persistence.Transient;
import javax.persistence.Version;

@Entity
public class Student implements Serializable {

//	@TableGenerator(name = "table_id_generator", 
//			table = "id_table", 
//			pkColumnName = "id", 
//			valueColumnName = "value", 
//			allocationSize = 1, 
//			pkColumnValue = "Student")
//	@GenericGenerator(name="system-uuid",  strategy = "uuid")
//	@GeneratedValue(strategy = GenerationType.TABLE, generator = "table_id_generator")
	
	@Id
	@GeneratedValue
	private Long id;
	@Column(length = 50, nullable = false, updatable = false)
	private String name;
	private String lastname;
	private Integer age;

	@Enumerated(EnumType.STRING)
	private Gender gender;

	@Version
	private Integer version;
	
	@OneToOne(cascade = {CascadeType.ALL}, fetch = FetchType.LAZY)
	private Address studentAddress = new Address();
	
	@ManyToOne(fetch = FetchType.LAZY)
	private Faculty faculty;
	
	@ManyToMany(fetch = FetchType.LAZY)
	private List<Lesson> lessons;
	
	@ElementCollection
	@CollectionTable(name = "student_hobbies")
	private List<String> hobbies;
	
	
	@Transient
	private Boolean check;
	

	public Long getId() {
		return id;
	}

	public void setId(Long id) {
		this.id = id;
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public String getLastname() {
		return lastname;
	}

	public void setLastname(String lastname) {
		this.lastname = lastname;
	}

	public Integer getAge() {
		return age;
	}

	public void setAge(Integer age) {
		this.age = age;
	}

	public Integer getVersion() {
		return version;
	}

	public void setVersion(Integer version) {
		this.version = version;
	}

	public Gender getGender() {
		return gender;
	}

	public void setGender(Gender gender) {
		this.gender = gender;
	}

	public Address getStudentAddress() {
		return studentAddress;
	}

	public void setStudentAddress(Address studentAddress) {
		this.studentAddress = studentAddress;
	}

	public Faculty getFaculty() {
		return faculty;
	}

	public void setFaculty(Faculty faculty) {
		this.faculty = faculty;
	}

	public List<Lesson> getLessons() {
		return lessons;
	}

	public void setLessons(List<Lesson> lessons) {
		this.lessons = lessons;
	}

	public List<String> getHobbies() {
		return hobbies;
	}

	public void setHobbies(List<String> hobbies) {
		this.hobbies = hobbies;
	}

	public Boolean getCheck() {
		return check;
	}

	public void setCheck(Boolean check) {
		this.check = check;
	}

}
