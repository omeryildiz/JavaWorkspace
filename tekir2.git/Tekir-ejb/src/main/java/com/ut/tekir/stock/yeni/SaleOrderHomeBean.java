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

package com.ut.tekir.stock.yeni;

import java.math.BigDecimal;
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
import org.jboss.seam.international.StatusMessage.Severity;

import com.ut.tekir.entities.Contact;
import com.ut.tekir.entities.ContactAddress;
import com.ut.tekir.entities.ControlType;
import com.ut.tekir.entities.DiscountOrExpense;
import com.ut.tekir.entities.DistributionStyle;
import com.ut.tekir.entities.DocumentType;
import com.ut.tekir.entities.FinanceAction;
import com.ut.tekir.entities.MoneySet;
import com.ut.tekir.entities.OrderItem;
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
import com.ut.tekir.entities.TradeAction;
import com.ut.tekir.entities.User;
import com.ut.tekir.entities.ord.TekirOrderNote;
import com.ut.tekir.entities.ord.TekirOrderNoteCurrencyRate;
import com.ut.tekir.entities.ord.TekirOrderNoteCurrencySummary;
import com.ut.tekir.entities.ord.TekirOrderNoteDetail;
import com.ut.tekir.entities.ord.TekirOrderNoteTaxSummary;
import com.ut.tekir.finance.AccountTxnAction;
import com.ut.tekir.finance.FinanceTxnAction;
import com.ut.tekir.finance.FoundationTxnAction;
import com.ut.tekir.finance.PosTxnAction;
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
import com.ut.tekir.invoice.yeni.ContactOperation;
import com.ut.tekir.options.LimitationOptionKey;
import com.ut.tekir.stock.ProductTxnAction;
import com.ut.tekir.tender.PriceProvider;
import com.ut.tekir.tender.TenderCalculationHomeBean;
import com.ut.tekir.util.NumberToText;
import com.ut.tekir.util.NumberToTextTR;
import com.ut.tekir.util.Utils;

/**
 *
 * @author sinan.yumak
 */
@Stateful
@Name("saleOrderHome")
@Scope(value = ScopeType.CONVERSATION)
public class SaleOrderHomeBean extends TenderCalculationHomeBean<TekirOrderNote> implements SaleOrderHome<TekirOrderNote> {
    
    @In
    PosTxnAction posTxnAction;
    @In
    PriceProvider priceProvider;
    @In
    OptionManager optionManager;
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
	JasperHandlerBean jasperReport;
    @In
    GeneralSuggestion generalSuggestion;
    @In
    SystemProperties systemProperties;    
    @In
    FoundationTxnAction foundationTxnAction;
    @In
    FinanceTxnAction financeTxnAction;
    @In
    AccountTxnAction accountTxnAction;
    
    private ContactAddress selectedAddress;
    private TekirOrderNoteDetail selectedDetail;
    private Product selectedDiscountExpense;
    private Integer selectedIndex;
    
    private Boolean showMiniTable = Boolean.TRUE;
    
	private PriceItem selectedPriceItem;
	private List<PriceItem> priceItemList;//fatura için gelebilecek fiyat listesi tanımlarını tutar. 

    private User authorizedUser;
    private boolean requestDiscountPermission = Boolean.FALSE;
    private ContactOperation contactOperation = ContactOperation.Existing;
	
    private List<String> zeroLineAmountWarnings = new ArrayList<String>();
    
    @Create 
    @Begin(join=true,flushMode = org.jboss.seam.annotations.FlushModeType.MANUAL)
    public void init() {
		log.info("Creating sale invoice home...");
//		PriceItem defaultPriceItem = priceProvider.findDefaultPriceItem();
//		
//		if (defaultPriceItem == null) {
//			facesMessages.add(Severity.ERROR,"Lütfen öncelikle ön tanımlı fiyat listenizi tanımlayın!.");
//		} else {
//			setSelectedPriceItem(defaultPriceItem);
//		}
    }

    @Out(required = false)
    public TekirOrderNote getSaleOrder() {
        return getEntity();
    }

    @In(required = false)
    public void setSaleOrder(TekirOrderNote orderNote) {
        setEntity(orderNote);
    }

    @Override
    public void createNew() {
        log.debug("Creating new sale order note...");

        entity = new TekirOrderNote();
        entity.setTradeAction(TradeAction.Sale);
        entity.setDocumentType(DocumentType.SaleOrder);
        entity.setStatus(OrderStatus.Open);
        entity.setActive(true);
		entity.setDeliveryDate(calendarManager.getCurrentDate());

        entity.setSerial(sequenceManager.getNewSerialNumber(SequenceType.SERIAL_ORDER_SALE));
        //FIXME: entity.setReference(sequenceManager.getNewSerialNumber(SequenceType.REFERENCE_SHIPMENT_PURCHASE));
        entity.setDate(calendarManager.getCurrentDate());
        
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
			
            entity.setDeliverer(entity.getContact().getFullname());
            entity.setRecepient(entity.getClerk().getFullName());

        	result = super.save();
        	
        	if (result.equals(BaseConsts.SUCCESS)) {
				//cari txn yansımaları...
                // dikkat : fatura kesilmeden cariye borc yazilmamali
				//financeTxnAction.createFinanceTxnRecordsForOrderNote(entity);

        		Payment collection = saveCollectionForOrderNote();

				if (collection != null && collection.getItems().size() >0 ) {
					//pos ile odeme alindi ise yansitiliyor
					posTxnAction.createPosTxnRecordsForCollection(collection);
					
					//Tahsilat üzerindeki nakit miktar kadar account txn yansıması yapıyoruz.
					accountTxnAction.savePayment(collection);
					
					//kapora alindi ise kesilen tahsilat dekontunun cariye yansimasi
					financeTxnAction.createFinanceTxnRecordsForPayment(collection);
				}

                //urunlere revize etiketi koy
        		productTxnAction.createProductTxnRecordsForOrderNote(entity);
        		
				//Eğer katkı satırları varsa, kurum txn yansımalarını yapar.
				foundationTxnAction.createFoundationTxnRecordsForOrderNote(entity);
        	}
        	entityManager.flush();
        } catch (Exception e) {
			facesMessages.add(Utils.getExceptionMessage(e));
			log.error("Kaydedilirken hata meydana geldi. Sebebi: {0}", e);
			result = BaseConsts.FAIL;
        }
        return result;
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

	private Payment saveCollectionForOrderNote() {
		if (entity.getId() != null) {
			deleteCollectionOfOrderNote();
		}
		Payment collection = createCollectionFromPaymentTable();
		
		if (collection != null && collection.getItems() != null && collection.getItems().size() >0) {

			if (entity.getAccount() == null) {
				throw new RuntimeException("Kasa seçimini yapmanız gerekmektedir.");
			}

			collection.setOrderNote(getEntity());
			
			entityManager.persist(collection);
		} else {
			collection = null;
		}

		return collection;
	}
    
	/**
	 * Sipariş ile bağlantılı olan tahsilatları ve tahsilatların bağlı
	 * olduğu txn kayıtlarını siler.
	 */
	@SuppressWarnings("unchecked")
	private void deleteCollectionOfOrderNote() {
		log.info("Deleting collection of order note...");

		List<Payment> paymentList = entityManager.createQuery("select p from Payment p where " +
																"orderNote = :orderNote")
																.setParameter("orderNote", entity)
																.getResultList();

		for (Payment p : paymentList) {
			posTxnAction.deletePosTxnRecordsForCollection(p);

    		accountTxnAction.deletePayment(p);
            financeTxnAction.deletePayment(p);
        	entityManager.remove(p);
    	}
		entityManager.flush();
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
		
		collection.setRecepient(entity.getRecepient());
		collection.setDeliverer(entity.getDeliverer());
		
		collection.setInfo("Sipariş: " + entity.getReference());
		collection.setSerial(sequenceManager.getNewSerialNumber( SequenceType.SERIAL_FUND_COLLECTION ));
        collection.setReference(collection.getSerial());
		
        collection.setCreateDate(entity.getCreateDate());
        collection.setCreateUser(entity.getCreateUser());
        collection.setUpdateUser(entity.getUpdateUser());
        collection.setUpdateDate(entity.getUpdateDate());

		collection.setTime(entity.getDate());


		BigDecimal totalAmount = BigDecimal.ZERO;
		
		PaymentTable pt = entity.getPaymentTable();
		if (pt != null) {
			PaymentItem collectionItem = null;
			Pos pos = null;
			for (PaymentTableDetail ptd : pt.getDetailList()) {

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
			collection.getTotalAmount().setValue(totalAmount);
		}
		return collection;
	}

	@Override
	@Transactional
	public String delete() {
		String result = BaseConsts.SUCCESS;
		try {
			financeTxnAction.deleteFinanceTxnRecordsForOrderNote(entity);
			
			deleteCollectionOfOrderNote();
			
			foundationTxnAction.deleteFoundationTxnRecordsForOrderNote(entity);

        	productTxnAction.deleteProductTxnRecordsForOrderNote(entity);

			result = super.delete();
			
		} catch (Exception e) {
			log.error("Sipariş silinirken hata oluştu. Sebebi :{0}", e.getMessage());
			facesMessages.add(Severity.ERROR, "Sipariş silinirken hata oluştu. Sebebi :{0}",e);
		}
		return result;
	}

	
	private void persistParents() {
		TekirOrderNoteDetail detail = null;
		for (int i=0;i<entity.getItems().size();i++) {
			detail = entity.getItems().get(i);
		
			if (detail.getChildList() != null && detail.getChildList().size() != 0) {
				if (detail.getId() == null) {
					entityManager.persist(detail);

					for (TekirOrderNoteDetail child : detail.getChildList()) {
						child.setParent(detail);
					}
				}
			}
		}
	}

	private void setParentOfLists() {
		for(TekirOrderNoteTaxSummary item : entity.getTaxSummaryList()) {
			item.setOwner(entity);
		}
		for(TekirOrderNoteCurrencySummary item : entity.getCurrencySummaryList()) {
			item.setOwner(entity);
		}
		for(TekirOrderNoteCurrencyRate item : entity.getCurrencyRateList()) {
			item.setOwner(entity);
		}
	}

	public void calculateEverything() {
		try {
			BigDecimal difference = BigDecimal.ZERO;
			TekirOrderNoteDetail item = null;
			for (int i=0;i<entity.getItems().size();i++) {
				item = entity.getItems().get(i);
				
				difference = checkTotalAmountChanges(item);
				if (difference.compareTo(BigDecimal.ZERO) != 0) {
					log.info("Satır toplam tutarında değişiklik var! Index :{0}", i);

					DiscountOrExpense discount = new DiscountOrExpense();
					
					discount.setCurrency(item.getCurrencyOfItem());
					discount.setValue(difference.abs());
					discount.setPercentage(false);
//					discount.setLocalAmount(difference.abs());
					
					TekirOrderNoteDetail discountLine = new TekirOrderNoteDetail();
					
					discountLine.setProductType(ProductType.Discount);
					discountLine.setDiscount(discount);
					
					discountLine.setOwner(entity);
					
					discountLine.setParent(item);

					item.getOwner().getItems().add(i+1,discountLine);
					
					item.getChildList().add(discountLine);
				}
			}

			calculateEverything(entity);
		} catch (Exception e) {
			log.error("Sipariş hesaplamaları yapılırken hata meydana geldi. Sebebi :{0}", e);
			facesMessages.add(Severity.ERROR, "Sipariş hesaplamaları yapılırken hata meydana geldi. Sebebi :{0}",e);
		}
	}

	private void makeEntityValidations() {
		masterValidations();
		
		detailValidations();
	}

	public void masterValidations() {
		if (entity.getItems().size() == 0 ) {
			throw new RuntimeException("#{messages['beanMessages.AtLeastOneItemRequired']}");
		}
	}

	public void detailValidations() {
		zeroLineAmountWarnings.clear();
		
		TekirOrderNoteDetail detail = null;
		boolean containsService = false;
		for(int i=0;i<entity.getItems().size();i++) {
			detail = entity.getItems().get(i);

			if (detail.getProductType().equals(ProductType.Product) || 
					detail.getProductType().equals(ProductType.Service)) {

				if (detail.getProductType().equals(ProductType.Service)) {
					containsService = true;
				}
				
				if (detail.getProduct() == null) {
					throw new RuntimeException(i+1 + ". satırda hizmet/ürün seçilmemiş!" );
				}

				if (detail.getQuantity().getValue() <= 0) {
					throw new RuntimeException(i+1 + ". satırda Sıfırdan büyük bir değer girmelisiniz!" );
				}

				if (detail.getTaxExcludedUnitPrice().getValue().compareTo(BigDecimal.ZERO)<=0) {
					
					switch( zeroLineAmountOption() ) {
						case Required:
							throw new RuntimeException( i+1 + messages.get("error.label.LineAmountZero") );
						case Warning:
							zeroLineAmountWarnings.add( i+1 + messages.get("warning.label.LineAmountZero") );break;
						case NoControl:
							//do nothing...
					}
				}
			}
		}
		entity.setContainsService(containsService);
	}
	
	public boolean hasZeroLineAmountWarning() {
		return zeroLineAmountOption().equals(ControlType.Warning) ? true : false;
	}
	
	private ControlType zeroLineAmountOption() {
		return optionManager.getOption(LimitationOptionKey.SALEORDER_ZERO_LINEAMOUNT, true).getAsEnum(ControlType.class);
	}

    /**
	 * Tipi unknown olarak işaretlenmiş olan satırları listeden kaldırır.
	 */
	private void clearUnknownDetails() {
		log.info("Unknown satırları siliniyor.");
		TekirOrderNoteDetail det = null;
		for (int i = entity.getItems().size() - 1; i >= 0 ; i--) {
			det = entity.getItems().get(i);
			if (det.getProductType().equals(ProductType.Unknown)) {
				entity.getItems().remove(i);
				log.info("Unknown tipli satır bulundu. Index:{0}", i);
			}
		}
	}

	public void setAddress() {
		log.info("Updating address list. Contact code :{0}",entity.getContact().getCaption());
		if (selectedAddress != null) {
			entity.setDeliveryAddress(selectedAddress.getAddress());
		} else {
			entity.setDeliveryAddress(null);
		}
	}
	
    public void createNewLine() {
        manualFlush();
        if (entity == null) {
            return;
        }

        log.debug("EntityManager.FlushMode : #0", entityManager.getFlushMode());

        TekirOrderNoteDetail it = new TekirOrderNoteDetail();
//        it.setProductType(ProductType.Product);
        it.setOwner(entity);

        entity.getItems().add(it);

        log.debug("yeni item eklendi");
    }

    public void deleteLine(OrderItem item) {
        manualFlush();
        if (entity == null) {
            return;
        }
        log.debug("Kayıt Silinecek : {0}", item);
        entity.getItems().remove(item);
    }

    public void deleteLine(Integer ix) {
        manualFlush();
        if (entity == null) {
            return;
        }

		TekirOrderNoteDetail item = getEntity().getItems().get(ix.intValue());

		if (item.getProductType().equals(ProductType.Product)) {
			TekirOrderNoteDetail child = null;
			for(int i=item.getChildList().size() - 1;i>=0;i--) {
				child = item.getChildList().get(i);
				entity.getItems().remove(child);
			}
			entity.getItems().remove(ix.intValue());
		} else if (item.getProductType().equals(ProductType.Discount)){
			item.getParent().getChildList().remove(item);
			
			entity.getItems().remove(ix.intValue());
		} else if (item.getProductType().equals(ProductType.DocumentDiscount)){
			entity.getItems().remove(ix.intValue());
		} else {
			entity.getItems().remove(ix.intValue());
		}

    }

    public void addContactDocumentDiscount() {
    	log.info("Adding contact document discount. Contact code :{0} ",entity.getContact().getCaption());
    	
        Contact contact = entity.getContact();
        if (contact != null) {
        	if (contact.getHasDiscount()) {
        		TekirOrderNoteDetail discountLine = new TekirOrderNoteDetail();
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

    public void selectProduct(Integer ix) {
        manualFlush();
        if (entity == null) {
            return;
        }
        log.debug("İşlenecek IX : {0}", ix);

        
		manualFlush();
		try {
			prepareSelectedLine(ix, false);
			
		} catch (Exception e) {
			log.error("Hata: {0}", e);
			facesMessages.add("Urun secildikten sonra hata meydana geldi. Sebebi \n:{0}",e.getMessage());
		}

    }

	public void selectProductWithBarcode(Integer ix) {
		log.info("Product selected with barcode on line :{0}", ix.intValue());
		manualFlush();
		
		try {
			TekirOrderNoteDetail detail = entity.getItems().get(ix);

			Product product = generalSuggestion.findProductWithBarcode(detail.getBarcode());
			
			if (product != null) {
				detail.setProduct(product);

				prepareSelectedLine(ix,false);
				
				//Bir satırı doldurduk. Yeni bir tane daha açalım.
				createNewLine();
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
	
	
	public void print() {
		try {
			log.info("Printing order, order id=:{0}",entity.getId());
		
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
			
			jasperReport.compileAndRunReportToPdf("Satis_Siparis_Listesi(" + entity.getReference()+ ")", "satis_siparis_fisi", params);

		} catch (Exception ex) {
			log.error("Sipariş yazdırılamadı. Sebebi :{0}", ex);
			facesMessages.add("Sipariş yazdırılamadı. Sebebi :{0}",ex.getMessage());
		}
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

    public void createNewPaymentTableDetail() {
        log.info("Creating new payment table detail.");
    	manualFlush();

    	PaymentTableDetail detail = new PaymentTableDetail();
    	detail.setPaymentType(PaymentType.Cash);
    	
    	PaymentTable pt = entity.getPaymentTable();
    	if (pt == null) {
    		pt = new PaymentTable();
    		entity.setPaymentTable(pt);
    	}

    	pt.getDetailList().add(detail);
    	detail.setOwner(pt);
    	
    	log.info("Added payment table detail.");
    }

    public void deletePaymentTableDetail(Integer ix) {
        manualFlush();
        log.info("Deleting payment table detail line with index :{0}", ix);
        
        entity.getPaymentTable().getDetailList().remove(ix.intValue());
    }
    
    public void createDocumentDiscountLine() {
        log.info("Creating new document discount line...");
    	manualFlush();
    	
        selectedDetail = new TekirOrderNoteDetail();
    	selectedDetail.setOwner(entity);
    	selectedDetail.setProductType(ProductType.DocumentDiscount);
    }

	@Override
	public void createDocumentExpenseLine() {
		log.info("Creating new document expense line...");
		manualFlush();
		
		selectedDetail = new TekirOrderNoteDetail();
		selectedDetail.setOwner(entity);
		selectedDetail.setProductType(ProductType.DocumentExpense);
	}

    public void createDiscountLine(Integer ix) {
    	log.info("Creating discount line for index:{0}",ix);
    	manualFlush();
    	
    	selectedIndex = ix;
    	
    	selectedDetail = new TekirOrderNoteDetail();
    	selectedDetail.setOwner(entity);
    	selectedDetail.setProductType(ProductType.Discount);
    }

	@Override
	public void createExpenseLine(Integer ix) {
		log.info("Creating expense line for index:{0}",ix);
		manualFlush();
		
		selectedIndex = ix;
		
		selectedDetail = new TekirOrderNoteDetail();
		selectedDetail.setOwner(entity);
		selectedDetail.setProductType(ProductType.Expense);
	}

	@Override
	public void createDiscountAdditionLine() {
		log.info("Creating new document discount line...");
		manualFlush();
		
		selectedDetail = new TekirOrderNoteDetail();
		selectedDetail.setOwner(entity);
		selectedDetail.setProductType(ProductType.DiscountAddition);
	}

	@Override
	public void createExpenseAdditionLine() {
		log.info("Creating new document expense line...");
		manualFlush();
		
		selectedDetail = new TekirOrderNoteDetail();
		selectedDetail.setOwner(entity);
		selectedDetail.setProductType(ProductType.ExpenseAddition);
	}

    public void addDiscountToItem() {
        log.info("Adding discount to line...");
    	manualFlush();

    	TekirOrderNoteDetail detail = new TekirOrderNoteDetail();
    	detail.setProductType(ProductType.Discount);
    	DiscountOrExpense discount = null;
    	if (selectedDiscountExpense != null) {

    		discount = selectedDiscountExpense.getDiscountOrExpense();

    		detail.getDiscount().setPercentage(discount.getPercentage());
    		detail.getDiscount().setRate(discount.getRate());
    		detail.getDiscount().setValue(discount.getValue());

    		
    		TekirOrderNoteDetail selectedItem = entity.getItems().get(selectedIndex);
    		
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
			TekirOrderNoteDetail detail = new TekirOrderNoteDetail();
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

    @Override
    public void addExpenseToItem() {
    	log.info("Adding expense to line...");
    	manualFlush();
    	
    	TekirOrderNoteDetail detail = new TekirOrderNoteDetail();
    	detail.setProductType(ProductType.Expense);
    	DiscountOrExpense expense = null;
    	if (selectedDiscountExpense != null) {
    		
    		expense = selectedDiscountExpense.getDiscountOrExpense();
    		
    		detail.getExpense().setPercentage(expense.getPercentage());
    		detail.getExpense().setRate(expense.getRate());
    		detail.getExpense().setValue(expense.getValue());
    		detail.setProduct(selectedDiscountExpense);
    		
    		TekirOrderNoteDetail selectedItem = entity.getItems().get(selectedIndex);
    		
    		if (selectedItem != null) {
    			selectedItem.getChildList().add(detail);
    			detail.setParent(selectedItem);
    			
    			entity.getItems().add(selectedIndex + 1, detail);
    			
    			detail.setOwner(entity);
    		}
    	}
    	
    }

    @Override
	public void addDiscountExpenseAdditionToItem() {
        log.info("Adding discount or expense addition to line...");
    	manualFlush();
		
    	TekirOrderNoteDetail detail = new TekirOrderNoteDetail();

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

    public void addDocumentDiscountToItems() {
    	log.info("Adding document discount to line...");
    	manualFlush();

    	UserDiscountRightHome userDiscountRight = (UserDiscountRightHome)Component.getInstance("userDiscountRight",true);
    	try {
			
	    	TekirOrderNoteDetail detail = new TekirOrderNoteDetail();
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
    
	public void refreshInvoiceWithSelectedPriceItem() {
		TekirOrderNoteDetail detail = null;
		try {
			for (int i = 0; i < entity.getItems().size(); i++) {
				detail = entity.getItems().get(i);
				if (!(detail.getProductType() != null && 
						(detail.getProductType().equals(ProductType.Product) ||
						 detail.getProductType().equals(ProductType.Service) || 
						 detail.getProductType().equals(ProductType.ContactDiscount) ||
						 detail.getProductType().equals(ProductType.DocumentDiscount) ) ))
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

	private void prepareSelectedLine(int indexOfDetail,boolean refreshLine) throws Exception {
		TekirOrderNoteDetail aDetail = entity.getItems().get(indexOfDetail);
		Product aProduct = aDetail.getProduct();
		
		aDetail.setProductType(aProduct.getProductType());
		aDetail.getQuantity().setUnit(aProduct.getUnit());
		aDetail.setUnitPrice(new MoneySet());
		aDetail.setTaxExcludedAmount(new MoneySet());

		if (!refreshLine) {
			aDetail.getQuantity().setValue(1.0);
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

	/**
	 * Parent detaya fiyat listesi üzerinden gelen indirimi ekler.
	 * @param priceItemDetail
	 * @param parentDetail
	 */
	private void addDiscountLine(PriceItemDetail priceItemDetail,int indexOfDetail) {
		TekirOrderNoteDetail parentDetail = entity.getItems().get(indexOfDetail);
		
		MoneySet discountPrice = priceItemDetail.getDiscountPrice();

		TekirOrderNoteDetail discountDetail = new TekirOrderNoteDetail();
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

	/**
	 * cari değiştiğinde fiyat listelerini güncellemek için kullanılacak.
	 */
	public void updatePriceItemList() {
		try {
			if (priceItemList == null && entity.getContact() != null && 
					entity.getContact().getCategory().getPriceList() != null  && entity.getDate() != null) {
				
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
	}

	private void setTotalAmountTransientFields() {
		for (TekirOrderNoteDetail item : entity.getProductItems()) {
			item.setTotalAmountTransient(new MoneySet(item.getTotalAmount()));
		}
	}

    public void manualFlush() {
        Conversation.instance().changeFlushMode(org.jboss.seam.annotations.FlushModeType.MANUAL);
    }

	public ContactAddress getSelectedAddress() {
		return selectedAddress;
	}

	public void setSelectedAddress(ContactAddress selectedAddress) {
		this.selectedAddress = selectedAddress;
	}

	public TekirOrderNoteDetail getSelectedDetail() {
		return selectedDetail;
	}

	public void setSelectedDetail(TekirOrderNoteDetail selectedDetail) {
		this.selectedDetail = selectedDetail;
	}

	public Integer getSelectedIndex() {
		return selectedIndex;
	}

	public void setSelectedIndex(Integer selectedIndex) {
		this.selectedIndex = selectedIndex;
	}

	public Boolean getShowMiniTable() {
		return showMiniTable;
	}

	public PriceItem getSelectedPriceItem() {
		return selectedPriceItem;
	}

	public void setSelectedPriceItem(PriceItem selectedPriceItem) {
		this.selectedPriceItem = selectedPriceItem;
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

	@Override
	public void updateDetailCurrencies() {
		for (int i=0;i<entity.getItems().size();i++) {
			calculateUnitPrice(i);
			setTaxExcludedAmount(i);
		}
	}
	
	public void setPriceItemList(List<PriceItem> priceItemList) {
		this.priceItemList = priceItemList;
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

	@Override
	public List<String> getZeroLineAmountWarnings() {
		return zeroLineAmountWarnings;
	}
	
}
