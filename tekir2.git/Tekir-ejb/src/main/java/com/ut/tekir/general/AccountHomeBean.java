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

import com.ut.tekir.entities.Account;
import com.ut.tekir.framework.EntityHome;
import com.ut.tekir.framework.OptionManager;
import com.ut.tekir.options.OrganizationSchemeOptionKey;

/**
 *
 * @author haky
 */
@Stateful
@Name("accountHome")
@Scope(value=ScopeType.CONVERSATION)
public class AccountHomeBean extends EntityHome<Account> implements AccountHome<Account>{
    
	@In
	OptionManager optionManager;
	
    @DataModel("accountList")
    private List<Account> entityList;
    
    @SuppressWarnings("unchecked")
	@Factory("accountList")
    public void initAccountList() {
        log.debug("Account Listesi hazırlanıyor...");
        
        setEntityList(getEntityManager().createQuery("select c from Account c")
        //.setMaxResults(100)
        //.setHint("org.hibernate.cacheable", true)
        .getResultList());
    }
    
    @Out(required=false)
    public Account getAccount() {
        return getEntity();
    }

    @In(required=false)
    public void setAccount(Account account) {
        setEntity( account );
    }
        
    @Override
    public void createNew() {
        log.debug("Yeni Şehir");
        entity = new Account();
        entity.setActive(true);
    }

    public static void main(String[] args) {
    	try {
//    		OKMAuthService service2 = new OKMAuthService();
//    		OKMAuth port2 = service2.getOKMAuthPort();
//    		String res = port2.login("zinan", "zinan");

    		
    	} catch (Exception e) {
    		e.printStackTrace();
		}
	}
    
    @Override
    public List<Account> getEntityList() {
        return entityList;
    }

    public void setEntityList(List<Account> entityList) {
        this.entityList = entityList;
    }

	public boolean getOrganizationSchemeOption() {
		return optionManager.getOption(OrganizationSchemeOptionKey.USE_SCHEME, true).getAsBoolean();
	}

}
