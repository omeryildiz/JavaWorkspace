package tr.gov.tubitak.course.service;

import java.io.Serializable;

import javax.ejb.Stateful;

@Stateful
public class CounterService implements Serializable{
	
	private Integer count = 0;

	public Integer getCount() {
		return count;
	}

	public void setCount(Integer count) {
		this.count = count;
	}

	public Integer nextCount() {
		count++;
		return count;
	}

}
