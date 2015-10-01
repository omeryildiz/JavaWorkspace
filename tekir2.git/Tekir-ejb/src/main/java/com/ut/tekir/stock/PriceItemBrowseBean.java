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

import java.util.HashMap;
import java.util.Map;

import javax.ejb.Stateful;

import org.hibernate.Criteria;
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
import org.jboss.seam.persistence.HibernateSessionProxy;

import com.ut.tekir.entities.PriceItem;
import com.ut.tekir.framework.BrowserBase;
import com.ut.tekir.framework.CalendarManager;

/**
 *
 * @author haky
 */
@Stateful
@Name("priceItemBrowse")
@Scope(ScopeType.SESSION)
public class PriceItemBrowseBean extends BrowserBase<PriceItem, PriceItemFilterModel> implements PriceItemBrowse<PriceItem, PriceItemFilterModel> {

    @In
    CalendarManager calendarManager;

    @Override
    public PriceItemFilterModel newFilterModel() {
    	PriceItemFilterModel fm = new PriceItemFilterModel();
        return fm;
    }

    @Override
    public DetachedCriteria buildCriteria() {

        DetachedCriteria crit = DetachedCriteria.forClass(PriceItem.class);
        
        if (isNotEmpty(filterModel.getCode())) {
            crit.add(Restrictions.ilike("this.code", filterModel.getCode(),MatchMode.START));
        }

        if (filterModel.getBeginDate() != null) {
        	crit.add(Restrictions.ge("this.beginDate", filterModel.getBeginDate()));
        }

        if (filterModel.getEndDate() != null) {
        	crit.add(Restrictions.le("this.endDate", filterModel.getEndDate()));
        }

        if (filterModel.getAction() != null) {
        	crit.add(Restrictions.eq("this.action", filterModel.getAction()));
        }
        
        crit.addOrder(Order.desc("this.beginDate"));
        crit.addOrder(Order.desc("this.code"));
        
        return crit;
    }
    
    @Override
    @SuppressWarnings("unchecked")
	public void search(){

        log.debug("Search Execute");
        
        HibernateSessionProxy session = (HibernateSessionProxy) getEntityManager().getDelegate();
        
        Criteria ecrit =  buildCriteria().getExecutableCriteria( session );
        //ecrit.setCacheable(true);
        ecrit.setMaxResults( 100 );

        ProjectionList pl = Projections.projectionList()
		.add(Projections.property("this.id"), "id")
		.add(Projections.property("this.active"), "active")
		.add(Projections.property("this.defaultItem"), "defaultItem")
		.add(Projections.property("this.info"), "info")
		.add(Projections.property("this.code"), "code")
		.add(Projections.property("this.endDate"), "endDate")
		.add(Projections.property("this.beginDate"), "beginDate")
        .add(Projections.property("this.action"), "action");
        

        ecrit.createAlias("this.owner", "owner",DetachedCriteria.LEFT_JOIN);

        pl.add(Projections.property("owner.code"), "ownerCode")
          .add(Projections.property("owner.listName"), "ownerListName");
       
        if (filterModel.getPriceList() != null) {

        	ecrit.add(Restrictions.eq("owner", filterModel.getPriceList()));

        }

        ecrit.createAlias("this.group", "group",DetachedCriteria.LEFT_JOIN);

        pl.add(Projections.property("group"), "group");
        
        if (filterModel.getGroup() != null) {
        	ecrit.add(Restrictions.eq("this.group", filterModel.getGroup()));
        }
        ecrit.setProjection(pl)
        	 .setResultTransformer(Transformers.aliasToBean(PriceItemFilterModel.class));

        setEntityList( ecrit.list());
        
    }

    @Observer("refreshBrowser:com.ut.tekir.entities.PriceItem")
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
        
        if(isNotEmpty(filterModel.getCode())){
        	params.put("pCode", filterModel.getCode());
        }

        if(filterModel.getPriceList() != null){
        	params.put("pOwnerId", filterModel.getPriceList().getId());
        }
        
        if(filterModel.getGroup() != null){
        	params.put("pGroupId", filterModel.getGroup().getId());
        }
        
        if(filterModel.getBeginDate() != null){
        	params.put("pBDate", filterModel.getBeginDate());
        }
        
        if(filterModel.getEndDate() != null){
        	params.put("pEDate",filterModel.getEndDate());
        }
        
        execPdf("fiyat_listesi_excel", "Alis_Irsaliye_Listesi", params);
        //execPdf("irsaliye_listesi", "Alis_Irsaliye_Listesi", params);
    }

    @SuppressWarnings("unchecked")
	public void detailedPdf() {
        Map params = new HashMap();

        execPdf("alis-satis-irsaliye", "Alis_Irsaliye_Detayi", params);
    }
    
    @SuppressWarnings("unchecked")
	public void xls() {
        Map params = new HashMap();
        
        if(isNotEmpty(filterModel.getCode())){
        	params.put("pCode", filterModel.getCode());
        }

        if(filterModel.getPriceList() != null){
        	params.put("pOwnerId", filterModel.getPriceList().getId());
        }
        
        if(filterModel.getGroup() != null){
        	params.put("pGroupId", filterModel.getGroup().getId());
        }
        
        if(filterModel.getBeginDate() != null){
        	params.put("pBDate", filterModel.getBeginDate());
        }
        
        if(filterModel.getEndDate() != null){
        	params.put("pEDate",filterModel.getEndDate());
        }
        
        execXls("fiyat_listesi_excel", "Fiyat_Listesi",params);
    }  
}
