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

package com.ut.tekir.stock.yeni;

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

import com.ut.tekir.entities.TradeAction;
import com.ut.tekir.entities.shp.TekirShipmentNote;
import com.ut.tekir.framework.BrowserBase;
import com.ut.tekir.framework.CalendarManager;
import com.ut.tekir.stock.ShipmentFilterModel;

/**
 *
 * @author sinan.yumak
 */
@Stateful
@Name("purchaseShipmentBrowse")
@Scope(ScopeType.SESSION)
public class PurchaseShipmentBrowseBean extends BrowserBase<TekirShipmentNote, ShipmentFilterModel> implements PurchaseShipmentBrowse<TekirShipmentNote, ShipmentFilterModel> {

    @In
    CalendarManager calendarManager;

    @Override
    public ShipmentFilterModel newFilterModel() {
        ShipmentFilterModel fm = new ShipmentFilterModel();
        fm.setBeginDate(calendarManager.getLastTenDay());
        fm.setEndDate(calendarManager.getCurrentDate());
        return fm;
    }

    @Override
    public DetachedCriteria buildCriteria() {

        DetachedCriteria crit = DetachedCriteria.forClass(TekirShipmentNote.class);
        
        crit.createAlias("contact", "contact");

        crit.setProjection(Projections.projectionList()
        		.add(Projections.property("id"), "id")
        		.add(Projections.property("serial"), "serial")
        		.add(Projections.property("reference"), "reference")
        		.add(Projections.property("code"), "code")
        		.add(Projections.property("date"), "date")
        		.add(Projections.property("info"), "info")
                .add(Projections.property("info1"), "info1")
        		.add(Projections.property("info2"), "info2")
        		.add(Projections.property("warehouse"), "warehouse")
        		.add(Projections.property("contact.fullname"), "contactName")
        		.add(Projections.property("contact.company"), "company")
                .add(Projections.property("contact.person"),"person")
        		.add(Projections.property("contact.code"), "contactCode")
                .add(Projections.property("workBunch"), "workBunch")
        ).setResultTransformer(Transformers.aliasToBean(ShipmentFilterModel.class));

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

        if (filterModel.getWarehouse() != null) {
            crit.add(Restrictions.eq("this.warehouse", filterModel.getWarehouse()));
        }
       
        if (isNotEmpty(filterModel.getContactName())) {
            crit.add(Restrictions.ilike("contact.fullname", filterModel.getContactName(),MatchMode.ANYWHERE));
        }

        if (isNotEmpty(filterModel.getContactCode())) {
            crit.add(Restrictions.ilike("contact.code", filterModel.getContactCode(),MatchMode.START ));
        }

        if ( filterModel.getInvoiced() != null ) {
        	if (filterModel.getInvoiced().equals(Boolean.TRUE)) {
        		crit.add( Restrictions.isNotNull("this.invoice") );
        	} else {
        		crit.add( Restrictions.isNull("this.invoice") );
        	}
        }

        if (filterModel.getWorkBunch() != null){
        	crit.add(Restrictions.eq("this.workBunch", filterModel.getWorkBunch()));
        }
        
        crit.add(Restrictions.eq("this.tradeAction", TradeAction.Purchase));
        crit.addOrder(Order.desc("this.date"));
        crit.addOrder(Order.desc("this.serial"));
        
        return crit;
    }

    @Observer("refreshBrowser:com.ut.tekir.entities.shp.TekirShipmentNote")
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




        execPdf("irsaliye_listesi", "Aliş İrsaliye Listesi", params);

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




        execPdf("alis-satis-irsaliye", "Aliş İrsaliye Detayi", params);
    }
}
