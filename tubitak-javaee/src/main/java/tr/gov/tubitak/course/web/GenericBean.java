package tr.gov.tubitak.course.web;

import java.io.Serializable;
import java.util.List;

import javax.faces.application.FacesMessage;
import javax.faces.context.FacesContext;

import tr.gov.tubitak.course.util.BaseBean;
import tr.gov.tubitak.course.util.GenericService;

public abstract class GenericBean<T> extends BaseBean<T> implements Serializable{

	public abstract GenericService<T> getService();
	
	private T instance =  getClassObject();
	private List<T> list;
	
	protected T getClassObject(){
		try {
			return getClassInstance().newInstance();
		} catch (InstantiationException | IllegalAccessException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
			return null;
		}
	}
	
	
	public void init() {
		updateList();
	}
	
	protected void updateList() {
		this.list = getService().getList();
	}
	
	public void save(){
		getService().save(instance);
		this.instance = getClassObject();
		updateList();
		FacesContext.getCurrentInstance().addMessage(null, new FacesMessage("Kayit basari ile yapildi..."));
	}
	
	public void remove(T removeObject){
		getService().remove(removeObject);
		updateList();
		FacesContext.getCurrentInstance().addMessage(null, new FacesMessage("Silme basari ile yapildi..."));
	}
	
	public void select(T removeObject){
		this.instance = removeObject;
	}
	
	public void update(){
		getService().update(instance);
		this.instance = getClassObject();
		updateList();
		FacesContext.getCurrentInstance().addMessage(null, new FacesMessage("Düzenleme basri ile yapildi..."));
	}
	
	public T getInstance() {
		return instance;
	}
	
	public void setInstance(T instance) {
		this.instance = instance;
	}
	
	public List<T> getList() {
		return list;
	}
	
	public void setList(List<T> list) {
		this.list = list;
	}

	
}
