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

import com.ut.tekir.entities.Address;
import com.ut.tekir.entities.Warehouse;
import com.ut.tekir.framework.BaseConsts;
import com.ut.tekir.framework.EntityHome;
import com.ut.tekir.framework.OptionManager;
import com.ut.tekir.options.OrganizationSchemeOptionKey;

/**
 * Warehouse Home Bean
 * @author haky
 */
@Stateful
@Name("warehouseHome")
@Scope(value=ScopeType.CONVERSATION)
public class WarehouseHomeBean extends EntityHome<Warehouse> implements WarehouseHome<Warehouse> {

	@In
	OptionManager optionManager;
	
    @DataModel("warehouseList")
    private List<Warehouse> entityList;
    
    @SuppressWarnings("unchecked")
	@Factory("warehouseList")
    public void initWarehouseList() {
        log.debug("Warehouse Listesi hazırlanıyor...");
        
        entityList = getEntityManager().createQuery("select c from Warehouse c")
        //.setMaxResults(100)
        //.setHint("org.hibernate.cacheable", true)
        .getResultList();
    }
    
    @Out(required=false)
    public Warehouse getWarehouse() {
    	if(getEntity().getAddress() == null){
    		getEntity().setAddress(new Address());
    	}
        return getEntity();
    }

    @In(required=false)
    public void setWarehouse(Warehouse warehouse) {
        setEntity( warehouse );
    }
    
    @Override
    public void createNew(){
        entity = new Warehouse();
        entity.setActive(true);
    }
    
    @Override
    public List<Warehouse> getEntityList() {
        return entityList;
    }

    public void setEntityList(List<Warehouse> entityList) {
        this.entityList = entityList;
    }
   
	@Override
	public String save(){
		try {
			entity.getAddress().setCity(entity.getCity().getName());
			if (entity.getProvince() != null) {
				entity.getAddress().setProvince(entity.getProvince().getName());
			}
		} catch (Exception e) {
			log.error("Hata :", e);
            return BaseConsts.FAIL;
		}
		String res = super.save();
		return res;
	}

	public boolean getOrganizationSchemeOption() {
		return optionManager.getOption(OrganizationSchemeOptionKey.USE_SCHEME, true).getAsBoolean();
	}

}
    

