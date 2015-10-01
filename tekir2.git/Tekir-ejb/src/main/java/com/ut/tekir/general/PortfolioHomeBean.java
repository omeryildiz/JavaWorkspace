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

import com.ut.tekir.entities.Portfolio;
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
@Name("portfolioHome")
@Scope(value=ScopeType.CONVERSATION)
public class PortfolioHomeBean extends EntityHome<Portfolio> implements PortfolioHome<Portfolio>{
    
    @DataModel("portfolioList")
    private List<Portfolio> entityList;
    
    @SuppressWarnings("unchecked")
	@Factory("portfolioList")
    public void initPortfolioList() {
        log.debug("Portfolio Listesi hazırlanıyor...");
        
        setEntityList(getEntityManager().createQuery("select c from Portfolio c")
        //.setMaxResults(100)
        //.setHint("org.hibernate.cacheable", true)
        .getResultList());
    }
    
    
    @Out(required=false)
    public Portfolio getPortfolio() {
        return getEntity();
    }

    @In(required=false)
    public void setPortfolio(Portfolio portfolio) {
        setEntity( portfolio );
    }
    
    
    
    
        
    @Override
    public void createNew() {
        log.debug("Yeni Portföy");
        entity = new Portfolio();
        entity.setActive(true);
    }
    
    public void clearBankAccount() {
		entity.setBankBranch(null);
		entity.setBankAccount(null);
	}

    @Override
    public List<Portfolio> getEntityList() {
        return entityList;
    }

    public void setEntityList(List<Portfolio> entityList) {
        this.entityList = entityList;
    }
    
}
