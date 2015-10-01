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

package com.ut.tekir.stock;

import java.util.Calendar;
import java.util.HashMap;
import java.util.Map;

import javax.ejb.Stateful;

import org.hibernate.criterion.DetachedCriteria;
import org.hibernate.criterion.MatchMode;
import org.hibernate.criterion.Order;
import org.hibernate.criterion.Projections;
import org.hibernate.transform.Transformers;
import org.hibernate.criterion.Restrictions;
import org.jboss.seam.ScopeType;
import org.jboss.seam.annotations.In;
import org.jboss.seam.annotations.Name;
import org.jboss.seam.annotations.Observer;
import org.jboss.seam.annotations.Scope;

import com.ut.tekir.entities.OrderNote;
import com.ut.tekir.entities.OrderStatus;
import com.ut.tekir.entities.TradeAction;
import com.ut.tekir.framework.BrowserBase;
import com.ut.tekir.framework.CalendarManager;

/**
 *
 * @author haky
 */
//@Stateful
//@Name("saleOrderBrowse")
@Scope(ScopeType.SESSION)
public class SaleOrderBrowseBean extends BrowserBase<OrderNote, OrderFilterModel> implements SaleOrderBrowse<OrderNote, OrderFilterModel> {

    @In
    CalendarManager calendarManager;

    @Override
    public OrderFilterModel newFilterModel() {
        OrderFilterModel fm = new OrderFilterModel();
        fm.setBeginDate(calendarManager.getLastTenDay());
        fm.setEndDate(calendarManager.getCurrentDate());
        return fm;
    }

	public OrderStatus[] getOrderStatus() {
		return OrderStatus.values();
	}
	
	public TradeAction[] getTradeAction() {
		return TradeAction.values();
	}
    
    @Override
    public DetachedCriteria buildCriteria() {

        DetachedCriteria crit = DetachedCriteria.forClass(OrderNote.class);

        crit.createAlias("contact", "contact");
        
        crit.setProjection(Projections.projectionList()
        		.add(Projections.property("id"), "id")
        		.add(Projections.property("serial"), "serial")
        		.add(Projections.property("reference"), "reference")
        		.add(Projections.property("code"), "code")
        		.add(Projections.property("status"), "status")
        		.add(Projections.property("date"), "date")
        		.add(Projections.property("info"), "info")
        		.add(Projections.property("warehouse"), "warehouse")
        		.add(Projections.property("contact.name"), "contactName")
        		.add(Projections.property("contact.code"), "contactCode")
        ).setResultTransformer(Transformers.aliasToBean(OrderFilterModel.class));

        if (isNotEmpty(filterModel.getSerial())) {
        	crit.add(Restrictions.ilike("serial", filterModel.getSerial(),MatchMode.START));
        }

        if (isNotEmpty(filterModel.getReference())) {
        	crit.add(Restrictions.ilike("reference", filterModel.getReference(),MatchMode.START));
        }

        if (isNotEmpty(filterModel.getCode())) {
        	crit.add(Restrictions.ilike("code", filterModel.getCode(),MatchMode.START));
        }

        if (filterModel.getBeginDate() != null) {
            crit.add(Restrictions.ge("date", filterModel.getBeginDate()));
        }

        if (filterModel.getEndDate() != null) {
            crit.add(Restrictions.le("date", filterModel.getEndDate()));
        }

        if (filterModel.getWarehouse() != null) {
            crit.add(Restrictions.eq("warehouse", filterModel.getWarehouse()));
        }

        if (isNotEmpty(filterModel.getContactCode())) {
            crit.add(Restrictions.ilike("contact.code", filterModel.getContactCode(),MatchMode.START ));
        }


        if (isNotEmpty(filterModel.getContactName())) {
            crit.add(Restrictions.ilike("contact.name", filterModel.getContactName(),MatchMode.START ));
        }

        if (filterModel.getStatus()!= null) {
        	crit.add(Restrictions.eq("status", filterModel.getStatus()));
        }
        
        crit.add(Restrictions.eq("action", TradeAction.Sale));
        crit.addOrder(Order.desc("serial"));
        
        return crit;
    }

    @Observer("refreshBrowser:com.ut.tekir.entities.OrderNote")
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

        if (filterModel.getWarehouse() != null) {
            params.put("pWare", filterModel.getWarehouse().getCode());
        }
        /*
        if (filterModel.getContact() != null) {
            params.put("pCon", filterModel.getContact().getId());
        }
		*/
        params.put("pAct", 0);


        execPdf("sipariş_listesi", "Siparis Listesi", params);

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


        if (filterModel.getWarehouse() != null) {
            params.put("pWare", filterModel.getWarehouse().getCode());
        }
        /*
        if (filterModel.getContact() != null) {
            params.put("pCon", filterModel.getContact().getId());
        }
		*/
        params.put("pAct", 0);




        execPdf("satis-siparis", "Satis Siparis Detayi", params);
    }
}