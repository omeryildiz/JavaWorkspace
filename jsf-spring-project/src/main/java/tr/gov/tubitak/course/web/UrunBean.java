package tr.gov.tubitak.course.web;

import java.io.Serializable;
import java.util.List;

import javax.annotation.PostConstruct;
import javax.inject.Named;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Scope;

import tr.gov.tubitak.course.entity.Kategori;
import tr.gov.tubitak.course.entity.Ozellikler;
import tr.gov.tubitak.course.entity.Urun;
import tr.gov.tubitak.course.service.UrunService;
import tr.gov.tubitak.course.util.GenericBean;
import tr.gov.tubitak.course.util.GenericService;

@Scope("session")
@Named
public class UrunBean extends GenericBean<Urun>implements Serializable {

	private static final long serialVersionUID = -3235310532799838747L;

	@Autowired
	UrunService service;

	private List<Kategori> kategoriList;
	private List<Ozellikler> ozelliklerList;

	@Override
	public GenericService<Urun> getService() {
		return service;
	}

	public List<Kategori> getKategoriList() {
		return kategoriList;
	}

	public void setKategoriList(List<Kategori> kategoriList) {
		this.kategoriList = kategoriList;
	}

	public List<Ozellikler> getOzelliklerList() {
		return ozelliklerList;
	}

	public void setOzelliklerList(List<Ozellikler> ozelliklerList) {
		this.ozelliklerList = ozelliklerList;
	}
	
	@PostConstruct
	@Override
	public void init(){
		super.init();
	}

}
