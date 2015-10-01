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

import com.ut.tekir.entities.BarcodeTxn;
import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.ejb.Stateful;
import javax.faces.application.FacesMessage;

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
import org.jboss.seam.international.StatusMessage.Severity;

import com.ut.tekir.entities.Contact;
import com.ut.tekir.entities.ContactAddress;
import com.ut.tekir.entities.ControlType;
import com.ut.tekir.entities.DiscountOrExpense;
import com.ut.tekir.entities.DistributionStyle;
import com.ut.tekir.entities.DocumentType;
import com.ut.tekir.entities.FinanceAction;
import com.ut.tekir.entities.InvoiceOrderLink;
import com.ut.tekir.entities.InvoicePaymentPlanItem;
import com.ut.tekir.entities.MoneySet;
import com.ut.tekir.entities.Payment;
import com.ut.tekir.entities.PaymentItem;
import com.ut.tekir.entities.PaymentItemCreditCard;
import com.ut.tekir.entities.PaymentPlan;
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
import com.ut.tekir.entities.ShipmentItem;
import com.ut.tekir.entities.ShipmentNote;
import com.ut.tekir.entities.TaxEmbeddable;
import com.ut.tekir.entities.TenderDetailBase;
import com.ut.tekir.entities.TradeAction;
import com.ut.tekir.entities.User;
import com.ut.tekir.entities.inv.TekirInvoice;
import com.ut.tekir.entities.inv.TekirInvoiceCurrencyRate;
import com.ut.tekir.entities.inv.TekirInvoiceCurrencySummary;
import com.ut.tekir.entities.inv.TekirInvoiceDetail;
import com.ut.tekir.entities.inv.TekirInvoiceTaxSummary;
import com.ut.tekir.entities.shp.TekirShipmentNote;
import com.ut.tekir.entities.shp.TekirShipmentNoteDetail;
import com.ut.tekir.finance.AccountTxnAction;
import com.ut.tekir.finance.FinanceTxnAction;
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
import com.ut.tekir.framework.UserDiscountRightHome;
import com.ut.tekir.general.GeneralSuggestion;
import com.ut.tekir.options.OptionKey;
import com.ut.tekir.stock.ProductTxnAction;
import com.ut.tekir.tender.PosPrinterHome;
import com.ut.tekir.tender.PriceProvider;
import com.ut.tekir.tender.TenderCalculationHomeBean;
import com.ut.tekir.util.Utils;
import org.jboss.seam.international.StatusMessage;

//FIXME: fatura kapatma işlemlerini düzenlemek gerek.

/**
 * İrsaliyeli alış ve normal alış faturası için kullanacağımız 
 * home sınıfımızdır.
 * Not:Faturalar için kullanılacak parametreler,
 * İrsaliyeli Faturalar İçin, it=0, at=1 
 * (PurchaseInvoice, ekranda irsaliye listesi çıkmaz. Otomatik olarak bir irsaliye üretilir.
 * Ancak döküman tipi PurchaseShipmentInvoice olmalıdır.)
 * Faturalar İçin,            it=1, at=1 
 * (PurchaseShipmentInvoice, ekranda bir irsaliye listesi çıkmalı.
 * Ancak döküman tipi PurchaseInvoice olmalıdır.)
 * İade Faturaları İçin,      it=2, at=0
 * @author sinan.yumak
 */
@Stateful
@Name("purchaseInvoiceHome")
@Scope(value = ScopeType.CONVERSATION)
public class PurchaseInvoiceHomeBean extends TenderCalculationHomeBean<TekirInvoice> implements PurchaseInvoiceHome<TekirInvoice> {

	@In
	PosTxnAction posTxnAction;
	@In
	TaxTxnAction taxTxnAction;
	@In
	OptionManager optionManager;
	@In
	PriceProvider priceProvider;
	@In
	Map<Object, String> messages;
    @In(create = true)
    SequenceManager sequenceManager;
    @In
    CalendarManager calendarManager;
    @In
    CurrencyManager currencyManager;
    @In
    ProductTxnAction productTxnAction;
    @In
    FinanceTxnAction financeTxnAction;
    @In
    AccountTxnAction accountTxnAction;
	@In(create=true,required=false)
	InvoiceSuggestion invoiceSuggestion;
    @In
    GeneralSuggestion generalSuggestion;
    @In(create=true)
    private PaymentPlanItemHome paymentPlanItemHome;

    private LimitationChecker limitationChecker;

    private OldState oldState;
    private TekirInvoiceDetail selectedDetail;
    private Product selectedDiscountExpense;
    private ContactAddress selectedAddress;
    private Integer selectedIndex;

    private Boolean showMiniTable = Boolean.TRUE;
    private User authorizedUser;
    private boolean requestDiscountPermission = Boolean.FALSE;
    
    private Integer invoiceType = 0;
    private Integer actionType = 0;

    private DocumentPrint documentPrint;

    private List<TekirShipmentNote> shipmentList = new ArrayList<TekirShipmentNote>();
    private List<TekirInvoice> invoiceList = new ArrayList<TekirInvoice>();

    private InvoiceSuggestionFilterModel filterModel = new InvoiceSuggestionFilterModel();

    @Create
    @Begin(join = true, flushMode = org.jboss.seam.annotations.FlushModeType.MANUAL)
    public void init() {

    }

    @Out(required = false)
    public TekirInvoice getPurchaseInvoice() {
        return getEntity();
    }

    @In(required = false)
    public void setPurchaseInvoice(TekirInvoice invoice) {
        setEntity(invoice);
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
        log.debug("Yeni Invoice");

        entity = new TekirInvoice();
        
		if (actionType == 1) {
			//eğer action type 1 ise alış faturası, değilse alış iade faturası.
			entity.setTradeAction(TradeAction.Purchase);
            entity.setSerial(sequenceManager.getNewSerialNumber(SequenceType.SERIAL_INVOICE_PURCHASE));
		} else {
            //eğer action type 2 de degilse bu bir alış iade faturasıdır. Bu nedenle
			entity.setTradeAction(TradeAction.PurchseReturn);
            entity.setSerial(sequenceManager.getNewSerialNumber(SequenceType.SERIAL_INVOICE_SALE));
		}

        if (invoiceType == 1) {
        	//eğer invoice type 1 ise, alış faturası açılır. Ekranda irsaliye 
        	//listesi görüntülenmeli.
        	entity.setDocumentType(DocumentType.PurchaseInvoice);
        } else if (invoiceType == 2){
            // it=2 irsaliyeli satis iade faturasi
        	entity.setDocumentType(DocumentType.SaleShipmentInvoice);
        } else  {
            // it=0 gelirse irsaliyeli satis faturasi
        	entity.setDocumentType(DocumentType.PurchaseShipmentInvoice);
        }

        entity.setActive(true);
        entity.setDate(calendarManager.getCurrentDate());
        entity.setShippingDate(calendarManager.getCurrentDate());
        entity.setTime(new java.util.Date());
    }

    //FIXME: item service ise serviceTxn e kayıt atılmalı. bu haliyle serviceTxn kaydı atılmıyor.
    @Override
    @Transactional
    public String save() {
		log.info("Saving order note. Order serial :{0}", entity.getSerial());
		String result = BaseConsts.FAIL;
		
        try {
			clearUnknownDetails();

			makeEntityValidations();
			
			calculateEverything();
			
			setParentOfLists();
			
			persistParents();
			
			copyDeliveryContactInfoFromContactCard();
			
			result = super.save();

			if (result.equals(BaseConsts.SUCCESS)) {

				if (entity.getTradeAction().equals(TradeAction.PurchseReturn))  {
                    //FIXME: satis iade faturasinin alis ft. tipinde irsaliyesinin, pro_txn,
					updateReturnedStatusOfInvoice();
				}

				//irsaliyeyi kesiyoruz.
				if (entity.getDocumentType().equals(DocumentType.PurchaseShipmentInvoice) || 
						entity.getDocumentType().equals(DocumentType.SaleShipmentInvoice)) {
					TekirShipmentNote tsn = saveShipmentNoteForInvoice();
					productTxnAction.createProductTxnRecordsForShipment(tsn);
				}

				setPaymentTableReferenceIds();

				//cari txn yansımaları...
				financeTxnAction.createFinanceTxnRecordsForInvoice(entity);

                //vergi txn yansimasi
				taxTxnAction.createTaxTxnRecordsForInvoice(entity);

				Payment collection = saveCollectionForInvoice();
				if (collection != null) {
					
					//kredi kartli satis yansimasi
					posTxnAction.createPosTxnRecordsForCollection(collection);
					
					//kasaya nakit satis yansimasi
					accountTxnAction.savePayment(collection);
					
					//cariye tahsilat yansimasi
					financeTxnAction.createFinanceTxnRecordsForPayment(collection);
				}
			}
			entityManager.flush();
			events.raiseEvent("refreshBrowser:com.ut.tekir.entities.shp.TekirShipmentNote");
        } catch (Exception e) {
			facesMessages.add(e.getMessage());
			log.error("Hata: {0}", e);
			result = BaseConsts.FAIL;
        }
        return result;
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
					BigDecimal convertedAmount = currencyManager.convertToLocal(ptd.getAmount().getValue(),
																			   ptd.getAmount().getCurrency(),
																			   entity.getDate());
					ptd.getAmount().setLocalAmount(convertedAmount);

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

	private void copyDeliveryContactInfoFromContactCard() {
		Contact contact = entity.getContact();

		entity.setDeliveryPerson(contact.getPerson());
		entity.setDeliveryCompany(contact.getCompany());
		entity.setDeliveryFullname(contact.getFullname());
		entity.setDeliverySsn(contact.getSsn());
		entity.setDeliveryTaxNumber(contact.getTaxNumber());
		entity.setDeliveryTaxOffice(contact.getTaxOffice());
	}

    private void updateLastSalePrices(TekirInvoiceDetail item) {
		MoneySet lastPurchasePrice = item.getProduct().getLastPurchasePrice();
		
		lastPurchasePrice.setCurrency(item.getCurrencyOfItem());

		TaxEmbeddable tax1 = item.getTax1();
		if (tax1 != null) {
			if (tax1.getTaxIncluded()) {
				BigDecimal quantityValue = BigDecimal.valueOf(item.getQuantity().getValue());
				BigDecimal purchaseValue = item.getTotalAmount().getValue().divide(quantityValue, 2, RoundingMode.HALF_UP);

				BigDecimal purchaseLocalAmount = item.getTotalAmount().getLocalAmount().divide(quantityValue, 2, RoundingMode.HALF_UP);

				lastPurchasePrice.setValue(purchaseValue);
				lastPurchasePrice.setLocalAmount(purchaseLocalAmount);
			} else {
				BigDecimal quantityValue = BigDecimal.valueOf(item.getQuantity().getValue());
				BigDecimal purchaseValue = item.getTaxExcludedTotal().getValue().divide(quantityValue, 2, RoundingMode.HALF_UP);

				BigDecimal purchaseLocalAmount = item.getTaxExcludedTotal().getLocalAmount().divide(quantityValue, 2, RoundingMode.HALF_UP);

				lastPurchasePrice.setValue(purchaseValue);
				lastPurchasePrice.setLocalAmount(purchaseLocalAmount);
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
//				entityManager.createQuery("update TekirShipmentNote tsn set tsn.invoice = null where " +
//										  "tsn.id=:id")
//										  .setParameter("id", tsn.getId())
//										  .executeUpdate();
			}
		}
		entityManager.flush();
	}

	private TekirShipmentNote createShipmentNoteFromInvoice() {
		log.info("Creating shipment note from invoice. Invoice code :{0}", entity.getSerial());
		TekirShipmentNote sn = new TekirShipmentNote();
		sn.setInvoice(entity);
		sn.setActive(Boolean.TRUE);

		//sn.setTradeAction(TradeAction.Purchase);  iade donusleri de burada
        sn.setTradeAction(entity.getTradeAction());
		sn.setContact(entity.getContact());
		sn.setCode(entity.getCode());
		//sn.setDocumentType(DocumentType.PurchaseShipmentNote);
        if (entity.getDocumentType().equals(DocumentType.SaleShipmentInvoice) ) {
                sn.setDocumentType(DocumentType.SaleShipmentNote);
        }  else  sn.setDocumentType(DocumentType.PurchaseShipmentNote);
		
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

	public void makeEntityValidations() {
		masterValidations();
		
		detailValidations();
	}

	public void masterValidations() {
		if (entity.getItems().size() == 0 ) {
			throw new RuntimeException("#{messages['beanMessages.AtLeastOneItemRequired']}");
		}
		
		getLimitationChecker().clearMessages();
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

	public void updateDetailCurrencies() {
		for (int i=0;i<entity.getItems().size();i++) {
			calculateUnitPrice(i);
			setTaxExcludedAmount(i);
		}
	}

	public void detailValidations() {
		TekirInvoiceDetail detail = null;
		for(int i=0;i<entity.getItems().size();i++) {
			detail = entity.getItems().get(i);

			if (detail.getProductType().equals(ProductType.Product) ||
					detail.getProductType().equals(ProductType.Service)) {

				if (detail.getProduct() == null) {
					throw new RuntimeException(i+1 + ". satırda hizmet/ürün seçilmemiş!" );
				}

				if (detail.getQuantity().getValue() <= 0) {
					throw new RuntimeException(i+1 + ". satırda Sıfırdan büyük bir değer girmelisiniz!" );
				}

				getLimitationChecker().purchaseInvoiceZeroLineAmount(i, detail.getTaxExcludedUnitPrice().getValue());

				//son alış fiyatlarını güncelliyoruz.
				if (!entity.getTradeAction().equals(TradeAction.PurchseReturn)) {
					updateLastSalePrices(detail);
				}
			}
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

    @Override
    @Transactional
    public String delete() {
		String result = BaseConsts.SUCCESS;
		try {
			deleteShipmentNotesOfInvoice();

			financeTxnAction.deleteFinanceTxnRecordsForInvoice(entity);
			
			taxTxnAction.deleteTaxTxnRecordsForInvoice(entity);
			
			deleteCollectionOfInvoice();

            PaymentTable pt = entity.getPaymentTable();
            if ( pt != null ) {
            	for (int i=pt.getDetailList().size()-1;i>=0;i--) {
            		deletePaymentTableDetail(i);
            	}
            }

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
			
			result = super.delete();
		} catch (Exception e) {
			log.error("Fatura silinirken hata oluştu. Sebebi :{0}", e.getMessage());
			facesMessages.add(Severity.ERROR, "Fatura silinirken hata oluştu. Sebebi :{0}",e);
		}
		return result;
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

    public void createNewDetail() {
        manualFlush();

        TekirInvoiceDetail it = new TekirInvoiceDetail();
        it.getTaxExcludedAmount().setCurrency(entity.getDocCurrency());
        it.setOwner(entity);
        entity.getItems().add(it);
    }

    public void deleteLine(Integer ix) {
        manualFlush();
        if (entity == null) {
            return;
        }

		TekirInvoiceDetail item = getEntity().getItems().get(ix.intValue());

		if (item.getProductType().equals(ProductType.Product) || 
				item.getProductType().equals(ProductType.Service)) {
			TekirInvoiceDetail child = null;
			for(int i=item.getChildList().size() - 1;i>=0;i--) {
				child = item.getChildList().get(i);
				entity.getItems().remove(child);
			}
			entity.getItems().remove(ix.intValue());
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
	
    @Override
	public void manualFlush() {
        Conversation.instance().changeFlushMode(org.jboss.seam.annotations.FlushModeType.MANUAL);
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

	public void print(String templateName) {
		log.info("Printing invoice, invoice id=:{0}, template name :{1}",entity.getId(), templateName);
		try {
			getDocumentPrint().print(templateName);
		} catch (Exception ex) {
			log.error("Invoice print error :{0}", ex);
			facesMessages.add(ex.getMessage());
		}
	}

	@Override
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
        }
	}

	private void setPaymentPlan() {
		Contact contact = entity.getContact();
		PaymentPlan paymentPlan = contact.getPaymentPlan();
		entity.setPaymentPlan(paymentPlan);
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
		
		BigDecimal convertedValue = currencyManager.convertToLocal(doe.getValue(), doe.getCurrency(), entity.getDate());
		doe.setLocalAmount(convertedValue);
		doe.setCurrency(entity.getDocCurrency());

		setLocalAmountOf(doe);
		
		entity.getItems().add(detail);
		detail.setOwner(entity);
	}

	@Override
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
						
						discount.setCurrency(getEntity().getDocCurrency());
						discount.setValue(difference.abs());
						discount.setPercentage(false);
//					discount.setLocalAmount(difference.abs());

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
	public void createDocumentDiscountLine() {
        log.info("Creating new document discount line...");
    	manualFlush();
    	
        selectedDetail = new TekirInvoiceDetail();
    	selectedDetail.setOwner(entity);
    	selectedDetail.setProductType(ProductType.DocumentDiscount);
		selectedDetail.getTaxExcludedAmount().setCurrency(entity.getDocCurrency());
    	authorizedUser = new User();
	}

	@Override
	public void createDocumentExpenseLine() {
		log.info("Creating new document expense line...");
		manualFlush();
		
		selectedDetail = new TekirInvoiceDetail();
		selectedDetail.setOwner(entity);
		selectedDetail.setProductType(ProductType.DocumentExpense);
		selectedDetail.getTaxExcludedAmount().setCurrency(entity.getDocCurrency());
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
		selectedDetail.setProductType(ProductType.ExpenseAddition);
		selectedDetail.getTaxExcludedAmount().setCurrency(entity.getDocCurrency());
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
	public TekirInvoiceDetail getSelectedDetail() {
		return selectedDetail;
	}

	@Override
	public void selectProduct(Integer ix) {
        manualFlush();
        if (entity == null) {
            return;
        }
        log.debug("İşlenecek IX : {0}", ix);

        TekirInvoiceDetail detail = entity.getItems().get(ix.intValue());
        if (detail != null && detail.getProduct() != null) {
        	detail.getQuantity().setUnit(detail.getProduct().getUnit());
        	detail.getQuantity().setValue(1.0);
        	detail.setProductType(ProductType.Product);
        } else {
			detail.setProduct(null);
			detail.setProductType(ProductType.Unknown);
			detail.getQuantity().setUnit(null);
			detail.getQuantity().setValue(0.0);
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
				detail.setProductType(ProductType.Product);
				detail.getQuantity().setValue(1.0);
				detail.getQuantity().setUnit(product.getUnit());

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

    @SuppressWarnings("unchecked")
	public List<TekirShipmentNote> findShipments() {
    	try {
	    	if (entity.getContact() != null) {
	    		
				List<TekirShipmentNote> resultList = entityManager.createQuery("select sn from TekirShipmentNote sn where " +
																				  "sn.tradeAction=:tradeAction and " + 
																				  "sn.invoice= null and " + 
																				  "sn.contact=:contact ")
																				  .setParameter("contact", entity.getContact())
																				  .setParameter("tradeAction", TradeAction.Purchase)
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

    @SuppressWarnings("unchecked")
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

    public void selectShipmentNote(int ix) {
    	log.info("Selected shipment note. Index :{0}", ix);
    	manualFlush();

        TekirShipmentNote sn = shipmentList.get(ix);

        sn.setInvoice(entity);
        entity.getShipmentList().add(sn);
        
        // irsaliyenin iş takip numarasını faturaya setle.
        if (entity.getWorkBunch() == null || !entity.getWorkBunch().equals(sn.getWorkBunch())){
        	entity.setWorkBunch(sn.getWorkBunch());
        }

        shipmentList.remove(ix);

    	addShipmentLines(sn);
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

    private void addShipmentLines(TekirShipmentNote tsn) {
    	TekirInvoiceDetail detail = null;
    	TekirInvoiceDetail childDetail = null;
    	for (TekirShipmentNoteDetail item : tsn.getItems()) {
    		
    		detail = new TekirInvoiceDetail();

    		moveShipmentFieldsToInvoice(item, detail);
    		
			entity.getItems().add(detail);
			detail.setOwner(entity);
    		for (TekirShipmentNoteDetail childItem : item.getChildList()) {
    			childDetail = new TekirInvoiceDetail();
    			moveShipmentFieldsToInvoice(childItem, childDetail);

    			detail.getChildList().add(childDetail);
    			childDetail.setParent(detail);

    			childDetail.setOwner(entity);

    			entity.getItems().add(childDetail);
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

	//FIXME:Şu an ihtiyaç yok. Ama ileride düzenlemek gerekecek.
	public void removeInvoice(int ix) {
		log.info("Removing invoice. Index :{0}", ix);
		manualFlush();
		
		invoiceList.remove(ix);
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

    private BigDecimal getPaymentTableRemainder() {
    	MoneySet amount = null;
    	BigDecimal convertedAmount = null;

    	BigDecimal tableTotal = BigDecimal.ZERO;
    	BigDecimal remainder = BigDecimal.ZERO;

    	PaymentTable pt = entity.getPaymentTable();
    	if (pt == null) {
    		pt = new PaymentTable();
    		entity.setPaymentTable(pt);
    	}

    	for (PaymentTableDetail item : entity.getPaymentTable().getDetailList()) {
    		amount = item.getAmount();

    		convertedAmount = currencyManager.convertToLocal(amount.getValue(), amount.getCurrency(), entity.getDate());

    		item.getAmount().setLocalAmount(convertedAmount);

    		tableTotal = tableTotal.add(convertedAmount);
    	}
    	tableTotal = tableTotal.setScale(2, RoundingMode.HALF_UP);

    	remainder = entity.getTotalAmount().getLocalAmount().subtract(tableTotal);

    	remainder = remainder.setScale(2, RoundingMode.HALF_UP);
    	return remainder;
    }

    public void checkPurchasePrices() {
    	log.info("Checking Purchase Prices...");
    	List<String> errorList = new ArrayList<String>();
    	try {
    		calculateEverything();
    		
        	for (TekirInvoiceDetail item : entity.getProductItems()) {
        		
        		if (item.getProduct() != null && entity.getContact() != null) {
        			PriceItemDetail priceItemDetail = priceProvider.findPurchasePriceItemDetailForProduct(item.getProduct(),entity.getContact());

        			if (priceItemDetail != null) {
        				try {
        					priceItemDetail.checkTolerance(item.getProduct(), item.getGrandTotal().getValue());
        				} catch (Exception e) {
        					errorList.add(e.getMessage());
        				}
        			}
        		}
        	}
    		for (String error : errorList) {
    			facesMessages.add(error);
    		}
    	} catch (Exception e) {
			facesMessages.add(Utils.getExceptionMessage(e));
			log.info("Alış fiyat listesi karşılaştırmaları yapılırken hata meydana geldi. Sebebi #0", e);
		}
    }
    
	@Override
	public void setSelectedDetail(TekirInvoiceDetail selectedDetail) {
		this.selectedDetail = selectedDetail;
	}
	
	@Override
	public void setId(Long id) {
        if (entity != null) {
            return;
        }

		super.setId(id);
		
		setTotalAmountTransientFields();
	}

	private void setTotalAmountTransientFields() {
		for (TekirInvoiceDetail item : entity.getProductItems()) {
			item.setTotalAmountTransient(new MoneySet(item.getTotalAmount()));
		}
	}

    public List<ReturnInvoiceStatus> getPossibleStatusList () {
		List<ReturnInvoiceStatus> possibleStatusList = new ArrayList<ReturnInvoiceStatus>();
		possibleStatusList.add(ReturnInvoiceStatus.Open);
		possibleStatusList.add(ReturnInvoiceStatus.Processing);
		return possibleStatusList;
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

	public Integer getInvoiceType() {
		return invoiceType;
	}

	public void setInvoiceType(Integer invoiceType) {
		this.invoiceType = invoiceType;
	}

	public List<TekirShipmentNote> getShipmentList() {
		return shipmentList;
	}

	public void setShipmentList(List<TekirShipmentNote> shipmentList) {
		this.shipmentList = shipmentList;
	}

	public Integer getActionType() {
		return actionType;
	}

	public void setActionType(Integer actionType) {
		this.actionType = actionType;
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

	public InvoiceSuggestionFilterModel getFilterModel() {
		filterModel.setTradeAction(TradeAction.Purchase);
		filterModel.setRetInvoiceStatus(getPossibleStatusList());
		filterModel.setContact(entity.getContact());
		return filterModel;
	}

	public void setFilterModel(InvoiceSuggestionFilterModel filterModel) {
		this.filterModel = filterModel;
	}

	private DocumentPrint getDocumentPrint() {
		if (documentPrint == null) {
			documentPrint = InvoicePrint.instance(getEntity());
		}
		return documentPrint;
	}
        
        /**
         * Alış yapılan ürünlerin hepsi için alış miktarında barcode basımı için kuyruğa gönderir.
        */
        @Override
        public void sendToBarcodeSpool() {
            
                for( TekirInvoiceDetail item :  entity.getProductItems() ){
                                
                    log.info("Sending to barcode pool, product caption :{0}", item.getProduct().getCaption());
                    try {
                         BarcodeTxn barcodeTxn = new BarcodeTxn();

                         barcodeTxn.setProduct(item.getProduct());
                         barcodeTxn.setUnit(item.getQuantity().getValue().intValue());

                         entityManager.persist(barcodeTxn);
                         entityManager.flush();

                         log.info("Barkod havuzuna başarılı bir şekilde gönderildi. Ürün kodu : {0}", item.getProduct().getCaption());

                     } catch (Exception e) {
                             log.error("Hata :{0}", e);
                             facesMessages.add(StatusMessage.Severity.ERROR,"Barkod havuzuna gönderirken hata meydana geldi.Hata sebebi:\n{0}",e.getMessage());
                     }

                }
            
		facesMessages.add("Barkod havuzuna başarılı bir şekilde gönderildi.");
	}

}