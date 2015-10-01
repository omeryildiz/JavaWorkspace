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

package com.ut.tekir.finance;

import java.util.Calendar;
import java.util.HashMap;
import java.util.Map;

import javax.ejb.Stateful;

import org.hibernate.criterion.DetachedCriteria;
import org.hibernate.criterion.MatchMode;
import org.hibernate.criterion.Order;
import org.hibernate.criterion.Projections;
import org.hibernate.criterion.Restrictions;
import org.hibernate.transform.Transformers;
import org.jboss.seam.ScopeType;
import org.jboss.seam.annotations.In;
import org.jboss.seam.annotations.Name;
import org.jboss.seam.annotations.Observer;
import org.jboss.seam.annotations.Scope;

import com.ut.tekir.entities.BankToContactTransfer;
import com.ut.tekir.entities.BankTransferType;
import com.ut.tekir.entities.FinanceAction;
import com.ut.tekir.framework.BrowserBase;
import com.ut.tekir.framework.CalendarManager;

/**
 *
 * @author selman
 */
@Stateful
@Name("contactToBankTransferBrowse")
@Scope(ScopeType.SESSION)
public class ContactToBankTransferBrowseBean extends BrowserBase<BankToContactTransfer, ContactToBankTransferFilterModel> implements ContactToBankTransferBrowse<BankToContactTransfer, ContactToBankTransferFilterModel> {

    @In
    CalendarManager calendarManager;

    @Override
    public ContactToBankTransferFilterModel newFilterModel() {
    	ContactToBankTransferFilterModel fm = new ContactToBankTransferFilterModel();
        fm.setBeginDate(calendarManager.getLastTenDay());
        fm.setEndDate(calendarManager.getCurrentDate());
        return fm;
    }

    @Override
    public DetachedCriteria buildCriteria() {

        DetachedCriteria crit = DetachedCriteria.forClass(BankToContactTransfer.class);
        
        crit.createAlias("contact", "contact");
        
        crit.setProjection(Projections.projectionList()
        		.add(Projections.property("id"), "id")
        		.add(Projections.property("serial"), "serial")
        		.add(Projections.property("reference"), "reference")
        		.add(Projections.property("code"), "code")
        		.add(Projections.property("bankAccount"), "bankAccount")
        		.add(Projections.property("bankBranch"), "bankBranch")
        		.add(Projections.property("bank"), "bank")
        		.add(Projections.property("contact.fullname"), "contactName")
        		.add(Projections.property("contact.code"), "contactCode")
        		.add(Projections.property("contact.company"), "company")
        		.add(Projections.property("contact.person"), "person")
        		.add(Projections.property("date"), "date")
        		.add(Projections.property("info"), "info")
        		.add(Projections.property("this.amount.value"), "amountValue")
        		.add(Projections.property("this.amount.currency"), "amountCurrency")
        		.add(Projections.property("this.cost.value"), "costValue")
        		.add(Projections.property("this.cost.currency"), "costCurrency")
        ).setResultTransformer(Transformers.aliasToBean(ContactToBankTransferFilterModel.class));

        if (filterModel.getWorkBunch() != null){
            crit.add(Restrictions.eq("this.workBunch", filterModel.getWorkBunch()));
        }

        if (isNotEmpty(filterModel.getSerial())) {
            crit.add(Restrictions.ilike("this.serial", filterModel.getSerial(),MatchMode.START));
        }

        if (isNotEmpty(filterModel.getReference())) {
            crit.add(Restrictions.ilike("this.reference", filterModel.getReference(),MatchMode.START));
        }

        if (isNotEmpty(filterModel.getCode())) {
            crit.add(Restrictions.ilike("this.code", filterModel.getCode(),MatchMode.START));
        }

        if (filterModel.getBeginDate() != null) {
            crit.add(Restrictions.ge("this.date", filterModel.getBeginDate()));
        }

        if (filterModel.getEndDate() != null) {
            crit.add(Restrictions.le("this.date", filterModel.getEndDate()));
        }

        if (isNotEmpty(filterModel.getContactName())) {
            crit.add(Restrictions.ilike("contact.fullname", filterModel.getContactName(),MatchMode.START ));
        }

        if (isNotEmpty(filterModel.getContactCode())) {
            crit.add(Restrictions.ilike("contact.code", filterModel.getContactCode(),MatchMode.START ));
        }

	    if (filterModel.getBank() != null) {
	        crit.add(Restrictions.eq("this.bank", filterModel.getBank()));
	    }
	    
	    if (filterModel.getBankBranch() != null) {
	        crit.add(Restrictions.eq("this.bankBranch", filterModel.getBankBranch()));
	    }
         
        if (filterModel.getBankAccount() != null) {
            crit.add(Restrictions.eq("this.bankAccount", filterModel.getBankAccount()));
        }
        if (filterModel.getTransferType() != BankTransferType.Unknown) {
        	crit.add(Restrictions.eq("this.transferType", filterModel.getTransferType()));
        }
        
        if (filterModel.getProcessType() != null) {
        	crit.add(Restrictions.eq("this.processType", filterModel.getProcessType()));
        }
        
        crit.add(Restrictions.eq("action", FinanceAction.Credit));

        crit.addOrder(Order.desc("this.date"));
        crit.addOrder(Order.desc("this.serial"));
        
        return crit;
    }

    @Observer("refreshBrowser:com.ut.tekir.entities.BankToContactTransfer")
    public void refreshResults() {
        log.debug("Uyarı geldi resultSet tazeleniyor");
        if (getEntityList() == null || getEntityList().isEmpty()) {
            return;
        }
        search();
    }

    @SuppressWarnings("unchecked")
	public void pdf() {
        Map params = new HashMap();



        if (filterModel.getSerial() != null && filterModel.getSerial().length() > 0) {
            params.put("pSeri", filterModel.getSerial());
        }

        if (filterModel.getReference() != null && filterModel.getReference().length() > 0) {
            params.put("pRef", filterModel.getReference());
        }

        if (filterModel.getCode() != null && filterModel.getCode().length() > 0) {
            params.put("pCode", filterModel.getCode());
        }


        Calendar c = Calendar.getInstance();
        c.set(1900, 1, 1);
        if (filterModel.getBeginDate() != null) {
            params.put("pBDate", filterModel.getBeginDate());
        }

        c.set(2100, 12, 31);
        if (filterModel.getEndDate() != null) {
            params.put("pEDate", filterModel.getEndDate());
        }

        /*
        if (filterModel.getContact() != null) {
            params.put("pContact", filterModel.getContact());
        }
		*/

        if (filterModel.getBank() != null) {
            params.put("pBank", filterModel.getBank());
        }
        
        if (filterModel.getBankBranch() != null) {
        	params.put("pBankBranch", filterModel.getBankBranch());
        }
        
        if (filterModel.getBankAccount() != null) {
            params.put("pBankAccount", filterModel.getBankAccount());
        }


       // execPdf("depo_sevk_listesi", "Depo Sevk Listesi", params);
        execPdf("cari_banka_transfer_listesi", "Cari Banka Transfer Listesi", params);
    }

    @SuppressWarnings("unchecked")
	public void detailedPdf() {
        Map params = new HashMap();



        if (filterModel.getSerial() != null && filterModel.getSerial().length() > 0) {
            params.put("pSeri", filterModel.getSerial());
        }

        if (filterModel.getReference() != null && filterModel.getReference().length() > 0) {
            params.put("pRef", filterModel.getReference());
        }

        if (filterModel.getCode() != null && filterModel.getCode().length() > 0) {
            params.put("pCode", filterModel.getCode());
        }


        Calendar c = Calendar.getInstance();
        c.set(1900, 1, 1);
        if (filterModel.getBeginDate() != null) {
            params.put("pBDate", filterModel.getBeginDate());
        }

        c.set(2100, 12, 31);
        if (filterModel.getEndDate() != null) {
            params.put("pEDate", filterModel.getEndDate());
        }
        /*
        if (filterModel.getContact() != null) {
            params.put("pContact", filterModel.getContact());
        }
		*/
        if (filterModel.getBank() != null) {
            params.put("pBank", filterModel.getBank());
        }
        
        if (filterModel.getBankBranch() != null) {
        	params.put("pBankBranch", filterModel.getBankBranch());
        }
        
        if (filterModel.getBankAccount() != null) {
            params.put("pBankAccount", filterModel.getBankAccount());
        }

        //execPdf("depo_sevk_detay", "Depo Sevk Detay", params);
        execPdf("cari_banka_transfer_detay", "Cari Banka Transfer Detay", params);
        
    }
	
    public void clearBankAccount() {
		filterModel.setBankBranch(null);
		filterModel.setBankAccount(null);
	}
}
