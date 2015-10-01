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

import com.ut.tekir.entities.Unit;
import com.ut.tekir.framework.EntityHome;
import java.util.List;
import javax.ejb.Stateful;
import org.jboss.seam.ScopeType;
import org.jboss.seam.annotations.Factory;
import org.jboss.seam.annotations.In;
import org.jboss.seam.annotations.Name;
import org.jboss.seam.annotations.Out;
import org.jboss.seam.annotations.Scope;
import org.jboss.seam.annotations.datamodel.DataModel;

/**
 *
 * @author haky
 */
@Stateful
@Name("unitHome")
@Scope(value=ScopeType.CONVERSATION)
public class UnitHomeBean extends EntityHome<Unit> implements UnitHome<Unit> {
    
    @DataModel("unitList")
    private List<Unit> entityList;
    
    @SuppressWarnings("unchecked")
	@Factory("unitList")
    public void initUnitList() {
        log.debug("Unit Listesi hazırlanıyor...");
        
        setEntityList(getEntityManager().createQuery("select c from Unit c")
        //.setMaxResults(100)
        //.setHint("org.hibernate.cacheable", true)
        .getResultList());
    }
    
    
    @Out(required=false)
    public Unit getUnit() {
        return getEntity();
    }

    @In(required=false)
    public void setUnit(Unit unit) {
        setEntity( unit );
    }
    
    
    
    
        
    @Override
    public void createNew() {
        log.debug("Yeni Birim");
        entity = new Unit();
        entity.setActive(true);
    }

    @Override
    public List<Unit> getEntityList() {
        return entityList;
    }

    public void setEntityList(List<Unit> entityList) {
        this.entityList = entityList;
    }
    
}
