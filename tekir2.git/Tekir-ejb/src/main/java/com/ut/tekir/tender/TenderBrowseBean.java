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

package com.ut.tekir.tender;

import javax.ejb.Stateful;

import org.hibernate.Criteria;
import org.hibernate.criterion.CriteriaSpecification;
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
import org.jboss.seam.persistence.HibernateSessionProxy;

import com.ut.tekir.entities.Tender;
import com.ut.tekir.entities.TenderStatus;
import com.ut.tekir.framework.BrowserBase;
import com.ut.tekir.framework.CalendarManager;

/**
 * @author sinan.yumak
 *
 */
@Stateful
@Name("tenderBrowse")
@Scope(ScopeType.SESSION)
public class TenderBrowseBean extends BrowserBase<Tender, TenderFilterModel> implements TenderBrowse<Tender, TenderFilterModel> {
    @In
    CalendarManager calendarManager;

    @Override
    public TenderFilterModel newFilterModel() {
    	TenderFilterModel fm = new TenderFilterModel();
        fm.setBeginDate(calendarManager.getLastTenDay());
        fm.setEndDate(calendarManager.getCurrentDate());
        return fm;
    }

    @Observer("refreshBrowser:com.ut.tekir.entities.Tender")
    public void refreshResults() {
        log.debug("#{messages['beanMessages.RefreshingResultSet']}");
        if (getEntityList() == null || getEntityList().isEmpty()) {
            return;
        }
        search();
    }

	@Override
	@SuppressWarnings("unchecked")
	public void search() {
        log.debug("Search Execute");
        
        HibernateSessionProxy session = (HibernateSessionProxy) getEntityManager().getDelegate();
        
        Criteria ecrit =  buildCriteria().getExecutableCriteria( session );
    
        ecrit.setMaxResults( 100 );
        
        ecrit.createAlias("contact", "contact", CriteriaSpecification.LEFT_JOIN);
        
        setEntityList( ecrit.list());
    }

	@Override
    public DetachedCriteria buildCriteria() {
        DetachedCriteria crit = DetachedCriteria.forClass(Tender.class);

        addProjections(crit);

        addRestrictions(crit);

        crit.addOrder(Order.desc("serial"));

        return crit;
    }

	private void addProjections(DetachedCriteria crit) {
        crit.setProjection(Projections.projectionList()
	    		.add(Projections.property("id"), "id")
	    		.add(Projections.property("serial"), "serial")
	    		.add(Projections.property("reference"), "reference")
	    		.add(Projections.property("status"), "status")
	    		.add(Projections.property("referenceSuffix"), "referenceSuffix")
	    		.add(Projections.property("code"), "code")
	    		.add(Projections.property("date"), "date")
	    		.add(Projections.property("info1"), "info1")
	    		.add(Projections.property("info2"), "info2")
	    		.add(Projections.property("contact.fullname"), "contactName")
	    		.add(Projections.property("contact.code"), "contactCode")
	    		.add(Projections.property("totalAmount.value"), "totalAmountValue")
	    		.add(Projections.property("totalAmount.currency"), "totalAmountCurrency")
	    		.add(Projections.property("totalDiscount.value"), "totalDiscountValue")
	    		.add(Projections.property("totalDiscount.currency"), "totalDiscountCurrency")
	    		.add(Projections.property("totalExpense.value"), "totalExpenseValue")
	    		.add(Projections.property("totalExpense.currency"), "totalExpenseCurrency")
	    		.add(Projections.property("totalFee.value"), "totalFeeValue")
	    		.add(Projections.property("totalFee.currency"), "totalFeeCurrency")
	    		.add(Projections.property("totalTax.value"), "totalTaxValue")
	    		.add(Projections.property("totalTax.currency"), "totalTaxCurrency")
        ).setResultTransformer(Transformers.aliasToBean(TenderFilterModel.class));
	}

	private void addRestrictions(DetachedCriteria crit) {
        if (isNotEmpty(filterModel.getSerial())) {
        	crit.add(Restrictions.ilike("this.serial", filterModel.getSerial(),MatchMode.START));
        }
        
        if (isNotEmpty(filterModel.getReference())) {
            crit.add(Restrictions.ilike("this.reference", filterModel.getReference(),MatchMode.START));
        }

        if (filterModel.getStatus() != null) {
        	crit.add(Restrictions.eq("this.status", filterModel.getStatus()));
        }

        if (isNotEmpty(filterModel.getCode())) {
            crit.add(Restrictions.ilike("this.code", filterModel.getCode(),MatchMode.START));
        }

        if (isNotEmpty(filterModel.getContactName())) {
        	crit.add(Restrictions.ilike("contact.name", filterModel.getContactName(),MatchMode.START));
        }

        if (isNotEmpty(filterModel.getContactCode())) {
        	crit.add(Restrictions.ilike("contact.code", filterModel.getContactCode(),MatchMode.START));
        }
        
        if (filterModel.getBeginDate() != null) {
            crit.add(Restrictions.ge("this.date", filterModel.getBeginDate()));
        }
        
        if (filterModel.getEndDate() != null) {
            crit.add(Restrictions.le("this.date", filterModel.getEndDate()));
        }
	}

	public TenderStatus[] getTenderStatusList() {
		return TenderStatus.values();
	}

}