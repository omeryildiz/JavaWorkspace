package tr.gov.tubitak.course.util;

import java.io.Serializable;
import java.util.Date;

import javax.persistence.PrePersist;
import javax.persistence.PreUpdate;

public class TubitakEntityListener implements Serializable{

	@PrePersist
	public void beforeSave(BaseEntity baseEntity){
		baseEntity.setCreatedUser("Melih");
		baseEntity.setCreatedDate(new Date());
	}
	
	@PreUpdate
	public void beforeUpdate(BaseEntity baseEntity){
		baseEntity.setUpdateUser("Ahmet");
		baseEntity.setUpdateDate(new Date());
	}
}
