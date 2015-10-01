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

import com.ut.tekir.contact.ContactModel;
import com.ut.tekir.entities.AccountOwnerType;
import com.ut.tekir.entities.Bank;
import com.ut.tekir.entities.BankAccount;
import com.ut.tekir.entities.BankBranch;
import com.ut.tekir.framework.BaseConsts;
import com.ut.tekir.framework.EntityHome;

import java.util.Calendar;
import java.util.Date;
import java.util.List;
import javax.ejb.Stateful;
import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import javax.persistence.PersistenceContextType;

import org.jboss.seam.ScopeType;
import org.jboss.seam.annotations.Factory;
import org.jboss.seam.annotations.In;
import org.jboss.seam.annotations.Name;
import org.jboss.seam.annotations.Out;
import org.jboss.seam.annotations.Scope;
import org.jboss.seam.annotations.datamodel.DataModel;

/**
 * BankAccount Home Bean
 * @author haky
 */
@Stateful
@Name("bankAccountHome")
@Scope(value=ScopeType.CONVERSATION)
public class BankAccountHomeBean extends EntityHome<BankAccount> implements BankAccountHome<BankAccount> {
    
    @PersistenceContext(type = PersistenceContextType.EXTENDED)
    private EntityManager em;
    
    @DataModel("bankAccountList")
    private List<BankAccount> entityList;
    
    @SuppressWarnings("unchecked")
	@Factory("bankAccountList")
    public void initBankAccountList() {
        log.debug("BankAccount Listesi hazırlanıyor...");
        
        entityList = getEntityManager().createQuery("select c from BankAccount c order by weight, accountNo ") 
        //		+ " where accountOwnerType = :ownerType")
        //.setMaxResults(100)
        //.setHint("org.hibernate.cacheable", true)
        //.setParameter("ownerType", AccountOwnerType.Mine)
        .getResultList();
    }
    
    @Out(required=false)
    public BankAccount getBankAccount() {
        return getEntity();
    }

    @In(required=false)
    public void setBankAccount(BankAccount bankAccount) {
        setEntity( bankAccount );
    }
    
    @Override
    public void createNew(){
        entity = new BankAccount();
        entity.setActive(true);
        entity.setCurrency(BaseConsts.SYSTEM_CURRENCY_CODE);
        entity.setAccountOwnerType(AccountOwnerType.Mine);
    }
    
    @Override
    public List<BankAccount> getEntityList() {
        return entityList;
    }

    public void setEntityList(List<BankAccount> entityList) {
        this.entityList = entityList;
    }
   
    public void calculateWorkEndDate() {
    	int term = entity.getTerm();
    	
    	Calendar cal = Calendar.getInstance();

    	cal.setTime(entity.getOpenDate());
    	
    	cal.add(Calendar.DAY_OF_YEAR, term);
    	
    	entity.setCloseDate(cal.getTime());
    	
    }
    
    public ContactModel getAccountOwner(BankAccount bankAccount){
    	ContactModel model = new ContactModel();
    	log.info("AccountOwner ne geldi:  #0", bankAccount);
    	
    	if (bankAccount != null) {
    		log.info("AccountOwner dolu geldi:  #0", bankAccount.getId());
	    	//try {
	    		//select cat from Cat cat,Owner owner where cat.OwnerId = owner.Id and owner.Name='Duke'
	    			
				Object[] result = (Object[])em.createQuery("select a.id, a.fullname, a.company from Contact a,ContactBankAccount b " +
														"where a.id = b.owner" +
														"  and b.BankAccount.id = :accountId")
								.setParameter("accountId", bankAccount.getId())
								//.setMaxResults(1)
								.getSingleResult();

				model.setId((Long)result[0]);
				model.setFullname((String)result[1]);
				model.setCompany((String)result[2]);
				log.info("AccountOwner sonuc ne geldi:  #0", result);
			//} catch (Exception e) {
			//	log.info("AccountOwner hata ne geldi:  #0", e);
			//} 
	    		
	    	return model; 
    	} else { 
    		log.info("AccountOwner bos geldi:  #0", model);
    		return null; 
		} 
    }
    
}
