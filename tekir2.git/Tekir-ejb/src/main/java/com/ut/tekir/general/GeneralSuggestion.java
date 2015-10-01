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
import java.util.Date;
import java.util.List;

import javax.ejb.Local;

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
import com.ut.tekir.entities.Customs;
import com.ut.tekir.entities.Department;
import com.ut.tekir.entities.DocumentType;
import com.ut.tekir.entities.WorkBunch;
import com.ut.tekir.entities.Foundation;
import com.ut.tekir.entities.MetricalConvert;
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
import com.ut.tekir.entities.PromissoryNote;
import com.ut.tekir.entities.PromissoryNoteHistory;
import com.ut.tekir.entities.Province;
import com.ut.tekir.entities.Tax;
import com.ut.tekir.entities.TaxRate;
import com.ut.tekir.entities.TaxReturnType;
import com.ut.tekir.entities.Unit;
import com.ut.tekir.entities.User;
import com.ut.tekir.entities.Warehouse;

/**
 *
 * @author haky
 */
@Local
public interface GeneralSuggestion {

    List<City> suggestCity(Object event);
    List<TaxReturnType> getTaxReturnTypes();
    List<Country> suggestCountry(Object event);
    List<Account> suggestAccount(Object event);
    List<Tax> suggestTax(Object event);
    List<Unit> suggestUnit(Object event);
    List<Portfolio> suggestPortfolio(Object event);
    List<Contact> getContactList();

    List<String> getUserName();
    String findUserName(Long id);
    User findUser(Long id);
    List<PriceItemDetail> getPriceItemDetail();
    List<Product> getProductList(Long groupId);
    List<Currency> getCurrencyList();
    List<Currency> getAllCurrencyList();
    List<Unit> getUnitList();
    List<Tax> getTaxList();
    List<Tax> getTax2List();
    List<Account> getAccountList();
    List<Warehouse> getWarehouseList();
    List<Portfolio> getPortfolioList();
    
    List<Bank> getBankList();
    List<Bank> getOurBankList();
    List<Bank> getAllBankList();
    
    List<BankBranch> getBankBranchList();
    List<BankBranch> getBankBranches(Bank bank);
    List<BankBranch> getOurBankBranches(Bank bank);
    
    List<BankAccount> getBankAccountList();
    List<BankAccount> getOurBankAccountList();
    List<BankAccount> getBankAccounts(BankBranch bankBranch);
    List<BankAccount> getLocalBankAccounts(BankBranch bankBranch);
    List<BankAccount> getOurBankAccounts(BankBranch bankBranch);
    
    List<Department> getDepartmentList();
    
    List<ChequeHistory> getChequeHistory(Cheque cheque);
    List<PromissoryNoteHistory> getPromissoryHistory(PromissoryNote promissoryNote);
    
    List<AccountOwnerType> getAccountOwnerType();
    
    List<ChequeStub> getChequeStubList();
    
    List<City> getCityList();
    List<City> getCityList(String country);
    List<Province> getProvinceList(City city);
    
    
    List<Customs> getCustomsList();
    
    List<PaymentPlan> getPaymentPlanList();

    List<User> getUserList();

    boolean kurKontrol(Date date);

    List<OrganizationLevel> getOrganizationLevelList();
    
    List<Organization> findSuitableOrganizations(String... organizationType);
    
    void destroy();
    
    List<MetricalConvert> findMetricalConvert( String mainUnit);
    MetricalConvert findSameMetricalConvert( String mainUnit, String changeUnit);
    
    List<Organization> findOrganizationsForContact(Contact contact);

    OrganizationLevel findOrganizationLevel(Integer i);
    
    List<OrganizationLevel> getOrganizationList();
    
    TaxRate findTaxRate(Tax tax,Date rateDate);
    
    List<PriceList> getPriceListList();

    Product findProductWithBarcode(String barcode);

    Product findProductWithId(Integer id);
    
    List<Account> getPosList();

    Pos findPos(String aPosCode);
    Pos findPos(Long aPosId);
    
    Account findAccount(String anAccountCode);
    
    PosCommision findPosCommision(Pos aPos, Date aDate) throws Exception;
    
    List<Product> getDiscountList();

    List<Product> getProductList();

    List<ProductGroup> getProductGroupList();

    List<PaymentCommission> getPaymentCommissionList();

    List<PaymentActionType> getPaymentActionTypeList();
    List<Country> getCountryList();
    List<Foundation> getFoundationList();
    List<Country> getAllCountryList();

    List<Product> getDiscountAdditionList();
    List<Product> getExpenseAdditionList();
    List<Product> getExpenseList();
    
    PaymentActionType findPaymentActionType(String aCode);
    Warehouse findWarehouse(String aWarehouseCode);
    Contact findContact(String contactCode);
    
    void findTaxRate(ProductTax productTax, Date rateDate);
    
    DocumentType[] getAllDocumentTypes();
    
    List<WorkBunch> getWorkBunchList();
    String findCurrencyDecimalCode(String currencyCode);
}
