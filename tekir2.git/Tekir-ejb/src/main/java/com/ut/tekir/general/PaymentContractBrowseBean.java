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

import javax.ejb.Stateful;

import org.hibernate.criterion.DetachedCriteria;
import org.hibernate.criterion.MatchMode;
import org.hibernate.criterion.Order;
import org.hibernate.criterion.Restrictions;
import org.jboss.seam.ScopeType;
import org.jboss.seam.annotations.In;
import org.jboss.seam.annotations.Name;
import org.jboss.seam.annotations.Observer;
import org.jboss.seam.annotations.Scope;

import com.ut.tekir.entities.PaymentContract;
import com.ut.tekir.framework.BrowserBase;
import com.ut.tekir.framework.CalendarManager;

/**
 *
 * @author sinan.yumak
 */
@Stateful
@Name("paymentContractBrowse")
@Scope(ScopeType.SESSION)
public class PaymentContractBrowseBean extends BrowserBase<PaymentContract, PaymentContractFilterModel> implements PaymentContractBrowse<PaymentContract, PaymentContractFilterModel> {

    @In
    CalendarManager calendarManager;

    @Override
    public PaymentContractFilterModel newFilterModel() {
    	PaymentContractFilterModel fm = new PaymentContractFilterModel();
        return fm;
    }

    @Override
    public DetachedCriteria buildCriteria() {

        DetachedCriteria crit = DetachedCriteria.forClass(PaymentContract.class);
        
        if (isNotEmpty(filterModel.getCode())) {
            crit.add(Restrictions.ilike("this.code", filterModel.getCode(),MatchMode.START));
        }

        if (isNotEmpty(filterModel.getName())) {
        	crit.add(Restrictions.ilike("this.name", filterModel.getName(),MatchMode.START));
        }

        if (filterModel.getBeginDate() != null) {
        	crit.add(Restrictions.ge("this.beginDate", filterModel.getBeginDate()));
        }

        if (filterModel.getEndDate() != null) {
        	crit.add(Restrictions.le("this.endDate", filterModel.getEndDate()));
        }

        if (filterModel.getCardOwnerType() != null) {
        	crit.add(Restrictions.eq("this.cardOwnerType", filterModel.getCardOwnerType()));
        }
        
        if (filterModel.getPaymentType() != null) {
        	crit.add(Restrictions.eq("this.paymentType", filterModel.getPaymentType()));
        }

        if (filterModel.getOwnerType() != null) {
        	crit.add(Restrictions.eq("this.ownerType", filterModel.getOwnerType()));
        }
        
        crit.addOrder(Order.desc("this.beginDate"));
        crit.addOrder(Order.desc("this.code"));
        
        return crit;
    }

    @Observer("refreshBrowser:com.ut.tekir.entities.PaymentContract")
    public void refreshResults() {
        log.debug("Uyarı geldi resultSet tazeleniyor");
        if (getEntityList() == null || getEntityList().isEmpty()) {
            return;
        }
        search();
    }

}
