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

package com.ut.tekir.invoice.yeni;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.ejb.Stateful;
import javax.ejb.TransactionAttribute;
import javax.ejb.TransactionAttributeType;
import javax.faces.application.FacesMessage;

import org.jboss.seam.Component;
import org.jboss.seam.ScopeType;
import org.jboss.seam.annotations.Begin;
import org.jboss.seam.annotations.Create;
import org.jboss.seam.annotations.FlushModeType;
import org.jboss.seam.annotations.In;
import org.jboss.seam.annotations.Name;
import org.jboss.seam.annotations.Out;
import org.jboss.seam.annotations.Scope;
import org.jboss.seam.annotations.Transactional;
import org.jboss.seam.international.StatusMessage.Severity;

import com.ut.tekir.entities.Account;
import com.ut.tekir.entities.Contact;
import com.ut.tekir.entities.ContactAddress;
import com.ut.tekir.entities.ControlType;
import com.ut.tekir.entities.DiscountOrExpense;
import com.ut.tekir.entities.DistributionStyle;
import com.ut.tekir.entities.DocumentType;
import com.ut.tekir.entities.FinanceAction;
import com.ut.tekir.entities.FoundationTxn;
import com.ut.tekir.entities.InvoiceOrderLink;
import com.ut.tekir.entities.InvoicePaymentPlanItem;
import com.ut.tekir.entities.MoneySet;
import com.ut.tekir.entities.Option;
import com.ut.tekir.entities.OrderStatus;
import com.ut.tekir.entities.Payment;
import com.ut.tekir.entities.PaymentItem;
import com.ut.tekir.entities.PaymentItemCreditCard;
import com.ut.tekir.entities.PaymentTable;
import com.ut.tekir.entities.PaymentTableDetail;
import com.ut.tekir.entities.PaymentType;
import com.ut.tekir.entities.Pos;
import com.ut.tekir.entities.PosCommision;
import com.ut.tekir.entities.PosCommisionDetail;
import com.ut.tekir.entities.PriceItemDetail;
import com.ut.tekir.entities.Product;
import com.ut.tekir.entities.ProductType;
import com.ut.tekir.entities.Quantity;
import com.ut.tekir.entities.ReturnInvoiceStatus;
import com.ut.tekir.entities.TaxEmbeddable;
import com.ut.tekir.entities.TenderDetailBase;
import com.ut.tekir.entities.TradeAction;
import com.ut.tekir.entities.User;
import com.ut.tekir.entities.Warehouse;
import com.ut.tekir.entities.inv.TekirInvoice;
import com.ut.tekir.entities.inv.TekirInvoiceCurrencyRate;
import com.ut.tekir.entities.inv.TekirInvoiceCurrencySummary;
import com.ut.tekir.entities.inv.TekirInvoiceDetail;
import com.ut.tekir.entities.inv.TekirInvoiceTaxSummary;
import com.ut.tekir.entities.ord.TekirOrderNote;
import com.ut.tekir.entities.ord.TekirOrderNoteDetail;
import com.ut.tekir.entities.shp.TekirShipmentNote;
import com.ut.tekir.entities.shp.TekirShipmentNoteDetail;
import com.ut.tekir.finance.AccountTxnAction;
import com.ut.tekir.finance.FinanceTxnAction;
import com.ut.tekir.finance.FoundationTxnAction;
import com.ut.tekir.finance.InvoiceSuggestion;
import com.ut.tekir.finance.PosTxnAction;
import com.ut.tekir.finance.TaxTxnAction;
import com.ut.tekir.finance.print.DocumentPrint;
import com.ut.tekir.finance.print.InvoicePrint;
import com.ut.tekir.framework.BaseConsts;
import com.ut.tekir.framework.CalendarManager;
import com.ut.tekir.framework.CurrencyManager;
import com.ut.tekir.framework.OptionManager;
import com.ut.tekir.framework.SequenceManager;
import com.ut.tekir.framework.SequenceType;
import com.ut.tekir.framework.SystemProperties;
import com.ut.tekir.framework.UserDiscountRightHome;
import com.ut.tekir.general.GeneralSuggestion;
import com.ut.tekir.options.OptionKey;
import com.ut.tekir.stock.ProductTxnAction;
import com.ut.tekir.stock.StockLevelFinder;
import com.ut.tekir.tender.PosPrinterHome;
import com.ut.tekir.tender.PriceProvider;
import com.ut.tekir.tender.TenderCalculationHomeBean;
import com.ut.tekir.util.Utils;


//FIXME:proforma faturadan üretilen döküman eşleme bağlantısı eksik.

/**
 * İrsaliyeli satış ,satış ve satış iade faturaları için kullanacağımız 
 * home sınıfımızdır.
 * Not:Faturalar için kullanılacak parametreler,
 * İrsaliyeli Faturalar İçin, it=0, at=1
 * Faturalar İçin,            it=1, at=1
 * İade Faturaları İçin,      it=2, at=0
 * 
 * @author sinan.yumak
 */
@Stateful
@Name("saleInvoiceHome")
@Scope(value = ScopeType.CONVERSATION)
public class SaleInvoiceHomeBean extends TenderCalculationHomeBean<TekirInvoice> implements SaleInvoiceHome<TekirInvoice> {

	@In
	PosTxnAction posTxnAction;
	@In
	TaxTxnAction taxTxnAction;
	@In
	PriceProvider priceProvider;
    @In
    Map<Object, String> messages;
	@In
	OptionManager optionManager;
    @In
    CurrencyManager currencyManager;
    @In
    SequenceManager sequenceManager;
	@In
	CalendarManager calendarManager;
	@In(create=true)
	StockLevelFinder stockLevelFinder;
	@In
	ProductTxnAction productTxnAction;
	@In
	FinanceTxnAction financeTxnAction;
	@In
	AccountTxnAction accountTxnAction;
	@In(create=true)
	InvoiceSuggestion invoiceSuggestion;
	@In
	GeneralSuggestion generalSuggestion;
    @In
    SystemProperties systemProperties;
    @In
    FoundationTxnAction foundationTxnAction;
    @In(create=true)
    PaymentPlanItemHome paymentPlanItemHome;

    private Integer invoiceType = 0;
    private Integer actionType = 0;

    private TekirInvoiceDetail selectedDetail;
    private Product selectedDiscountExpense;
    private ContactAddress selectedAddress;
    private Integer selectedIndex;
    
    private User authorizedUser;
    private boolean requestDiscountPermission = Boolean.FALSE;
    private Boolean showMiniTable = Boolean.TRUE;
    
    private ProformaInvoiceController proformaInvController;
    private LimitationChecker limitationChecker;

    private DocumentPrint documentPrint;
    
    private List<TekirOrderNote> orderList = new ArrayList<TekirOrderNote>();
    private List<TekirShipmentNote> shipmentList = new ArrayList<TekirShipmentNote>();
    private List<TekirInvoice> invoiceList = new ArrayList<TekirInvoice>();

    
    //sayfa edit için açıldığında eski birimleri tutar.
    private final Map<Product, Double> oldQuantities = new HashMap<Product, Double>();
    private OldState oldState;

    /**
     * Kurum geri ödeme ekranından gelen txn kayıtlarının listesini tutar.
     */
    private List<FoundationTxn> txnList = new ArrayList<FoundationTxn>();

    private InvoiceSuggestionFilterModel filterModel = new InvoiceSuggestionFilterModel();
    
    @Create
	@Begin(join=true,flushMode= FlushModeType.MANUAL)
	public void init() {
	}

	@Out(required=false)
	public TekirInvoice getSaleInvoice() {
		return getEntity();
	}

	@In(required=false)
	public void setSaleInvoice(TekirInvoice tekirSaleInvoice) {
		setEntity(tekirSaleInvoice);
	}

	@SuppressWarnings("deprecation")
	public void preparePaymentPlanItems() {
		log.info("Preparing payment plan items");
		try {
			paymentPlanItemHome.setPlanHolder(entity);
			paymentPlanItemHome.prepareItems();
		} catch (Exception e) {
			facesMessages.add(FacesMessage.SEVERITY_ERROR,Utils.getExceptionMessage(e));
			log.error("Ödeme planı hazırlanırken hata meydana geldi. Sebebi #0", e);
		}
	}
	
	@Override
	public void createNew() {
		log.debug("#{messages['logMessages.CreatedEntity']}");
		
		entity = new TekirInvoice();
        if (invoiceType == 1) {
        	//eğer invoice type 1 ise, satış faturası açılır. Ekranda irsaliye 
        	//listesi görüntülenmeli.
        	entity.setDocumentType(DocumentType.SaleInvoice);
        } else if (invoiceType == 2){
            // it=2 irsaliyeli satis iade faturasi
        	entity.setDocumentType(DocumentType.PurchaseShipmentInvoice);
        } else if (invoiceType == 3){
        	// it=3 proforma satış faturası
        	entity.setDocumentType(DocumentType.SaleProformaInvoice);
        } else  {
            // it=0 gelirse irsaliyeli satis faturasi
        	entity.setDocumentType(DocumentType.SaleShipmentInvoice);
        }
		
		if (actionType == 1) {
			//eğer action type 1 ise satış faturası, değilse satış iade faturası.
			entity.setTradeAction(TradeAction.Sale);
			if (entity.getDocumentType().equals(DocumentType.SaleProformaInvoice)) {
				entity.setSerial(sequenceManager.getNewSerialNumber(SequenceType.SERIAL_PROFORMAINVOICE_SALE));
			} else {
				entity.setSerial(sequenceManager.getNewSerialNumber(SequenceType.SERIAL_INVOICE_SALE));
			}
		} else {
            //eğer action type 2 de degilse bu bir satış iade faturasıdır. Bu nedenle
			entity.setTradeAction(TradeAction.SaleReturn);
            entity.setSerial(sequenceManager.getNewSerialNumber(SequenceType.SERIAL_INVOICE_PURCHASE));
		}
		
		entity.setActive(true);        
        entity.setDate(calendarManager.getCurrentDate());        
		entity.setShippingDate(calendarManager.getCurrentDate());
		entity.setTime(entity.getDate());
	}

	public void createNewDetail() {
        manualFlush();

        TekirInvoiceDetail detail = new TekirInvoiceDetail();
        detail.getTaxExcludedAmount().setCurrency(entity.getDocCurrency());
        detail.setOwner(entity);
        entity.getItems().add(detail);

        log.debug("#{messages['logMessages.NewItemAdded']}");
    }

	public void createNewPaymentTableDetail() {
        log.info("Creating new payment table detail.");
    	manualFlush();

    	PaymentTable pt = entity.getPaymentTable();
    	if (pt == null) {
    		pt = new PaymentTable();
    		entity.setPaymentTable(pt);
    	}

    	PaymentTableDetail detail = new PaymentTableDetail();
    	detail.setPaymentType(PaymentType.Cash);
    	
    	BigDecimal remainder = getPaymentTableRemainder();
    	MoneySet amount = new MoneySet(remainder,
    								   remainder,
    								   entity.getTotalAmount().getCurrency());

    	currencyManager.setLocalAmountOf(amount, entity.getDate());
    	detail.setAmount(amount);
    	
    	pt.getDetailList().add(detail);
    	detail.setOwner(pt);
    	
    	log.info("Added payment table detail.");
    }

    private BigDecimal getPaymentTableRemainder() {
    	BigDecimal remainder = entity.getGrandTotal().getValue().subtract( getPaymentTableTotal() );
    	return remainder.setScale(2, RoundingMode.HALF_UP);
    }

    private BigDecimal getPaymentTableTotal() {
    	BigDecimal tableTotal = BigDecimal.ZERO;

    	PaymentTable pt = entity.getPaymentTable();
    	if (pt == null) {
    		pt = new PaymentTable();
    		entity.setPaymentTable(pt);
    	}

    	for (PaymentTableDetail item : entity.getPaymentTable().getDetailList()) {
    		setLocalAmountOf(item.getAmount());

    		tableTotal = tableTotal.add(item.getAmount().getValue());
    	}
    	return tableTotal.setScale(2, RoundingMode.HALF_UP);
    }
    
    public List<PosCommisionDetail> prepareCommisionPeriodForLine(Integer ix) {
    	log.info("Preparing commision period line for line :{0}", ix);
    	
    	List<PosCommisionDetail> result = new ArrayList<PosCommisionDetail>();
    	try {
    		PaymentTable pt = entity.getPaymentTable();
    		
    		if (pt == null) {
    			return null;
    		}

    		PaymentTableDetail item = pt.getDetailList().get(ix.intValue());

    		
    		if (item.getPos() != null) {
    			PosCommision pc = generalSuggestion.findPosCommision(item.getPos(), entity.getDate());

//FIXME:Gereksiz. Silinecek.
//    			item.setCommisionDetail(pc.getDefaultDetail());
    			
    			result =  pc.getDetailList();
    		}
		} catch (Exception e) {
			log.error("Pos için dönem bulunurken hata meydana geldi! Sebebi :{0}", e);
			facesMessages.add(Severity.ERROR,"Pos için dönem bulunurken hata meydana geldi! Sebebi :{0}",e.getMessage());
		}
		return result;
    }

    public void deletePaymentTableDetail(Integer ix) {
        manualFlush();
        log.info("Deleting payment table detail line with index :{0}", ix);
        
        PaymentTableDetail ptd = entity.getPaymentTable().getDetailList().get(ix.intValue());
        
        if (ptd.getReferenceId() != null) {
        	//yani referansla gelmiş ise...
        	
        	PaymentTable pt = null;
        	for (InvoiceOrderLink ol : entity.getOrderLinks()) {
        		pt = ol.getOrderNote().getPaymentTable();
        		if (pt != null) {
        			
        			for (PaymentTableDetail td : pt.getDetailList()) {
        				if (ptd.getReferenceId().equals(td.getId())) {
        					//referansı bulduk.
        					td.setProcessed(false);
        					td.setReferenceId(null);
        				}
        			}
        		}
        	}

        }
        entity.getPaymentTable().getDetailList().remove(ix.intValue());
    }

	@Override
	public void createDocumentDiscountLine() {
        log.info("Creating new document discount line...");
    	manualFlush();
    	
        selectedDetail = new TekirInvoiceDetail();
    	selectedDetail.setOwner(entity);
    	selectedDetail.getTaxExcludedAmount().setCurrency(entity.getDocCurrency());
    	selectedDetail.setProductType(ProductType.DocumentDiscount);

    	authorizedUser = new User();
	}

	@Override
	public void createDocumentExpenseLine() {
		log.info("Creating new document expense line...");
		manualFlush();
		
		selectedDetail = new TekirInvoiceDetail();
		selectedDetail.setOwner(entity);
    	selectedDetail.getTaxExcludedAmount().setCurrency(entity.getDocCurrency());
		selectedDetail.setProductType(ProductType.DocumentExpense);
	}

	@Override
	public void createDiscountLine(Integer ix) {
    	log.info("Creating discount line for index:{0}",ix);
    	manualFlush();
    	
    	selectedIndex = ix;
    	
    	selectedDetail = new TekirInvoiceDetail();
    	selectedDetail.setOwner(entity);
    	selectedDetail.setProductType(ProductType.Discount);
	}

	@Override
	public void createExpenseLine(Integer ix) {
		log.info("Creating expense line for index:{0}",ix);
		manualFlush();
		
		selectedIndex = ix;
		
		selectedDetail = new TekirInvoiceDetail();
		selectedDetail.setOwner(entity);
		selectedDetail.setProductType(ProductType.Expense);
	}

	@Override
	public void createDiscountAdditionLine() {
		log.info("Creating new document discount line...");
		manualFlush();
		
		selectedDetail = new TekirInvoiceDetail();
		selectedDetail.setOwner(entity);
		selectedDetail.getTaxExcludedAmount().setCurrency(entity.getDocCurrency());
		selectedDetail.setProductType(ProductType.DiscountAddition);
	}

	@Override
	public void createExpenseAdditionLine() {
		log.info("Creating new document expense line...");
		manualFlush();
		
		selectedDetail = new TekirInvoiceDetail();
		selectedDetail.setOwner(entity);
		selectedDetail.getTaxExcludedAmount().setCurrency(entity.getDocCurrency());
		selectedDetail.setProductType(ProductType.ExpenseAddition);
	}

	@Override
	public void addDocumentDiscountToItems() {
    	log.info("Adding document discount to line...");
    	manualFlush();

    	UserDiscountRightHome userDiscountRight = (UserDiscountRightHome)Component.getInstance("userDiscountRight",true);
    	try {
			
	    	TekirInvoiceDetail detail = new TekirInvoiceDetail();
	    	detail.setProductType(ProductType.DocumentDiscount);
	    	detail.getTaxExcludedAmount().setCurrency(entity.getDocCurrency());
	    	
	    	DiscountOrExpense discount = null;
	    	if (selectedDiscountExpense == null) {
	    		
	    		if (requestDiscountPermission) {
	    			userDiscountRight.hasAuthorizedUserRight(authorizedUser, selectedDetail.getDiscount());

	    			detail.setInfo(authorizedUser.getUserName() + " kullanıcısının izni ile bu indirim yapılmıştır.");
	    		} else {
	    			userDiscountRight.hasLoggedUserRight(selectedDetail.getDiscount());
	    		}
	    		
	    		discount = selectedDetail.getDiscount();
	    		
	    		detail.setDiscountStyle(selectedDetail.getDiscountStyle());

	    	} else {
	    		discount = selectedDiscountExpense.getDiscountOrExpense();
	    		//FIXME: burası nasıl olmalı?
	    		detail.setDiscountStyle(DistributionStyle.Equally);
	    	}
	    	detail.getDiscount().setPercentage(discount.getPercentage());
	    	detail.getDiscount().setRate(discount.getRate());
	    	detail.getDiscount().setValue(discount.getValue());
	    	detail.getDiscount().setCurrency(entity.getDocCurrency());
	    	
	    	setLocalAmountOf(detail.getDiscount());
	    	
	    	entity.getItems().add(detail);
	    	detail.setOwner(entity);
    	} catch (Exception e) {
    		facesMessages.add(Severity.ERROR, e.getMessage());
    	}
    	requestDiscountPermission = false;
	}
	
	@Override
	public void addDocumentExpenseToItems() {
		log.info("Adding document expense to line...");
		manualFlush();
		
		try {
			TekirInvoiceDetail detail = new TekirInvoiceDetail();
			detail.setProductType(ProductType.DocumentExpense);
			detail.getTaxExcludedAmount().setCurrency(entity.getDocCurrency());
			
			DiscountOrExpense expense = null;
			if (selectedDiscountExpense == null) {
				
				expense = selectedDetail.getExpense();
				
				detail.setExpenseStyle(selectedDetail.getExpenseStyle());
				
			} else {
				expense = selectedDiscountExpense.getDiscountOrExpense();
				detail.setExpenseStyle(DistributionStyle.Equally);
			}
			detail.getExpense().setPercentage(expense.getPercentage());
			detail.getExpense().setRate(expense.getRate());
			detail.getExpense().setValue(expense.getValue());
			detail.getExpense().setCurrency(entity.getDocCurrency());
			detail.setProduct(selectedDiscountExpense);
	    	
	    	setLocalAmountOf(detail.getExpense());

			entity.getItems().add(detail);
			detail.setOwner(entity);
		} catch (Exception e) {
			facesMessages.add(Severity.ERROR, e.getMessage());
		}
	}

    @Override
    public void addExpenseToItem() {
    	log.info("Adding expense to line...");
    	manualFlush();
    	
    	TekirInvoiceDetail detail = new TekirInvoiceDetail();
    	detail.setProductType(ProductType.Expense);
    	detail.getTaxExcludedAmount().setCurrency(entity.getDocCurrency());
    	
    	DiscountOrExpense expense = null;
    	if (selectedDiscountExpense != null) {
    		
    		expense = selectedDiscountExpense.getDiscountOrExpense();
    		
    		detail.getExpense().setPercentage(expense.getPercentage());
    		detail.getExpense().setRate(expense.getRate());
    		detail.getExpense().setValue(expense.getValue());
    		detail.getExpense().setCurrency(entity.getDocCurrency());
    		detail.setProduct(selectedDiscountExpense);
    		
	    	setLocalAmountOf(detail.getExpense());

    		TekirInvoiceDetail selectedItem = entity.getItems().get(selectedIndex);
    		
    		if (selectedItem != null) {
    			selectedItem.getChildList().add(detail);
    			detail.setParent(selectedItem);
    			
    			entity.getItems().add(selectedIndex + 1, detail);
    			
    			detail.setOwner(entity);
    		}
    	}
    	
    }

	public void addDiscountExpenseAdditionToItem() {
        log.info("Adding discount or expense addition to line...");
    	manualFlush();
		
    	TekirInvoiceDetail detail = new TekirInvoiceDetail();

		detail.setProductType(selectedDetail.getProductType());
		detail.setProduct(selectedDiscountExpense);
		detail.getTaxExcludedAmount().setCurrency(entity.getDocCurrency());
		
		DiscountOrExpense doe = null;
		if (detail.getProductType().equals(ProductType.DiscountAddition)) {
			doe = detail.getDiscount();
			doe.setRate(selectedDiscountExpense.getDiscountOrExpense().getRate());
			doe.setValue(selectedDetail.getDiscount().getValue());
		} else {
			doe = detail.getExpense();
			doe.setRate(selectedDiscountExpense.getDiscountOrExpense().getRate());
			doe.setValue(selectedDetail.getExpense().getValue());
		}

		doe.setPercentage(false);
		doe.setCurrency(entity.getDocCurrency());

    	setLocalAmountOf(doe);

		entity.getItems().add(detail);
		detail.setOwner(entity);
	}
	
    public void addDiscountToItem() {
        log.info("Adding discount to line...");
    	manualFlush();

    	TekirInvoiceDetail detail = new TekirInvoiceDetail();
    	detail.setProductType(ProductType.Discount);
    	detail.getTaxExcludedAmount().setCurrency(entity.getDocCurrency());
    	
    	DiscountOrExpense discount = null;
    	if (selectedDiscountExpense != null) {

    		discount = selectedDiscountExpense.getDiscountOrExpense();

    		detail.getDiscount().setPercentage(discount.getPercentage());
    		detail.getDiscount().setRate(discount.getRate());
    		detail.getDiscount().setValue(discount.getValue());
    		detail.getDiscount().setCurrency(entity.getDocCurrency());

	    	setLocalAmountOf(detail.getDiscount());

    		TekirInvoiceDetail selectedItem = entity.getItems().get(selectedIndex);
    		
    		if (selectedItem != null) {
    			selectedItem.getChildList().add(detail);
    			detail.setParent(selectedItem);
    			
    			entity.getItems().add(selectedIndex + 1, detail);

    			detail.setOwner(entity);
    		}
    	}
    	
    }

	public void calculateEverything() {
		try {
			BigDecimal difference = BigDecimal.ZERO;
			TekirInvoiceDetail item = null;
			for (int i=0;i<entity.getItems().size();i++) {
				item = entity.getItems().get(i);

				if (item.getProductType().equals(ProductType.Product) || 
						item.getProductType().equals(ProductType.Service)) {

					difference = checkTotalAmountChanges(item);
					if (difference.compareTo(BigDecimal.ZERO) != 0) {
						log.info("Satır toplam tutarında değişiklik var! Index :{0}", i);
	
						DiscountOrExpense discount = new DiscountOrExpense();
						
						discount.setCurrency(getTender().getDocCurrency());
						discount.setValue(difference.abs());
						discount.setPercentage(false);
	
						setLocalAmountOf(discount);
						
						TekirInvoiceDetail discountLine = new TekirInvoiceDetail();
						
						discountLine.setProductType(ProductType.Discount);
						discountLine.setDiscount(discount);
						discountLine.getTaxExcludedAmount().setCurrency(getTender().getDocCurrency());
	
						discountLine.setOwner(entity);
						
						discountLine.setParent(item);
	
						item.getOwner().getItems().add(i+1,discountLine);
						
						item.getChildList().add(discountLine);
					}

				}

			}
			calculateEverything(entity);
		} catch (Exception e) {
			log.error("Fatura hesaplamaları yapılırken hata meydana geldi. Sebebi :{0}", e);
			facesMessages.add(Severity.ERROR, "Fatura hesaplamaları yapılırken hata meydana geldi. Sebebi :{0}",e);
		}
	}
	
	public void sendToPosPrinter() {
		try {
			clearUnknownDetails();
			
			PosPrinterHome printerHome = (PosPrinterHome)Component.getInstance("posPrinterHome", true);
			printerHome.sendToPosPrinter(entity);
		} catch (Exception e) {
			log.error("Yazıcıya gönderirken hata oluştu. Sebebi {0}", e);
			facesMessages.add(Severity.ERROR,"Yazıcıya gönderirken hata oluştu. Sebebi {0}", e.getMessage());
		}
	}
	
	public void selectProduct(Integer ix) {
		log.info("Product selected on line :{0}", ix.intValue());

		manualFlush();
		try {
			prepareSelectedLine(ix, false);
			
		} catch (Exception e) {
			log.error("Hata: {0}", e);
			facesMessages.add("Urun secildikten sonra hata meydana geldi. Sebebi \n:{0}",e.getMessage());
		}
	}

	public void prepareSelectedLine(int indexOfDetail,boolean refreshLine) throws Exception {
		TekirInvoiceDetail aDetail = entity.getItems().get(indexOfDetail);
		Product aProduct = aDetail.getProduct();
		
		if (aProduct == null) return;

		aDetail.setProductType(aProduct.getProductType());
		aDetail.getQuantity().setUnit(aProduct.getUnit());
		aDetail.setUnitPrice(new MoneySet());
		aDetail.setForeignUnitPrice(new MoneySet());
		aDetail.setTaxExcludedAmount(new MoneySet(entity.getDocCurrency()));

		if (!refreshLine) {
			aDetail.getQuantity().setValue(1.0);
		}
		
		PriceItemDetail priceItemDetail = priceProvider.findSalePriceItemDetailForProduct(aProduct);

		setLocalAmountOf(priceItemDetail.getGrossPrice());
		aDetail.setUnitPrice( convertToCurrency(priceItemDetail.getGrossPrice(), entity.getDocCurrency()) );
		
		MoneySet lineAmount = new MoneySet();
		lineAmount.setCurrency(aDetail.getUnitPrice().getCurrency());
		
		BigDecimal unitPrice = aDetail.getUnitPrice().getValue();
		BigDecimal quantity = BigDecimal.valueOf(aDetail.getQuantity().getValue());
		
		lineAmount.setValue(unitPrice.multiply(quantity));
		
		
		aDetail.setTaxExcludedAmount(lineAmount);

		calculateTaxExcludedUnitPriceAndAmount(aDetail);

		if (priceItemDetail.getHasDiscount()) {
			addDiscountLine(priceItemDetail, indexOfDetail);
		} 

		calculateForeignUnitPrice(indexOfDetail);
	}

	public void prepareAllProductAndServiceLines() {
		for (TekirInvoiceDetail item : entity.getProductItems()) {
			calculateTaxExcludedUnitPriceAndAmount(item);
		}
	}
	
	public void selectProductWithBarcode(Integer ix) {
		log.info("Product selected with barcode on line :{0}", ix.intValue());
		manualFlush();
		
		try {
			TekirInvoiceDetail detail = entity.getItems().get(ix);

			Product product = generalSuggestion.findProductWithBarcode( addZerosToBarcode(detail.getBarcode()) );
			
			if (product != null) {
				detail.setProduct(product);

				prepareSelectedLine(ix,false);
				
				//Bir satırı doldurduk. Yeni bir tane daha açalım.
				createNewDetail();
			} else {
				detail.setProduct(null);
				detail.setProductType(ProductType.Unknown);
				detail.getQuantity().setUnit(null);
				detail.getQuantity().setValue(0.0);
			}
		} catch (Exception e) {
			log.error("Hata: {0}", e);
			facesMessages.add("Urun secildikten sonra hata meydana geldi. Sebebi :{0}",e.getMessage());
		}
	}
	
	private String addZerosToBarcode(String aBarcode) {
		StringBuffer sb = new StringBuffer(aBarcode);
		if (sb.length() < 5) {
			while (sb.length() != 13) {
				sb.insert(0, "0");
			}
		}
		return sb.toString();
	}
	
	/**
	 * Parent detaya fiyat listesi üzerinden gelen indirimi ekler.
	 * @param priceItemDetail
	 * @param parentDetail
	 */
	private void addDiscountLine(PriceItemDetail priceItemDetail,int indexOfDetail) {
		TekirInvoiceDetail parentDetail = entity.getItems().get(indexOfDetail);
		
		MoneySet discountPrice = priceItemDetail.getDiscountPrice();

		TekirInvoiceDetail discountDetail = new TekirInvoiceDetail();
		discountDetail.setProductType(ProductType.Discount);

		
		DiscountOrExpense doe = new DiscountOrExpense();
		doe.setCurrency(discountPrice.getCurrency());
		doe.setLocalAmount(discountPrice.getLocalAmount());
		doe.setPercentage(priceItemDetail.getDiscountType());
		doe.setRate(priceItemDetail.getDiscountRate());
		doe.setValue(discountPrice.getValue());

		discountDetail.setDiscount(doe);
		
		discountDetail.setOwner(entity);

		entity.getItems().add(indexOfDetail + 1, discountDetail);

		discountDetail.setParent(parentDetail);
		parentDetail.getChildList().add(discountDetail);
		log.info("Adding discount line to index: {0}", indexOfDetail + 1);
	}

	@Override
	@Transactional
	public String save() {
		log.info("Saving invoice. Invoice serial :{0}", entity.getSerial());
		String result = BaseConsts.FAIL;

		try {
			clearUnknownDetails();

			calculateEverything();

			makeEntityValidations();

			setParentOfLists();
			
			persistParents();

			copyDeliveryContactInfoFromContactCard();

			result = super.save();

			if (result.equals(BaseConsts.SUCCESS) && !entity.isProformaDocument()) {

				getProformaInvController().save(entity);
				
				if (entity.getTradeAction().equals(TradeAction.SaleReturn))  {
                    //FIXME: satis iade faturasinin alis ft. tipinde irsaliyesinin, pro_txn,
					updateReturnedStatusOfInvoice();
				}
				setClosedQuantitiesForOrderNotes();

				setPaymentTableReferenceIds();

				updateStatusOfOrderNotes();
				
				updateOldQuantities();

				//irsaliyeyi kesiyoruz.
				if (entity.getDocumentType().equals(DocumentType.SaleShipmentInvoice) ||
                        entity.getDocumentType().equals(DocumentType.PurchaseShipmentInvoice) ) {
					TekirShipmentNote tsn = saveShipmentNoteForInvoice();
					productTxnAction.createProductTxnRecordsForShipment(tsn);
				}
				
				//cari txn borc yansımaları...
				financeTxnAction.createFinanceTxnRecordsForInvoice(entity);

                //vergi txn yansimasi
                taxTxnAction.createTaxTxnRecordsForInvoice(entity);

				//Eğer katkı satırları varsa, kurum txn yansımalarını yapar.
				foundationTxnAction.createFoundationTxnRecordsForInvoice(entity);

				Payment collection = saveCollectionForInvoice();
				if (collection != null && collection.getItems().size() >0 ) {
					
					//kredi kartli satis yansimasi
					posTxnAction.createPosTxnRecordsForCollection(collection);
					
					//kasaya nakit satis yansimasi
					accountTxnAction.savePayment(collection);
					
					//cariye tahsilat yansimasi
					financeTxnAction.createFinanceTxnRecordsForPayment(collection);
				}

				//Kullanılan kaparoları iptal eder.
				if (entity.getPaymentTable() != null) {
					accountTxnAction.updateTradeinFields(entity.getPaymentTable(), false);					
					posTxnAction.updateTradeinFields(entity.getPaymentTable(), false);					
				}
                
				updateFoundationTxnInformations();
			}			
			entityManager.flush();
			events.raiseEvent("refreshBrowser:com.ut.tekir.entities.shp.TekirShipmentNote");
		} catch (Exception e) {
			facesMessages.add(Utils.getExceptionMessage(e));
			log.error("Hata: #0", e);
			result = BaseConsts.FAIL;
		}
		return result;
	}

	private void updateFoundationTxnInformations() {
		log.info("Updating foundation txn informations...");
		for (FoundationTxn txn : txnList) {
			txn.setRepaidStatus(true);
			txn.setReferenceId(entity.getId());
			txn.setReferenceDocumentType(entity.getDocumentType());
			entityManager.merge(txn);
		}

		if (txnList != null && txnList.size() >0 ) {
			FoundationTxn txn = new FoundationTxn();

			FoundationTxn firstElement = txnList.get(0);
			
			txn.setFoundation(firstElement.getFoundation());
			txn.setDate(entity.getDate());
			txn.setSerial(entity.getSerial());
			txn.setReference(entity.getReference());
			txn.setDocumentId(entity.getId());
			txn.setDocumentType(entity.getDocumentType());
			txn.setInfo(entity.getContact().getName() + " kurumunun yapmış olduğu ödemedir.");
			txn.setActive(true);
			txn.setReferenceId(entity.getId());
			txn.setReferenceDocumentType(entity.getDocumentType());
			txn.setRepaidStatus(true);
			
			txn.setAction(FinanceAction.Credit);

			txn.setAmount(new MoneySet(entity.getGrandTotal()));
			
			txn.setAdverseCode(entity.getContact().getCode());
			txn.setAdverseName(entity.getContact().getName());
			

			txn.setMaturityDate(entity.getDate());

			entityManager.persist(txn);
		}
	}
	
	private void copyDeliveryContactInfoFromContactCard() {
		Contact contact = entity.getContact();

		entity.setDeliveryPerson(contact.getPerson());
		entity.setDeliveryCompany(contact.getCompany());
		entity.setDeliveryFullname(contact.getFullname());
		entity.setDeliveryTaxNumber(contact.getTaxNumber());
		entity.setDeliveryTaxOffice(contact.getTaxOffice());
        entity.setDeliverySsn(contact.getSsn());

	}

	private void setPaymentTableReferenceIds() {
		PaymentTable pt = entity.getPaymentTable();
		if (pt != null) {
			for (PaymentTableDetail pdt : pt.getDetailList()) {
				if (pdt.getReferenceId() != null) {
					PaymentTableDetail detail = getPaymentTableDetail(pdt.getReferenceId());
					
					if (detail != null) {
						detail.setReferenceId(pdt.getId());
						
						entityManager.merge(detail);
					}
				}
			}
		}
	}

	private PaymentTableDetail getPaymentTableDetail(Long aDetailId) {
		PaymentTable pt = null;
		for (InvoiceOrderLink ol : entity.getOrderLinks()) {
			pt = ol.getOrderNote().getPaymentTable();
			
			for (PaymentTableDetail ptd : pt.getDetailList()) {
				if (ptd.getId().equals(aDetailId)) return ptd;
			}
			
		}
		return null;
	}

	private void updateStatusOfOrderNotes() {

		for (InvoiceOrderLink ol : entity.getOrderLinks()) {
			TekirOrderNote ton = ol.getOrderNote();

            int totalLineSize = 0;
			int unclosedLineSize = 0;
			for (TekirOrderNoteDetail det : ton.getItems()) {
				if (det.getProductType().equals(ProductType.Product) ) {

                    if (!det.isClosed()) {
                        unclosedLineSize++;
                    }
                    totalLineSize++;
                }
			}

            if (unclosedLineSize == totalLineSize) {
				ton.setStatus(OrderStatus.Open);
			} else if (unclosedLineSize == 0) {
				ton.setStatus(OrderStatus.Closed);
			} else if (unclosedLineSize < totalLineSize) {
				ton.setStatus(OrderStatus.Processing);
			}
			entityManager.merge(ton);
		}
	}

	private void setClosedQuantitiesForOrderNotes() {
		for (TekirInvoiceDetail item : entity.getItems()) {
			if (item.getProductType().equals(ProductType.Product)) {
				TekirOrderNoteDetail orderNoteDetail = getOrderNoteDetail(item.getReferenceDocumentId());
				
				if (orderNoteDetail != null) {
					Double orderNoteQuantity = orderNoteDetail.getQuantity().getValue();
					Double orderNoteClosedQuantity = orderNoteDetail.getClosedQuantity();

					Double orderNoteUsedQuantity = oldQuantities.get(item.getProduct());
					Double orderNoteRemainingQuantity = null;
					//edit edeceğiz. o yüzden eski miktarı ekliyoruz.
					if (orderNoteUsedQuantity != null) {
						orderNoteRemainingQuantity = orderNoteQuantity - orderNoteClosedQuantity + orderNoteUsedQuantity;
					} else  {
						orderNoteRemainingQuantity = orderNoteQuantity - orderNoteClosedQuantity;
					}
					
					if (orderNoteRemainingQuantity.compareTo(item.getQuantity().getValue()) == 0) {
						orderNoteDetail.setClosedQuantity(orderNoteQuantity);
						orderNoteDetail.setClosed(true);

					} else if (orderNoteRemainingQuantity.compareTo(item.getQuantity().getValue()) < 0) {
						throw new RuntimeException("Sipariş edilen miktarı aşacak miktar giremezsiniz!");
						
					} else if (orderNoteRemainingQuantity.compareTo(item.getQuantity().getValue()) > 0) {
						if (orderNoteUsedQuantity != null) {
							orderNoteDetail.setClosedQuantity(orderNoteClosedQuantity + item.getQuantity().getValue() - orderNoteUsedQuantity);
						} else {
							orderNoteDetail.setClosedQuantity(orderNoteClosedQuantity + item.getQuantity().getValue());
						}
						orderNoteDetail.setClosed(false);
					}
					orderNoteDetail.setReferenceDocumentId(item.getId());
					orderNoteDetail.setReferenceDocumentType(entity.getDocumentType());
					entityManager.merge(orderNoteDetail);
				}
			}
		}
		entityManager.flush();
	}

	private TekirOrderNoteDetail getOrderNoteDetail(Long aDetailId) {
		TekirOrderNote on = null;
		for (InvoiceOrderLink ol : entity.getOrderLinks()) {
			on = ol.getOrderNote();
			
			for (TekirOrderNoteDetail det : on.getItems()) {
				if (det.getId().equals(aDetailId)) return det;
			}
		}
		return null;
	}

	private void updateReturnedStatusOfInvoice() {
		Map<Long, TekirInvoice> returnedInvoiceMap = new HashMap<Long, TekirInvoice>();

		TekirInvoice returnedInvoice = null;
		TekirInvoiceDetail returnedInvoiceDetail = null;
		for (TekirInvoiceDetail item : entity.getItems()) {
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

	/**
	 * Tipi unknown olarak işaretlenmiş olan satırları listeden kaldırır.
	 */
	private void clearUnknownDetails() {
		log.info("Unknown satırları siliniyor.");
		TekirInvoiceDetail det = null;
		for (int i = entity.getItems().size() - 1; i >= 0 ; i--) {
			det = entity.getItems().get(i);
			if (det.getProductType().equals(ProductType.Unknown)) {
				entity.getItems().remove(i);
				log.info("Unknown tipli satır bulundu. Index:{0}", i);
			}
		}
	}

	private TekirShipmentNote saveShipmentNoteForInvoice() {
		if (entity.getId() != null) {
			deleteShipmentNotesOfInvoice();
		}
		TekirShipmentNote tsn = createShipmentNoteFromInvoice();
		
		tsn.setInvoice(getEntity());

		entityManager.persist(tsn);

		return tsn;
	}

	private TekirShipmentNote createShipmentNoteFromInvoice() {
		log.info("Creating shipment note from invoice. Invoice code :{0}", entity.getSerial());
		TekirShipmentNote sn = new TekirShipmentNote();
		sn.setInvoice(entity);
		sn.setActive(Boolean.TRUE);
       
		//sn.setTradeAction(TradeAction.Sale);  --ReturnSale de buraya dusuyor...
        sn.setTradeAction(entity.getTradeAction());
		sn.setContact(entity.getContact());
		sn.setCode(entity.getCode());
		//sn.setDocumentType(DocumentType.SaleShipmentNote); --ReturnSale de buraya dusuyor...
		if (entity.getDocumentType().equals(DocumentType.PurchaseShipmentInvoice) ) {
                sn.setDocumentType(DocumentType.PurchaseShipmentNote);        
        }  else  sn.setDocumentType(DocumentType.SaleShipmentNote); 


		//FIXME: buraya hangi tarihi koymalı? Teslim tarihini mi?
		sn.setDate(entity.getDate());
		sn.setInfo1(entity.getInfo1());
		sn.setInfo2(entity.getInfo2());
		sn.setReference(entity.getReference());
		sn.setSerial(entity.getSerial());
		sn.setWarehouse(entity.getWarehouse());

		sn.setCreateDate(entity.getCreateDate());
		sn.setCreateUser(entity.getCreateUser());
		sn.setUpdateUser(entity.getUpdateUser());
		sn.setUpdateDate(entity.getUpdateDate());
		sn.setWorkBunch(entity.getWorkBunch());

		TekirShipmentNoteDetail snd = null;
		
		for (TekirInvoiceDetail item : entity.getProductItems()) {
			snd = new TekirShipmentNoteDetail();
			snd.setAmount(item.getAmount());
			snd.setBeforeTax(item.getBeforeTax());
			snd.setDiscount(item.getDiscount());
			snd.setDiscountStyle(item.getDiscountStyle());
			snd.setExpense(item.getExpense());
			snd.setExpenseStyle(item.getExpenseStyle());
			snd.setFee(item.getFee());
			snd.setGrandTotal(item.getGrandTotal());
			snd.setInfo(item.getInfo());
			snd.setLineCode(item.getLineCode());
			snd.setProduct(item.getProduct());
			snd.setProductType(item.getProductType());
			snd.setQuantity(item.getQuantity());
			snd.setTax1(item.getTax1());
			snd.setTax2(item.getTax2());
			snd.setTax3(item.getTax3());
			snd.setTax4(item.getTax4());
			snd.setTax5(item.getTax5());
			snd.setTaxExcludedAmount(item.getTaxExcludedAmount());
			snd.setTaxExcludedTotal(item.getTaxExcludedTotal());
			snd.setTaxExcludedUnitPrice(item.getTaxExcludedUnitPrice());
			snd.setTaxTotalAmount(item.getTaxTotalAmount());
			snd.setTotalAmount(item.getTotalAmount());
			snd.setUnitPrice(item.getUnitPrice());

			if (item.getReferenceDocumentId() != null) {
				snd.setReferenceDocumentId(item.getReferenceDocumentId());
				snd.setReferenceDocumentType(item.getReferenceDocumentType());
			}
			
			snd.setOwner(sn);
			sn.getItems().add(snd);
		}
		return sn;
	}

	/**
	 * Fatura ile bağlantılı olan irsaliyeleri ve irsaliyelerin bağlı
	 * olduğu product txn kayıtlarını siler.
	 */
	@SuppressWarnings("unchecked")
	private void deleteShipmentNotesOfInvoice() {
		log.info("Deleting shipment notes of invoice...");

		if (entity.getDocumentType().equals(DocumentType.SaleShipmentInvoice) || 
				entity.getDocumentType().equals(DocumentType.PurchaseShipmentInvoice)) {
			List<TekirShipmentNote> tsn = entityManager.createQuery("select sn from TekirShipmentNote sn where " +
																	"invoice = :invoice")
																	.setParameter("invoice", entity)
																	.getResultList();

			for (TekirShipmentNote sn: tsn) {
				productTxnAction.deleteProductTxnRecordsForShipmentNote(sn);
				entityManager.remove(sn);
			}
		} else {
			for (TekirShipmentNote tsn : entity.getShipmentList()) {

				tsn.setInvoice(null);
			}
		}
		entityManager.flush();
	}

	private void persistParents() {
		TekirInvoiceDetail detail = null;
		for (int i=0;i<entity.getItems().size();i++) {
			detail = entity.getItems().get(i);
		
			if (detail.getChildList() != null && detail.getChildList().size() != 0) {
				if (detail.getId() == null) {
					entityManager.persist(detail);

//					detail.setParent(detail);
					for (TekirInvoiceDetail child : detail.getChildList()) {
						child.setParent(detail);
					}
				}
			}
		}
	}

	public boolean hasPaymentTableExceededInvoiceTotal() {
		BigDecimal remainder = getPaymentTableRemainder();
		
		if (remainder.compareTo(BigDecimal.ZERO) < 0) {
			return true;
		}
		return false;
	}

	public void makeEntityValidations() {
		masterValidations();
		
		detailValidations();
	}

	public void masterValidations() {
		if (entity.getItems().size() == 0 ) {
			throw new RuntimeException("#{messages['beanMessages.AtLeastOneItemRequired']}");
		}
		
		if (entity.getContact().getDefaultAddress() == null) {
			throw new RuntimeException("Faturanın kesilebilmesi için seçili carinin en azından ön tanımlı bir adresi olmalıdır.");
		}
		getLimitationChecker().clearMessages();
		getLimitationChecker().contactCreditAndRisk(entity.getContact(), getRiskAmount());
	}
	
	private BigDecimal getRiskAmount() {
		return getEntity().getGrandTotal().getValue().subtract(getPaymentTableTotal())
													 .add(getOldState().getContactLimit());
	}
	
	public void detailValidations() {
		TekirInvoiceDetail detail = null;
		double activeLevel = 0d;
		for(int i=0;i<entity.getItems().size();i++) {
			detail = entity.getItems().get(i);

			if (detail.getProductType().equals(ProductType.Product) ||
					detail.getProductType().equals(ProductType.Service)) {

				if (detail.getProduct() == null) {
					throw new RuntimeException(i+1 + ". satırda hizmet/ürün seçilmemiş!" );
				}

//				activeLevel = stockLevelFinder.findActiveLevel(detail.getProduct(),entity.getWarehouse());
//				if (activeLevel < detail.getQuantity().getValue()) {
//					throw new Exception(i+1 + ". satırdaki ürün için yeterli stok bulunamadı! Stokta bulunan miktar :"+ activeLevel );
//				}

				if (detail.getQuantity().getValue() <= 0) {
					throw new RuntimeException(i+1 + ". satırda Sıfırdan büyük bir değer girmelisiniz!" );
				}

				getLimitationChecker().saleInvoiceZeroLineAmount(i, detail.getTaxExcludedUnitPrice().getValue());
		
                                //İçinde bulunan irsaliytenin ID'sini kontrol için gönderiyoruz.
                                Long id = null;
                                if( entity.getShipmentList().size() > 0 ){
                                    id = entity.getShipmentList().get(0).getId();
                                }
				getLimitationChecker().stockLevel(i, detail.getProduct(), detail.getQuantity().getValue(),entity.getWarehouse().getCode(), id);

				//son satış fiyatlarını güncelliyoruz.
				if (!entity.getTradeAction().equals(TradeAction.SaleReturn)) {
					updateLastSalePrices(detail);
				}

				updateClerk(detail);
			}
		}
	}

	private void updateClerk(TekirInvoiceDetail item) {
		if (entity.getClerk() != null) {
			item.setClerk(entity.getClerk());
		}
	}
	
    private void updateLastSalePrices(TekirInvoiceDetail item) {
		MoneySet lastSalePrice = item.getProduct().getLastSalePrice();
		
		TaxEmbeddable tax1 = item.getTax1();
		if (tax1 != null) {
			if (tax1.getTaxIncluded()) {
				BigDecimal quantityValue = BigDecimal.valueOf(item.getQuantity().getValue());
				BigDecimal purchaseValue = item.getTotalAmount().getValue().divide(quantityValue, 2, RoundingMode.HALF_UP);

				BigDecimal purchaseLocalAmount = item.getTotalAmount().getLocalAmount().divide(quantityValue, 2, RoundingMode.HALF_UP);

				lastSalePrice.setValue(purchaseValue);
				lastSalePrice.setLocalAmount(purchaseLocalAmount);
			} else {
				BigDecimal quantityValue = BigDecimal.valueOf(item.getQuantity().getValue());
				BigDecimal purchaseValue = item.getTaxExcludedTotal().getValue().divide(quantityValue, 2, RoundingMode.HALF_UP);

				BigDecimal purchaseLocalAmount = item.getTaxExcludedTotal().getLocalAmount().divide(quantityValue, 2, RoundingMode.HALF_UP);

				lastSalePrice.setValue(purchaseValue);
				lastSalePrice.setLocalAmount(purchaseLocalAmount);
			}
		}
    }

	private void setParentOfLists() {
		for(TekirInvoiceTaxSummary item : entity.getTaxSummaryList()) {
			item.setOwner(entity);
		}
		for(TekirInvoiceCurrencySummary item : entity.getCurrencySummaryList()) {
			item.setOwner(entity);
		}
		for(TekirInvoiceCurrencyRate item : entity.getCurrencyRateList()) {
			item.setOwner(entity);
		}
		for(InvoicePaymentPlanItem item : entity.getPaymentPlanItems()) {
			item.setOwner(entity);
		}
	}

	@Override
	public void deleteLine(Integer ix) {
		log.info("Deleting line. Line number :{0}", ix.intValue());
		manualFlush();
	
		TekirInvoiceDetail item = getEntity().getItems().get(ix.intValue());

		if (item.getProductType().equals(ProductType.Product) || 
				item.getProductType().equals(ProductType.Service)) {
			for(int i=item.getChildList().size() - 1;i>=0;i--) {
				entity.getItems().remove(ix.intValue() + 1);
			}
			entity.getItems().remove(ix.intValue());

			if (item.getReferenceDocumentId() != null) {
				TekirOrderNote ton = null;
				for (InvoiceOrderLink ol : entity.getOrderLinks()) {
					ton = ol.getOrderNote();
					
					for (TekirOrderNoteDetail ond : ton.getItems()) {
						if (item.getId() != null && item.getReferenceDocumentId().equals(ond.getId())) {
							ond.setClosedQuantity(ond.getClosedQuantity() - item.getQuantity().getValue());
							ond.setReferenceDocumentId(null);
							ond.setReferenceDocumentType(null);
							ond.setClosed(false);
							entityManager.merge(ond);
							break;
						}
					}
				}
			}

		} else if (item.getProductType().equals(ProductType.Discount)){
			item.getParent().getChildList().remove(item);
			
			entity.getItems().remove(ix.intValue());

		} else if (item.getProductType().equals(ProductType.DocumentDiscount) || 
				item.getProductType().equals(ProductType.ContactDiscount)){
			entity.getItems().remove(ix.intValue());

		} else {
			entity.getItems().remove(ix.intValue());
		}
	}

	public void deleteReturnLine(Integer ix) {
		log.info("Deleting line. Line number :{0}", ix.intValue());
		manualFlush();
	
		TekirInvoiceDetail item = getEntity().getItems().get(ix.intValue());

		if (item.getProductType().equals(ProductType.Product) || 
				item.getProductType().equals(ProductType.Service)) {

			TekirInvoiceDetail childItem = null;

			for(int i=item.getChildList().size() - 1;i>=0;i--) {
				childItem = entity.getItems().get(ix.intValue());
				
				if (childItem.getReferenceDocumentId() != null) {
					resetReturnInvoiceFields(childItem.getReferenceDocumentId());
				}
				entity.getItems().remove(ix.intValue() + 1);
			}
			
			if (item.getReferenceDocumentId() != null) {
				resetReturnInvoiceFields(item.getReferenceDocumentId());
			}
			entity.getItems().remove(ix.intValue());

		} else if (item.getProductType().equals(ProductType.Discount)){
			item.getParent().getChildList().remove(item);
			
			entity.getItems().remove(ix.intValue());

		} else if (item.getProductType().equals(ProductType.DocumentDiscount) || 
				item.getProductType().equals(ProductType.ContactDiscount)){

			if (item.getReferenceDocumentId() != null) {
				resetReturnInvoiceFields(item.getReferenceDocumentId());
			}
			entity.getItems().remove(ix.intValue());
		} else {
			entity.getItems().remove(ix.intValue());
		}
	}

	public void addContactDocumentDiscount() {
    	
    	Contact contact = entity.getContact();
        
    	if (contact != null) {
        	
        	log.info("Adding contact document discount. Contact code :{0} ",entity.getContact().getCaption());
        	clearContactDiscounts();

        	if (contact.getHasDiscount()) {
        		TekirInvoiceDetail discountLine = new TekirInvoiceDetail();
        		discountLine.setProductType(ProductType.ContactDiscount);
        		DiscountOrExpense doe = discountLine.getDiscount();
        		
        		doe.setCurrency(contact.getDiscount().getCurrency());
        		doe.setPercentage(contact.getDiscount().getPercentage());
        		doe.setRate(contact.getDiscount().getRate());
        		doe.setValue(contact.getDiscount().getValue());
        		

        		discountLine.setOwner(entity);
    			entity.getItems().add(discountLine);
        	}

        	setPaymentPlan();
        	setCurrencyRateType();
        }
	}
	
	private void setPaymentPlan() {
		Contact contact = entity.getContact();
		entity.setPaymentPlan(contact.getPaymentPlan());
	}

	private void clearContactDiscounts() {
		TenderDetailBase item = null;
		for (int i=0;i<entity.getItems().size();i++) {
			item = entity.getItems().get(i);
			if (item.getProductType().equals(ProductType.ContactDiscount)) {
				entity.getItems().remove(i);
				return;
			}
		}
	}

	@Override
	@Transactional
	public String delete() {
		String result = BaseConsts.SUCCESS;
		try {
			deleteShipmentNotesOfInvoice();

			financeTxnAction.deleteFinanceTxnRecordsForInvoice(entity);

			taxTxnAction.deleteTaxTxnRecordsForInvoice(entity);

			foundationTxnAction.deleteFoundationTxnRecordsForInvoice(entity);

            deleteCollectionOfInvoice();
            
            PaymentTable pt = entity.getPaymentTable();
            if (pt != null) {
				accountTxnAction.updateTradeinFields(pt, true);
				posTxnAction.updateTradeinFields(pt, true);
            	for (int i=pt.getDetailList().size()-1;i>=0;i--) {
					deletePaymentTableDetail(i);
				}
            }
            
			if (entity.getTradeAction().equals(TradeAction.SaleReturn))  {
                TekirInvoiceDetail item = null;
				for (int i=entity.getItems().size()-1;i>=0;i-- ) {
					item = entity.getItems().get(i);
					if (item.getReferenceDocumentId() != null) {
						resetReturnInvoiceFields(item.getReferenceDocumentId());
					}
    			}
                updateReturnedStatusOfInvoice();
            } 

            updateStatusOfOrderNotes();

            getProformaInvController().delete(entity);
            
			result = super.delete();
		} catch (Exception e) {
			log.error("Fatura silinirken hata oluştu. Sebebi :#0", e);
			facesMessages.add(Severity.ERROR, "Fatura silinirken hata oluştu. Sebebi :{0}",e);
			result = BaseConsts.FAIL;
		}
		return result;
	}

	public void setAddress() {
		log.info("Updating address list. Contact code :{0}",entity.getContact().getCaption());
		if (selectedAddress != null) {
			entity.setDeliveryAddress(selectedAddress.getAddress());
		} else {
			entity.setDeliveryAddress(null);
		}
	}

    public void toggleMiniTable() {
    	if (showMiniTable) {
    		showMiniTable = Boolean.FALSE;
    	} else {
    		showMiniTable = Boolean.TRUE;
    	}
    }

	@Override
	public void setId(Long id) {
        if (entity != null) {
            return;
        }

		super.setId(id);

		setTotalAmountTransientFields();
		updateOldQuantities();
		setCurrencyRateType();
		setOldStates();
	}

	private void setOldStates() {
		getOldState().setContactLimit(getPaymentTableTotal());
	}

	private void setCurrencyRateType() {
		if (entity.getContact() != null) {
			entity.setRateType(entity.getContact().getCurrencyRateType());
		}
	}

	private void setTotalAmountTransientFields() {
		for (TekirInvoiceDetail item : entity.getProductItems()) {
			item.setTotalAmountTransient(new MoneySet(item.getTotalAmount()));
		}
	}

	private void updateOldQuantities() {
		for (TekirInvoiceDetail item : entity.getProductItems()) {
			if (!oldQuantities.containsKey(item.getProduct())) {
				oldQuantities.put(item.getProduct(), item.getQuantity().getValue());
			} else {
				Double productQuantity = oldQuantities.get(item.getProduct());

				productQuantity = productQuantity + item.getQuantity().getValue();
			}
		}
	}

    @SuppressWarnings("unchecked")
	public List<TekirOrderNote> findOrders() {
    	try {
	    	if (entity.getContact() != null) {
	    		
				List<TekirOrderNote> resultList = entityManager.createQuery("select sn from TekirOrderNote sn where " +
																				  "sn.tradeAction=:tradeAction and " + 
																				  "(sn.status =:openStatus or sn.status =:processingStatus) and " + 
																				  "sn.contact=:contact ")
																				  .setParameter("contact", entity.getContact())
																				  .setParameter("tradeAction", TradeAction.Sale)
																				  .setParameter("openStatus", OrderStatus.Open)
																				  .setParameter("processingStatus", OrderStatus.Processing)
																				  .getResultList();
		
		        for (InvoiceOrderLink ol : entity.getOrderLinks()) {
		            if (resultList.contains(ol.getOrderNote())) {
		                resultList.remove(ol.getOrderNote());
		            }
		        }

		        orderList.clear();
		        
		        orderList.addAll(resultList);
	    	} 
		} catch (Exception e) {
			log.error("Hata oluştu. Sebebi :{0}", e);
		}
		return orderList;
	}

    public List<TekirInvoice> findInvoices() {
    	try {
    		if (entity.getContact() != null) {
    			invoiceSuggestion.setFilterModel(getFilterModel());
    			List<TekirInvoice> resultList = invoiceSuggestion.suggestInvoice();
    			
		        for (TekirInvoice inv: entity.getReturnInvoiceList()) {
		            if (resultList.contains(inv)) {
		                resultList.remove(inv);
		            }
		        }

    			invoiceList.clear();
    			
    			invoiceList.addAll(resultList);
    		} 
    	} catch (Exception e) {
    		log.error("Hata oluştu. Sebebi :{0}", e);
    	}
    	return invoiceList;
    }

    public List<ReturnInvoiceStatus> getPossibleStatusList () {
		List<ReturnInvoiceStatus> possibleStatusList = new ArrayList<ReturnInvoiceStatus>();
		possibleStatusList.add(ReturnInvoiceStatus.Open);
		possibleStatusList.add(ReturnInvoiceStatus.Processing);
		return possibleStatusList;
    }
    
    public void selectOrderNote(int ix) {
    	log.info("Selected order note. Index :{0}", ix);
    	manualFlush();

        TekirOrderNote on = orderList.get(ix);

        InvoiceOrderLink ol = new InvoiceOrderLink();
        ol.setInvoice(entity);
        ol.setOrderNote(on);
        
        entity.getOrderLinks().add(ol);


        entity.setClerk(on.getClerk());
        entity.setWarehouse(on.getWarehouse());

        
        orderList.remove(ix);

    	addOrderLines(on);
    	
    	addPaymentTableDetails(on);

    	calculateEverything();
    }

    private void addPaymentTableDetails(TekirOrderNote sn) {
    	PaymentTable pt = sn.getPaymentTable();
    	if (pt != null) {
    		
    		PaymentTable invoicePaymentTable = entity.getPaymentTable();
    		if (invoicePaymentTable == null) {
    			invoicePaymentTable = new PaymentTable();
    			entity.setPaymentTable(invoicePaymentTable);
    		}
    		PaymentTableDetail newDetail = null;
    		for (PaymentTableDetail item : pt.getDetailList()) {
                if (! item.isProcessed()) {
                    newDetail = new PaymentTableDetail();
                    newDetail.setAmount(item.getAmount());
                    newDetail.setPaymentType(item.getPaymentType());
                    newDetail.setReferenceId(item.getId());

                    if (item.getPaymentType().equals(PaymentType.CreditCard)) {
                        newDetail.setBank(item.getBank());
                        newDetail.setPeriod(item.getPeriod());
                        newDetail.setCreditCardNumber(item.getCreditCardNumber());
                        newDetail.setPos(item.getPos());
                        newDetail.setOwner(entity.getPaymentTable());
                    }

                    //FIXME:daha persist edilmediğinden referansı setlemeyecektir.
                    item.setReferenceId(newDetail.getId());
                    item.setProcessed(true);

                    invoicePaymentTable.getDetailList().add(newDetail);
                    newDetail.setOwner(invoicePaymentTable);
                }
    		}
    	}
	}

    private void addPaymentTableDetails(TekirInvoice inv) {
    	PaymentTable pt = inv.getPaymentTable();
    	if (pt != null) {
    		
    		PaymentTable invoicePaymentTable = entity.getPaymentTable();
    		if (invoicePaymentTable == null) {
    			invoicePaymentTable = new PaymentTable();
    			entity.setPaymentTable(invoicePaymentTable);
    		}
    		PaymentTableDetail newDetail = null;
    		for (PaymentTableDetail item : pt.getDetailList()) {
                newDetail = new PaymentTableDetail();
                newDetail.setAmount(item.getAmount());
                newDetail.setPaymentType(item.getPaymentType());
                newDetail.setReferenceId(item.getId());

                if (item.getPaymentType().equals(PaymentType.CreditCard)) {
                    newDetail.setBank(item.getBank());
                    newDetail.setPeriod(item.getPeriod());
                    newDetail.setCreditCardNumber(item.getCreditCardNumber());
                    newDetail.setPos(item.getPos());
                    newDetail.setOwner(entity.getPaymentTable());
                }

                invoicePaymentTable.getDetailList().add(newDetail);
                newDetail.setOwner(invoicePaymentTable);
    		}
    	}
	}

    public void selectInvoice(int ix) {
    	log.info("Selected invoice. Index :{0}", ix);
    	manualFlush();
    	
    	TekirInvoice inv = invoiceList.get(ix);
    	
    	
		if (entity.getTradeAction().equals(TradeAction.PurchseReturn))  {
            TekirInvoiceDetail item = null;
			for (int i=entity.getItems().size()-1;i>=0;i-- ) {
				item = entity.getItems().get(i);
				if (item.getReferenceDocumentId() != null) {
					resetReturnInvoiceFields(item.getReferenceDocumentId());
				}
			}
            updateReturnedStatusOfInvoice();
        } 

    	entity.getReturnInvoiceList().clear();
    	
    	entity.getReturnInvoiceList().add(inv);
    	inv.setReturnInvoice(entity);
    	inv.setReturnInvoiceStatus(ReturnInvoiceStatus.Open);

    	// faturanın iş takip numarasını iade faturasına setle.
    	if (entity.getWorkBunch() == null || !entity.getWorkBunch().equals(inv.getWorkBunch())){
    		entity.setWorkBunch(inv.getWorkBunch());
    	}
    	
    	
    	invoiceList.remove(ix);
    	
    	entity.getItems().clear();
        if (inv != null) {
			entity.setInfo("Bu fatura, " + inv.getSerial() + " no'lu faturanın iadesidir.");
            //satisdaki bilgiler ile ayni olsun
            entity.setClerk(inv.getClerk());
            entity.setWarehouse(inv.getWarehouse());
            entity.setAccount(inv.getAccount());
		}
    	addInvoiceLines(inv);

    	addPaymentTableDetails(inv);
    }

	private void resetReturnInvoiceFields(Long anInvoiceDetailId) {
		TekirInvoiceDetail referencedInvoiceItem = findInvoiceDetail(anInvoiceDetailId);

		//NOT:Alttaki alanlar üzerinde çalışma yapamıyoruz. Çünkü bu alanlar
		//sipariş, irsaliye vb. referans bilgilerini tutuyor olabilir.
		//referencedInvoiceItem.setReferenceDocumentId(null);
		//referencedInvoiceItem.setReferenceDocumentType(null);
		referencedInvoiceItem.setReturned(false);
		referencedInvoiceItem.getOwner().setReturnInvoice(null);
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

	private void addInvoiceLines(TekirInvoice inv) {
		for (TekirInvoiceDetail item : inv.getItems()) {
	    		if (item.getParent() == null && !item.isReturned()) {
	    		
    			TekirInvoiceDetail detail = new TekirInvoiceDetail();
    			
    			moveInvoiceFieldsToInvoice(item, detail);
    			detail.setReferenceDocumentId(item.getId());
    			detail.setReferenceDocumentType(inv.getDocumentType());
    			item.setReturned(true);
    			
    			entity.getItems().add(detail);
    			
    			detail.setOwner(entity);
    			for (TekirInvoiceDetail childItem : item.getChildList()) {
    				TekirInvoiceDetail childDetail = new TekirInvoiceDetail();
    				moveInvoiceFieldsToInvoice(childItem, childDetail);
    				
    				detail.getChildList().add(childDetail);
    				childDetail.setParent(detail);
    				
    				childDetail.setOwner(entity);
    				childDetail.setReferenceDocumentId(childItem.getId());
    				childDetail.setReferenceDocumentType(inv.getDocumentType());
    				childDetail.setReturned(true);
    				
    				entity.getItems().add(childDetail);
    			}
	    	}
		}
    }

    private Payment saveCollectionForInvoice() {
		if (entity.getId() != null) {
			deleteCollectionOfInvoice();
		}
		Payment collection = createCollectionFromPaymentTable();

		if (collection != null && collection.getItems() != null && collection.getItems().size() >0) {

			if (entity.getAccount() == null) {
				throw new RuntimeException("Kasa seçimini yapmanız gerekmektedir.");
			}

			collection.setInvoice(getEntity());
			
			entityManager.persist(collection);
		} else {
			collection = null;
		}
		return collection;
	}

    /**
	 * Fatura ile bağlantılı olan tahsilatları ve tahsilatların bağlı
	 * olduğu txn kayıtlarını siler.
	 */
    private Payment createCollectionFromPaymentTable() {
		log.info("Creating collection from order note. Order note serial :{0}", entity.getSerial());

		Payment collection = new Payment();
		collection.setAccount(entity.getAccount());
		collection.setActive(Boolean.TRUE);
		collection.setCode(entity.getCode());
		collection.setContact(entity.getContact());
		collection.setCreateDate(entity.getCreateDate());
        collection.setUpdateDate(entity.getUpdateDate());
        collection.setCreateUser(entity.getCreateUser());
        collection.setUpdateUser(entity.getUpdateUser());
		collection.setDate(entity.getDate());
		if (entity.getTradeAction().equals(TradeAction.SaleReturn)) {
			collection.setAction(FinanceAction.Debit);
		} else {
			collection.setAction(FinanceAction.Credit);
		}

		if (entity.getContact() != null) {
			collection.setDeliverer(entity.getContact().getName());//ödemeyi yapan.
		}

        collection.setInfo("Fatura: " + entity.getReference());
		collection.setSerial(sequenceManager.getNewSerialNumber( SequenceType.SERIAL_FUND_COLLECTION ));
        collection.setReference(collection.getSerial());

		collection.setTime(entity.getDate());

		collection.setCreateDate(entity.getCreateDate());
		collection.setCreateUser(entity.getCreateUser());
		collection.setUpdateUser(entity.getUpdateUser());
		collection.setUpdateDate(entity.getUpdateDate());

		BigDecimal totalAmount = BigDecimal.ZERO;

		PaymentTable pt = entity.getPaymentTable();
		if (pt != null) {
			PaymentItem collectionItem = null;
			Pos pos = null;
			for (PaymentTableDetail ptd : pt.getDetailList()) {
				//eğer ödeme satırı referansla gelmiş ise, tahsilat fişine ekleme.
				if (entity.getTradeAction().equals(TradeAction.SaleReturn) || ptd.getReferenceId() == null) {

					pos = ptd.getPos();
					if (pos != null) {
						collectionItem = new PaymentItemCreditCard();

						StringBuilder sb = new StringBuilder();

						if (pos.getBankAccount() != null) {
	                        sb.append(pos.getBank().getName()).append(", ");
						}

						if (ptd.getCreditCardNumber() != null && ptd.getCreditCardNumber().length() >0 ) {
							sb.append(ptd.getCreditCardNumber()).append(" numaralı kredi kartı tahsilatıdır.");
						} else {
							sb.append("kredi kartı tahsilatıdır.");
						}
						collectionItem.setInfo(sb.toString());

						((PaymentItemCreditCard)collectionItem).setPos(pos);
						((PaymentItemCreditCard)collectionItem).setCreditCardNumber(ptd.getCreditCardNumber());
						((PaymentItemCreditCard)collectionItem).setBank(ptd.getBank());
						((PaymentItemCreditCard)collectionItem).setPeriod(ptd.getPeriod());
					} else {
						collectionItem = new PaymentItem();
						collectionItem.setInfo("Nakit ");
					}

					setLocalAmountOf(ptd.getAmount());

					collectionItem.setPaymentTableReferenceId(ptd.getId());
					
					collectionItem.setAmount(ptd.getAmount());
					collectionItem.setLineType(ptd.getPaymentType());
					collectionItem.setWorkBunch(entity.getWorkBunch());

					collectionItem.setOwner(collection);
					collection.getItems().add(collectionItem);


					totalAmount = totalAmount.add(ptd.getAmount().getLocalAmount());

				}

			}
			collection.getTotalAmount().setValue(totalAmount);
		}
		return collection;
	}

    @SuppressWarnings("unchecked")
    private void deleteCollectionOfInvoice() {
		log.info("Deleting collection of invoice...");

		List<Payment> paymentList = entityManager.createQuery("select p from Payment p where " +
																"invoice = :invoice")
																.setParameter("invoice", entity)
																.getResultList();

		for (Payment p : paymentList) {

			financeTxnAction.deletePayment(p);
			posTxnAction.deletePosTxnRecordsForCollection(p);
			accountTxnAction.deletePayment(p);

			entityManager.remove(p);
		}
		entityManager.flush();
	}

	private void addOrderLines(TekirOrderNote tsn) {
    	boolean add = true;
		for (TekirOrderNoteDetail item : tsn.getItems()) {
    		if (item.getParent() == null && 
    				(item.getProductType().equals(ProductType.Product) ||
    				 item.getProductType().equals(ProductType.Service) )) {
	    		
	    		double remainingQuantity = item.getQuantity().getValue() - item.getClosedQuantity();
	    		
	    		if (item.getProductType().equals(ProductType.Product) &&
	    				remainingQuantity == 0d) {
	    			
	    			add = false;
	    		}
	    		if (add) {
	    			TekirInvoiceDetail detail = new TekirInvoiceDetail();
	    			
	    			moveOrderFieldsToInvoice(item, detail);
	    			detail.setReferenceDocumentId(item.getId());
	    			detail.setReferenceDocumentType(tsn.getDocumentType());
	    			
	    			
	    			entity.getItems().add(detail);
	    			
	    			detail.setOwner(entity);
	    			for (TekirOrderNoteDetail childItem : item.getChildList()) {
	    				TekirInvoiceDetail childDetail = new TekirInvoiceDetail();
	    				moveOrderFieldsToInvoice(childItem, childDetail);
	    				
	    				detail.getChildList().add(childDetail);
	    				childDetail.setParent(detail);
	    				
	    				childDetail.setOwner(entity);
	    				childDetail.setReferenceDocumentId(childItem.getId());
	    				childDetail.setReferenceDocumentType(tsn.getDocumentType());
	    				
	    				entity.getItems().add(childDetail);
	    			}
	    			if (item.getOrderDiscount().getValue().compareTo(BigDecimal.ZERO) != 0) {
	    				TekirInvoiceDetail childDetail = new TekirInvoiceDetail();
	    				childDetail.setProductType(ProductType.Discount);
	    				
	    				MoneySet orderDiscount = item.getOrderDiscount();
	    				
	    				DiscountOrExpense discount = new DiscountOrExpense();
	    				discount.setPercentage(false);
	    				discount.setCurrency(orderDiscount.getCurrency());
	    				

	    				BigDecimal quantityValue = BigDecimal.valueOf(item.getQuantity().getValue());
	    				discount.setLocalAmount(orderDiscount.getLocalAmount().divide(quantityValue, 2, RoundingMode.HALF_UP));
	    				discount.setValue(orderDiscount.getValue().divide(quantityValue, 2, RoundingMode.HALF_UP));


	    				childDetail.setDiscount(discount);

	    				detail.getChildList().add(childDetail);
	    				childDetail.setParent(detail);

	    				childDetail.setOwner(entity);
	    				childDetail.setReferenceDocumentId(item.getId());
	    				childDetail.setReferenceDocumentType(tsn.getDocumentType());

	    				entity.getItems().add(childDetail);
	    			}
	    		}
	    		add = true;
	    	}
		}
    }

	private void moveOrderFieldsToInvoice(TekirOrderNoteDetail fromDetail, TekirInvoiceDetail toDetail) {

		if (fromDetail.getAmount() != null ) toDetail.getAmount().moveFieldsOf(fromDetail.getAmount());
		if (fromDetail.getBeforeTax() != null ) toDetail.getBeforeTax().moveFieldsOf(fromDetail.getBeforeTax());
//		if (item.getChildList() != null ) detail.getBeforeTax().moveFieldsOf(item.getBeforeTax());
		if (fromDetail.getCurrencyOfItem() != null ) toDetail.getCurrencyOfItem();
		
		if (fromDetail.getDiscount() != null ) toDetail.getDiscount().moveFieldsOf(fromDetail.getDiscount());
		if (fromDetail.getDiscountStyle() != null ) toDetail.setDiscountStyle(fromDetail.getDiscountStyle());

		if (fromDetail.getExpense() != null ) toDetail.getExpense().moveFieldsOf(fromDetail.getExpense());
		if (fromDetail.getExpenseStyle() != null ) toDetail.setExpenseStyle(fromDetail.getExpenseStyle());

		if (fromDetail.getFee() != null ) toDetail.getFee().moveFieldsOf(fromDetail.getFee());
		if (fromDetail.getGrandTotal() != null ) toDetail.getGrandTotal().moveFieldsOf(fromDetail.getGrandTotal());
		if (fromDetail.getInfo() != null ) toDetail.setInfo(fromDetail.getInfo());
		if (fromDetail.getLineCode() != null ) toDetail.setLineCode(fromDetail.getLineCode());

//		if (item.getOwner() != null ) detail.setLineCode(item.getLineCode());
//		if (item.getParent() != null ) detail.setLineCode(item.getLineCode());
		if (fromDetail.getProduct() != null ) toDetail.setProduct(fromDetail.getProduct());
		if (fromDetail.getProductType() != null ) toDetail.setProductType(fromDetail.getProductType());

		Quantity quantity = fromDetail.getQuantity();
		if (quantity != null ) { 
			Quantity unclosedClosedQuantity = new Quantity(quantity.getValue() - fromDetail.getClosedQuantity(), quantity.getUnit());
				
			toDetail.setQuantity(unclosedClosedQuantity);

			BigDecimal unclosedClosedQuantityMultiplier = BigDecimal.valueOf(unclosedClosedQuantity.getValue());

			
			MoneySet taxExcludedAmount = new MoneySet();
			taxExcludedAmount.setCurrency(fromDetail.getTaxExcludedUnitPrice().getCurrency());
			taxExcludedAmount.setLocalAmount(unclosedClosedQuantityMultiplier.multiply(fromDetail.getTaxExcludedUnitPrice().getLocalAmount()));
			taxExcludedAmount.setValue(unclosedClosedQuantityMultiplier.multiply(fromDetail.getTaxExcludedUnitPrice().getValue()));
			
			toDetail.setTaxExcludedAmount(taxExcludedAmount);
		}
	
		if (fromDetail.getTax1() != null ) toDetail.getTax1().moveFieldsOf(fromDetail.getTax1());
		if (fromDetail.getTax2() != null ) toDetail.getTax1().moveFieldsOf(fromDetail.getTax2());
		if (fromDetail.getTax3() != null ) toDetail.getTax1().moveFieldsOf(fromDetail.getTax3());
		if (fromDetail.getTax4() != null ) toDetail.getTax1().moveFieldsOf(fromDetail.getTax4());
		if (fromDetail.getTax5() != null ) toDetail.getTax1().moveFieldsOf(fromDetail.getTax5());
		
//		if (fromDetail.getTaxExcludedAmount() != null ) toDetail.getTaxExcludedAmount().moveFieldsOf(fromDetail.getTaxExcludedAmount());

		if (fromDetail.getTaxExcludedTotal() != null ) toDetail.getTaxExcludedTotal().moveFieldsOf(fromDetail.getTaxExcludedTotal());
		if (fromDetail.getTaxExcludedUnitPrice() != null ) toDetail.setTaxExcludedUnitPrice(fromDetail.getTaxExcludedUnitPrice());
		if (fromDetail.getTaxTotalAmount() != null ) toDetail.getTaxTotalAmount().moveFieldsOf(fromDetail.getTaxTotalAmount());
		if (fromDetail.getTotalAmount() != null ) toDetail.getTotalAmount().moveFieldsOf(fromDetail.getTotalAmount());
		if (fromDetail.getUnitPrice() != null ) toDetail.setUnitPrice(fromDetail.getUnitPrice());

	}

	private void moveInvoiceFieldsToInvoice(TekirInvoiceDetail fromDetail, TekirInvoiceDetail toDetail) {

		if (fromDetail.getAmount() != null ) toDetail.getAmount().moveFieldsOf(fromDetail.getAmount());
		if (fromDetail.getBeforeTax() != null ) toDetail.getBeforeTax().moveFieldsOf(fromDetail.getBeforeTax());
//		if (item.getChildList() != null ) detail.getBeforeTax().moveFieldsOf(item.getBeforeTax());
		if (fromDetail.getCurrencyOfItem() != null ) toDetail.getCurrencyOfItem();
		
		if (fromDetail.getDiscount() != null ) toDetail.getDiscount().moveFieldsOf(fromDetail.getDiscount());
		if (fromDetail.getDiscountStyle() != null ) toDetail.setDiscountStyle(fromDetail.getDiscountStyle());

		if (fromDetail.getExpense() != null ) toDetail.getExpense().moveFieldsOf(fromDetail.getExpense());
		if (fromDetail.getExpenseStyle() != null ) toDetail.setExpenseStyle(fromDetail.getExpenseStyle());

		if (fromDetail.getFee() != null ) toDetail.getFee().moveFieldsOf(fromDetail.getFee());
		if (fromDetail.getGrandTotal() != null ) toDetail.getGrandTotal().moveFieldsOf(fromDetail.getGrandTotal());
		if (fromDetail.getInfo() != null ) toDetail.setInfo(fromDetail.getInfo());
		if (fromDetail.getLineCode() != null ) toDetail.setLineCode(fromDetail.getLineCode());

//		if (item.getOwner() != null ) detail.setLineCode(item.getLineCode());
//		if (item.getParent() != null ) detail.setLineCode(item.getLineCode());
		if (fromDetail.getProduct() != null ) toDetail.setProduct(fromDetail.getProduct());
		if (fromDetail.getProductType() != null ) toDetail.setProductType(fromDetail.getProductType());

		if (fromDetail.getQuantity() != null ) toDetail.setQuantity(new Quantity(fromDetail.getQuantity().getValue(),fromDetail.getQuantity().getUnit()));
	
		if (fromDetail.getTax1() != null ) toDetail.getTax1().moveFieldsOf(fromDetail.getTax1());
		if (fromDetail.getTax2() != null ) toDetail.getTax1().moveFieldsOf(fromDetail.getTax2());
		if (fromDetail.getTax3() != null ) toDetail.getTax1().moveFieldsOf(fromDetail.getTax3());
		if (fromDetail.getTax4() != null ) toDetail.getTax1().moveFieldsOf(fromDetail.getTax4());
		if (fromDetail.getTax5() != null ) toDetail.getTax1().moveFieldsOf(fromDetail.getTax5());
		
		if (fromDetail.getTaxExcludedAmount() != null ) toDetail.getTaxExcludedAmount().moveFieldsOf(fromDetail.getTaxExcludedAmount());
		if (fromDetail.getTaxExcludedTotal() != null ) toDetail.getTaxExcludedTotal().moveFieldsOf(fromDetail.getTaxExcludedTotal());
		if (fromDetail.getTaxExcludedUnitPrice() != null ) toDetail.setTaxExcludedUnitPrice(fromDetail.getTaxExcludedUnitPrice());
		if (fromDetail.getTaxTotalAmount() != null ) toDetail.getTaxTotalAmount().moveFieldsOf(fromDetail.getTaxTotalAmount());
		if (fromDetail.getTotalAmount() != null ) toDetail.getTotalAmount().moveFieldsOf(fromDetail.getTotalAmount());
		if (fromDetail.getUnitPrice() != null ) toDetail.setUnitPrice(fromDetail.getUnitPrice());

	}

	public void removeOrderNote(int ix) {
    	log.info("Removing order note. Index :{0}", ix);
    	manualFlush();
    	
        
    	TekirOrderNote sn = entity.getOrderLinks().get(ix).getOrderNote();

    	for (TekirOrderNoteDetail ondet : sn.getItems()) {

    		ondet.setClosedQuantity(0d);
    		ondet.setReferenceDocumentId(null);
    		ondet.setReferenceDocumentType(null);

    		TekirInvoiceDetail invoiceDetail = null;
    		for (int j= entity.getItems().size() -1;j>=0;j--) {
    			invoiceDetail = entity.getItems().get(j);
    			
    			if (invoiceDetail.getReferenceDocumentId() != null && 
    					invoiceDetail.getReferenceDocumentId().equals(ondet.getId())) {

    				entity.getItems().remove(j);
    			}
    		}
    	}
    	
        entity.getOrderLinks().remove(ix);

        orderList.remove(sn);
	}

	//FIXME:Şu an ihtiyaç yok. Ama ileride düzenlemek gerekecek.
	public void removeInvoice(int ix) {
		log.info("Removing invoice. Index :{0}", ix);
		manualFlush();
		
		invoiceList.remove(ix);
	}

    @SuppressWarnings("unchecked")
	public List<TekirShipmentNote> findShipments() {
    	try {
	    	if (entity.getContact() != null) {
	    		
				List<TekirShipmentNote> resultList = entityManager.createQuery("select sn from TekirShipmentNote sn where " +
																				  "sn.tradeAction=:tradeAction and " + 
																				  "sn.invoice= null and " + 
																				  "sn.contact=:contact ")
																				  .setParameter("contact", entity.getContact())
																				  .setParameter("tradeAction", TradeAction.Sale)
																				  .getResultList();
		
		        for (TekirShipmentNote sn : entity.getShipmentList()) {
		            if (resultList.contains(sn)) {
		                resultList.remove(sn);
		            }
		        }

		        shipmentList.clear();
		        
		        shipmentList.addAll(resultList);
	    	} 
		} catch (Exception e) {
			log.error("Hata oluştu. Sebebi :{0}", e);
		}
		return shipmentList;
	}

    public void removeShipmentNote(int ix) {
    	log.info("Removing shipment note. Index :{0}", ix);
    	manualFlush();
    	
        TekirShipmentNote sn = entity.getShipmentList().get(ix);

        sn.setInvoice(null);
        entity.getShipmentList().remove(ix);

        shipmentList.remove(sn);

        entity.getItems().clear();

        for (TekirShipmentNote item : entity.getShipmentList()) {
        	addShipmentLines(item);
        }
    }

    public void selectShipmentNote(int ix) {
    	log.info("Selected shipment note. Index :{0}", ix);
    	manualFlush();

        TekirShipmentNote sn = shipmentList.get(ix);

        sn.setInvoice(entity);
        entity.getShipmentList().add(sn);

        shipmentList.remove(ix);

        entity.setClerk(sn.getClerk());
        entity.setWarehouse(sn.getWarehouse());
        // irsaliyenin iş takip numarasını faturaya setle.
        if (entity.getWorkBunch() == null || !entity.getWorkBunch().equals(sn.getWorkBunch())){
        	entity.setWorkBunch(sn.getWorkBunch());
        }
        
    	addShipmentLines(sn);
    	
    	calculateEverything();
    }

    private void addShipmentLines(TekirShipmentNote tsn) {
    	TekirInvoiceDetail detail = null;
    	for (TekirShipmentNoteDetail item : tsn.getItems()) {
    		if (item.getParent() == null && 
    				(item.getProductType().equals(ProductType.Product) ||
    				 item.getProductType().equals(ProductType.Service) )) {
    		
	    		detail = new TekirInvoiceDetail();
	
	    		moveShipmentFieldsToInvoice(item, detail);
	    		
				entity.getItems().add(detail);
				detail.setOwner(entity);

				for (TekirShipmentNoteDetail childItem : item.getChildList()) {
			    	TekirInvoiceDetail childDetail = new TekirInvoiceDetail();
	    			moveShipmentFieldsToInvoice(childItem, childDetail);
	
	    			detail.getChildList().add(childDetail);
	    			childDetail.setParent(detail);
	
	    			childDetail.setOwner(entity);
	
	    			entity.getItems().add(childDetail);
	    		}

				if (item.getShipmentDiscount().getValue().compareTo(BigDecimal.ZERO) != 0) {
    				TekirInvoiceDetail childDetail = new TekirInvoiceDetail();
    				childDetail.setProductType(ProductType.Discount);
    				
    				MoneySet shipmentDiscount = item.getShipmentDiscount();
    				
    				DiscountOrExpense discount = new DiscountOrExpense();
    				discount.setPercentage(false);
    				discount.setCurrency(shipmentDiscount.getCurrency());

    				BigDecimal quantityValue = BigDecimal.valueOf(item.getQuantity().getValue());
    				discount.setLocalAmount(shipmentDiscount.getLocalAmount().divide(quantityValue, 2, RoundingMode.HALF_UP));
    				discount.setValue(shipmentDiscount.getValue().divide(quantityValue, 2, RoundingMode.HALF_UP));
    				
    				
    				discount.setLocalAmount(shipmentDiscount.getLocalAmount());
    				discount.setValue(shipmentDiscount.getValue());
    				
    				childDetail.setDiscount(discount);

    				detail.getChildList().add(childDetail);
    				childDetail.setParent(detail);

    				childDetail.setOwner(entity);
    				childDetail.setReferenceDocumentId(item.getId());
    				childDetail.setReferenceDocumentType(tsn.getDocumentType());

    				entity.getItems().add(childDetail);
    			}

    		}
    	}
    }
    
	private void moveShipmentFieldsToInvoice(TekirShipmentNoteDetail fromDetail, TekirInvoiceDetail toDetail) {

		if (fromDetail.getAmount() != null ) toDetail.getAmount().moveFieldsOf(fromDetail.getAmount());
		if (fromDetail.getBeforeTax() != null ) toDetail.getBeforeTax().moveFieldsOf(fromDetail.getBeforeTax());
//		if (item.getChildList() != null ) detail.getBeforeTax().moveFieldsOf(item.getBeforeTax());
		if (fromDetail.getCurrencyOfItem() != null ) toDetail.getCurrencyOfItem();
		
		if (fromDetail.getDiscount() != null ) toDetail.getDiscount().moveFieldsOf(fromDetail.getDiscount());
		if (fromDetail.getDiscountStyle() != null ) toDetail.setDiscountStyle(fromDetail.getDiscountStyle());

		if (fromDetail.getExpense() != null ) toDetail.getExpense().moveFieldsOf(fromDetail.getExpense());
		if (fromDetail.getExpenseStyle() != null ) toDetail.setExpenseStyle(fromDetail.getExpenseStyle());

		if (fromDetail.getFee() != null ) toDetail.getFee().moveFieldsOf(fromDetail.getFee());
		if (fromDetail.getGrandTotal() != null ) toDetail.getGrandTotal().moveFieldsOf(fromDetail.getGrandTotal());
		if (fromDetail.getInfo() != null ) toDetail.setInfo(fromDetail.getInfo());
		if (fromDetail.getLineCode() != null ) toDetail.setLineCode(fromDetail.getLineCode());

//		if (item.getOwner() != null ) detail.setLineCode(item.getLineCode());
//		if (item.getParent() != null ) detail.setLineCode(item.getLineCode());
		if (fromDetail.getProduct() != null ) toDetail.setProduct(fromDetail.getProduct());
		if (fromDetail.getProductType() != null ) toDetail.setProductType(fromDetail.getProductType());
		if (fromDetail.getQuantity() != null ) toDetail.setQuantity(fromDetail.getQuantity());
	
		if (fromDetail.getTax1() != null ) toDetail.getTax1().moveFieldsOf(fromDetail.getTax1());
		if (fromDetail.getTax2() != null ) toDetail.getTax1().moveFieldsOf(fromDetail.getTax2());
		if (fromDetail.getTax3() != null ) toDetail.getTax1().moveFieldsOf(fromDetail.getTax3());
		if (fromDetail.getTax4() != null ) toDetail.getTax1().moveFieldsOf(fromDetail.getTax4());
		if (fromDetail.getTax5() != null ) toDetail.getTax1().moveFieldsOf(fromDetail.getTax5());
		
		if (fromDetail.getTaxExcludedAmount() != null ) toDetail.getTaxExcludedAmount().moveFieldsOf(fromDetail.getTaxExcludedAmount());
		if (fromDetail.getTaxExcludedTotal() != null ) toDetail.getTaxExcludedTotal().moveFieldsOf(fromDetail.getTaxExcludedTotal());
		if (fromDetail.getTaxExcludedUnitPrice() != null ) toDetail.setTaxExcludedUnitPrice(fromDetail.getTaxExcludedUnitPrice());
		if (fromDetail.getTaxTotalAmount() != null ) toDetail.getTaxTotalAmount().moveFieldsOf(fromDetail.getTaxTotalAmount());
		if (fromDetail.getTotalAmount() != null ) toDetail.getTotalAmount().moveFieldsOf(fromDetail.getTotalAmount());
		if (fromDetail.getUnitPrice() != null ) toDetail.setUnitPrice(fromDetail.getUnitPrice());

	}

	public void print(String templateName) {
		log.info("Printing invoice, invoice id=:{0}, template name :{1}",entity.getId(), templateName);
		try {
			getDocumentPrint().print(templateName);
		} catch (Exception ex) {
			log.error("Invoice print error :{0}", ex);
			facesMessages.add(ex.getMessage());
		}
	}
	
	public void setDefaultAccount() throws Exception {
		Option o = optionManager.getOption("systemSettings.control.DefaultAccount");
		
		if (o != null) {
			String defaultAccount = o.getValue();
			
			Account foundAccount = generalSuggestion.findAccount(defaultAccount);

			if (entity.getAccount() == null) {
				if (foundAccount != null) {
					entity.setAccount(foundAccount);
				} else {
					throw new Exception("Ön tanımlı kasa bulunamadı. İşlem yapabilmeniz için " +
										"ön tanımlı kasa olması gerekmektedir.");
				}
			}
		}
	}

	public void setDefaultWarehouse() throws Exception {
		Option o = optionManager.getOption("systemSettings.control.DefaultWarehouse");
		
		if (o != null) {
			String defaultWarehouse = o.getValue();
			
			Warehouse foundWarehouse = generalSuggestion.findWarehouse(defaultWarehouse);
			
			if (entity.getWarehouse() == null) {
				if (foundWarehouse != null) {
					entity.setWarehouse(foundWarehouse);
				} else {
					throw new Exception("Ön tanımlı depo bulunamadı. İşlem yapabilmeniz için " +
					"ön tanımlı depo olması gerekmektedir.");
				}
			}
		}
	}

	public void updateDocumentCurrency() {
		updateMasterCurrencies();
		updateDetailCurrencies();
	}

	private void updateMasterCurrencies() {
		entity.getTotalTaxExcludedAmount().setCurrency(entity.getDocCurrency());
		entity.getTotalBeforeTax().setCurrency(entity.getDocCurrency());
		entity.getTotalDiscount().setCurrency(entity.getDocCurrency());
		entity.getTotalDocumentDiscount().setCurrency(entity.getDocCurrency());
		entity.getTaxExcludedTotal().setCurrency(entity.getDocCurrency());
		entity.getTotalTax().setCurrency(entity.getDocCurrency());
		entity.getTotalExpense().setCurrency(entity.getDocCurrency());
		entity.getTotalDocumentExpense().setCurrency(entity.getDocCurrency());
		entity.getTotalDiscountAddition().setCurrency(entity.getDocCurrency());
		entity.getTotalExpenseAddition().setCurrency(entity.getDocCurrency());
		entity.getTotalAmount().setCurrency(entity.getDocCurrency());
		entity.getGrandTotal().setCurrency(entity.getDocCurrency());
	}

	@Override
	public void updateDetailCurrencies() {
		for (int i=0;i<entity.getItems().size();i++) {
			calculateUnitPrice(i);
			setTaxExcludedAmount(i);
		}
	}

	@TransactionAttribute(TransactionAttributeType.REQUIRES_NEW)
	public String createInvoice() {
		manualFlush();
		try {
			setEntity( getProformaInvController().createInvoice(entity) );
			entity.setSerial(sequenceManager.getNewSerialNumber(SequenceType.SERIAL_INVOICE_SALE));
			return BaseConsts.SUCCESS;
		} catch (Exception e) {
			facesMessages.add(Severity.ERROR, "Proforma fatura, faturalanmaya çalışılırken hata meydana geldi.");
			log.error("Proforma fatura, faturalanmaya çalışılırken hata meydana geldi. Sebebi #0", e);
		}
		return BaseConsts.FAIL;
	}
	
	public Integer getInvoiceType() {
		return invoiceType;
	}

	public void setInvoiceType(Integer invoiceType) {
		this.invoiceType = invoiceType;
	}

	public Integer getActionType() {
		return actionType;
	}

	public void setActionType(Integer actionType) {
		this.actionType = actionType;
	}

	public TekirInvoiceDetail getSelectedDetail() {
		return selectedDetail;
	}

	public void setSelectedDetail(TekirInvoiceDetail selectedDetail) {
		this.selectedDetail = selectedDetail;
	}

	public ContactAddress getSelectedAddress() {
		return selectedAddress;
	}

	public void setSelectedAddress(ContactAddress selectedAddress) {
		this.selectedAddress = selectedAddress;
	}

	public Boolean getShowMiniTable() {
		return showMiniTable;
	}

	public boolean isRequestDiscountPermission() {
		return requestDiscountPermission;
	}

	public void setRequestDiscountPermission(boolean requestDiscountPermission) {
		this.requestDiscountPermission = requestDiscountPermission;
	}

	public User getAuthorizedUser() {
		return authorizedUser;
	}

	public void setAuthorizedUser(User authorizedUser) {
		this.authorizedUser = authorizedUser;
	}

	public List<TekirOrderNote> getOrderList() {
		return orderList;
	}

	public void setOrderList(List<TekirOrderNote> orderList) {
		this.orderList = orderList;
	}

	public List<TekirShipmentNote> getShipmentList() {
		return shipmentList;
	}

	public void setShipmentList(List<TekirShipmentNote> shipmentList) {
		this.shipmentList = shipmentList;
	}

	public List<TekirInvoice> getInvoiceList() {
		return invoiceList;
	}

	public void setInvoiceList(List<TekirInvoice> invoiceList) {
		this.invoiceList = invoiceList;
	}

	public Product getSelectedDiscountExpense() {
		return selectedDiscountExpense;
	}

	public void setSelectedDiscountExpense(Product selectedDiscountExpense) {
		this.selectedDiscountExpense = selectedDiscountExpense;
	}

	public List<FoundationTxn> getTxnList() {
		return txnList;
	}

	public void setTxnList(List<FoundationTxn> txnList) {
		this.txnList = txnList;
	}

	public InvoiceSuggestionFilterModel getFilterModel() {
		filterModel.setTradeAction(TradeAction.Sale);
		filterModel.setRetInvoiceStatus(getPossibleStatusList());
		filterModel.setContact(entity.getContact());
		return filterModel;
	}

	public void setFilterModel(InvoiceSuggestionFilterModel filterModel) {
		this.filterModel = filterModel;
	}

	public ProformaInvoiceController getProformaInvController() {
		if (proformaInvController == null) {
			proformaInvController = ProformaInvoiceController.instance();
		}
		return proformaInvController;
	}

	public LimitationChecker getLimitationChecker() {
		if (limitationChecker == null) {
			limitationChecker = LimitationChecker.instance();
		}
		return limitationChecker;
	}
	
	public boolean hasWarningOrRequiredLimitation() {
		return getLimitationChecker().hasWarningOrRequired();
	}

	public boolean hasRequiredLimitation() {
		return getLimitationChecker().hasRequired();
	}
	
	public boolean isCheckingRequired(){
		return getLimitationChecker().isRequired();
	}

	public LimitationMessages getLimitationMessages() {
		return getLimitationChecker().getLimMessages();
	}

	public ControlType getOption(OptionKey key) {
		return getLimitationChecker().getOption(key);
	}

	public OldState getOldState() {
		if (oldState == null) {
			oldState = new OldState();
		}
		return oldState;
	}

	private DocumentPrint getDocumentPrint() {
		if (documentPrint == null) {
			documentPrint = InvoicePrint.instance(getEntity());
		}
		return documentPrint;
	}

}
