package tr.gov.tubitak.course.service;

import java.io.Serializable;

import javax.annotation.PostConstruct;
import javax.annotation.PreDestroy;
import javax.ejb.Stateless;

@Stateless
public class CounterService implements Serializable {

	private Integer count = 0;

	@PostConstruct
	public void init() {
		System.out.println("Counter service olusturuldu");
	}

	@PreDestroy
	public void destroy() {
		System.out.println("Counter service yok edildi");
	}

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
