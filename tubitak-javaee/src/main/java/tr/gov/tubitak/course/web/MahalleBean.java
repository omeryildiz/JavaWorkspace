package tr.gov.tubitak.course.web;

import java.io.Serializable;

import javax.faces.bean.SessionScoped;
import javax.inject.Inject;
import javax.inject.Named;

import tr.gov.tubitak.course.entity.Mahalle;
import tr.gov.tubitak.course.service.MahalleService;
import tr.gov.tubitak.course.util.GenericService;

@Named
@SessionScoped
public class MahalleBean extends GenericBean<Mahalle> implements Serializable{

	@Inject MahalleService mahalleService; 
	@Override
	public GenericService<Mahalle> getService() {
		// TODO Auto-generated method stub
		return mahalleService;
	}

}
