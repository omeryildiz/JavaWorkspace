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

import org.hibernate.criterion.CriteriaSpecification;
import org.hibernate.criterion.DetachedCriteria;
import org.hibernate.criterion.MatchMode;
import org.hibernate.criterion.Order;
import org.hibernate.criterion.ProjectionList;
import org.hibernate.criterion.Projections;
import org.hibernate.criterion.Restrictions;
import org.hibernate.transform.Transformers;
import org.jboss.seam.ScopeType;
import org.jboss.seam.annotations.In;
import org.jboss.seam.annotations.Name;
import org.jboss.seam.annotations.Observer;
import org.jboss.seam.annotations.Scope;

import com.ut.tekir.entities.FinanceAction;
import com.ut.tekir.entities.Payment;
import com.ut.tekir.framework.BrowserBase;
import com.ut.tekir.framework.CalendarManager;

/**
 *
 * @author selman
 */
@Stateful
@Name( "paymentBrowse" )
@Scope(ScopeType.SESSION)
public class PaymentBrowseBean extends BrowserBase<Payment, PaymentFilterModel> implements PaymentBrowse<Payment, PaymentFilterModel> {

    @In 
    CalendarManager calendarManager;
    
    @Override
    public PaymentFilterModel newFilterModel() {
        PaymentFilterModel fm = new PaymentFilterModel();
        fm.setBeginDate( calendarManager.getLastTenDay());
        fm.setEndDate( calendarManager.getCurrentDate());
        return fm;
    }

    @Override
    public DetachedCriteria buildCriteria() {
        DetachedCriteria crit = DetachedCriteria.forClass( Payment.class );
        
        crit.createAlias("this.contact", "contact");
        crit.createAlias("this.account", "account");
        
        ProjectionList pl = Projections.projectionList();
        pl.add(Projections.property("this.id"), "id")
        	.add(Projections.property("this.serial"), "serial")
        	.add(Projections.property("this.reference"), "reference")
        	.add(Projections.property("this.code"), "code")
        	.add(Projections.property("this.date"), "date")
        	.add(Projections.property("this.info"), "info")
        	.add(Projections.property("contact.code"), "contactCode")
        	.add(Projections.property("contact.name"), "contactName")
        	.add(Projections.property("contact.company"), "company")
        	.add(Projections.property("contact.person"),"person")
        	.add(Projections.property("account.code"), "accountCode")
        	.add(Projections.property("this.totalAmount.currency"), "totalAmountCurrency")
        	.add(Projections.property("this.totalAmount.value"), "totalAmountValue");
        
        if (filterModel.getWorkBunch() != null){
        	crit.createAlias("this.items", "it", CriteriaSpecification.LEFT_JOIN);
        	pl.add(Projections.property("it.workBunch"), "workBunch");
        	pl.add(Projections.property("it.amount.value"), "itemAmountValue");
        	crit.add(Restrictions.eq("it.workBunch", filterModel.getWorkBunch()));
        }

        crit.setProjection(pl).setResultTransformer(Transformers.aliasToBean(PaymentFilterModel.class));

/*
        crit.setProjection(Projections.projectionList()
				.add(Projections.property("this.id"), "id")
				.add(Projections.property("this.serial"), "serial")
				.add(Projections.property("this.reference"), "reference")
				.add(Projections.property("this.code"), "code")
				.add(Projections.property("this.date"), "date")
				.add(Projections.property("this.info"), "info")
				.add(Projections.property("contact.code"), "contactCode")
				.add(Projections.property("contact.name"), "contactName")
				.add(Projections.property("contact.company"), "company")
				.add(Projections.property("contact.person"),"person")
				.add(Projections.property("account.code"), "accountCode")
				.add(Projections.property("this.totalAmount.currency"), "totalAmountCurrency")
				.add(Projections.property("this.totalAmount.value"), "totalAmountValue")
		)
		.setResultTransformer(Transformers.aliasToBean(PaymentFilterModel.class));
*/
        if( isNotEmpty( filterModel.getSerial())){
            crit.add( Restrictions.ilike("this.serial", filterModel.getSerial() ,MatchMode.START));
        }
        
        if( isNotEmpty( filterModel.getReference())){
            crit.add( Restrictions.ilike("this.reference", filterModel.getReference(),MatchMode.START ));
        }
        
        if( isNotEmpty( filterModel.getCode())){
            crit.add( Restrictions.ilike("this.code", filterModel.getCode() ,MatchMode.START ));
        }
        
        if( filterModel.getBeginDate() != null ){
            crit.add( Restrictions.ge("this.date", filterModel.getBeginDate() ));
        }
        
        if( filterModel.getEndDate() != null ){
            crit.add( Restrictions.le("this.date", filterModel.getEndDate() ));
        }

        if (isNotEmpty(filterModel.getContactCode())) {
        	crit.add(Restrictions.ilike("contact.code", filterModel.getContactCode(),MatchMode.START));
        }
        
        if (isNotEmpty(filterModel.getContactName())) {
        	crit.add(Restrictions.ilike("contact.fullname", filterModel.getContactName(),MatchMode.START));
        }

        if (filterModel.getProcessType() != null) {
        	crit.add(Restrictions.eq("this.processType", filterModel.getProcessType()));
        }
        
        crit.add( Restrictions.eq( "this.action", FinanceAction.Debit ));
        crit.addOrder(Order.desc("this.date"));
        crit.addOrder(Order.desc("serial"));
        
        return crit;
    }
    
    @Observer("refreshBrowser:com.ut.tekir.entities.Payment")
    public void refreshResults(){
        //log.debug("Uyarı geldi resultSet tazeleniyor");
        if ( getEntityList() == null || getEntityList().isEmpty() ) return;
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

        params.put("pAct", 1);


        execPdf("tahsilat_tediye_listesi", "Tediye Fişi Listesi", params);

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

        params.put("pAct", 1);

        execPdf("tahsilat_tediye_detay", "Tediye Fişi Detayi", params);
    }
    
    
}
