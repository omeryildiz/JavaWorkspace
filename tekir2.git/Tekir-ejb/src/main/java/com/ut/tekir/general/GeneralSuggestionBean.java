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

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import javax.ejb.Remove;
import javax.ejb.Stateful;
import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import javax.persistence.PersistenceContextType;
import javax.persistence.Query;
import javax.persistence.TemporalType;

import org.jboss.seam.annotations.AutoCreate;
import org.jboss.seam.annotations.Destroy;
import org.jboss.seam.annotations.In;
import org.jboss.seam.annotations.Logger;
import org.jboss.seam.annotations.Name;
import org.jboss.seam.log.Log;

import com.ut.tekir.entities.Account;
import com.ut.tekir.entities.AccountOwnerType;
import com.ut.tekir.entities.Bank;
import com.ut.tekir.entities.BankAccount;
import com.ut.tekir.entities.BankBranch;
import com.ut.tekir.entities.Cheque;
import com.ut.tekir.entities.ChequeHistory;
import com.ut.tekir.entities.ChequeStub;
import com.ut.tekir.entities.City;
import com.ut.tekir.entities.Contact;
import com.ut.tekir.entities.Country;
import com.ut.tekir.entities.Currency;
import com.ut.tekir.entities.CurrencyRate;
import com.ut.tekir.entities.Customs;
import com.ut.tekir.entities.Department;
import com.ut.tekir.entities.DocumentType;
import com.ut.tekir.entities.ExpenseType;
import com.ut.tekir.entities.WorkBunch;
import com.ut.tekir.entities.Foundation;
import com.ut.tekir.entities.Interest;
import com.ut.tekir.entities.MetricalConvert;
import com.ut.tekir.entities.Option;
import com.ut.tekir.entities.Organization;
import com.ut.tekir.entities.OrganizationLevel;
import com.ut.tekir.entities.PaymentActionType;
import com.ut.tekir.entities.PaymentCommission;
import com.ut.tekir.entities.PaymentPlan;
import com.ut.tekir.entities.Portfolio;
import com.ut.tekir.entities.Pos;
import com.ut.tekir.entities.PosCommision;
import com.ut.tekir.entities.PriceItemDetail;
import com.ut.tekir.entities.PriceList;
import com.ut.tekir.entities.Product;
import com.ut.tekir.entities.ProductGroup;
import com.ut.tekir.entities.ProductTax;
import com.ut.tekir.entities.ProductType;
import com.ut.tekir.entities.PromissoryNote;
import com.ut.tekir.entities.PromissoryNoteHistory;
import com.ut.tekir.entities.Province;
import com.ut.tekir.entities.Tax;
import com.ut.tekir.entities.TaxRate;
import com.ut.tekir.entities.TaxReturnType;
import com.ut.tekir.entities.TaxType;
import com.ut.tekir.entities.Unit;
import com.ut.tekir.entities.User;
import com.ut.tekir.entities.Warehouse;
import com.ut.tekir.finance.AdvanceType;
import com.ut.tekir.framework.BaseConsts;
import com.ut.tekir.framework.OptionManager;
import com.ut.tekir.framework.TekirException;

/**
 *
 * @author haky
 */
@Stateful
@AutoCreate
@Name("generalSuggestion")
public class GeneralSuggestionBean implements GeneralSuggestion {

    @Logger
    private Log log;
    @PersistenceContext(type = PersistenceContextType.EXTENDED)
    private EntityManager em;

    @In
    OptionManager optionManager;
    
    @In(required=false)
    User activeUser;

    @SuppressWarnings("unchecked")
	public List<Country> suggestCountry(Object event) {
        String pref = event.toString();

        log.debug("suggestCountry Req : {0}", pref);

        return em.createQuery("select c from Country c where ( c.code like :code or c.name like :name ) and active = 1")
                .setParameter("code", pref + "%")
                .setParameter("name", "%" + pref + "%")
                .setMaxResults(30)
                .setHint("org.hibernate.cacheable", true)
                .getResultList();
    }
/*
    @SuppressWarnings("unchecked")
    public List<DocumentType> getAllDocumentType(){
    	return em.createQuery("Select d from DocumentType d")
    			.getResultList();
    }
  */  
    
    @SuppressWarnings("unchecked")
    public List<TaxReturnType> getTaxReturnTypes(){
    	return em.createQuery("select c from TaxReturnType c")
    						.getResultList();
    }
    
    @SuppressWarnings("unchecked")
	public List<City> suggestCity(Object event) {
        String pref = event.toString();

        log.debug("suggestCity Req : {0}", pref);

        return em.createQuery("select c from City c where ( c.code like :code or c.name like :name ) and active = 1")
                .setParameter("code", pref + "%")
                .setParameter("name", "%" + pref + "%")
                .setMaxResults(30)
                .setHint("org.hibernate.cacheable", true)
                .getResultList();
    }

    
    @SuppressWarnings("unchecked")
    public User findUser(Long id){
    	User us =null;
    	
    	try{
    		 List<User> users= em.createQuery("select c from User c where c.id =:clerkid")
    		.setParameter("clerkid", id)
    		.getResultList();
    		 
    		 if(users!=null && users.size()>0){
    			 us=users.get(0);
    		 }
    		 
    	}catch (Exception e) {
			log.debug("Bir hata oluştu :{0}",e);
		}
    	return us;
    }
    
    public String findCurrencyDecimalCode(String currencyCode){
    	String result = "";
    	try{
    		result = (String)em.createQuery("select c.decimalCode from Currency c where c.code =:currCode")
								    		.setParameter("currCode", currencyCode)
								    		.getSingleResult();
    		 
    		 if (result == null){
    			 result = "";
    		 }
    	}catch (Exception e) {
			log.debug("Bir hata oluştu :{0}",e);
		}
    	return result;
    }
    
    
    @SuppressWarnings("unchecked")
    public List<PriceItemDetail> getPriceItemDetail() {
    	log.debug("Querying Product List..." );

    	return em.createQuery("select pd from PriceItemDetail pd " )
    						  .getResultList();
    }
    
    @SuppressWarnings("unchecked")
	public List<Portfolio> suggestPortfolio(Object event){
        String pref = event.toString();

        log.debug("suggest portfolio  : {0}", pref );
        
        return em.createQuery("select c from Portfolio c where ( c.code like :code or c.name like :name ) and active = 1" )
                .setParameter("code", pref + "%")
                .setParameter("name", "%" + pref + "%")
                .setMaxResults(30)
                .setHint("org.hibernate.cacheable", true)
                .getResultList();
    }
    
    @SuppressWarnings("unchecked")
	public List<Account> suggestAccount(Object event){
        String pref = event.toString();

        log.debug("suggestAccount Req : {0}", pref );
        
        return em.createQuery("select c from Account c where ( c.code like :code or c.name like :name ) and active = 1" )
                .setParameter("code", pref + "%")
                .setParameter("name", "%" + pref + "%") 
                .setMaxResults(30)
                .setHint("org.hibernate.cacheable", true)
                .getResultList();
    }
    
    @SuppressWarnings("unchecked")
	public List<Tax> suggestTax(Object event){
        String pref = event.toString();

        log.debug("suggestTax Req : {0}", pref );
        
        return em.createQuery("select c from Tax c where ( c.code like :code or c.name like :name ) and active = 1" )
                .setParameter("code", pref + "%")
                .setParameter("name", "%" + pref + "%")  
                .setMaxResults(10)
                .setHint("org.hibernate.cacheable", true)
                .getResultList();
    }
    
    @SuppressWarnings("unchecked")
	public List<Unit> suggestUnit(Object event){
        String pref = event.toString();

        log.debug("suggestUnit Req : {0}", pref );
        
        return em.createQuery("select c from Unit c where ( c.code like :code or c.name like :name ) and active = 1" )
                .setParameter("code", pref + "%")
                .setParameter("name", "%" + pref + "%")   
                .setMaxResults(10)
                .setHint("org.hibernate.cacheable", true)
                .getResultList();
    }

    @SuppressWarnings("unchecked")
	public List<Unit> getUnitList(){
        log.debug("Unit List " );
        
        return em.createQuery("select c from Unit c where active = 1" )
                .setHint("org.hibernate.cacheable", true)
                .getResultList();
    }

    @SuppressWarnings("unchecked")
    public List<PriceList> getPriceListList() {
    	log.debug("Querying Price List.." );
    	
    	return em.createQuery("select c from PriceList c where active = 1" )
    	.setHint("org.hibernate.cacheable", false)
    	.getResultList();
    }

    @SuppressWarnings("unchecked")
    public List<PaymentCommission> getPaymentCommissionList() {
    	log.debug("Querying paymentCommissionList List.." );

    	return em.createQuery("select c from PaymentCommission c where active = 1 order by weight" )
    	.setHint("org.hibernate.cacheable", false)
    	.getResultList();
    }

    @SuppressWarnings("unchecked")
    public List<PaymentActionType> getPaymentActionTypeList() {
    	log.debug("Querying paymentCommissionList List.." );

    	return em.createQuery("select c from PaymentActionType c where active = 1 order by weight" )
    	.setHint("org.hibernate.cacheable", false)
    	.getResultList();
    }

    public PaymentActionType findPaymentActionType(String aCode) {
    	log.debug("Querying paymentActionType with code :{0}", aCode);
    	return (PaymentActionType)em.createQuery("select pat from PaymentActionType pat where " +
    											 "active = 1 and " +
    											 "code =:code")
    											 .setParameter("code", aCode)
    											 .getSingleResult();
    }

    @SuppressWarnings("unchecked")
    public List<ProductGroup> getProductGroupList() {
    	log.debug("Querying Product Group List.." );
    	
    	return em.createQuery("select pg from ProductGroup pg where " +
    						  "active = 1 " +
    						  "order by pg.code")
    	.setHint("org.hibernate.cacheable", false)
    	.getResultList();
    }
    
    @SuppressWarnings("unchecked")
    public List<Product> getProductList() {
    	log.debug("Querying Product List.." );

    	return em.createQuery("select p from Product p where " +
                              "active = 1 and " +
                              "p.productType = :productType")
                              .setParameter("productType", ProductType.Product)
                              .getResultList();
    }

    @SuppressWarnings("unchecked")
    public List<Product> getProductList(Long groupId) {
    	log.debug("Querying Product List.." );

        String groupQuery = "";
	    	if(groupId != null) {
	    		groupQuery = "p.group.id=" + groupId + " and ";
	    	} else {
	    		groupQuery = "p.group.id is null and ";
	    	}

    	return em.createQuery("select p from Product p where " +
                              "active = 1 and " +
                              //"p.group.id =:groupId and "+
                              groupQuery+
                              " p.productType = :productType")
                              //.setParameter("groupId",groupId )
                              .setParameter("productType", ProductType.Product)
                              .getResultList();
    }

    @SuppressWarnings("unchecked")
	public List<Tax> getTaxList(){
        log.debug("Tax List " );
        
        return em.createQuery("select c from Tax c where active = 1 and type=:type" )
                .setParameter("type", TaxType.VAT)
                .setHint("org.hibernate.cacheable", true)
                .getResultList();
    }

    // KDV harici diger vergileri dodurur.
    @SuppressWarnings("unchecked")
	public List<Tax> getTax2List(){
        log.debug("Tax2 List " );

        return em.createQuery("select c from Tax c where active = 1 and type != :type" )
                .setParameter("type", TaxType.VAT)
                .setHint("org.hibernate.cacheable", true)
                .getResultList();
    }
    
    @SuppressWarnings("unchecked")
	public List<Account> getAccountList(){
        log.debug("Account List " );
        
        if( activeUser != null ){
        	if( activeUser.getAccount() != null ){
        		List<Account> ls = new ArrayList<Account>();
        		ls.add(activeUser.getAccount());
        		return ls;
        	}
        }
        
        return em.createQuery("select c from Account c where active = 1" )
                .setHint("org.hibernate.cacheable", true)
                .getResultList();
    }

    @SuppressWarnings("unchecked")
    public Account findAccount(String anAccountCode) {
    	log.debug("Querying Acount with code :{0}", anAccountCode);
    	Account result = null;
    	try {
    		List<Account> accountList = em.createQuery("select ac from Account ac where " +
    										   "active = 1 and " +
    										   "ac.code = :code")
									    		.setParameter("code", anAccountCode)
									    		.getResultList();

    		if (accountList != null && accountList.size() >0 ) {
    			result = accountList.get(0);
    		}
		} catch (Exception e) {
			log.error("An error occured when querying account. Reason :{0}", e);
		}
    	return result;
    }

    @SuppressWarnings("unchecked")
    public Warehouse findWarehouse(String aWarehouseCode) {
    	log.debug("Querying Warehouse with code :{0}", aWarehouseCode);
    	Warehouse result = null;
    	try {
    		List<Warehouse> warehouseList = em.createQuery("select wh from Warehouse wh where " +
    													   "active = 1 and " +
    													   "wh.code = :code")
    													   .setParameter("code", aWarehouseCode)
    													   .getResultList();
    		
    		if (warehouseList != null && warehouseList.size() >0 ) {
    			result = warehouseList.get(0);
    		}
    	} catch (Exception e) {
    		log.error("An error occured when querying warehouse. Reason :{0}", e);
    	}
    	return result;
    }

    @SuppressWarnings("unchecked")
    public List<Account> getPosList() {
    	log.debug("Querying Pos List..." );
    	
    	return em.createQuery("select p from Pos p where active = 1" )
    	.setHint("org.hibernate.cacheable", true)
    	.getResultList();
    }

    @SuppressWarnings("unchecked")
    public List<Foundation> getFoundationList() {
    	log.debug("Querying Foundation List..." );
    	
    	return em.createQuery("select f from Foundation f" )
					    	.setHint("org.hibernate.cacheable", true)
					    	.getResultList();
    }

    @SuppressWarnings("unchecked")
    public List<Product> getProductList(ProductType type) {
    	log.debug("Querying Product List..." );
    	
    	return em.createQuery("select pr from Product pr where " +
    						  "pr.active = 1 and " +
    						  "pr.productType= :productType")
    						  .setParameter("productType", type)
    						  .getResultList();
    }

    public List<Product> getDiscountList() {
    	return getProductList(ProductType.Discount);
    }

    public List<Product> getExpenseList() {
    	return getProductList(ProductType.Expense);
    }

    public List<Product> getDiscountAdditionList() {
    	return getProductList(ProductType.DiscountAddition);
    }
    
    public List<Product> getExpenseAdditionList() {
    	return getProductList(ProductType.ExpenseAddition);
    }
    
    @SuppressWarnings("unchecked")
    public Pos findPos(String aPosCode) {
    	log.debug("Querying Pos with code :{0}", aPosCode);
    	Pos result = null;
    	try {
    		List<Pos> posList = em.createQuery("select p from Pos p where " +
    										   "active = 1 and " +
    										   "p.code = :code")
									    		.setParameter("code", aPosCode)
									    		.getResultList();

    		if (posList != null && posList.size() >0 ) {
    			result = posList.get(0);
    		}
		} catch (Exception e) {
			log.error("An error occured when querying pos. Reason :{0}", e);
		}
    	return result;
    }

    @SuppressWarnings("unchecked")
    public Pos findPos(Long aPosId) {
    	log.debug("Querying Pos with id :{0}", aPosId);
    	Pos result = null;
    	try {
    		List<Pos> posList = em.createQuery("select p from Pos p where " +
    				"active = 1 and " +
    				"p.id = :id")
		    		.setParameter("id", aPosId)
		    		.getResultList();
    		
    		if (posList != null && posList.size() >0 ) {
    			result = posList.get(0);
    		}
    	} catch (Exception e) {
    		log.error("An error occured when querying pos. Reason :{0}", e);
    	}
    	return result;
    }

    @SuppressWarnings("unchecked")
    public PosCommision findPosCommision(Pos aPos, Date aDate) throws Exception {
    	log.debug("Querying Pos Commisions with pos code :{0} and date :{1}", aPos.getCode(), aDate);
    	PosCommision result = null;
		List<PosCommision> posCommisionList = em.createQuery("select pc from PosCommision pc where " +
															 "active = 1 and " +
															 "pc.pos = :pos and " +
															 "pc.startDate <= :startDate " +
															 "order by pc.startDate desc")
															 .setParameter("pos", aPos)
															 .setParameter("startDate", aDate)
															 .getResultList();

		if (posCommisionList != null && posCommisionList.size() > 0 ) {
			result = posCommisionList.get(0);
		} else  {
			throw new Exception(aPos.getCode() + " kodlu pos için " +
					 			aDate.toString() + " tarihine uygun bir komisyon tanımı bulunamamıştır.");
		}
		return result;
    }
    
    @SuppressWarnings("unchecked")
	public List<Currency> getCurrencyList(){
        log.debug("Currency List " );
        
        return em.createQuery("select c from Currency c where active = 1" )
                .setHint("org.hibernate.cacheable", true)
                .getResultList();
    }

    @SuppressWarnings("unchecked")
	public List<Currency> getAllCurrencyList(){
        log.debug("All Currency List " );

        return em.createQuery("select c from Currency c order by c.name" )
                .setHint("org.hibernate.cacheable", true)
                .getResultList();
    }
    
    @SuppressWarnings("unchecked")
	public List<Warehouse> getWarehouseList(){
        log.debug("Warehouse List " );
        
        if( activeUser != null ){
        	if( activeUser.getWarehouse() != null ){
        		List<Warehouse> ls = new ArrayList<Warehouse>();
        		ls.add(activeUser.getWarehouse());
        		return ls;
        	}
        }
        
        return em.createQuery("select c from Warehouse c where active = 1" )
                .setHint("org.hibernate.cacheable", true)
                .getResultList();
    }
    
    @SuppressWarnings("unchecked")
	public List<Portfolio> getPortfolioList(){
        log.debug("Portfolio List " );
        
        return em.createQuery("select c from Portfolio c where active = 1" )
                .setHint("org.hibernate.cacheable", true)
                .getResultList();
    }
    
    /**
     *  banka listesi (aktif olanlar)
     */
    @SuppressWarnings("unchecked")
	public List<Bank> getBankList(){
        log.debug("Bank List " );
        
        return em.createQuery("select c from Bank c where active = 1 order by weight asc, code asc, name asc" )
                .setHint("org.hibernate.cacheable", true)
                .getResultList();
    }
    
    /**
     *  firmanin banka listesi
     */
    @SuppressWarnings("unchecked")
	public List<Bank> getOurBankList(){
        log.debug("Our Bank List " );
        
        return em.createQuery("select distinct b.bankBranch.bank from BankAccount b " +
        		" where b.active = 1 " +
        		" and b.bankBranch.bank.active = 1 " +
        		" and b.accountOwnerType = :ownerType " )
        		//+ " order by b.weight asc, b.code asc, b.name asc" )
        		.setParameter("ownerType", AccountOwnerType.Mine)
                .setHint("org.hibernate.cacheable", true)
                .getResultList();
    }

    /**
     *  tum banka listesi (pasif de dahil)
     */
    @SuppressWarnings("unchecked")
	public List<Bank> getAllBankList(){
        log.debug("All Bank List " );

        return em.createQuery("select c from Bank c order by weight asc, code asc, name asc" )
                .setHint("org.hibernate.cacheable", true)
                .getResultList();
    }

    /**
     *  sube listesi
     */
    @SuppressWarnings("unchecked")
	public List<BankBranch> getBankBranchList(){
        log.debug("BankBranch List " );
        
        return em.createQuery("select c from BankBranch c where c.active = 1 and c.bank.active = 1" )
                .setHint("org.hibernate.cacheable", true)
                .getResultList();
    }
    
    /**
     * sube listesi bankaya gore
     */
    @SuppressWarnings("unchecked")
	public List<BankBranch> getBankBranches(Bank bank){
    	log.debug("BankBranches" );
    	
    	if (bank != null) {
    		Long bank_id = bank.getId();
	        return em.createQuery("select c from BankBranch c where " +
	        					  "c.active = 1 AND " +
	        					  "c.bank.id = :bank and " +
	        					  "c.bank.active = 1 " )
	        		.setParameter("bank", bank_id)
	                .setHint("org.hibernate.cacheable", true)
	                .getResultList();
    	} else { 
    		return em.createQuery("select c from BankBranch c where " +
    							  "c.active = 1 and " +
    							  "c.bank.active = 1 " )
                 .setHint("org.hibernate.cacheable", true)
                 .getResultList();
    	}
    }
    
    /**
     * firmanın subeleri bankaya gore
     */
    @SuppressWarnings("unchecked")
	public List<BankBranch> getOurBankBranches(Bank bank){
    	log.debug("BankBranches" );
    	
    	if (bank != null) {
    		Long bank_id = bank.getId();
	        return em.createQuery("select distinct c.bankBranch from BankAccount c " +
	        					  " where c.active = 1 " +
	        					  " and c.bankBranch.active = 1 " +
	        					  " and c.bankBranch.bank.id = :bank " +
	        					  " and c.accountOwnerType = :ownerType " )
	        		.setParameter("bank", bank_id)
	        		.setParameter("ownerType", AccountOwnerType.Mine)
	                .setHint("org.hibernate.cacheable", true)
	                .getResultList();
    	} else { 
    		return em.createQuery("select distinct c.bankBranch from BankAccount c " +
    							  " where c.active = 1 " +
	        					  " and c.bankBranch.active = 1 " +
			  					  " and c.accountOwnerType = :ownerType " )
			  		.setParameter("ownerType", AccountOwnerType.Mine)
			  		.setHint("org.hibernate.cacheable", true)
			  		.getResultList();
    	}
    }

    /**
     *  tum kayitli banka hesaplari. Firma ve musteri
     * 
     */
    @SuppressWarnings("unchecked")
	public List<BankAccount> getBankAccountList(){
        log.debug("BankAccount List " );
        
        return em.createQuery("select c from BankAccount c where active = 1" )
                .setHint("org.hibernate.cacheable", true)
                .getResultList();
    }    
    
    /**
     *  firmanin hesaplari
     * 
     */
    @SuppressWarnings("unchecked")
	public List<BankAccount> getOurBankAccountList(){
        log.debug("BankAccount List " );
        
        return em.createQuery("select c from BankAccount c " +
        		" where active = 1" +
        		" AND accountOwnerType = :ownerType " )
        		.setParameter("ownerType", AccountOwnerType.Mine)
                .setHint("org.hibernate.cacheable", true)
                .getResultList();
    }   
    
    /**
     * tum kayitli hesaplar subeye gore, Firma ve musteri icin
     *  
     */
    @SuppressWarnings("unchecked")
	public List<BankAccount> getBankAccounts(BankBranch bankBranch){
    	log.debug("BankAccounts" );
    	
    	// FIXME: Cari kart hesapları içinde kullanım var ise geliştirmek veya ayırmak gerekebilir.
    	if (bankBranch != null) {
    		Long bankBranch_id = bankBranch.getId();
	        return em.createQuery("select c from BankAccount c where active = 1 " +
	        		" AND bankBranch.id = :bankBranch " )
	        		.setParameter("bankBranch", bankBranch_id)
	                .setHint("org.hibernate.cacheable", true)
	                .getResultList();
    	} else {
    		return em.createQuery("select c from BankAccount c where active = 1" )
    				.setHint("org.hibernate.cacheable", true)
    				.getResultList();
    	}
    }

    /**
     *
     * yerel doviz tipinde ve firmanin kendine ait banka hesaplarını dondurur.
     *
     */
    @SuppressWarnings("unchecked")
	public List<BankAccount> getLocalBankAccounts(BankBranch bankBranch){
    	log.debug("BankAccounts" );

    	if (bankBranch != null) {
    		Long bankBranch_id = bankBranch.getId();
	        return em.createQuery("select c from BankAccount c where active = 1 " +
	        		" and c.currency = :localCurrency " +
	        		" AND bankBranch.id = :bankBranch " +
	        		" AND accountOwnerType = :ownerType " )
    				.setParameter("ownerType", AccountOwnerType.Mine)
                    .setParameter("localCurrency", BaseConsts.SYSTEM_CURRENCY_CODE)
	        		.setParameter("bankBranch", bankBranch_id)
	                .setHint("org.hibernate.cacheable", true)
	                .getResultList();
    	} else {
    		return em.createQuery("select c from BankAccount c where active = 1 and c.currency = :localCurrency" + 
		    		" AND accountOwnerType = :ownerType " )
					.setParameter("ownerType", AccountOwnerType.Mine)
                    .setParameter("localCurrency", BaseConsts.SYSTEM_CURRENCY_CODE)
                    .setHint("org.hibernate.cacheable", true)
                    .getResultList();
    	}
    }
    
    /**
    *
    * firmanin kendine ait (tüm döviz türleri) banka hesaplarını dondurur.
    *
    */
   @SuppressWarnings("unchecked")
	public List<BankAccount> getOurBankAccounts(BankBranch bankBranch){
   	log.debug("BankAccounts" );

   	if (bankBranch != null) {
   		Long bankBranch_id = bankBranch.getId();
	        return em.createQuery("select c from BankAccount c " +
	        		" where active = 1 " +
	        		" AND bankBranch.id = :bankBranch " +
	        		" AND accountOwnerType = :ownerType " )
	        		.setParameter("ownerType", AccountOwnerType.Mine)
	        		.setParameter("bankBranch", bankBranch_id)
	                .setHint("org.hibernate.cacheable", true)
	                .getResultList();
   	} else {
   		return em.createQuery("select c from BankAccount c " +
   					" where active = 1 " +
		    		" AND accountOwnerType = :ownerType " )
					.setParameter("ownerType", AccountOwnerType.Mine)
					.setHint("org.hibernate.cacheable", true)
					.getResultList();
   	}
   }
   
    
  	@SuppressWarnings("unchecked")
	public List<ChequeHistory> getChequeHistory(Cheque cheque) {
    	log.debug("ChequeHistory" );
    	
    	if (cheque != null) {
    		Long chequeHistory_id = cheque.getId();
	        return em.createQuery("select ch from ChequeHistory ch where owner.id = :chequeHistory_id" )
	        		.setParameter("chequeHistory_id", chequeHistory_id)
	                .setHint("org.hibernate.cacheable", true)
	                .getResultList();
    	} else {
    		return null;
    	}
    }
	
	@SuppressWarnings("unchecked")	
	public List<MetricalConvert> findMetricalConvert( String mainUnit){
		
		return em.createQuery("select m from MetricalConvert m where m.mainUnit = :mainUnit or m.changeUnit = :mainUnit ")
										.setParameter("mainUnit", mainUnit)
										.getResultList();
	}
	
	public MetricalConvert findSameMetricalConvert( String mainUnit, String changeUnit){
		
		MetricalConvert result = null;
		result = (MetricalConvert) em.createQuery("select m from MetricalConvert m where m.mainUnit = :mainUnit and m.changeUnit = :changeUnit ")
										.setParameter("mainUnit", mainUnit)
										.setParameter("changeUnit", changeUnit)
										.setMaxResults(1)
										.getSingleResult();
		return result;
	}
	
	@SuppressWarnings("unchecked")
	public List<PromissoryNoteHistory> getPromissoryHistory(PromissoryNote promissoryNote) {
    	log.debug("PromissoryNoteHistory" );
    	
    	if (promissoryNote != null) {
    		Long promissoryHistory_id = promissoryNote.getId();
	        return em.createQuery("select pn from PromissoryNoteHistory pn where owner.id = :promissoryHistory_id" )
	        		.setParameter("promissoryHistory_id", promissoryHistory_id)
	                .setHint("org.hibernate.cacheable", true)
	                .getResultList();
    	} else {
    		return null;
    	}
    }
	
	public List<AccountOwnerType> getAccountOwnerType(){
		List<AccountOwnerType> ownerTypes = new ArrayList<AccountOwnerType>();
			ownerTypes.add(AccountOwnerType.Mine);
			ownerTypes.add(AccountOwnerType.Customer);
		
		return ownerTypes; 
	}
	
    
	public Product findProductWithBarcode(String barcode) {
		log.info("Querying product with barcode :{0}", barcode);
		
		Product result = null;
		try {
			result = (Product) em.createQuery("select p from Product p where  " +
												"p.barcode1 like :barcode or " +
												"p.barcode2 like :barcode or " +
												"p.barcode3 like :barcode")
												.setParameter("barcode", barcode)
												.getSingleResult();

			log.info("Found product :{0}", result.getName());
		} catch (Exception e) {
			log.error("Unable to find product/service with barcode = {0}",barcode);
		}
		return result;
	}
	
	public Product findProductWithId(Integer id) {
		log.info("Querying product with id :{0}", id);
		
		Product result = null;
		try {
			result = (Product) em.createQuery("select p from Product p where  " +
												"p.id =:id")
												.setParameter("id", id)
												.getSingleResult();
			log.info("Found product :{0}", result.getName());
		} catch (Exception e) {
			log.error("Unable to find product/service with id = {0}",id);
		}
		return result;
	}
	

    @SuppressWarnings("unchecked")
	public List<Department> getDepartmentList() {
        log.debug("Department List " );
        
        return em.createQuery("select c from Department c where active = 1" )
                .setHint("org.hibernate.cacheable", true)
                .getResultList();
    } 
    
	@SuppressWarnings("unchecked")
	public List<ChequeStub> getChequeStubList() {
		
		
		return em.createQuery("select cs from ChequeStub cs")
		.setHint("org.hibernate.cacheable", true)
        .getResultList();
	}
	
	
	@SuppressWarnings("unchecked")
	public List<ExpenseType> getExpenseTypeList(){
        log.debug("ExpenseType List " );
        
        return em.createQuery("select c from ExpenseType c where active = 1" )
                .setHint("org.hibernate.cacheable", true)
                .getResultList();
    }
	
	public List<City> getCityList(){
		return getCityList(null);
	}
	
	@SuppressWarnings("unchecked")
	public List<City> getCityList(String country) {
	    log.info("City list for country : #0", country);
	    
	    StringBuilder queryString = new StringBuilder();
	    queryString.append("select c from City c where active = 1 ");
	    
	    if(country != null){
	    	queryString.append(" and country=:country ");
	    }
	    
	    queryString.append(" order by weight ");
	    
	    Query query = em.createQuery(queryString.toString());
	    
	    if (country != null){
	    	query.setParameter("country", country);
	    }
	    
	    return query.setHint("org.hibernate.cacheable", true).getResultList();
        
	}
	
	@SuppressWarnings("unchecked")
	public List<Interest> getInterestList() {
		log.debug("Interest Listesi " );
		
		return em.createQuery("select c from Interest c where active = 1" )
				.setHint("org.hibernate.cacheable", true)
				.getResultList();
	}
	
	@SuppressWarnings("unchecked")
	public List<Country> getCountryList(){
        log.debug("Country List " );
        
        return em.createQuery("select c from Country c where active = 1 order by weight" )
                .setHint("org.hibernate.cacheable", true)
                .getResultList();
    }

    @SuppressWarnings("unchecked")
	public List<Country> getAllCountryList(){
        log.debug("All Country List " );

        return em.createQuery("select c from Country c order by c.name" )
                .setHint("org.hibernate.cacheable", true)
                .getResultList();
    }
     
	 @SuppressWarnings("unchecked")
		public List<PaymentPlan> getPaymentPlanList() {
			log.debug("PaymentPlan Listesi " );
			
			return em.createQuery("select pp from PaymentPlan pp where pp.active = 1" )
					.setHint("org.hibernate.cacheable", true)
					.getResultList();
		}
	 
	/**
	 * Verilen parametrelere göre vergi oranı bulur
	 * 
	 * @param taxCode		vergi kodu  
	 * @param beginDate		başlama tarihi
	 * @param endDate		bitiş tarihi
	 * @return BigDecimal	vergi oranı
	 * @author dumlupinar
	 */
	public BigDecimal findTaxRate(String taxCode, Date beginDate, Date endDate) {
		BigDecimal result = BigDecimal.ZERO;
		
		if (taxCode != null && taxCode.length() > 0) {
			
			try {
				
				String statement = "select r.rate from Tax t " +
									 "left join t.rates r " +
									 "where t.active = :active " +
									 "  and t.code = :code ";
				
				if (beginDate != null) statement += " and r.beginDate >= :beginDate ";
				if (endDate != null)   statement += " and r.endDate >= :endDate ";
				
				Query query = em.createQuery(statement);
				query.setParameter("active", Boolean.TRUE);
				query.setParameter("code", taxCode);
				
				if (beginDate != null) query.setParameter("beginDate", beginDate);
				if (endDate != null)   query.setParameter("endDate", endDate);
		
				
				result = (BigDecimal) query.setMaxResults(1).getSingleResult();
				
			} catch (Exception e) {

			}
		
		}
		
		return result;
	}

    /**
     * Returns suitable tax rate. If rate date is 
     * null, today's suitable rate will be returned. 
     * @param rateDate
     * @return result
     */
	public TaxRate findTaxRate(Tax tax,Date rateDate) {
		log.info("findTaxRate() #0", tax.getName());
		TaxRate result = null;
		if (rateDate == null) {
			rateDate = new Date();
		}
		try {
			result = (TaxRate)em.createQuery("select tr from TaxRate tr where " +
											 "tr.tax.id = :taxId and " +
											 "tr.beginDate <= :rateDate and " +
											 "tr.endDate >= :rateDate ")
								.setParameter("taxId", tax.getId())
								.setParameter("rateDate", rateDate)
								.getSingleResult();
		} catch (Exception e) {
			log.error("Hata :", e);
		}
		return result;
	}

	public void findTaxRate(ProductTax productTax, Date rateDate) {
		if (productTax.getRate() == null) productTax.setRate( findTaxRate(productTax.getTax(), rateDate) );
	}
	
    /**
     * Bugune ait kurlar kaydedilmis mi kontrolü yapar
     * @return
     */
	@SuppressWarnings("unchecked")
	public boolean kurKontrol(Date date) {
		List<CurrencyRate> curRateList = null;
		curRateList = em.createQuery("select cr from CurrencyRate cr where date=:date")
				.setParameter("date", date, TemporalType.DATE)
				.getResultList();

			if (curRateList != null && curRateList.size() > 0)
				return true;//bugünün kurları alınmış
		
		return false;
	}

	/**
	 * Checks whether the daily rates has been saved for given date. 
	 * @param aDate 
	 * 		  Control date 
	 * @throws TekirException thrown if daily rates has not been saved.
	 */
	public void checkDailyRatesSaved(Date aDate) throws TekirException {
		if (!kurKontrol(aDate)) {
			throw new TekirException("Günlük kurlar kaydedilmemiş");
		}
	}
	
	/**
	 * Verilen parametrelere göre faiz oranı bulur
	 * 
	 * @param baseDuration 	gün bazında dilim bilgisi; 1a karşılığı 30 veya 1y karşılığı 360 gibi
	 * @param interestCode	faiz kodu
	 * @param currencyCode	para birimi
	 * @param beginDate		başlama tarihi
	 * @param endDate		bitiş tarihi
	 * @return Float		faiz oranı
	 * @author dumlupinar
	 */
	public Float findInterestRate(String interestCode, String currencyCode,
									Integer baseDuration, Date beginDate, Date endDate) {
		Float result = 0f;
		
		if (interestCode != null && interestCode.length() > 0 
			&& currencyCode != null && currencyCode.length() > 0 
			&& baseDuration != null && ! baseDuration.equals(0)) {
			
			try {
				
				String statement = "select r.rate from InterestRate r " +
									 "left join r.interest t " +
									 "where t.active = :active " +
									 "  and t.code = :code " +
									 "  and r.currencyCode = :currencyCode " +
									 "  and r.baseDuration = :baseDuration ";
				
				if (beginDate != null) statement += " and r.effectiveDate >= :beginDate ";
				if (endDate != null)   statement += " and r.effectiveDate >= :endDate ";
				
				Query query = em.createQuery(statement);
				query.setParameter("active", Boolean.TRUE);
				query.setParameter("code", interestCode);
				query.setParameter("currencyCode", currencyCode);
				query.setParameter("baseDuration", baseDuration);
				
				if (beginDate != null) query.setParameter("beginDate", beginDate);
				if (endDate != null)   query.setParameter("endDate", endDate);
				
				result = (Float) query.setMaxResults(1).getSingleResult();
				
			} catch (Exception e) {

			}
		
		}
		
		return result;
	}
	
	/**
	 * Bölge Kodu ve Hesap Tipine göre Banka Hesabını bulur
	 * 
	 * @param bolge			Bölge Kodu
	 * @param hesapTipi		Hesap Tipi
	 * @return BankAccount	Banka Hesabı
	 * @author dumlupinar
	 */
	public BankAccount findBankAccount(String bolge) {
		BankAccount result = null;
		
		try {
			result = (BankAccount) em.createQuery("select b from BankAccount b " +
													"where b.regionCode = :regionCode " +
													"  and b.accountType = :accountType")
												.setParameter("regionCode", bolge)
											.setMaxResults(1)
											.getSingleResult();
		} catch (Exception e) {
			
		}
		
		return result;
	}

	@SuppressWarnings("unchecked")
	public List<Province> getProvinceList(City city) {
	    log.debug("İlçe Listesi " );
	    if (city != null) {
		    Long city_id = city.getId();
		    return em.createQuery("select c from Province c where city.id like :city and active = 1 order by weight" )
		    		.setParameter("city", city_id)
		            .setHint("org.hibernate.cacheable", true)
		            .getResultList();
	    } else {
	    	return null;
	    }
	    
	}

    @SuppressWarnings("unchecked")
    public List<Contact> getContactList(){
        return em.createQuery("select c from Contact c where c.active = 1")
                .setHint("org.hibernate.cacheable", true)
                .getResultList();

    }
	@SuppressWarnings("unchecked")
	public List<Customs> getCustomsList() {
        log.debug("Customs List " );
        
        return em.createQuery("select c from Customs c where active = 1" )
                .setHint("org.hibernate.cacheable", true)
                .getResultList();
	
	}
	
	public City findCity(Long id) {
		City result = null;
		result = (City) em.createQuery("select c from City c where c.id = :id ")
										.setParameter("id", id)
										.setMaxResults(1)
										.getSingleResult();
		return result;
	}

	@SuppressWarnings("unchecked")
	public Contact findContact(String contactCode) {
		List<Contact> contactList = null;
		contactList = em.createQuery("select c from Contact c where c.code = :code")
										.setParameter("code", contactCode)
										.getResultList();
		
		if (contactList != null && contactList.size() > 0) {
			return contactList.get(0);
		}
		return null;
	}
	

	
    @SuppressWarnings("unchecked")
    public List<String> getUserName(){
	
    	log.debug("Name List");
    	
		return em.createQuery("select c.userName from User c")
		.setHint("org.hibernate.cacheable", true)
		.getResultList();
		
    }
    

	
	
    @SuppressWarnings("unchecked")
    public String findUserName(Long id){
    	String userName = null;
    	
    	try{
    		List<User> userList = em.createQuery("select c from User c where c.id=:userId")
    		.setParameter("userId", id)
    		.setHint("org.hibernate.cacheable", true)
    		.getResultList();
    		
    		if(userList !=null && userList.size()>0){
    			userName=userList.get(0).getUserName();
    		}
    		
    	}catch (Exception e) {
			log.debug("Hata ile karşılaşıldı. Sebebi :{0}", e);
		}    	
    	
    	return userName;
    }
    
    

	
	
    @SuppressWarnings("unchecked")
	public List<User> getUserList(){

        log.debug("User List");

        return em.createQuery("select c from User c where active = 1" )
                .setHint("org.hibernate.cacheable", true)
                .getResultList();
    }

    /**
     * Returns all items from the organization level table.
     * @return levelList
     */
    @SuppressWarnings("unchecked")
    public List<OrganizationLevel> getOrganizationLevelList() {
    	List<OrganizationLevel> levelList = null;
    	
    	try {
			levelList = em.createQuery("select ol from OrganizationLevel ol ")
						  			   .getResultList();
		} catch (Exception e) {
			log.error("Hata: ", e);
		}
		return levelList;
    }

    public List<Organization> findOrganizationsForContact(Contact contact) {
    	List<Organization> resultList = null;
    	List<String> typeList = new ArrayList<String>();

    	if (contact.getCustomerType()) {
    		typeList.add("Customer");
    	}
    	if (contact.getProviderType()) {
    		typeList.add("Provider");
    	}
    	if (contact.getAgentType()) {
    		typeList.add("Agent");
    	}
    	if (contact.getPersonnelType()) {
    		typeList.add("Personnel");
    	}
    	if (contact.getContactType()) {
    		typeList.add("Contact");
    	}
    	
    	resultList = findSuitableOrganizations(typeList.toArray(new String[typeList.size()]));

    	return resultList;
    }
    
    @SuppressWarnings("unchecked")
    public List<Organization> findSuitableOrganizations(String... organizationTypes) {
    	List<Organization> organizationList = null;
    	
    	try {
    		Option levelOption = null;
    		List<String> typeList = new ArrayList<String>();
    		for(String type : organizationTypes) {
    			levelOption = optionManager.getOption("organizationLevelOption.value." + type);
    			typeList.add(levelOption.getValue());
    		}

    		if (levelOption != null) {
	    		organizationList = em.createQuery("select o from Organization o where " +
	    									      "o.level.name in ( :name)")
	    									      .setParameter("name", typeList)
	    									      .getResultList();
    		}

    	} catch (Exception e) {
			log.error("Hata: ", e);
		}
		return organizationList;
    }
    
    /**
     * @return Organization List
     */
    @SuppressWarnings("unchecked")
	public List<Organization> organizationList(){
    	return em.createQuery("select o from Organization where active = 1").getResultList();
    }
    
    public OrganizationLevel findOrganizationLevel(Integer i) {
    	OrganizationLevel result = null;
    	try {
			result = (OrganizationLevel)em.createQuery("select ol from OrganizationLevel ol where " +
									"ol.level=:level")
									.setParameter("level", i)
								    .getSingleResult();
		} catch (Exception e) {
			log.error("Hata: ", e);
		}
		return result;
    }

    @SuppressWarnings("unchecked")
    public List<OrganizationLevel> getOrganizationList() {
    	List<OrganizationLevel> result = null;
    	try {
    		result = em.createQuery("select o from Organization o")
    				   .getResultList();
    	} catch (Exception e) {
    		log.error("Hata: ", e);
    	}
    	return result;
    }

    public DocumentType[] getAllDocumentTypes() {
    	return DocumentType.values();
    }

    @SuppressWarnings("unchecked")
    public List<WorkBunch> getWorkBunchList() {
    	List<WorkBunch> results = null;
    	try {
    		results = em.createQuery("select f from WorkBunch f").getResultList();
    	} catch (Exception e) {
    		log.error("Hata: ", e);
    	}
    	return results;
    }
    
	@Remove @Destroy
    public void destroy() {
    }

}
