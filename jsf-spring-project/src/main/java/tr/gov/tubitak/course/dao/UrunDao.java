package tr.gov.tubitak.course.dao;

import java.io.Serializable;

import org.springframework.stereotype.Repository;

import tr.gov.tubitak.course.entity.Urun;
import tr.gov.tubitak.course.util.GenericDAO;

@Repository
public class UrunDao extends GenericDAO<Urun>implements Serializable {

	private static final long serialVersionUID = 6040967577021576269L;

}
