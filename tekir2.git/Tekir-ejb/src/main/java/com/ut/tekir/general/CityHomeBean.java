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

import com.ut.tekir.entities.City;
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
 * City Home Bean
 * @author haky
 */
@Stateful
@Name("cityHome")
@Scope(value=ScopeType.CONVERSATION)
public class CityHomeBean extends EntityHome<City> implements CityHome<City> {
    
    @DataModel("cityList")
    private List<City> entityList;
    
    @SuppressWarnings("unchecked")
	@Factory("cityList")
    public void initCityList() {
        log.debug("city Listesi hazırlanıyor...");
        
        entityList = getEntityManager().createQuery("select c from City c order by weight")
        //.setMaxResults(100)
        //.setHint("org.hibernate.cacheable", true)
        .getResultList();
    }
    
    
    @Out(required=false)
    public City getCity() {
        return getEntity();
    }

    @In(required=false)
    public void setCity(City city) {
        setEntity( city );
    }
    
    
    @Override
    public void createNew() {
        log.debug("Yeni Şehir");
        entity = new City();
        entity.setActive(true);
    }

    @Override
    public List<City> getEntityList() {
        return entityList;
    }

    public void setEntityList(List<City> entityList) {
        this.entityList = entityList;
    }

    
    
}
