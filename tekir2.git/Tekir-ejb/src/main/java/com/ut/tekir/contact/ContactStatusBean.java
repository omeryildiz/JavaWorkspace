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

package com.ut.tekir.contact;

import com.ut.tekir.entities.Contact;
import com.ut.tekir.entities.FinanceAction;
import com.ut.tekir.entities.FinanceTxn;
import com.ut.tekir.framework.BaseConsts;
import com.ut.tekir.framework.CalendarManager;
import java.util.Calendar;
import java.util.Date;
import java.util.Iterator;
import java.util.List;
import javax.ejb.Remove;
import javax.ejb.Stateful;
import javax.persistence.EntityManager;
import org.hibernate.Criteria;
import org.hibernate.criterion.DetachedCriteria;
import org.hibernate.criterion.Projections;
import org.hibernate.criterion.Restrictions;
import org.jboss.seam.ScopeType;
import org.jboss.seam.annotations.Begin;
import org.jboss.seam.annotations.Create;
import org.jboss.seam.annotations.Destroy;
import org.jboss.seam.annotations.In;
import org.jboss.seam.annotations.Logger;
import org.jboss.seam.annotations.Name;
import org.jboss.seam.annotations.Scope;
import org.jboss.seam.faces.FacesMessages;
import org.jboss.seam.log.Log;
import org.jboss.seam.persistence.HibernateSessionProxy;


/**
 *
 * @author haky
 */
@Stateful()
@Name( "contactStatus")
@Scope(value=ScopeType.CONVERSATION)
public class ContactStatusBean implements ContactStatus {
    
    private Integer year;
    private String currency;
    
    @Logger
    protected Log log;
    
    @In
    protected EntityManager entityManager;
    
    @In
    protected FacesMessages facesMessages;
    
    @In
    protected CalendarManager calendarManager;
    
    private Long contactId;
    private Contact contact;
    
    private StatusTable dataTable = new StatusTable();
    
    @Remove @Destroy
    public void destroy() {
    }
    
    @SuppressWarnings("unchecked")
	public List getResultList( ){
        log.debug("Cari durum sorgusu çekiliyor" );
        return getResultList( 1l );
    }
    
    @SuppressWarnings("unchecked")
	public List getResultList( Long contactId ){
       
         log.debug("Cari durum sorgusu çekiliyor" );
         
        DetachedCriteria crit = DetachedCriteria.forClass( FinanceTxn.class );
        
        crit.add( Restrictions.eq( "active", true ) );
        crit.add( Restrictions.eq( "contact.id", contactId ) );
        
        /*
        crit.setProjection( Projections.sum("amount")) 
            .setProjection( Projections.groupProperty("contact"))
            .setProjection( Projections.property("contact"));
        */
        
        HibernateSessionProxy session = (HibernateSessionProxy) entityManager.getDelegate();
        
        Criteria ecrit =  crit.getExecutableCriteria( session );

        log.debug("Cari durum sorgusu çekiliyor" );
        //ecrit.setMaxResults( 100 );
        
        return ecrit.list();
    }
    
    @Create @Begin( join=true )
    public void init(){
        year = calendarManager.getCurrentYear();
    }
    
    
    public String beginStatusReport(){
        
        log.debug(" parametre ne : #{params[contactId]}");
        log.debug(" Eldeki id ne : #0",getContactId());
        
        contact = entityManager.find(Contact.class,getContactId());
        
        if ( getContact() == null ){
            facesMessages.add("Aranılan Cari bulunamadı");
            return null;
        }
        
        //Bişiler Yapılacak işte
        
        return BaseConsts.SUCCESS;
    }

    public void calculateStatus(){
        dataTable = new StatusTable();
        calculateOffset();
        calculateMonths();
        dataTable.sort();
    }
    
    /**
     * Önce devir hesaplayalım...
     * 
     */
    @SuppressWarnings("unchecked")
	protected void calculateOffset(){
        
    	HibernateSessionProxy session = (HibernateSessionProxy) entityManager.getDelegate();
        
        Calendar cal = Calendar.getInstance();
        cal.set(getYear(), 0, 1);
        Date beginDate = cal.getTime();
        
        Criteria crit = session.createCriteria(FinanceTxn.class );
        
        crit.add( Restrictions.eq("contact", contact ))
            .add( Restrictions.lt("date", beginDate ))
            .add( Restrictions.eq("active", true )); //Tarih kontrolü yapılacak
        
        crit.setProjection( Projections.projectionList()
                .add( Projections.groupProperty("amount.currency"), "amountCurrency" )
                .add( Projections.groupProperty("action"), "action" )
                .add( Projections.sum("amount.value"), "amount" )
                .add( Projections.sum("localAmount.value"), "localAmount" )
            );
        
        List ls = crit.list();
        
        
        for (Iterator it = ls.iterator(); it.hasNext(); ) {
                Object obj[] = (Object[]) it.next(); 
                dataTable.addRow( "OPEN", (String)obj[0], (FinanceAction)obj[1], (Double)obj[2], (Double)obj[3]);
        }
    }
    
    private void calculateMonths(){
        calculateMonth( 0 );
        calculateMonth( 1 );
        calculateMonth( 2 );
        calculateMonth( 3 );
        calculateMonth( 4 );
        calculateMonth( 5 );
        calculateMonth( 6 );
        calculateMonth( 7 );
        calculateMonth( 8 );
        calculateMonth( 9 );
        calculateMonth( 10 );
        calculateMonth( 11 );
    }
    
    @SuppressWarnings("unchecked")
	private void calculateMonth( Integer month ){
        
        String rowKey = "MONTH" + ( month < 10 ? "0" + month : month );
        
        Calendar cal = Calendar.getInstance();
        cal.set(getYear(),month, 1);
        
        Date beginDate = cal.getTime();
        
        if( month == 12 ){
            cal.set(getYear() + 1, 1, 1);
        } else {
            cal.set(getYear(), month + 1, 1);
        }
    
        
        Date endDate   = cal.getTime();
        
        
        
        
        HibernateSessionProxy session = (HibernateSessionProxy) entityManager.getDelegate();
        
        Criteria crit = session.createCriteria(FinanceTxn.class );
        
        crit.add( Restrictions.eq("contact", contact ))
            .add( Restrictions.ge("date", beginDate ))
            .add( Restrictions.lt("date", endDate ))
            .add( Restrictions.eq("active", true )); //Tarih kontrolü yapılacak
        
        crit.setProjection( Projections.projectionList()
                .add( Projections.groupProperty("amount.currency"), "amountCurrency" )
                .add( Projections.groupProperty("action"), "action" )
                .add( Projections.sum("amount.value"), "amount" )
                .add( Projections.sum("localAmount.value"), "localAmount" )
            );
        
        List ls = crit.list();
        
        
        for (Iterator it = ls.iterator(); it.hasNext(); ) {
                Object obj[] = (Object[]) it.next(); 
                dataTable.addRow( rowKey, (String)obj[0], (FinanceAction)obj[1], (Double)obj[2], (Double)obj[3]);
        }
    }
    
    public Contact getContact() {
        return contact;
    }

    public void setContact(Contact contact) {
        this.contact = contact;
    }

    public Long getContactId() {
        return contactId;
    }

    public void setContactId(Long contactId) {
        this.contactId = contactId;
    }
    
    public List<StatusRow> getStatusRows(){
        if ( dataTable.getDataRows() == null ){
            calculateStatus();
        }
        
        return dataTable.getDataRows();
    }

    public Integer getYear() {
        return year;
    }

    public void setYear(Integer year) {
        this.year = year;
    }

    public String getCurrency() {
        return currency;
    }

    public void setCurrency(String currency) {
        this.currency = currency;
    }
}
