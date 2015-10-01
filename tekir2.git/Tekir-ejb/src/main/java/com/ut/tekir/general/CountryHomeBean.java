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

import com.ut.tekir.entities.Country;
import com.ut.tekir.framework.EntityHome;

/**
 * Country Editor Bean
 * @author haky
 */
@Stateful

@Name("countryHome")

@Scope(value = ScopeType.CONVERSATION)
public class CountryHomeBean extends EntityHome<Country> implements CountryHome<Country> {

    @DataModel("countryList")
    private List<Country> entityList;

    @SuppressWarnings("unchecked")
	@Factory("countryList")
    public void initCountryList() {
        log.debug("Country Listesi hazırlanıyor...");

        entityList = getEntityManager().createQuery("select c from Country c") //.setMaxResults(100)
                //.setHint("org.hibernate.cacheable", true)
                .getResultList();
    }

    @Out(required = false)
    public Country getCountry() {
        return getEntity();
    }

    @In(required = false)
    public void setCountry(Country country) {
        setEntity(country);
    }

    @Override
    public void createNew() {
        entity = new Country();
        entity.setActive(true);
    }

    @Override
    public List<Country> getEntityList() {
        return entityList;
    }

    public void setEntityList(List<Country> entityList) {
        this.entityList = entityList;
    }
}
