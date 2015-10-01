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

import com.ut.tekir.entities.ShipmentNote;
import com.ut.tekir.entities.TradeAction;
import com.ut.tekir.framework.BrowserBase;
import com.ut.tekir.framework.CalendarManager;
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

/**
 *
 * @author haky
 */
//@Stateful
//@Name( "saleShipmentBrowse" )
@Scope(ScopeType.SESSION)
public class SaleShipmentBrowseBean extends BrowserBase<ShipmentNote, ShipmentFilterModel> implements SaleShipmentBrowse<ShipmentNote, ShipmentFilterModel> {
    
    @In CalendarManager calendarManager;
    
    @Override
    public ShipmentFilterModel newFilterModel() {
        ShipmentFilterModel fm = new ShipmentFilterModel();
        fm.setBeginDate( calendarManager.getLastTenDay());
        fm.setEndDate( calendarManager.getCurrentDate());
        return fm;
    }

    @Override
    public DetachedCriteria buildCriteria() {
        
        DetachedCriteria crit = DetachedCriteria.forClass( ShipmentNote.class );
        
        crit.createAlias("contact", "contact");
        
        crit.setProjection(Projections.projectionList()
        		.add(Projections.property("id"), "id")
        		.add(Projections.property("serial"), "serial")
        		.add(Projections.property("reference"), "reference")
        		.add(Projections.property("code"), "code")
        		.add(Projections.property("date"), "date")
        		.add(Projections.property("info"), "info")
        		.add(Projections.property("warehouse"), "warehouse")
        		.add(Projections.property("contact.fullname"), "contactName")
        		.add(Projections.property("contact.code"), "contactCode")
        ).setResultTransformer(Transformers.aliasToBean(ShipmentFilterModel.class));

        if( isNotEmpty( filterModel.getSerial())){
            crit.add( Restrictions.ilike("this.serial", filterModel.getSerial() ,MatchMode.START));
        }
        
        if( isNotEmpty( filterModel.getReference())){
            crit.add( Restrictions.ilike("this.reference", filterModel.getReference() ,MatchMode.START ));
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

        if( filterModel.getWarehouse() != null ){
            crit.add( Restrictions.eq("this.warehouse", filterModel.getWarehouse() ));
        }

        if (isNotEmpty(filterModel.getContactCode())) {
            crit.add(Restrictions.ilike("contact.code", filterModel.getContactCode(),MatchMode.START ));
        }

        if (isNotEmpty(filterModel.getContactName())) {
            crit.add(Restrictions.ilike("contact.fullname", filterModel.getContactName(),MatchMode.ANYWHERE ));
        }
        
        crit.add( Restrictions.eq("this.action", TradeAction.Sale ));
        crit.addOrder(Order.desc("this.serial"));
        
        return crit;
    }

    
    
    @Observer("refreshBrowser:com.ut.tekir.entities.ShipmentNote")
    public void refreshResults(){
        log.debug("Uyarı geldi resultSet tazeleniyor");
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


        if (filterModel.getWarehouse() != null) {
            params.put("pWare", filterModel.getWarehouse().getCode());
        }
        /*
        if (filterModel.getContact() != null) {
            params.put("pCon", filterModel.getContact().getId());
        }
		*/
        params.put("pAct", 1);




        execPdf("irsaliye_listesi", "Satis İrsaliye Listesi", params);

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

        c.set(9999, 12, 13);
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
        params.put("pAct", 1);




        execPdf("alis-satis-irsaliye", "Satis İrsaliye Detayi", params);
    }

}
