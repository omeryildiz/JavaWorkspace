package tr.gov.tubitak.course.entity;

import java.io.Serializable;

import javax.persistence.Entity;

import org.hibernate.envers.Audited;

import tr.gov.tubitak.course.util.BaseEntity;

@Entity
@Audited
public class City extends BaseEntity implements Serializable {

	private Integer population;	

	public Integer getPopulation() {
		return population;
	}

	public void setPopulation(Integer population) {
		this.population = population;
	}
}
