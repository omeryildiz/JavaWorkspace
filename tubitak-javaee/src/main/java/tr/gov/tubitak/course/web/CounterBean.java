package tr.gov.tubitak.course.web;

import java.io.Serializable;

import javax.enterprise.context.SessionScoped;
import javax.inject.Inject;
import javax.inject.Named;

import tr.gov.tubitak.course.service.CounterService;

@Named
@SessionScoped
public class CounterBean implements Serializable{

	private Integer count;
	@Inject
	CounterService service;

	public void next() {
		this.count = service.nextCount();
	}
	
	public Integer getCount() {
		return count;
	}

	public void setCount(Integer count) {
		this.count = count;
	}
	
}
