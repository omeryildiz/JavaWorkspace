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

import com.ut.tekir.entities.Province;
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
 * Province Home Bean
 * @author Nebioglu
 */
@Stateful
@Name("provinceHome")
@Scope(value=ScopeType.CONVERSATION)
public class ProvinceHomeBean extends EntityHome<Province> implements ProvinceHome<Province> {
    
    @DataModel("provinceList")
    private List<Province> entityList;
    
    @SuppressWarnings("unchecked")
	@Factory("provinceList")
    public void initProvinceList() {
        log.debug("province Listesi hazırlanıyor...");
        
        entityList = getEntityManager().createQuery("select c from Province c order by city_id,weight")
        //.setMaxResults(100)
        //.setHint("org.hibernate.cacheable", true)
        .getResultList();
    }
    
    
    @Out(required=false)
    public Province getProvince() {
        return getEntity();
    }

    @In(required=false)
    public void setProvince(Province province) {
        setEntity( province );
    }
    
    
    @Override
    public void createNew() {
        log.debug("Yeni Kasaba");
        entity = new Province();
        entity.setActive(true);
    }

    @Override
    public List<Province> getEntityList() {
        return entityList;
    }

    public void setEntityList(List<Province> entityList) {
        this.entityList = entityList;
    }

}
