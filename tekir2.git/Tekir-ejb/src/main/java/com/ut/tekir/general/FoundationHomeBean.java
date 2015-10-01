/*
 * Copyleft 2007-2011 Ozgur Yazilim A.S.
 *
 * Distributable under LGPL license.
 * See terms of license at gnu.org.
 * http://www.gnu.org/licenses/lgpl.html
 *
 * www.tekir.com.tr
 * www.ozguryazilim.com.tr
 *
 */

package com.ut.tekir.general;

import java.util.List;

import javax.ejb.Stateful;


import org.jboss.seam.ScopeType;
import org.jboss.seam.annotations.Factory;
import org.jboss.seam.annotations.In;
import org.jboss.seam.annotations.Name;
import org.jboss.seam.annotations.Out;
import org.jboss.seam.annotations.Scope;
import org.jboss.seam.annotations.datamodel.DataModel;

import com.ut.tekir.entities.Foundation;
import com.ut.tekir.framework.EntityHome;


@Stateful
@Name("foundationHome")
@Scope(value=ScopeType.CONVERSATION)
public class FoundationHomeBean extends EntityHome<Foundation> implements FoundationHome<Foundation>{

	@DataModel("foundationList")
	private List<Foundation> entityList;
	
	@SuppressWarnings("unchecked")
	@Factory("foundationList")
	public void initFoundationList(){
		log.debug("Kurum Listesi Oluşturuluyooore");
		
		entityList = getEntityManager().createQuery("select c from Foundation c")
		.getResultList();
	}
	
	@Out(required=false)
	public Foundation getFoundation(){
		return getEntity();
	}
	
	@In(required=false)
	public void setFoundation(Foundation foundation){
		setEntity(foundation);
	}
	
	@Override
	public void createNew(){
		entity = new Foundation();
	}
	
	@Override
	public List<Foundation> getEntityList(){
		return entityList;
	}
	
	public void setEntityList(List<Foundation> entityList) {
		this.entityList=entityList;
	}
	
}
