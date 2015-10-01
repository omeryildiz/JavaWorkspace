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

import com.ut.tekir.entities.CountNote;
import com.ut.tekir.entities.CountStatus;
import com.ut.tekir.framework.BrowserBase;
import com.ut.tekir.framework.CalendarManager;

/**
 * Sayım fişleri browse bileşenidir.
 * @author sinan.yumak
 */
@Name("countNoteBrowse")
@Scope(ScopeType.SESSION)
public class CountNoteBrowseBean extends BrowserBase<CountNote, CountNoteFilterModel> implements CountNoteBrowse<CountNote, CountNoteFilterModel> {

    @In CalendarManager calendarManager;
    
    @Override
    public CountNoteFilterModel newFilterModel() {
    	CountNoteFilterModel fm = new CountNoteFilterModel();
        fm.setBeginDate( calendarManager.getLastTenDay() );
        fm.setEndDate( calendarManager.getCurrentDate() );
    	return fm;
    }

    @Override
    public DetachedCriteria buildCriteria() {
        DetachedCriteria crit = DetachedCriteria.forClass( CountNote.class );

        crit.setProjection(Projections.projectionList()
        		.add(Projections.property("this.id"),"id")
        		.add(Projections.property("this.serial"),"serial")
        		.add(Projections.property("this.reference"),"reference")
        		.add(Projections.property("this.code"),"code")
        		.add(Projections.property("this.info"),"info")
        		.add(Projections.property("this.date"),"date")
        		.add(Projections.property("this.hasQuantity"),"hasQuantity")
        		.add(Projections.property("this.approved"),"approved")
        ).setResultTransformer(Transformers.aliasToBean(CountNote.class));
        
        if( isNotEmpty( filterModel.getSerial())){
            crit.add( Restrictions.like("this.serial", filterModel.getSerial(), MatchMode.START));
        }
        
        if( isNotEmpty( filterModel.getReference())){
            crit.add( Restrictions.like("this.reference", filterModel.getReference(), MatchMode.START));
        }
        
        if( isNotEmpty( filterModel.getCode())){
            crit.add( Restrictions.like("this.code", filterModel.getCode(),MatchMode.START));
        }
        
        if( filterModel.getBeginDate() != null ){
            crit.add( Restrictions.ge("this.date", filterModel.getBeginDate() ));
        }
        
        if( filterModel.getEndDate() != null ){
            crit.add( Restrictions.le("this.date", filterModel.getEndDate() ));
        }

        if( filterModel.getStatusList() != null ){
        	crit.add( Restrictions.in("this.status", filterModel.getStatusList() ));
        }
        
        crit.addOrder(Order.desc("date"));
        crit.addOrder(Order.desc("serial"));
        return crit;
    }

    public void prepareOpenCounts() {
    	setFilterForUncounted();
    	search();
    }
    
    private void setFilterForUncounted() {
    	getFilterModel().clear();
    	getFilterModel().setStatusList(new CountStatus[]{CountStatus.Counting,CountStatus.Editing,CountStatus.Open});
    }
    
    @Observer("refreshBrowser:com.ut.tekir.entities.CountNote")
    public void refreshResults(){
        log.debug("Uyarı geldi resultSet tazeleniyor");
        if ( getEntityList() == null || getEntityList().isEmpty() ) return;
        search();
    }
    
}
