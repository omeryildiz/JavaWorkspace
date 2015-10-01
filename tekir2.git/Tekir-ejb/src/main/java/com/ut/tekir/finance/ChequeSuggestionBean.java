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

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import javax.ejb.Remove;
import javax.ejb.Stateful;
import javax.persistence.EntityManager;

import org.hibernate.Criteria;
import org.hibernate.criterion.DetachedCriteria;
import org.hibernate.criterion.MatchMode;
import org.hibernate.criterion.Restrictions;
import org.jboss.seam.ScopeType;
import org.jboss.seam.annotations.AutoCreate;
import org.jboss.seam.annotations.Destroy;
import org.jboss.seam.annotations.In;
import org.jboss.seam.annotations.Logger;
import org.jboss.seam.annotations.Name;
import org.jboss.seam.annotations.Scope;
import org.jboss.seam.core.Events;
import org.jboss.seam.log.Log;
import org.jboss.seam.persistence.HibernateSessionProxy;

import com.ut.tekir.entities.BankAccount;
import com.ut.tekir.entities.Cheque;
import com.ut.tekir.entities.ChequeStatus;
import com.ut.tekir.entities.Contact;

/**
 *
 * @author huseyin
 */
@Stateful
@Name("chequeSuggestion")
@Scope(ScopeType.CONVERSATION)
@AutoCreate
public class ChequeSuggestionBean implements ChequeSuggestion {

    @Logger
    private Log log;
    
    @In
    private EntityManager entityManager;

    @In
    protected Events events;

    @SuppressWarnings("unchecked")
	private List<Cheque> chequeList;
    private String referenceNo;
    private String serialNo;
    private Date beginDate;
    private Date endDate;
    private String bankName;
	private Date issueDate;
    private String bankBranch;
    private String bankAccount;
    private BankAccount bankAccountId;
    private String chequeOwner;
    private ChequeStatus lastStatus;
    private ChequeStatus previousStatus;
    private ChequeStatus targetStatus;  //alacağı yeni status
    private Cheque cheque = new Cheque() ;
    private String callerObserveString;
    private Boolean clientCheque = Boolean.FALSE;
    private Contact contact;
    private Date historyBeginDate;
    private Date historyEndDate;
    
	@SuppressWarnings("unchecked")
	public List<Cheque> selectChequeList(){
        
    	HibernateSessionProxy session = (HibernateSessionProxy) entityManager.getDelegate();
    	DetachedCriteria crit = DetachedCriteria.forClass(Cheque.class);

    	
    	//TODO: projection saglanmali, embedded money nesnesi sorun cikariyor   
//    	crit.forClass(Money.class, "money");
//    	crit.setProjection(Projections.projectionList()
//         	.add(Projections.property("id"), "id")
//            .add(Projections.property("maturityDate"), "maturityDate")
//            .add(Projections.property("referenceNo"), "referenceNo")
//            .add(Projections.property("bankName"), "bankName")
//            .add(Projections.property("bankBranch"), "bankBranch")
//            .add(Projections.property("accountNo"), "accountNo")
//            .add(Projections.property("chequeOwner"), "chequeOwner")
//            .add(Projections.property("contact"), "contact")
//            .add(Projections.property("lastStatus"), "lastStatus")
//            .add(Projections.property("previousStatus"), "previousStatus")
//            .add(Projections.property("serialNo"), "serialNo"));
//    	 
//        crit.setResultTransformer(Transformers.aliasToBean(Cheque.class));
    	if(getHistoryBeginDate() != null || getHistoryEndDate() != null){
    		crit.createAlias("history", "history");
    		if(getHistoryBeginDate() != null ){
    			crit.add( Restrictions.ge("history.date", getHistoryBeginDate()));
    		}
    		if(getHistoryEndDate() != null ){
    			crit.add( Restrictions.le("history.date", getHistoryEndDate()));
    		}
    		crit.add(Restrictions.eqProperty("history.status", "this.lastStatus"));
    		
    	}
        if( getReferenceNo() != null && getReferenceNo().length() > 0 ){
        	crit.add( Restrictions.ilike("referenceNo", getReferenceNo(), MatchMode.ANYWHERE));
        }
        
        if( getSerialNo() != null && getSerialNo().length() > 0 ){
            crit.add( Restrictions.ilike("serialNo", getSerialNo(), MatchMode.START));
        }
         
        if( getBeginDate() != null ){
            crit.add( Restrictions.ge("maturityDate", getBeginDate() ));
        }
        
        if( getEndDate() != null ){
            crit.add( Restrictions.le("maturityDate", getEndDate() ));
        }
		
        if( getBankName() != null && getBankName().length() > 0 ){
            crit.add( Restrictions.ilike("bankName", getBankName(), MatchMode.START));
        } 
        
        if( getBankBranch() != null && getBankBranch().length() > 0  ){
            crit.add( Restrictions.ilike("bankBranch", getBankBranch(), MatchMode.START));
        }
        
        if( getChequeOwner() != null && getChequeOwner().length() > 0 ){
        	crit.add( Restrictions.ilike("chequeOwner", getChequeOwner(), MatchMode.START));
        }
        
        if( getBankBranch() != null && getBankBranch().length() > 0  ){
            crit.add( Restrictions.ilike("bankBranch", getBankBranch(), MatchMode.START));
        }
        
        if( getBankAccount() != null && getBankAccount().length() > 0  ){
            crit.add( Restrictions.ilike("bankAccount", getBankAccount(), MatchMode.START));
        }

        if( getBankAccountId() != null){
            crit.add( Restrictions.eq("bankAccountId", getBankAccountId().getId() ));
        }
        
        if( getContact() != null){
            crit.add( Restrictions.eq("contact", getContact() ));
        }
        
        if (lastStatus != null) {
        	crit.add(Restrictions.eq("lastStatus", lastStatus));
        } else {
        	List<ChequeStatus> possibleStatus = null;
	        if (clientCheque != null && clientCheque.equals(Boolean.TRUE)) {
	        	possibleStatus = possibleStatusForCustomer(targetStatus);
	        } else {
	        	possibleStatus = possibleStatusForFirm(targetStatus);
	        }
        
	        if (possibleStatus != null) {
	        	crit.add( Restrictions.in("lastStatus", possibleStatus));
	        }
        }
        
        crit.add(Restrictions.eq("clientCheque", getClientCheque()));
        
        Criteria ecrit =  crit.getExecutableCriteria( session );

        chequeList= ecrit.list();
        
        return chequeList;
    }
    
    /**
     * Bu metod, müşteri çek/senetleri statü değişikliklerinde, parametre olarak verilen requestStatus
     * bilgisine göre mümkün statüleri verir.
     * 
     * <p>
     * <b><u>Örnek;</u></b>
     *      Statüsünü Portoy yapacağımız çek/senet ler seçilirken, 
     *      seçim listesine statüsü sadece (Ciro, BankaTahsilatta, BankaTeminat, Karsiliksiz, Takipte) olanlar gelebilir
     * </p>
     * <p>
     * <b><u>Örnek;</u></b>
     *      KasaTahsilat' a gönderilecek olan çek/senet ler seçilirken, 
     *      seçim listesine statüsü sadece (Portfoy, Karsiliksiz, Takipte) olanlar gelebilir
     * </p>
     * 
     * @param requestStatus hedef statü
     * @return List kabul edilebilecek statü listesi
     * @author dumlupinar
     * @since 12.Ocak.2009 Ptesi
     */
    public List<ChequeStatus> possibleStatusForCustomer(ChequeStatus requestStatus) {
    	List<ChequeStatus> status = new ArrayList<ChequeStatus>();
    	
		switch (requestStatus) {
		
			case Portfoy:
				status.add(ChequeStatus.Ciro);
				status.add(ChequeStatus.BankaTahsilatta);
				status.add(ChequeStatus.BankaTeminat);
				status.add(ChequeStatus.Karsiliksiz);
				status.add(ChequeStatus.Takipte);
				break;

			case Ciro:
			case BankaTeminat:
			case BankaTahsilatta:
				status.add(ChequeStatus.Portfoy);
				break;
				
			case KasaTahsilat:
				status.add(ChequeStatus.Portfoy);
				status.add(ChequeStatus.Karsiliksiz);
				status.add(ChequeStatus.Takipte);
				break;
				
			case BankaTahsilEdildi:
				status.add(ChequeStatus.BankaTahsilatta);
				break;
				
			case Takipte:
			case Karsiliksiz:
				status.add(ChequeStatus.Portfoy);
				status.add(ChequeStatus.Ciro);
				status.add(ChequeStatus.BankaTahsilatta);
				status.add(ChequeStatus.BankaTeminat);
				break;
				
			case Supheli:
				status.add(ChequeStatus.Karsiliksiz);
				status.add(ChequeStatus.Takipte);
				break;
				
			case Kapandi:
				status.add(ChequeStatus.Ciro);
				status.add(ChequeStatus.KasaTahsilat);
				status.add(ChequeStatus.BankaTahsilEdildi);
				status.add(ChequeStatus.Supheli);
				break;
				
		}
    	
		if (status.size() == 0) status = null;
		
    	return status;
    }

    /**
     * Bu metod, firma çek/senetleri statü değişikliklerinde, parametre olarak verilen requestStatus
     * bilgisine göre mümkün statüleri verir.
     * 
     * <p>
     * <b><u>Örnek;</u></b>
     *      Statüsünü Portoy yapacağımız çek/senet ler seçilirken, 
     *      seçim listesine statüsü sadece (Cikis, BankaTeminat) olanlar gelebilir
     * </p>
     * <p>
     * <b><u>Örnek;</u></b>
     *      Kapandi olarak işaretlenecek çek/senet ler seçilirken, 
     *      seçim listesine statüsü sadece (Portfoy, KasaOdeme, BankaOdeme) olanlar gelebilir
     * </p>
     * 
     * @param requestStatus hedef statü
     * @return List kabul edilebilecek statü listesi
     * @author dumlupinar
     * @since 12.Ocak.2009 Ptesi
     */
    public List<ChequeStatus> possibleStatusForFirm(ChequeStatus requestStatus) {
    	List<ChequeStatus> status = new ArrayList<ChequeStatus>();
    	
		switch (requestStatus) {
		
			case Portfoy:
				status.add(ChequeStatus.Cikis);
				status.add(ChequeStatus.BankaTeminat);
				break;
				
			case Cikis:
			case BankaTeminat:
				status.add(ChequeStatus.Portfoy);
				break;
				
			case KasaOdeme:
			case BankaOdeme:
				status.add(ChequeStatus.Cikis);
				break;
				
			case Kapandi:
				status.add(ChequeStatus.Portfoy);
				status.add(ChequeStatus.KasaOdeme);
				status.add(ChequeStatus.BankaOdeme);
				break;
				
		}
    		
		if (status.size() == 0) status = null;

		return status;
    }

	public void selectedCheque(int rowKey) {
    	
		Long id = null;
    	
		try {
    		id = (Long) chequeList.get(rowKey).getId();
    	} catch (Exception e) {
    		e.printStackTrace();
    	}
    	
    	if (id != null) {
    		cheque= entityManager.find(Cheque.class, id);
    	}
    	
    	if (callerObserveString != null) {
    		events.raiseTransactionSuccessEvent(callerObserveString, cheque);
    		log.debug("Event raised!");
    	} else {
    		log.debug("Event did not raise!");
    	}

    }

	public void setCallerObserveString(String callerString) {
		this.callerObserveString = callerString;
	}
	
    public String getSerialNo() {
        return serialNo;
    }

    public void setSerialNo(String serialNo) {
        this.serialNo = serialNo;
    }

    public String getReferenceNo() {
        return referenceNo;
    }

    public void setReferenceNo(String referenceNo) {
        this.referenceNo = referenceNo;
    }

	public Date getEndDate() {
		return endDate;
	}

	public void setEndDate(Date endDate) {
		this.endDate = endDate;
	}

	public ChequeStatus getLastStatus() {
		return lastStatus;
	}

	public void setLastStatus(ChequeStatus lastStatus) {
		this.lastStatus = lastStatus;
	}
	
	public ChequeStatus getPreviousStatus() {
		return previousStatus;
	}

	public void setPreviousStatus(ChequeStatus previousStatus) {
		this.previousStatus = previousStatus;
	}
	
	public ChequeStatus getTargetStatus() {
		return targetStatus;
	}

	public void setTargetStatus(ChequeStatus targetStatus) {
		this.targetStatus = targetStatus;
	}

	public String getChequeOwner() {
		return chequeOwner;
	}

	public void setChequeOwner(String chequeOwner) {
		this.chequeOwner = chequeOwner;
	}
	
	public String getBankName() {
		return bankName;
	}

	public void setBankName(String bankName) {
		this.bankName = bankName;
	}

	public String getBankBranch() {
		return bankBranch;
	}

	public void setBankBranch(String bankBranch) {
		this.bankBranch = bankBranch;
	}

	public String getBankAccount() {
		return bankAccount;
	}

	public void setBankAccount(String bankAccount) {
		this.bankAccount = bankAccount;
	}

	public Cheque getCheque() {
		return cheque;
	}

	public void setCheque(Cheque cheque) {
		this.cheque = cheque;
	}

	public Boolean getClientCheque() {
		return clientCheque;
	}

	public void setClientCheque(Boolean clientCheque) {
		this.clientCheque = clientCheque;
	}

	public Contact getContact() {
		return contact;
	}

	public void setContact(Contact contact) {
		this.contact = contact;
	}

	public Date getBeginDate() {
		return beginDate;
	}

	public void setBeginDate(Date beginDate) {
		this.beginDate = beginDate;
	}

	public Date getIssueDate() {
		return issueDate;
	}

	public void setIssueDate(Date issueDate) {
		this.issueDate = issueDate;
	}

	public List<Cheque> getChequeList() {
		return chequeList;
	}

	public void setChequeList(List<Cheque> chequeList) {
		this.chequeList = chequeList;
	}
    
    @Remove @Destroy
    public void destroy() {

    }

	public Date getHistoryBeginDate() {
		return historyBeginDate;
	}

	public void setHistoryBeginDate(Date historyBeginDate) {
		this.historyBeginDate = historyBeginDate;
	}

	public Date getHistoryEndDate() {
		return historyEndDate;
	}

	public void setHistoryEndDate(Date historyEndDate) {
		this.historyEndDate = historyEndDate;
	}

    public BankAccount getBankAccountId() {
        return bankAccountId;
    }

    public void setBankAccountId(BankAccount bankAccountId) {
        this.bankAccountId = bankAccountId;
    }
}
