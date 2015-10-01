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

import com.ut.tekir.entities.Bank;
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
 * Bank Home Bean
 * @author haky
 */
@Stateful
@Name("bankHome")
@Scope(value=ScopeType.CONVERSATION)
public class BankHomeBean extends EntityHome<Bank> implements BankHome<Bank> {
    
    @DataModel("bankList")
    private List<Bank> entityList;
    
    @SuppressWarnings("unchecked")
	@Factory("bankList")
    public void initBankList() {
        log.debug("Bank Listesi hazırlanıyor...");
        
        entityList = getEntityManager().createQuery("select c from Bank c")
        //.setMaxResults(100)
        //.setHint("org.hibernate.cacheable", true)
        .getResultList();
    }
    

    @Out(required=false)
    public Bank getBank() {
        return getEntity();
    }

    @In(required=false)
    public void setBank(Bank bank) {
        setEntity( bank );
    }
    
    @Override
    public void createNew(){
        entity = new Bank();
        entity.setActive(true);
    }
    
    @Override
    public List<Bank> getEntityList() {
        return entityList;
    }

    public void setEntityList(List<Bank> entityList) {
        this.entityList = entityList;
    }
   
    
}
