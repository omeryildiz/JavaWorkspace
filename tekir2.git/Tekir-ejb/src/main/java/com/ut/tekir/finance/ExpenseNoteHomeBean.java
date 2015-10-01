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

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.ejb.Stateful;

import org.jboss.seam.Component;
import org.jboss.seam.ScopeType;
import org.jboss.seam.annotations.Begin;
import org.jboss.seam.annotations.Create;
import org.jboss.seam.annotations.In;
import org.jboss.seam.annotations.Name;
import org.jboss.seam.annotations.Out;
import org.jboss.seam.annotations.Scope;
import org.jboss.seam.annotations.Transactional;
import org.jboss.seam.core.Conversation;

import com.ut.tekir.entities.Bank;
import com.ut.tekir.entities.BankBranch;
import com.ut.tekir.entities.BankToContactTransfer;
import com.ut.tekir.entities.BankTransferType;
import com.ut.tekir.entities.Contact;
import com.ut.tekir.entities.ContactAddress;
import com.ut.tekir.entities.DocumentMatch;
import com.ut.tekir.entities.DocumentType;
import com.ut.tekir.entities.ExpenseNote;
import com.ut.tekir.entities.ExpenseNoteItem;
import com.ut.tekir.entities.FinanceAction;
import com.ut.tekir.entities.MoneySet;
import com.ut.tekir.entities.Option;
import com.ut.tekir.entities.Payment;
import com.ut.tekir.entities.PaymentFrom;
import com.ut.tekir.entities.PaymentItem;
import com.ut.tekir.entities.PaymentType;
import com.ut.tekir.entities.ProductTax;
import com.ut.tekir.entities.ProductType;
import com.ut.tekir.entities.ReturnInvoiceStatus;
import com.ut.tekir.entities.TenderDetailBase;
import com.ut.tekir.entities.TradeAction;
import com.ut.tekir.entities.inv.TekirInvoice;
import com.ut.tekir.entities.inv.TekirInvoiceDetail;
import com.ut.tekir.framework.BaseConsts;
import com.ut.tekir.framework.CalendarManager;
import com.ut.tekir.framework.EntityBase;
import com.ut.tekir.framework.OptionManager;
import com.ut.tekir.framework.SequenceManager;
import com.ut.tekir.framework.SequenceType;
import com.ut.tekir.framework.TekirRuntimeException;
import com.ut.tekir.general.GeneralSuggestion;
import com.ut.tekir.invoice.yeni.InvoiceSuggestionFilterModel;
import com.ut.tekir.options.AmountLimitationOptionKey;
import com.ut.tekir.stock.ProductTxnAction;
import com.ut.tekir.stock.StockSuggestion;
import com.ut.tekir.tender.TenderCalculationHomeBean;
import com.ut.tekir.util.Utils;

/**
 * Gider pusula home bileşenidir.
 * @author sinan.yumak
 */
@Stateful
@Name("expenseNoteHome")
@Scope(value = ScopeType.CONVERSATION)
public class ExpenseNoteHomeBean extends EntityBase<ExpenseNote> implements ExpenseNoteHome<ExpenseNote> {

	@In
	TaxTxnAction taxTxnAction;
	@In
	OptionManager optionManager;
    @In
    BankTxnAction bankTxnAction;
    @In(create = true)
    SequenceManager sequenceManager;
    @In
    CalendarManager calendarManager;
    @In(create=true)
    StockSuggestion stockSuggestion;
    @In
    FinanceTxnAction financeTxnAction;
    @In
    ProductTxnAction productTxnAction;
    @In
    AccountTxnAction accountTxnAction;
	@In(create=true)
	InvoiceSuggestion invoiceSuggestion;
	@In
	GeneralSuggestion generalSuggestion;

	private TenderCalculationHomeBean tenderCalculationHome;
	
    private Bank bank;
    private BankBranch bankBranch;
    private BigDecimal bankTransferCost = BigDecimal.ZERO;

    private ContactAddress selectedAddress;
    private DocumentType documentType = DocumentType.ExpenseNote;
    private List<TekirInvoice> invoiceList = new ArrayList<TekirInvoice>();
    private InvoiceSuggestionFilterModel filterModel = new InvoiceSuggestionFilterModel();

    @Create 
    @Begin(join=true, flushMode = org.jboss.seam.annotations.FlushModeType.MANUAL)
    public void init() {
    }

    @Out(required = false)
    public ExpenseNote getExpenseNote() {
        return getEntity();
    }

    @In(required = false)
    public void setExpenseNote(ExpenseNote ExpenseNote) {
        setEntity(ExpenseNote);
    }

    @Override
    public void createNew() {
        log.debug("Yeni ExpenseNote");

        entity = new ExpenseNote();
        entity.setDocumentType(documentType);
        entity.setDate(calendarManager.getCurrentDate());
        entity.setSerial(sequenceManager.getNewSerialNumber(SequenceType.SERIAL_EXPENSE_NOTE));
    }

    @Override
    @Transactional
    public String save() {
		log.info("Saving expense note. Serial :{0}", entity.getSerial());
		
		String result = BaseConsts.FAIL;

        try {
            makeEntityValidations();

            calcTotalAmounts();

            copyDeliveryContactInfoFromContactCard();
            
            result = super.save();

            if(result.equals(BaseConsts.SUCCESS)){
				if (entity.getDocumentType().equals(DocumentType.ReturnExpenseNote))  {
					updateReturnedStatusOfInvoice();
				}

            	financeTxnAction.saveExpenseNote(entity);
            	productTxnAction.saveExpenseNote(entity);
            	taxTxnAction.saveExpenseNote(entity);

            	if(entity.getPaymentFrom().equals(PaymentFrom.Account)){
            		savePayment();
            	}else{
            		saveBankToContactTransfer();
            	}
            }
            
        } catch (Exception e) {
			facesMessages.add( Utils.getExceptionMessage(e) );
        	log.error("Hata :", e);
            return BaseConsts.FAIL;
        }
        return result;
    }
	
	private void copyDeliveryContactInfoFromContactCard() {
		Contact contact = entity.getContact();

		entity.setDeliveryPerson(contact.getPerson());
		entity.setDeliveryCompany(contact.getCompany());
		entity.setDeliverySsn(contact.getSsn());
		entity.setDeliveryFullname(contact.getFullname());
		entity.setDeliveryTaxNumber(contact.getTaxNumber());
		entity.setDeliveryTaxOffice(contact.getTaxOffice());
	}

	public void setAddress() {
		log.debug("Updating address list. Contact code :{0}",entity.getContact().getCaption());
		if (selectedAddress != null) {
			entity.setDeliveryAddress(selectedAddress.getAddress());
		} else {
			entity.setDeliveryAddress(null);
		}
	}

	private void makeEntityValidations() {
    	masterValidations();
    	
    	detailValidations();
    }

    private void masterValidations() {
    	if (entity.getItems().size() == 0) {
    		throw new RuntimeException("En az bir detay girmelisiniz!");
    	}

    	if (entity.getPaymentFrom().equals(PaymentFrom.Account)) {
    		checkPaymentLimit();
    	}
    }

    private void detailValidations() {
    	for(int i=0;i<entity.getItems().size();i++) {
    		ExpenseNoteItem item = entity.getItems().get(i);
    		if ( item.getAmount().getValue().compareTo(BigDecimal.ZERO) <0) {
                throw new RuntimeException(". satırda sıfırdan büyük bir değer girmelisiniz.");
    		}

    		if ( item.getService() == null) {
				throw new RuntimeException(i+1 + ". satırda hizmet/ürün seçilmemiş!" );
			}
		}
    }
    
    public List<TekirInvoice> findInvoices() {
    	try {
    		if (entity.getContact() != null) {
    			invoiceSuggestion.setFilterModel(getFilterModel());
    			List<TekirInvoice> resultList = invoiceSuggestion.suggestInvoice();

    			if ( entity.getInvoice() != null ) {
    				resultList.remove(entity.getInvoice());
    			}

	            invoiceList.clear();
	
    			invoiceList.addAll(resultList);
    		} 
    	} catch (Exception e) {
    		log.error("Hata oluştu. Sebebi :{0}", e);
    	}
    	return invoiceList;
    }

    public void selectInvoice(int ix) {
    	log.info("Selected invoice. Index :{0}", ix);
    	manualFlush();
    	
    	TekirInvoice inv = invoiceList.get(ix);

    	if (entity.getItems() != null && entity.getItems().size() >0 ) {
			for (int i=entity.getItems().size()-1;i>=0;i-- ) {
				ExpenseNoteItem item = entity.getItems().get(i);
				if (item.getReferenceDocumentId() != null) {
					resetReturnInvoiceFields(item.getReferenceDocumentId());
				}
			}
            updateReturnedStatusOfInvoice();
    		
    	}

    	entity.getItems().clear();
    	for (TekirInvoiceDetail invItem : inv.getProductItems()) {
    		invItem.setReturned(true);
    		
    		ExpenseNoteItem noteItem = new ExpenseNoteItem();

    		noteItem.setOwner(entity);
    		noteItem.setQuantity(invItem.getQuantity());
    		noteItem.setService(invItem.getProduct());

    		noteItem.setReferenceDocumentId(invItem.getId());

    		noteItem.setAmount(new MoneySet(invItem.getBeforeTax()));
    		noteItem.setNetAmount(new MoneySet(invItem.getGrandTotal()));
    		
    		noteItem.setInvoiceUnitExpense( new MoneySet(invItem.getLineExpenseTotal()) );
    		noteItem.setInvoiceUnitPrice( new MoneySet(invItem.getTaxIncludedUnitPrice()) );
    		
//    		noteItem.setUnitPrice( new MoneySet(invItem.getTaxExcludedUnitPrice()) );
    		noteItem.setUnitPrice( new MoneySet(invItem.getDiscountIncludedUnitPrice()) );
    		
    		entity.getItems().add(noteItem);
    		
    		entityManager.merge(invItem);
    		entityManager.flush();

    		taxCalculationFor(noteItem);
    	}
    	entity.setInvoice(inv);

    	entity.setWarehouse(inv.getWarehouse());
    	entity.setAccount(inv.getAccount());
    }

    private void calculateLineAmount(ExpenseNoteItem item) {
    	BigDecimal amount = item.getUnitPrice().getValue().multiply(BigDecimal.valueOf(item.getQuantity().getValue()));
        item.setAmount(new MoneySet(amount, amount));
        
        taxCalculationFor(item);
	}

    @SuppressWarnings("unchecked")
	public void taxCalculationFor(ExpenseNoteItem anItem) {
		TenderDetailBase calculationItem = new TekirInvoiceDetail();
		calculationItem.setProduct(anItem.getService());
		calculationItem.setQuantity(anItem.getQuantity());
		calculationItem.setTaxExcludedAmount(new MoneySet(anItem.getAmount()));

		TekirInvoice invoice = new TekirInvoice();
		invoice.setDate(entity.getDate());
		getTenderCalculationHome().setEntity( invoice );

		getTenderCalculationHome().calculateEverythingForItem(calculationItem);
		
		anItem.setTaxAmount(calculationItem.getTaxTotalAmount());
		anItem.setNetAmount(calculationItem.getGrandTotal());

		if (entity.isAddInvoiceExpenses()) {
			addLineExpenses(anItem);
		}
    }

    public void addLineExpenseAction() {
    	for (ExpenseNoteItem item : entity.getItems()) {
    		addLineExpenses( item );
    	}
    }
    
    private void addLineExpenses(ExpenseNoteItem anItem) {
    	log.info("Önce #0", anItem.getNetAmount());
    	if (entity.isAddInvoiceExpenses()) {
    		anItem.getNetAmount().add( new MoneySet(anItem.getInvoiceUnitExpense()).multiply( anItem.getQuantity().asBigDecimal()) );
    	} else {
    		anItem.getNetAmount().substract( new MoneySet(anItem.getInvoiceUnitExpense()).multiply( anItem.getQuantity().asBigDecimal()) );
    	}
    	log.info("Sonra #0", anItem.getNetAmount());
	}

	private void updateReturnedStatusOfInvoice() {
		Map<Long, TekirInvoice> returnedInvoiceMap = new HashMap<Long, TekirInvoice>();

		TekirInvoice returnedInvoice = null;
		TekirInvoiceDetail returnedInvoiceDetail = null;
		for (ExpenseNoteItem item : entity.getItems()) {
			if (item.getReferenceDocumentId() != null) {
				returnedInvoiceDetail = findInvoiceDetail(item.getReferenceDocumentId());
				
				if (returnedInvoiceDetail != null) {
					returnedInvoice = returnedInvoiceDetail.getOwner();
					
					if (!returnedInvoiceMap.containsKey(returnedInvoice.getId())) {
						returnedInvoiceMap.put(returnedInvoice.getId(), returnedInvoice);
					}
				}
			}
		}
		
		for (TekirInvoice inv : returnedInvoiceMap.values()) {

			int totalLineSize = 0;
			int unreturnedLineSize = 0; 
			for (TekirInvoiceDetail item : inv.getItems()) {

				if (item.getProductType().equals(ProductType.Product)) {
					
					if (!item.isReturned()) {
						unreturnedLineSize++;
					}
					totalLineSize++;
				}
			}
			
			if (unreturnedLineSize == totalLineSize) {
				inv.setReturnInvoiceStatus(ReturnInvoiceStatus.Open);
			} else if (unreturnedLineSize == 0) {
				inv.setReturnInvoiceStatus(ReturnInvoiceStatus.Closed);
			} else if (unreturnedLineSize < totalLineSize) {
				inv.setReturnInvoiceStatus(ReturnInvoiceStatus.Processing);
			}
			entityManager.merge(inv);
			entityManager.flush();
		}

	}

	private void resetReturnInvoiceFields(Long anInvoiceDetailId) {
		TekirInvoiceDetail referencedInvoiceItem = findInvoiceDetail(anInvoiceDetailId);

		referencedInvoiceItem.setReturned(false);
//		referencedInvoiceItem.getOwner().setReturnInvoice(null);
		entityManager.merge(referencedInvoiceItem);
		entityManager.flush();
	}

	@SuppressWarnings("unchecked")
	private TekirInvoiceDetail findInvoiceDetail(Long anInvoiceDetailId) {
		TekirInvoiceDetail result = null;
		List<TekirInvoiceDetail> resultList = entityManager.createQuery("select d from TekirInvoiceDetail d where " +
								  									    "d.id=:anInvoiceDetailId ")
																	    .setParameter("anInvoiceDetailId", anInvoiceDetailId)
																	    .getResultList();
		
		
		if (resultList != null && resultList.size() >0 ) {
			result = resultList.get(0);
		}
		return result;
	}

    private void savePayment(){
        if(entity.getId() != null){
            deletePayment();
        }

        Payment payment = new Payment();
        
        payment.setAccount(entity.getAccount());
        payment.setAction(FinanceAction.Debit);
        payment.setCode(entity.getCode());
        payment.setContact(entity.getContact());
        payment.setDate(entity.getDate());
        if (entity.getDocumentType().equals(DocumentType.ExpenseNote)) {
        	payment.setInfo("Gider Pusulası: " + entity.getReference());
        } else {
        	payment.setInfo("İade Gider Pusulası: " + entity.getReference());
        }
        payment.setReference(entity.getReference());
        payment.setSerial(entity.getSerial());
        payment.setTotalAmount(entity.getTotalNetAmount());
        payment.setExpenseNote(entity);
        
        payment.setCreateDate(entity.getCreateDate());
        payment.setCreateUser(entity.getCreateUser());
        payment.setUpdateUser(entity.getUpdateUser());
        payment.setUpdateDate(entity.getUpdateDate());

        // PaymentItem kaydı da atılmalı.
        for(ExpenseNoteItem noteItem : entity.getItems()){

        	//FIXME: bu kodları documentMatchProvider yapısına taşımak lazım.
        	
            // dokuman esleme kaydı. her item icin atilmali
            DocumentMatch match = new DocumentMatch();
            match.setAmount(noteItem.getNetAmount());
        	//FIXME: bu kodları document match provider yapısına geçirmek gerek.
//            match.setTotalAmount(entity.getTotalNetAmount().getValue());
            match.setMatchDate(entity.getDate());
        	match.setMatchedDocumentType(entity.getDocumentType());
            match.setMatchedDocumentId(entity.getId());
            match.setMatchedDocumentSerial(entity.getSerial());
            match.setDocumentSerial(entity.getSerial());
            match.setDocumentType(DocumentType.Payment);

            PaymentItem payItem = new PaymentItem();
            payItem.setLineType(PaymentType.Cash);
            payItem.setAmount(noteItem.getNetAmount());
//            payItem.setDocumentMatch(match);
            payItem.setOwner(payment);
            payItem.setLineCode(noteItem.getLineCode());
            payment.getItems().add(payItem);
        	payItem.setInfo( noteItem.getInfo() );
        }

        entityManager.persist(payment);
        entityManager.flush();

        // olusturulan tediyenin txn kayitlari yapiliyor.
        financeTxnAction.savePayment(payment);
        accountTxnAction.savePayment(payment);
    }

    private void checkPaymentLimit() {
    	BigDecimal maxPaymentAmount = getMaxPaymentAmount();
    	
    	if ( entity.getTotalAmount().getValue().compareTo( maxPaymentAmount ) > 0 ) {
    		StringBuilder sb = new StringBuilder();
    		sb.append("Kasadan ödenebilecek üst limit tutarı ")
    		  .append(maxPaymentAmount)
    		  .append(entity.getTotalAmount().getCurrency())
    		  .append(" dir.");
    		throw new TekirRuntimeException(sb.toString());
    	}
	}

	private BigDecimal getMaxPaymentAmount() {
		Option o = optionManager.getOption(AmountLimitationOptionKey.MAX_PAYMENT_AMOUNT, true);

		BigDecimal minPaymentAmount = BigDecimal.ZERO;
		if (o != null) {
			minPaymentAmount = o.getAsBigDecimal();
		}
		return minPaymentAmount;
	}

	private void saveBankToContactTransfer(){
        if(entity.getId() != null){
            deleteBankToContact();
        }

        //TODO: sadece yerel doviz tipiyle islem yapilacak sekilde yerel tutarlar setlendi. 
        //Farkli dovizlerle islem yapılacaksa, yerel tutar doviz kurlari ile hesaplanmali !
        
        BankToContactTransfer transfer = new BankToContactTransfer();
        DocumentMatch match = new DocumentMatch();

    	//FIXME: bu kodları documentMatchProvider yapısına taşımak lazım.
        match.setAmount(new MoneySet(entity.getTotalNetAmount()));
    	//FIXME: bu kodları document match provider yapısına geçirmek gerek.
//        match.setTotalAmount(entity.getTotalNetAmount().getValue());
        match.setMatchDate(entity.getDate());
        match.setMatchedDocumentType(entity.getDocumentType());
        match.setMatchedDocumentId(entity.getId());
        match.setMatchedDocumentSerial(entity.getSerial());
        match.setDocumentSerial(entity.getSerial());
        match.setDocumentType(DocumentType.BankToContactTransfer);

        transfer.setAction(FinanceAction.Debit);

        transfer.getAmount().setValue(entity.getTotalNetAmount().getValue());
        transfer.getAmount().setLocalAmount(entity.getTotalNetAmount().getValue());
        transfer.getAmount().setCurrency(BaseConsts.SYSTEM_CURRENCY_CODE);

        transfer.setBankAccount(entity.getBankAccount());
        transfer.setBankBranch(transfer.getBankAccount().getBankBranch());
        transfer.setBank(transfer.getBankBranch().getBank());
        transfer.setCode(entity.getCode());
        transfer.setContact(entity.getContact());
        transfer.setDate(entity.getDate());

        if (entity.getDocumentType().equals(DocumentType.ExpenseNote)) {
        	transfer.setInfo("Gider Pusulası: " + entity.getReference());
        } else {
        	transfer.setInfo("İade Gider Pusulası: " + entity.getReference());
        }
        
        transfer.setReference(entity.getReference());
        // daha sonra silme islemlerinde serial alanindan ilgili kayit bulunacak!
        transfer.setSerial(entity.getSerial());
        transfer.setTransferType(BankTransferType.Havale);
        transfer.getCost().setCurrency(BaseConsts.SYSTEM_CURRENCY_CODE);
        transfer.getCost().setValue(getBankTransferCost());
        transfer.getCost().setLocalAmount(getBankTransferCost());
      //FIXME: bu bileşenin döküman eşleme ile ilgili eksiklikleri var. Düzenlemek gerek.
//        transfer.setDocumentMatch(match);

        transfer.setCreateDate(entity.getCreateDate());
        transfer.setCreateUser(entity.getCreateUser());
        transfer.setUpdateUser(entity.getUpdateUser());
        transfer.setUpdateDate(entity.getUpdateDate());
        transfer.setExpenseNote(entity);
        
        entityManager.persist(transfer);
        entityManager.flush();

        // olusturulan bankadan cariye transfer fisinin txn kayitlari atiliyor.
        bankTxnAction.saveBankToContactTransfer(transfer);
        financeTxnAction.saveBankToContactTransfer(transfer);
    }

    private void deletePayment() {
    	Payment payment = findPayment();
        if ( payment != null ) {
            entityManager.remove(payment);
            financeTxnAction.deletePayment(payment);
            accountTxnAction.deletePayment(payment);
            
            entityManager.flush();
        }
    }

    private void deleteBankToContact() {
    	BankToContactTransfer transfer = findBankToContact();
    	if ( transfer != null ) {
            entityManager.remove( transfer );
            bankTxnAction.deleteBankToContactTransfer( transfer );
            financeTxnAction.deleteBankToContactTransfer( transfer );
            
            entityManager.flush();
        }
    }

    @SuppressWarnings("unchecked")
	private Payment findPayment() {
        try {
        	List<Payment> paymentList = entityManager.createQuery("select c from Payment c where " +
           											  "c.expenseNote.id = :expenseId")
			                                          .setParameter("expenseId", entity.getId())
			                                          .getResultList();

        	if (paymentList != null && paymentList.size() == 1) {
        		return paymentList.get(0);
        	}
        } catch (Exception e) {
            log.error("Tediye fişi sorgulanırken hata meydana geldi. Sebebi #0", e);
            facesMessages.add("Tediye sorgulanırken hata meydana geldi. Sebebi #0",Utils.getExceptionMessage(e));
        }
		return null;
    }

    @SuppressWarnings("unchecked")
    private BankToContactTransfer findBankToContact() {
        try {
        	List<BankToContactTransfer> transferList = entityManager.createQuery("select c from BankToContactTransfer c " +
        																		 "where c.expenseNote.id = :expenseId")
						                                                        .setParameter("expenseId", entity.getId())
						                                                        .getResultList();

        	if (transferList != null && transferList.size() == 1) {
        		return transferList.get(0);
        	}
        } catch (Exception e) {
            log.error("Cari transfer fişi sorgulanırken hata meydana geldi. Sebebi #0", e);
            facesMessages.add("Tediye sorgulanırken hata meydana geldi. Sebebi #0",Utils.getExceptionMessage(e));
        }
		return null;
    }
    
    private void calculateAmounts(ExpenseNoteItem item) {
    	log.info("Calculating amounts of line :#0", item);

    	//FIXME: dekont eger dovizle calisir olursa burada dovize gore cevrim kontrol edilmeli
    	item.getUnitPrice().setLocalAmount(item.getUnitPrice().getValue());
    	log.info("birim lokal fiyat: #0", item.getUnitPrice().getLocalAmount());

		if(item.getService() == null){
			throw new RuntimeException("Stok/Hizmet boş bırakılamaz.");
		}
		
		if (entity.getDocumentType().equals(DocumentType.ExpenseNote)) {
			calculateLineWitholding(item);
		} else {
			calculateLineAmount(item);
		}

    }

    public void calculateAmountsAction(ExpenseNoteItem item) {
    	try {
			calculateAmounts(item);
		} catch (Exception e) {
			facesMessages.add(Utils.getExceptionMessage(e));
		}
    }
    
	private void calculateLineWitholding(ExpenseNoteItem item) {
    	ProductTax witholdingTax = item.getService().getWitholdingTax();
    	
    	if(witholdingTax == null){
    		throw new RuntimeException("Stopaj vergisi tanımlanmamış.");
        }

    	generalSuggestion.findTaxRate(witholdingTax, entity.getDate());
    	if(witholdingTax.getRate() == null){
    		throw new RuntimeException("Stopaj vergisi için vergi oranı bulunamadı.");
        }

    	BigDecimal netValue = BigDecimal.ZERO;
    	BigDecimal grossValue = BigDecimal.ZERO;
    	BigDecimal witholdingAmount = BigDecimal.ZERO;
    	BigDecimal amount = item.getUnitPrice().getValue().multiply(BigDecimal.valueOf(item.getQuantity().getValue()));

    	if ( witholdingTax.getTaxIncluded() ) {
    		grossValue = amount;
    		witholdingAmount = grossValue.multiply( witholdingTax.getRate().getRate() )
    								  .divide(BigDecimal.valueOf(100), 2, RoundingMode.HALF_UP);

    		netValue = grossValue.subtract(witholdingAmount);
    	} else {
    		netValue = amount;
    		witholdingAmount = netValue.multiply( witholdingTax.getRate().getRate() )
    								  .divide(BigDecimal.valueOf(100), 2, RoundingMode.HALF_UP);

    		grossValue = netValue.add(witholdingAmount);
    	}

        item.setWithholdRate( witholdingTax.getRate().getRate() );
        item.setWithholdAmount(new MoneySet(witholdingAmount, witholdingAmount));
        item.setNetAmount(new MoneySet(netValue, netValue));
        item.setAmount(new MoneySet(grossValue, grossValue));
    }
    
    private void calcTotalAmounts(){
    	clearDocumentTotals();

    	for(ExpenseNoteItem item : entity.getItems()){
    		calculateAmounts(item);
    		
    		entity.getTotalAmount().add( item.getAmount() );
        	entity.getTotalNetAmount().add( item.getNetAmount() );
        	entity.getTotalWithhold().add( item.getWithholdAmount() );
        	entity.getTotalTaxAmount().add( item.getTaxAmount() );
        	if (entity.isAddInvoiceExpenses()) {
        		entity.getTotalInvoiceUnitExpense().add( new MoneySet(item.getInvoiceUnitExpense()).multiply(item.getQuantity().asBigDecimal()) );
        	}
        }
    }

    public void calcTotalAmountsAction() {
    	try {
			calcTotalAmounts();
		} catch (Exception e) {
			facesMessages.add(Utils.getExceptionMessage(e));
		}
    }

    private void clearDocumentTotals() {
        entity.getTotalAmount().clearMoney();
        entity.getTotalNetAmount().clearMoney();
        entity.getTotalWithhold().clearMoney();
        entity.getTotalTaxAmount().clearMoney();
        entity.getTotalInvoiceUnitExpense().clearMoney();
    }
    
    public void createNewLine() {
    	manualFlush();
        
        ExpenseNoteItem it = new ExpenseNoteItem();
        it.setOwner(entity);
        
        it.getAmount().setCurrency(BaseConsts.SYSTEM_CURRENCY_CODE);
        entity.getItems().add(it);
        log.debug("yeni item eklendi");
    }

    @Override
    public String delete(){
		String result = BaseConsts.SUCCESS;
		try {
			if(entity.getPaymentFrom().equals(PaymentFrom.Account)){
				deletePayment();
			}else{
				deleteBankToContact();
			}
			
			financeTxnAction.deleteExpenseNote(entity);
			productTxnAction.deleteExpenseNote(entity);
			taxTxnAction.deleteExpenseNote(entity);

			if (entity.getDocumentType().equals(DocumentType.ReturnExpenseNote))  {
				for (int i=entity.getItems().size()-1;i>=0;i-- ) {
					ExpenseNoteItem item = entity.getItems().get(i);
					if (item.getReferenceDocumentId() != null) {
						resetReturnInvoiceFields(item.getReferenceDocumentId());
					}
    			}
                updateReturnedStatusOfInvoice();
            } 

			result = super.delete();
		} catch (Exception e) {
			log.error("Pusula silinirken hata oluştu. Sebebi :#0", e);
			facesMessages.add(Utils.getExceptionMessage(e));
			result = BaseConsts.FAIL;			
		}
        return result;
    }

    public void deleteLine(ExpenseNoteItem item) {
 
    	manualFlush();
        
    	if (entity == null) {
            return;
        }

    	log.debug("Kayıt Silinecek : {0}", item);
        entity.getItems().remove(item);
    }

    public void selectLine(Integer ix) {
        
    	manualFlush();
        if (entity == null) {
            return;
        }
        log.debug("İşlenecek IX : {0}", ix);

        ExpenseNoteItem noteItem = entity.getItems().get(ix);
        
        if( noteItem.getService() != null) {
        
        	noteItem.getQuantity().setUnit(noteItem.getService().getUnit());
        }

    }
    
	public void deleteReturnLine(Integer ix) {
		log.info("Deleting line. Line number :{0}", ix.intValue());
		manualFlush();
	
		ExpenseNoteItem item = getEntity().getItems().get(ix.intValue());

		if (item.getReferenceDocumentId() != null) {
			resetReturnInvoiceFields(item.getReferenceDocumentId());
		}
		entity.getItems().remove(ix.intValue());
	}

    public void deleteLine(Integer ix) {
        
    	manualFlush();
        if (entity == null) {
            return;
        }

        log.debug("Kayıt Silinecek IX : {0}", ix);
        log.debug("Toplam Kayıt : {0}", entity.getItems().size());
        Object o = entity.getItems().remove(ix.intValue());
        log.debug("Silinen : {0}", o);
        log.debug("Toplam Kayıt : {0}", entity.getItems().size());
    }

    @Override
    public void setId(Long id){
        if (entity != null) {
            return;
        }

        super.setId(id);
        if(entity.getPaymentFrom().equals(PaymentFrom.BankAccount)){
            fillBankFields();
        }
    }

    private void fillBankFields(){
        if(entity.getId() != null){

            setBankBranch(entity.getBankAccount().getBankBranch());
            setBank(getBankBranch().getBank());
            setBankTransferCost( findBankToContact().getCost().getValue() );
        }
    }

    public void manualFlush() {
        Conversation.instance().changeFlushMode(org.jboss.seam.annotations.FlushModeType.MANUAL);
    }

    public void clearBankAccount(){
    	setBankBranch(null);
    	entity.setBankAccount(null);
    }

    public Bank getBank() {
        return bank;
    }

    public void setBank(Bank bank) {
        this.bank = bank;
    }

    public BankBranch getBankBranch() {
        return bankBranch;
    }

    public void setBankBranch(BankBranch bankBranch) {
        this.bankBranch = bankBranch;
    }

    public BigDecimal getBankTransferCost() {
        return bankTransferCost;
    }

    public void setBankTransferCost(BigDecimal bankTransferCost) {
        this.bankTransferCost = bankTransferCost;
    }

	public InvoiceSuggestionFilterModel getFilterModel() {
		filterModel.setTradeAction(TradeAction.Sale);
		filterModel.setRetInvoiceStatus(getPossibleStatusList());
		filterModel.setContact(entity.getContact());
		return filterModel;
	}

    public List<ReturnInvoiceStatus> getPossibleStatusList () {
		List<ReturnInvoiceStatus> possibleStatusList = new ArrayList<ReturnInvoiceStatus>();
		possibleStatusList.add(ReturnInvoiceStatus.Open);
		possibleStatusList.add(ReturnInvoiceStatus.Processing);
		return possibleStatusList;
    }

    @SuppressWarnings("unchecked")
	private TenderCalculationHomeBean getTenderCalculationHome() {
    	if (tenderCalculationHome == null) {
    		tenderCalculationHome = (TenderCalculationHomeBean)Component.getInstance("tenderCalculationHome",ScopeType.SESSION);
    	}
    	return tenderCalculationHome;
    }
    
    public void setFilterModel(InvoiceSuggestionFilterModel filterModel) {
		this.filterModel = filterModel;
	}

	public DocumentType getDocumentType() {
		return documentType;
	}

	public void setDocumentType(DocumentType documentType) {
		this.documentType = documentType;
	}

	public List<TekirInvoice> getInvoiceList() {
		return invoiceList;
	}

	public void setInvoiceList(List<TekirInvoice> invoiceList) {
		this.invoiceList = invoiceList;
	}

	public ContactAddress getSelectedAddress() {
		return selectedAddress;
	}

	public void setSelectedAddress(ContactAddress selectedAddress) {
		this.selectedAddress = selectedAddress;
	}

}