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
import com.ut.tekir.entities.BankBranch;
import com.ut.tekir.entities.Warehouse;
import com.ut.tekir.framework.EntityHome;
import com.ut.tekir.framework.OptionManager;
import com.ut.tekir.options.OrganizationSchemeOptionKey;

/**
 * BankBranch Home Bean
 * @author haky
 */
@Stateful
@Name("bankBranchHome")
@Scope(value=ScopeType.CONVERSATION)
public class BankBranchHomeBean extends EntityHome<BankBranch> implements BankBranchHome<BankBranch> {
    
	@In
	OptionManager optionManager;
	
    @DataModel("bankBranchList")
    private List<BankBranch> entityList;
    
    @SuppressWarnings("unchecked")
	@Factory("bankBranchList")
    public void initBankBranchList() {
        log.debug("BankBranch Listesi hazırlanıyor...");
        
        entityList = getEntityManager().createQuery("select c from BankBranch c")
        //.setMaxResults(100)
        //.setHint("org.hibernate.cacheable", true)
        .getResultList();
    }
    
    @Out(required=false)
    public BankBranch getBankBranch() {
        return getEntity();
    }

    @In(required=false)
    public void setBankBranch(BankBranch bankBranch) {
        setEntity( bankBranch );
    }
    
    @Override
    public void createNew(){
        entity = new BankBranch();
        entity.setActive(true);
    }
    
    @Override
    public List<BankBranch> getEntityList() {
        return entityList;
    }

    public void setEntityList(List<BankBranch> entityList) {
        this.entityList = entityList;
    }
   
	public boolean getOrganizationSchemeOption() {
		return optionManager.getOption(OrganizationSchemeOptionKey.USE_SCHEME, true).getAsBoolean();
	}
    
}
