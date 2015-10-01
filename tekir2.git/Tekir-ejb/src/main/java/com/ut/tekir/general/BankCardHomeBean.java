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

import com.ut.tekir.entities.BankCard;
import com.ut.tekir.framework.EntityHome;

/**
 * Banka Kartı tanımlamalarının yapıldığı home sınıfı..
 * @author sinan.yumak
 */
@Stateful
@Name("bankCardHome")
@Scope(value=ScopeType.CONVERSATION)
public class BankCardHomeBean extends EntityHome<BankCard> implements BankCardHome<BankCard> {
    
    @DataModel("bankCardList")
    private List<BankCard> entityList;
    
    @SuppressWarnings("unchecked")
	@Factory("bankCardList")
    public void initBankCardList() {
        log.debug("Preparing Bank Card List...");
        
        setEntityList(getEntityManager().createQuery("select bc from BankCard bc " +
        											 "order by bc.bank.name, bc.code")
        			 .getResultList());
    }
    
    @Out(required=false)
    public BankCard getBankCard() {
        return getEntity();
    }

    @In(required=false)
    public void setBankCard(BankCard bankCard) {
        setEntity( bankCard );
    }

    @Override
    public void createNew() {
        log.debug("New BankCard");
        entity = new BankCard();
        entity.setActive(true);
    }

    @Override
    public List<BankCard> getEntityList() {
        return entityList;
    }

    public void setEntityList(List<BankCard> entityList) {
        this.entityList = entityList;
    }

}