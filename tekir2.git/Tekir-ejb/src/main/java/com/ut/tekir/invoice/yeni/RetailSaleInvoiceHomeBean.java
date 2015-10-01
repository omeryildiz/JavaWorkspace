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
import com.ut.tekir.entities.InvoiceOrderLink;
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
import com.ut.tekir.entities.PriceItem;
import com.ut.tekir.entities.PriceItemDetail;
import com.ut.tekir.entities.Product;
import com.ut.tekir.entities.ProductType;
import com.ut.tekir.entities.Quantity;
import com.ut.tekir.entities.TaxEmbeddable;
import com.ut.tekir.entities.TenderDetailBase;
import com.ut.tekir.entities.TradeAction;
import com.ut.tekir.entities.User;
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
import com.ut.tekir.finance.PosTxnAction;
import com.ut.tekir.finance.TaxTxnAction;
import com.ut.tekir.framework.BaseConsts;
import com.ut.tekir.framework.CalendarManager;
import com.ut.tekir.framework.CurrencyManager;
import com.ut.tekir.framework.JasperHandlerBean;
import com.ut.tekir.framework.OptionManager;
import com.ut.tekir.framework.SequenceManager;
import com.ut.tekir.framework.SequenceType;
import com.ut.tekir.framework.SystemProperties;
import com.ut.tekir.framework.UserDiscountRightHome;
import com.ut.tekir.general.GeneralSuggestion;
import com.ut.tekir.options.AmountLimitationOptionKey;
import com.ut.tekir.options.LimitationOptionKey;
import com.ut.tekir.options.OptionKey;
import com.ut.tekir.stock.ProductTxnAction;
import com.ut.tekir.stock.StockLevelFinder;
import com.ut.tekir.tender.PosPrinterHome;
import com.ut.tekir.tender.PriceProvider;
import com.ut.tekir.tender.TenderCalculationHomeBean;
import com.ut.tekir.util.NumberToText;
import com.ut.tekir.util.NumberToTextTR;
import com.ut.tekir.util.Utils;


//FIXME:Find defaultPos and account metotlarını artık kullanmayacak mıyız?
//FIXME: default price list tanımları çöp oldu. Koddan temizlemek gerek.

/**
 * Perakande satış faturası home bileşeni
 * @author sinan.yumak
 *
 */
@Stateful
@Name("retailSaleInvoiceHome")
@Scope(ScopeType.CONVERSATION)
public class RetailSaleInvoiceHomeBean extends TenderCalculationHomeBean<TekirInvoice> implements RetailSaleInvoiceHome<TekirInvoice> {
    @In
    SequenceManager sequenceManager;
	@In
	CalendarManager calendarManager;
	@In
	PriceProvider priceProvider;
	@In
	ProductTxnAction productTxnAction;
	@In
	FinanceTxnAction financeTxnAction;
	@In
	AccountTxnAction accountTxnAction;
	@In
	PosTxnAction posTxnAction;
	@In
	GeneralSuggestion generalSuggestion;
	@In
	OptionManager optionManager;
	@In(create=true)
	StockLevelFinder stockLevelFinder;
    @In
    CurrencyManager currencyManager;
    @In
	TaxTxnAction taxTxnAction;
    @In
    FoundationTxnAction foundationTxnAction;
    @In
    private SystemProperties systemProperties;
    @In
    Map<Object, String> messages;
    
	private PriceItem selectedPriceItem;
	private List<PriceItem> priceItemList;//fatura için gelebilecek fiyat listesi tanımlarını tutar. 
	private String barcodeInput;
	private Double barcodeQuantity = 1d;
	private User clerk;
	private LimitationChecker limitationChecker;
	
    private Boolean showMiniTable = Boolean.TRUE;

    
    private TekirInvoiceDetail selectedDetail;
    private Product selectedDiscountExpense;
    private ContactAddress selectedAddress;
    private Integer selectedIndex;
    private ContactOperation contactOperation = ContactOperation.Existing;
    
    private User authorizedUser;
    private boolean requestDiscountPermission = Boolean.FALSE;
    

    private List<TekirOrderNote> orderList = new ArrayList<TekirOrderNote>();

    //sayfa edit için açıldığında eski birimleri tutar.
    private Map<Product, Double> oldQuantities = new HashMap<Product, Double>();

    private List<String> zeroLineAmountWarnings = new ArrayList<String>();
    
    private Payment paymentOfInvoice;
    private TekirShipmentNote shipmentOfInvoice;
    
	@Create
	@Begin(join=true,flushMode= FlushModeType.MANUAL)
	public void init() {		
	}
	
	@Out(required=false)
	public TekirInvoice getRetailSaleInvoice() {
		return getEntity();
	}

	@In(required=false)
	public void setRetailSaleInvoice(TekirInvoice tekirSaleInvoice) {
		setEntity(tekirSaleInvoice);
	}
	
	@Override
	public void createNew() {
		log.debug("#{messages['logMessages.CreatedEntity']}");
		
		entity = new TekirInvoice();
		entity.setTradeAction(TradeAction.Sale);
		entity.setDocumentType(DocumentType.RetailSaleShipmentInvoice);
		entity.setActive(true);
        entity.setSerial(sequenceManager.getNewSerialNumber(SequenceType.SERIAL_INVOICE_SALE));
        entity.setDate(calendarManager.getCurrentDate());
        
		entity.setShippingDate(calendarManager.getCurrentDate());
		entity.setTime(entity.getDate());
		
		// perakende fatura da is hizlansin login adamin bilgileri gelsin
		
		// siparis secilirse onun bilgileri ile degisir 
	    User activeUser = (User)Component.getInstance("activeUser",true);

	    //log.info("Login olan kullanici id :{0}", activeUser.getId());
		entity.setClerk(activeUser);
		
		//log.info("Satıcı set edildi :{0}", entity.getClerk().getFullName());
		
		entity.setAccount(activeUser.getAccount());			
		entity.setWarehouse(activeUser.getWarehouse());			
		
		
	}

	@Override
	public void createDocumentDiscountLine() {
        log.info("Creating new document discount line...");
    	manualFlush();
    	
        selectedDetail = new TekirInvoiceDetail();
    	selectedDetail.setOwner(entity);
    	selectedDetail.setProductType(ProductType.DocumentDiscount);

    	authorizedUser = new User();
	}

	@Override
	public void createDocumentExpenseLine() {
		log.info("Creating new document expense line...");
		manualFlush();
		
		selectedDetail = new TekirInvoiceDetail();
		selectedDetail.setOwner(entity);
		selectedDetail.setProductType(ProductType.DocumentExpense);
	}
	
	@Override
	public void createDiscountAdditionLine() {
		log.info("Creating new document discount line...");
		manualFlush();
		
		selectedDetail = new TekirInvoiceDetail();
		selectedDetail.setOwner(entity);
		selectedDetail.setProductType(ProductType.DiscountAddition);
	}

	@Override
	public void createExpenseAdditionLine() {
		log.info("Creating new document expense line...");
		manualFlush();
		
		selectedDetail = new TekirInvoiceDetail();
		selectedDetail.setOwner(entity);
		selectedDetail.setProductType(ProductType.ExpenseAddition);
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
	public void addDocumentDiscountToItems() {
    	log.info("Adding document discount to line...");
    	manualFlush();

    	UserDiscountRightHome userDiscountRight = (UserDiscountRightHome)Component.getInstance("userDiscountRight",true);
    	try {
			
	    	TekirInvoiceDetail detail = new TekirInvoiceDetail();
	    	detail.setProductType(ProductType.DocumentDiscount);
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
	    		detail.setProduct(selectedDiscountExpense);
	    	}
	    	detail.getDiscount().setPercentage(discount.getPercentage());
	    	detail.getDiscount().setRate(discount.getRate());
	    	detail.getDiscount().setValue(discount.getValue());
	    	
	    	
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
			detail.setProduct(selectedDiscountExpense);
			
			entity.getItems().add(detail);
			detail.setOwner(entity);
		} catch (Exception e) {
			facesMessages.add(Severity.ERROR, e.getMessage());
		}
	}

	public void addDiscountExpenseAdditionToItem() {
        log.info("Adding discount or expense addition to line...");
    	manualFlush();
		
    	TekirInvoiceDetail detail = new TekirInvoiceDetail();

		detail.setProductType(selectedDetail.getProductType());
		detail.setProduct(selectedDiscountExpense);
    	
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
		
		BigDecimal convertedValue = currencyManager.convertToLocal(doe.getValue(), doe.getCurrency(), entity.getDate());
		doe.setLocalAmount(convertedValue);
		
		entity.getItems().add(detail);
		detail.setOwner(entity);
	}
	
    public void addDiscountToItem() {
        log.info("Adding discount to line...");
    	manualFlush();

    	TekirInvoiceDetail detail = new TekirInvoiceDetail();
    	detail.setProductType(ProductType.Discount);
    	DiscountOrExpense discount = null;
    	if (selectedDiscountExpense != null) {

    		discount = selectedDiscountExpense.getDiscountOrExpense();

    		detail.getDiscount().setPercentage(discount.getPercentage());
    		detail.getDiscount().setRate(discount.getRate());
    		detail.getDiscount().setValue(discount.getValue());
    		detail.setProduct(selectedDiscountExpense);
    		
    		TekirInvoiceDetail selectedItem = entity.getItems().get(selectedIndex);
    		
    		if (selectedItem != null) {
    			selectedItem.getChildList().add(detail);
    			detail.setParent(selectedItem);
    			
    			entity.getItems().add(selectedIndex + 1, detail);

    			detail.setOwner(entity);
    		}
    	}
    	
    }

    @Override
    public void addExpenseToItem() {
    	log.info("Adding expense to line...");
    	manualFlush();
    	
    	TekirInvoiceDetail detail = new TekirInvoiceDetail();
    	detail.setProductType(ProductType.Expense);
    	DiscountOrExpense expense = null;
    	if (selectedDiscountExpense != null) {
    		
    		expense = selectedDiscountExpense.getDiscountOrExpense();
    		
    		detail.getExpense().setPercentage(expense.getPercentage());
    		detail.getExpense().setRate(expense.getRate());
    		detail.getExpense().setValue(expense.getValue());
    		detail.setProduct(selectedDiscountExpense);
    		
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
						
						discount.setCurrency(item.getCurrencyOfItem());
						discount.setValue(difference.abs());
						discount.setPercentage(false);
	//					discount.setLocalAmount(difference.abs());
						
						TekirInvoiceDetail discountLine = new TekirInvoiceDetail();
						
						discountLine.setProductType(ProductType.Discount);
						discountLine.setDiscount(discount);
						
						discountLine.setOwner(entity);
						
						discountLine.setParent(item);
	
						item.getOwner().getItems().add(i+1,discountLine);
						
						item.getChildList().add(discountLine);
					}
				}
			}

			calculateEverything(entity);
		} catch (Exception e) {
			log.error("Sipariş hesaplamaları yapılırken hata meydana geldi. Sebebi :{0}", e);
			facesMessages.add(Severity.ERROR, "Sipariş hesaplamaları yapılırken hata meydana geldi. Sebebi :{0}",e);
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

	private void prepareSelectedLine(int indexOfDetail,boolean refreshLine) throws Exception {
		TekirInvoiceDetail aDetail = entity.getItems().get(indexOfDetail);
		Product aProduct = aDetail.getProduct();
		
		if (aProduct == null) return;
		
		aDetail.setProductType(aProduct.getProductType());
		aDetail.getQuantity().setUnit(aProduct.getUnit());
		aDetail.setUnitPrice(new MoneySet());
		aDetail.setTaxExcludedAmount(new MoneySet());

		if (!refreshLine) {
			if (barcodeQuantity.compareTo(1.0d)> 0) {
				aDetail.getQuantity().setValue(barcodeQuantity.doubleValue());
				barcodeQuantity = 1d;
			} else {
				aDetail.getQuantity().setValue(1.0);
			}
		}
		
		PriceItemDetail priceItemDetail = priceProvider.findSalePriceItemDetailForProduct(aProduct);

		aDetail.setUnitPrice(priceItemDetail.getGrossPrice());
		
		MoneySet lineAmount = new MoneySet();
		lineAmount.setCurrency(aDetail.getUnitPrice().getCurrency());
		
		BigDecimal unitPrice = aDetail.getUnitPrice().getValue();
		BigDecimal quantity = BigDecimal.valueOf(aDetail.getQuantity().getValue());
		
		lineAmount.setValue(unitPrice.multiply(quantity));
		
		
		aDetail.setTaxExcludedAmount(lineAmount);

		calculateTaxExcludedUnitPriceAndAmount(aDetail);

//			if (refreshLine) {
//				for (TekirInvoiceDetail det : aDetail.getChildList()) {
//					aDetail.getOwner().getItems().remove(det);
//				}
//				aDetail.getChildList().clear();
//			}

		if (priceItemDetail.getHasDiscount()) {
			addDiscountLine(priceItemDetail, indexOfDetail);
		} 
		
//		if (!refreshLine) {
//			//hemen yeni bir satır oluşturuyoruz.
//			createNewDetail();
//		}
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
	
	public void createNewLineWithBarcode() {
        manualFlush();

        try {
			Product product = generalSuggestion.findProductWithBarcode(barcodeInput);
			if (product != null) {
				TekirInvoiceDetail detail = new TekirInvoiceDetail();
				detail.setOwner(entity);
				entity.getItems().add(detail);

				detail.setProduct(product);
				
				detail.getQuantity().setValue(getBarcodeQuantity());
				prepareSelectedLine(entity.getItems().size() - 1, false);
			}
		} catch (Exception e) {
			log.error("Hata: {0}", e);
			facesMessages.add("Barkod ile satır oluştururken hata oluştu. Sebebi :\n{0}",e.getMessage());
		}
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
		manualFlush();
		log.info("Saving invoice. Invoice serial :{0}", entity.getSerial());
		String result = BaseConsts.FAIL;

		try {
			clearUnknownDetails();

			calculateEverything();

			roundDecimals();
			
			makeEntityValidations();
			
			setParentOfLists();
			
			persistParents();
			
			copyDeliveryContactInfoFromContactCard();

			result = super.save();

			if (result.equals(BaseConsts.SUCCESS)) {
				setClosedQuantitiesForOrderNotes();
				
				setPaymentTableReferenceIds();

				updateStatusOfOrderNotes();

				
				updateOldQuantities();

				//irsaliyeyi kesiyoruz.
				TekirShipmentNote tsn = saveShipmentNoteForInvoice();
				productTxnAction.createProductTxnRecordsForShipment(tsn);

				//cari txn borc yansımaları...
				financeTxnAction.createFinanceTxnRecordsForInvoice(entity);

                //vergi txn yansimasi
                taxTxnAction.createTaxTxnRecordsForInvoice(entity);

                //kredi kartli satis yansimasi
				Payment collection = saveCollectionForInvoice();
				if (collection != null && collection.getItems().size() != 0) {
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

				//Eğer katkı satırları varsa, kurum txn yansımalarını yapar.
				foundationTxnAction.createFoundationTxnRecordsForInvoice(entity);

				entityManager.flush();
			}
		} catch (Exception ex) {
			facesMessages.add( Utils.getExceptionMessage(ex) );
			log.error("Hata: {0}", ex);
			result = BaseConsts.FAIL;
		}
		return result;
	}

	/**
	 * Fatura üzerinde yapılabilecek toplam yuvarlama miktarına bakar,
	 * eğer yuvarlama mümkünse yuvarlar.
	 */
	private void roundDecimals() {
		BigDecimal maxRoundingAmount = getMaxRoundingAmount();

		BigDecimal remainder = getPaymentTableRemainder();
		
		if ((remainder.abs()).compareTo(maxRoundingAmount) <= 0) {

			if (remainder.compareTo(BigDecimal.ZERO) >= 0) {
				MoneySet taxExcludedTotal = entity.getTaxExcludedTotal();
				
				taxExcludedTotal.setValue(taxExcludedTotal.getValue().subtract(remainder));

				BigDecimal convertedExcludedTotal = currencyManager.convertToLocal(taxExcludedTotal.getValue(), taxExcludedTotal.getCurrency(), entity.getDate());
				taxExcludedTotal.setLocalAmount(convertedExcludedTotal);
				
				
				MoneySet totalAmount = entity.getTotalAmount();

				totalAmount.setValue(totalAmount.getValue().subtract(remainder));

				BigDecimal convertedTotalAmount = currencyManager.convertToLocal(totalAmount.getValue(), totalAmount.getCurrency(), entity.getDate());
				totalAmount.setLocalAmount(convertedTotalAmount);
				
				
                MoneySet grandTotal = entity.getGrandTotal();

                grandTotal.setValue(grandTotal.getValue().subtract(remainder));

                BigDecimal convertedGrandTotal = currencyManager.convertToLocal(grandTotal.getValue(), grandTotal.getCurrency(), entity.getDate());
                grandTotal.setLocalAmount(convertedGrandTotal);
			} else {
				MoneySet totalTax = entity.getTotalTax();

				totalTax.setValue(totalTax.getValue().add(remainder.abs()));

				BigDecimal convertedTotalTax = currencyManager.convertToLocal(totalTax.getValue(), totalTax.getCurrency(), entity.getDate());
				totalTax.setLocalAmount(convertedTotalTax);



				List<TekirInvoiceTaxSummary> summaryList = entity.getTaxSummaryList();
				if (summaryList != null && summaryList.size() > 0) {
					TekirInvoiceTaxSummary ts = summaryList.get(0);

					ts.getAmount().setValue(ts.getAmount().getValue().add(remainder.abs()));
				}
				
				
				MoneySet totalAmount = entity.getTotalAmount();

				totalAmount.setValue(totalAmount.getValue().add(remainder.abs()));

				BigDecimal convertedTotalAmount = currencyManager.convertToLocal(totalAmount.getValue(), totalAmount.getCurrency(), entity.getDate());
				totalAmount.setLocalAmount(convertedTotalAmount);

				
                MoneySet grandTotal = entity.getGrandTotal();

                grandTotal.setValue(grandTotal.getValue().add(remainder.abs()));

                BigDecimal convertedGrandTotal = currencyManager.convertToLocal(grandTotal.getValue(), grandTotal.getCurrency(), entity.getDate());
                grandTotal.setLocalAmount(convertedGrandTotal);
			}
		} 
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

	private void updateStatusOfOrderNotes() {
		boolean totallyClosed = true;
		for (InvoiceOrderLink ol : entity.getOrderLinks()) {
			TekirOrderNote ton = ol.getOrderNote();
			
			for (TekirOrderNoteDetail det : ton.getItems()) {
				if (det.getProductType().equals(ProductType.Product) && !det.isClosed()) {
					totallyClosed = false;
					break;
				}
			}
			if (totallyClosed) {
				ton.setStatus(OrderStatus.Closed);
			} else {
				ton.setStatus(OrderStatus.Processing);
			}
			entityManager.merge(ton);
		}
	}

	private void setPaymentTableReferenceIds() {
		PaymentTable pt = entity.getPaymentTable();
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
		collection.setAction(FinanceAction.Credit);
		
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
				if (ptd.getReferenceId() == null) {
					
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
					BigDecimal convertedAmount = currencyManager.convertToLocal(ptd.getAmount().getValue(),
																			   ptd.getAmount().getCurrency(), 
																			   entity.getDate());
					ptd.getAmount().setLocalAmount(convertedAmount);
	
					collectionItem.setPaymentTableReferenceId(ptd.getId());
					
					collectionItem.setAmount(ptd.getAmount());
					collectionItem.setLineType(ptd.getPaymentType());
					collectionItem.setWorkBunch(entity.getWorkBunch());
					
					collectionItem.setOwner(collection);
					collection.getItems().add(collectionItem);
	
				
					totalAmount = totalAmount.add(convertedAmount);

				}

			}
			collection.getTotalAmount().setValue(totalAmount);
		}
		return collection;
	}

	/**
	 * Fatura ile bağlantılı olan tahsilatları ve tahsilatların bağlı
	 * olduğu txn kayıtlarını siler.
	 */
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

	private TekirShipmentNote saveShipmentNoteForInvoice() {
		if (entity.getId() != null) {
			deleteShipmentNotesOfInvoice();
		}
		TekirShipmentNote tsn = createShipmentNoteFromInvoice();
		
		entityManager.persist(tsn);
		return tsn;
	}

	private TekirShipmentNote createShipmentNoteFromInvoice() {
		log.info("Creating shipment note from invoice. Invoice code :{0}", entity.getSerial());
		TekirShipmentNote sn = new TekirShipmentNote();
		sn.setInvoice(entity);
		sn.setActive(Boolean.TRUE);
		sn.setTradeAction(TradeAction.Sale);
		sn.setContact(entity.getContact());
		sn.setCode(entity.getCode());
		sn.setDocumentType(DocumentType.SaleShipmentNote);
		
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
	
	private void makeEntityValidations() {
		masterValidations();
		
		detailValidations();
	}

	public void masterValidations() {
		setDefaultAccount();
		
//		setDefaultPos();

		if (entity.getItems().size() == 0 ) {
			throw new RuntimeException("#{messages['beanMessages.AtLeastOneItemRequired']}");
		}
		
		if (entity.getContact().getDefaultAddress() == null) {
			throw new RuntimeException("Faturanın kesilebilmesi için seçili carinin en azından ön tanımlı bir adresi olmalıdır.");
		}

		BigDecimal remainder = getPaymentTableRemainder();
	
		if (remainder.compareTo(BigDecimal.ZERO) != 0) {
			throw new RuntimeException("Ödeme tablosu toplamı ile fatura toplam tutarı arasında fark var. Bulunan Fark : " +remainder);
		}
	}

	public void detailValidations() {
		zeroLineAmountWarnings.clear();
		getLimitationChecker().clearMessages();
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
				updateLastSalePrices(detail);

				updateClerk(detail);
			}
		}
	}
	
	
	public boolean hasZeroLineAmountWarning() {
		return zeroLineAmountOption().equals(ControlType.Warning) ? true : false;
	}
	
	private ControlType zeroLineAmountOption() {
		return optionManager.getOption(LimitationOptionKey.SALEINVOICE_ZERO_LINEAMOUNT, true).getAsEnum(ControlType.class);
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
				lastSalePrice.setCurrency(item.getCurrencyOfItem());
			} else {
				BigDecimal quantityValue = BigDecimal.valueOf(item.getQuantity().getValue());
				BigDecimal purchaseValue = item.getTaxExcludedTotal().getValue().divide(quantityValue, 2, RoundingMode.HALF_UP);

				BigDecimal purchaseLocalAmount = item.getTaxExcludedTotal().getLocalAmount().divide(quantityValue, 2, RoundingMode.HALF_UP);

				lastSalePrice.setValue(purchaseValue);
				lastSalePrice.setLocalAmount(purchaseLocalAmount);
				lastSalePrice.setCurrency(item.getCurrencyOfItem());
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
	}

	public void createNewDetail() {
        manualFlush();

        TekirInvoiceDetail detail = new TekirInvoiceDetail();
        detail.setOwner(entity);
        entity.getItems().add(detail);

        log.debug("#{messages['logMessages.NewItemAdded']}");
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

		} else if (item.getProductType().equals(ProductType.Discount) || 
				   item.getProductType().equals(ProductType.Expense)){
			item.getParent().getChildList().remove(item);
			
			entity.getItems().remove(ix.intValue());

		} else if (item.getProductType().equals(ProductType.DocumentDiscount) || 
				item.getProductType().equals(ProductType.ContactDiscount)){
			entity.getItems().remove(ix.intValue());

		} else {
			entity.getItems().remove(ix.intValue());
		}
	}
	
	public void addContactDocumentDiscount() {
    	clearContactDiscounts();
    	
    	Contact contact = entity.getContact();
        if (contact != null) {
        	log.info("Adding contact document discount. Contact code :{0} ",entity.getContact().getCaption());

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
        }
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
			
			for (int i=entity.getItems().size()-1;i>=0;i-- ) {
				deleteLine(i);
			}
			
			updateStatusOfOrderNotes();
			
			result = super.delete();
		} catch (Exception e) {
			log.error("Fatura silinirken hata oluştu. Sebebi :{0}", e.getMessage());
			facesMessages.add(Severity.ERROR, "Fatura silinirken hata oluştu. Sebebi :{0}",e);
		}
		return result;
	}

	/**
	 * Fatura ile bağlantılı olan irsaliyeleri ve irsaliyelerin bağlı
	 * olduğu product txn kayıtlarını siler.
	 */
	@SuppressWarnings("unchecked")
	private void deleteShipmentNotesOfInvoice() {
		log.info("Deleting shipment notes of invoice...");

		List<TekirShipmentNote> tsn = entityManager.createQuery("select sn from TekirShipmentNote sn where " +
																"invoice = :invoice")
																.setParameter("invoice", entity)
																.getResultList();

		
		for (TekirShipmentNote sn: tsn) {
			productTxnAction.deleteProductTxnRecordsForShipmentNote(sn);
			entityManager.remove(sn);
		}
		entityManager.flush();
	}

	public void setSelectedPriceItem(PriceItem selectedPriceItem) {
		this.selectedPriceItem = selectedPriceItem;
	}

	public void refreshInvoiceWithSelectedPriceItem() {
		TekirInvoiceDetail detail = null;
		try {
			for (int i = 0; i < entity.getItems().size(); i++) {
				detail = entity.getItems().get(i);
				if (!(detail.getProductType() != null && 
						(detail.getProductType().equals(ProductType.Product) ||
						 detail.getProductType().equals(ProductType.Service) || 
						 detail.getProductType().equals(ProductType.DocumentDiscount) || 
						 detail.getProductType().equals(ProductType.ContactDiscount)) ))
					entity.getItems().remove(i);
				
			}
			for (int i = 0; i < entity.getItems().size(); i++) {
				detail = entity.getItems().get(i);
				
				if (detail.getProductType() != null && 
						(detail.getProductType().equals(ProductType.Product) ||
						 detail.getProductType().equals(ProductType.Service)) ) {
					detail.getChildList().clear();
					prepareSelectedLine(i, true);
				}
			}
		} catch (Exception e) {
			log.error("Hata : {0}", e);
			facesMessages.add(Severity.ERROR,"Faturayı tazelerken hata meydana geldi. Sebebi :\n {0}",e.getMessage());
		}
	}
	
	/**
	 * cari değiştiğinde fiyat listelerini güncellemek için kullanılacak.
	 */
	public void updatePriceItemList() {
		try {
			addContactDocumentDiscount();
			
			if (priceItemList == null && entity.getContact() != null && 
					entity.getContact().getCategory() != null  && entity.getDate() != null) {
				
				priceItemList = priceProvider.getPriceItemList(entity.getContact().getCategory().getPriceList(), 
															   entity.getDate());
				
				if (priceItemList == null || priceItemList.size() == 0) {
					log.warn("Unable to find a price list");
				} else {
					selectedPriceItem = priceItemList.get(0);
				}
			}
		} catch (Exception e) {
			log.error("Fiyat listeleri bulunurken hata oluştu. Hata sebebi: {0}\n", e);
			facesMessages.add(Severity.ERROR,"Fiyat listeleri bulunurken hata oluştu. Hata sebebi: {0}\n",e.getMessage());
		}
	}
	
	public List<PriceItem> getPriceItemList() {
		try {
			if (priceItemList == null && entity.getContact() != null && entity.getContact().getCategory() != null 
					&& entity.getDate() != null) {
				priceItemList = priceProvider.getPriceItemList(entity.getContact().getCategory().getPriceList(), 
						entity.getDate());
				
			}
		} catch (Exception e) {
			log.error("Fiyat listeleri bulunurken hata oluştu. Hata sebebi: {0}", e);
		}
		return priceItemList;
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
    	BigDecimal tableTotal = getPaymentTableTotal();

    	BigDecimal remainder = entity.getGrandTotal().getLocalAmount().subtract(tableTotal);

    	remainder = remainder.setScale(2, RoundingMode.HALF_UP);
    	return remainder;
    }
    
    private BigDecimal getPaymentTableTotal() {
    	MoneySet amount = null;
    	BigDecimal convertedAmount = null;

    	PaymentTable pt = entity.getPaymentTable();
    	if (pt == null) {
    		pt = new PaymentTable();
    		entity.setPaymentTable(pt);
    	}

    	BigDecimal tableTotal = BigDecimal.ZERO;

    	for (PaymentTableDetail item : entity.getPaymentTable().getDetailList()) {
    		amount = item.getAmount();

    		convertedAmount = currencyManager.convertToLocal(amount.getValue(), amount.getCurrency(), entity.getDate());

    		item.getAmount().setLocalAmount(convertedAmount);
    		
    		tableTotal = tableTotal.add(convertedAmount);
    	}
    	tableTotal = tableTotal.setScale(2, RoundingMode.HALF_UP);

    	return tableTotal;
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

	public void setAddress() {
		log.info("Updating address list. Contact code :{0}",entity.getContact().getCaption());
		if (selectedAddress != null) {
			entity.setDeliveryAddress(selectedAddress.getAddress());
		} else {
			entity.setDeliveryAddress(null);
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

    public void selectOrderNote(int ix) {
    	log.info("Selected order note. Index :{0}", ix);
    	manualFlush();

        TekirOrderNote sn = orderList.get(ix);

        InvoiceOrderLink ol = new InvoiceOrderLink();
        ol.setInvoice(entity);
        ol.setOrderNote(sn);
        
        entity.getOrderLinks().add(ol);
        entity.setClerk(sn.getClerk());
        entity.setWarehouse(sn.getWarehouse());
        entity.setAccount(sn.getAccount());

        orderList.remove(ix);

    	addOrderLines(sn);
    	
    	addPaymentTableDetails(sn);
    	
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

        removePaymentTableDetails(sn);

        orderList.remove(sn);
	}

    private void removePaymentTableDetails(TekirOrderNote orderNote) {
    	PaymentTable pt =entity.getPaymentTable();
    	PaymentTable orderNotept = orderNote.getPaymentTable();

    	if (pt != null && orderNotept != null) {
    		
    		PaymentTableDetail det = null;
    		for (int i=pt.getDetailList().size() - 1;i>=0;i--) {
    			det = pt.getDetailList().get(i);
    			
    			if (det.getReferenceId() != null) {
    			
    				for (PaymentTableDetail ptd : orderNotept.getDetailList()) {
    					if (ptd.getId().equals(det.getReferenceId())) {
    						ptd.setProcessed(false);
    						ptd.setReferenceId(null);
    						pt.getDetailList().remove(det);
    						break;
    					}
    				}
    			}
    			
    		}
    	}
		
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
	    			
	    			moveShipmentFieldsToInvoice(item, detail);
	    			detail.setReferenceDocumentId(item.getId());
	    			detail.setReferenceDocumentType(tsn.getDocumentType());
	    			
	    			
	    			entity.getItems().add(detail);
	    			
	    			detail.setOwner(entity);
	    			for (TekirOrderNoteDetail childItem : item.getChildList()) {
	    				TekirInvoiceDetail childDetail = new TekirInvoiceDetail();
	    				moveShipmentFieldsToInvoice(childItem, childDetail);
	    				
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

	private void moveShipmentFieldsToInvoice(TekirOrderNoteDetail fromDetail, TekirInvoiceDetail toDetail) {

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
		}
	
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

    public void setPriceItemList(List<PriceItem> priceItemList) {
		this.priceItemList = priceItemList;
	}

	public String getBarcodeInput() {
		return barcodeInput;
	}

	public void setBarcodeInput(String barcodeInput) {
		this.barcodeInput = barcodeInput;
	}

	public Double getBarcodeQuantity() {
		return barcodeQuantity;
	}

	public void setBarcodeQuantity(Double barcodeQuantity) {
		this.barcodeQuantity = barcodeQuantity;
	}

	public PriceItem getSelectedPriceItem() {
		return selectedPriceItem;
	}

	public void findUserPosAndAccount() {
		try {
//			if (entity.getPaymentType().equals(PaymentType.CreditCard)) {
//				setDefaultPos();
//				setDefaultAccount();
//			} else if (entity.getPaymentType().equals(PaymentType.Cash)) {
//				setDefaultAccount();
//			}

		} catch (Exception e) {
			log.error("Ön tanımlı kasa ve pos bulunurken hata meydana geldi. Hata sebebi:{0}\n", e);
			facesMessages.add(Severity.ERROR,"Ön tanımlı kasa ve pos bulunurken hata meydana geldi. Hata sebebi:{0}\n",e.getMessage());
		}
	}
	
	private void setDefaultPos() throws Exception {
		Option o = optionManager.getOption("systemSettings.control.DefaultPos");

		if (o != null) {
			String defaultPos = o.getValue();
			
//			Pos foundPos = generalSuggestion.findPos(defaultPos);
			
//			if (entity.getPos() == null) {
//				if (foundPos != null) {
//					entity.setPos(foundPos);
//				} else if (foundPos ==  null){
//					throw new Exception("Ön tanımlı pos bulunamadı. Kredi kartı ile işlem " +
//					"yapabilmeniz için ön tanımlı pos olması gerekmektedir.");
//				}
//			}
		}
	}

	private BigDecimal getMaxRoundingAmount() {
		Option o = optionManager.getOption(AmountLimitationOptionKey.MAX_ROUNDING_AMOUNT, true);

		BigDecimal maxRoundingAmount = BigDecimal.ZERO;
		if (o != null) {
			maxRoundingAmount = o.getAsBigDecimal();
		}
		return maxRoundingAmount;
	}
	
	private void setDefaultAccount() {
		Option o = optionManager.getOption("systemSettings.control.DefaultAccount");
		
		if (o != null) {
			String defaultAccount = o.getValue();
			
			Account foundAccount = generalSuggestion.findAccount(defaultAccount);

			if (entity.getAccount() == null) {
				if (foundAccount != null) {
					entity.setAccount(foundAccount);
				} else {
					throw new RuntimeException("Ön tanımlı kasa bulunamadı. İşlem yapabilmeniz için " +
											   "ön tanımlı kasa olması gerekmektedir.");
				}
			}
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

	public void print() {
		try {
			log.info("Printing invoice, invoice id=:{0}",entity.getId());
		
			JasperHandlerBean jasperReport = (JasperHandlerBean)Component.getInstance("jasperReport");

			Map<Object,Object> params = new HashMap<Object,Object>();
			params.put("pInvoiceId", entity.getId());
			
			NumberToText ntt = new NumberToTextTR();
			String totalAsText = ntt.convert(entity.getTotalAmount().getLocalAmount().doubleValue(), 
											 BaseConsts.SYSTEM_CURRENCY_CODE, 
											 BaseConsts.SYSTEM_CURRENCYDEC_CODE);

			params.put("pTotalAsText", totalAsText);

            String path =  systemProperties.getProperties().get("folder.templates") + Utils.getFileSeperator() ;
            params.put("SUBREPORT_DIR", path );
			
			jasperReport.compileAndRunReportToPdf("Perakende_Satis_Faturasi(" + entity.getReference()+ ")", "perakende_satis_faturasi", params);

		} catch (Exception ex) {
			log.error("Fatura yazdırılamadı. Sebebi :{0}", ex);
			facesMessages.add("Fatura yazdırılamadı. Sebebi :{0}",ex.getMessage());
		} 
	}

	public Payment getPaymentOfInvoice() {
		if (paymentOfInvoice == null) {
			paymentOfInvoice = findPaymentOfInvoice();
		}
		return paymentOfInvoice;
	}

	@SuppressWarnings("unchecked")
	private Payment findPaymentOfInvoice() {
		log.debug("Finding payment with invoice id :#0", entityId());
		List<Payment> resultList = entityManager.createQuery("select new Payment(p.id, p.serial) from Payment p where " +
															  "p.invoice.id=:invoiceId")
															  .setParameter("invoiceId", entityId())
															  .getResultList();

		
		if (resultList != null && resultList.size() == 1) {
			return resultList.get(0);
		}
		return null;
	}

	public TekirShipmentNote getShipmentOfInvoice() {
		if (shipmentOfInvoice == null) {
			shipmentOfInvoice = findShipmentOfInvoice();
		}
		return shipmentOfInvoice;
	}

	@SuppressWarnings("unchecked")
	private TekirShipmentNote findShipmentOfInvoice() {
		log.debug("Finding shipment with invoice id :#0", entityId());
		List<TekirShipmentNote> resultList = entityManager.createQuery("select new TekirShipmentNote(sn.id, sn.serial) from TekirShipmentNote sn where " +
																	   "sn.invoice.id=:invoiceId")
																	   .setParameter("invoiceId", entityId())
																	   .getResultList();

		if (resultList != null && resultList.size() == 1) {
			return resultList.get(0);
		}
		return null;
	}

	@Override
	public void updateDetailCurrencies() {
		for (int i=0;i<entity.getItems().size();i++) {
			calculateUnitPrice(i);
			setTaxExcludedAmount(i);
		}
	}

	public User getClerk() {
		return clerk;
	}

	public void setClerk(User clerk) {
		this.clerk = clerk;
	}

	public Boolean getShowMiniTable() {
		return showMiniTable;
	}

	public TekirInvoiceDetail getSelectedDetail() {
		return selectedDetail;
	}

	public void setSelectedDetail(TekirInvoiceDetail selectedDetail) {
		this.selectedDetail = selectedDetail;
	}

	public ContactAddress getSelectedAddress() {
		if (selectedAddress == null) {
			selectedAddress = new ContactAddress();
		}
		return selectedAddress;
	}

	public void setSelectedAddress(ContactAddress selectedAddress) {
		this.selectedAddress = selectedAddress;
	}

	public User getAuthorizedUser() {
		return authorizedUser;
	}

	public void setAuthorizedUser(User authorizedUser) {
		this.authorizedUser = authorizedUser;
	}

	public boolean isRequestDiscountPermission() {
		return requestDiscountPermission;
	}

	public void setRequestDiscountPermission(boolean requestDiscountPermission) {
		this.requestDiscountPermission = requestDiscountPermission;
	}

	public Integer getSelectedIndex() {
		return selectedIndex;
	}

	public void setSelectedIndex(Integer selectedIndex) {
		this.selectedIndex = selectedIndex;
	}

	public List<TekirOrderNote> getOrderList() {
		return orderList;
	}

	public void setOrderList(List<TekirOrderNote> orderList) {
		this.orderList = orderList;
	}

	public Product getSelectedDiscountExpense() {
		return selectedDiscountExpense;
	}

	public void setSelectedDiscountExpense(Product selectedDiscountExpense) {
		this.selectedDiscountExpense = selectedDiscountExpense;
	}

	public ContactOperation getContactOperation() {
		return contactOperation;
	}

	public void setContactOperation(ContactOperation contactOperation) {
		this.contactOperation = contactOperation;
	}

	public List<String> getZeroLineAmountWarnings() {
		return zeroLineAmountWarnings;
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
	
	

}
