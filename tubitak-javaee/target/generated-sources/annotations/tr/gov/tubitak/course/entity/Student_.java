package tr.gov.tubitak.course.entity;

import javax.annotation.Generated;
import javax.persistence.metamodel.ListAttribute;
import javax.persistence.metamodel.SingularAttribute;
import javax.persistence.metamodel.StaticMetamodel;

@Generated(value = "org.hibernate.jpamodelgen.JPAMetaModelEntityProcessor")
@StaticMetamodel(Student.class)
public abstract class Student_ {

	public static volatile SingularAttribute<Student, Gender> gender;
	public static volatile ListAttribute<Student, String> hobbies;
	public static volatile SingularAttribute<Student, String> name;
	public static volatile SingularAttribute<Student, Address> studentAddress;
	public static volatile SingularAttribute<Student, Long> id;
	public static volatile SingularAttribute<Student, Integer> version;
	public static volatile SingularAttribute<Student, Integer> age;
	public static volatile SingularAttribute<Student, String> lastname;
	public static volatile SingularAttribute<Student, Faculty> faculty;
	public static volatile ListAttribute<Student, Lesson> lessons;

}

