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
import org.jboss.seam.international.StatusMessage.Severity;

import com.ut.tekir.entities.Contact;
import com.ut.tekir.entities.ContactAddress;
import com.ut.tekir.entities.ControlType;
import com.ut.tekir.entities.DiscountOrExpense;
import com.ut.tekir.entities.DistributionStyle;
import com.ut.tekir.entities.DocumentType;
import com.ut.tekir.entities.MoneySet;
import com.ut.tekir.entities.OrderShipmentLink;
import com.ut.tekir.entities.OrderStatus;
import com.ut.tekir.entities.PriceItemDetail;
import com.ut.tekir.entities.Product;
import com.ut.tekir.entities.ProductType;
import com.ut.tekir.entities.Quantity;
import com.ut.tekir.entities.ShipmentItem;
import com.ut.tekir.entities.TradeAction;
import com.ut.tekir.entities.User;
import com.ut.tekir.entities.ord.TekirOrderNote;
import com.ut.tekir.entities.ord.TekirOrderNoteDetail;
import com.ut.tekir.entities.shp.TekirShipmentNote;
import com.ut.tekir.entities.shp.TekirShipmentNoteCurrencyRate;
import com.ut.tekir.entities.shp.TekirShipmentNoteCurrencySummary;
import com.ut.tekir.entities.shp.TekirShipmentNoteDetail;
import com.ut.tekir.entities.shp.TekirShipmentNoteTaxSummary;
import com.ut.tekir.framework.BaseConsts;
import com.ut.tekir.framework.CalendarManager;
import com.ut.tekir.framework.JasperHandlerBean;
import com.ut.tekir.framework.OptionManager;
import com.ut.tekir.framework.SequenceManager;
import com.ut.tekir.framework.SequenceType;
import com.ut.tekir.framework.SystemProperties;
import com.ut.tekir.framework.UserDiscountRightHome;
import com.ut.tekir.general.GeneralSuggestion;
import com.ut.tekir.options.LimitationOptionKey;
import com.ut.tekir.stock.ProductTxnAction;
import com.ut.tekir.stock.StockSuggestion;
import com.ut.tekir.tender.PriceProvider;
import com.ut.tekir.tender.TenderCalculationHomeBean;
import com.ut.tekir.util.NumberToText;
import com.ut.tekir.util.NumberToTextTR;
import com.ut.tekir.util.Utils;

/**
 * Yeni irsaliye yapısına uygun home bileşenimiz.
 * @author sinan.yumak
 */
@Stateful
@Name("saleShipmentHome")
@Scope(value = ScopeType.CONVERSATION)
public class SaleShipmentHomeBean extends TenderCalculationHomeBean<TekirShipmentNote> implements SaleShipmentHome<TekirShipmentNote>{

	@In
	PriceProvider priceProvider;
	@In
	OptionManager optionManager;
    @In
    Map<Object, String> messages;
	@In
	JasperHandlerBean jasperReport;
	@In(create = true)
    SequenceManager sequenceManager;
	@In(create=true)
	StockSuggestion stockSuggestion;
	@In
    CalendarManager calendarManager;
	@In
    ProductTxnAction productTxnAction;
	@In
	GeneralSuggestion generalSuggestion;
    @In
    private SystemProperties systemProperties;
	
    private TekirShipmentNoteDetail selectedDetail;
    private Boolean showMiniTable = Boolean.TRUE;
    private Product selectedDiscountExpense;
    private Integer selectedIndex;

    private ContactAddress selectedAddress;

    private User authorizedUser;
    private boolean requestDiscountPermission = Boolean.FALSE;

    private List<TekirOrderNote> orderList = new ArrayList<TekirOrderNote>();
    
    //sayfa edit için açıldığında eski birimleri tutar.
    private Map<Product, Double> oldQuantities = new HashMap<Product, Double>();

    private List<String> zeroLineAmountWarnings = new ArrayList<String>();

    @Create 
    @Begin(join=true,flushMode = org.jboss.seam.annotations.FlushModeType.MANUAL)
    public void init() {
    }

    @Out(required = false)
    public TekirShipmentNote getSaleShipment() {
        return getEntity();
    }

    @In(required = false)
    public void setSaleShipment(TekirShipmentNote shipmentNote) {
        setEntity(shipmentNote);
    }

	public void calculateEverything() {
		try {
			calculateEverything(entity);
		} catch (Exception e) {
			log.error("İrsaliye hesaplamaları yapılırken hata meydana geldi. Sebebi :{0}", e);
			facesMessages.add(Severity.ERROR, "İrsaliye hesaplamaları yapılırken hata meydana geldi. Sebebi :{0}",e);
		}
	}

    @Override
    public void createNew() {
        log.debug("Yeni ShipmentNote");

        entity = new TekirShipmentNote();
        entity.setTradeAction(TradeAction.Sale);
        entity.setDocumentType(DocumentType.SaleShipmentNote);
        entity.setActive(true);
        entity.setSerial(sequenceManager.getNewSerialNumber(SequenceType.SERIAL_SHIPMENT_SALE));

        entity.setDate(calendarManager.getCurrentDate());
        entity.setTime(new java.util.Date());
        
        //FIXME:burada ne olması gerekiyor? Hata yı engellemek için 
        //ataması yapıldı.
        stockSuggestion.setProductType(ProductType.Product);
        stockSuggestion.setDisableProductCombo(true);

        //ekran açılır açılmaz yeni bir detay eklenmesini sağlıyoruz.
        createNewLine();
    }

    @Transactional
    @Override
    public String save() {
		log.info("Saving shipment. Shipment serial :{0}", entity.getSerial());
		String result = BaseConsts.FAIL;

        try {
			clearUnknownDetails();

        	makeEntityValidations();

        	calculateEverything();

			setParentOfLists();
			
			persistParents();

			copyDeliveryContactInfoFromContactCard();
			
        	result = super.save();

        	if (BaseConsts.SUCCESS.equals(result)) {
        		setClosedQuantitiesForOrderNotes();
        		
        		updateStatusOfOrderNotes();
        		
        		updateOldQuantities();
        		
        		productTxnAction.createProductTxnRecordsForShipment(entity);
        	}
        	entityManager.flush();
        } catch (Exception e) {
			facesMessages.add("Kaydedilirken hata meydana geldi. Sebebi :{0}",e.getMessage());
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
		entity.setDeliverySsn(contact.getSsn());
		entity.setDeliveryTaxNumber(contact.getTaxNumber());
		entity.setDeliveryTaxOffice(contact.getTaxOffice());
	}

	private void setClosedQuantitiesForOrderNotes() {
		for (TekirShipmentNoteDetail item : entity.getItems()) {
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

	private void updateStatusOfOrderNotes() {
		boolean totallyClosed = true;
		for (OrderShipmentLink ol : entity.getOrderLinks()) {
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

	private TekirOrderNoteDetail getOrderNoteDetail(Long aDetailId) {
		TekirOrderNote on = null;
		for (OrderShipmentLink ol : entity.getOrderLinks()) {
			on = ol.getOrderNote();
			
			for (TekirOrderNoteDetail det : on.getItems()) {
				if (det.getId().equals(aDetailId)) return det;
			}
		}
		return null;
	}

	private void persistParents() {
		TekirShipmentNoteDetail detail = null;
		for (int i=0;i<entity.getItems().size();i++) {
			detail = entity.getItems().get(i);
		
			if (detail.getChildList() != null && detail.getChildList().size() != 0) {
				if (detail.getId() == null) {
					entityManager.persist(detail);

					for (TekirShipmentNoteDetail child : detail.getChildList()) {
						child.setParent(detail);
					}
				}
			}
		}
	}

	private void setParentOfLists() {
		for(TekirShipmentNoteTaxSummary item : entity.getTaxSummaryList()) {
			item.setOwner(entity);
		}
		for(TekirShipmentNoteCurrencySummary item : entity.getCurrencySummaryList()) {
			item.setOwner(entity);
		}
		for(TekirShipmentNoteCurrencyRate item : entity.getCurrencyRateList()) {
			item.setOwner(entity);
		}
	}

    /**
	 * Tipi unknown olarak işaretlenmiş olan satırları listeden kaldırır.
	 */
	private void clearUnknownDetails() {
		log.info("Unknown satırları siliniyor.");
		TekirShipmentNoteDetail det = null;
		for (int i = entity.getItems().size() - 1; i >= 0 ; i--) {
			det = entity.getItems().get(i);
			if (det.getProductType().equals(ProductType.Unknown)) {
				entity.getItems().remove(i);
				log.info("Unknown tipli satır bulundu. Index:{0}", i);
			}
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

		TekirShipmentNoteDetail detail = null;
		for(int i=0;i<entity.getItems().size();i++) {
			detail = entity.getItems().get(i);

			if (detail.getProductType().equals(ProductType.Product) ||
					detail.getProductType().equals(ProductType.Service)) {

				if (detail.getParent() == null && detail.getProduct() == null) {
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

	            updateClerk(detail);
			}
		}
	}
	
	public boolean hasZeroLineAmountWarning() {
		return zeroLineAmountOption().equals(ControlType.Warning) ? true : false;
	}
	
	private ControlType zeroLineAmountOption() {
		return optionManager.getOption(LimitationOptionKey.SALESHIPMENT_ZERO_LINEAMOUNT, true).getAsEnum(ControlType.class);
	}

	private void updateClerk(TekirShipmentNoteDetail item) {
		if (entity.getClerk() != null) {
			item.setClerk(entity.getClerk());
		}
	}

	@Override
    public String delete() {
    	String result = BaseConsts.SUCCESS;
    	try {
    		productTxnAction.deleteProductTxnRecordsForShipmentNote(entity);

			for (int i=entity.getItems().size()-1;i>=0;i-- ) {
				deleteLine(i);
			}

			updateStatusOfOrderNotes();
			
    		result = super.delete();
		} catch (Exception e) {
			log.error("irsaliye silinirken hata oluştu. Sebebi :{0}", e.getMessage());
			facesMessages.add(Severity.ERROR, "İrsaliye silinirken hata oluştu. Sebebi :{0}",e);
		}
		return result;
    }

    public void createNewLine() {
        manualFlush();
        if (entity == null) {
            return;
        }

        log.debug("EntityManager.FlushMode : #0", entityManager.getFlushMode());

        TekirShipmentNoteDetail it = new TekirShipmentNoteDetail();
        it.setOwner(entity);
        entity.getItems().add(it);
        log.debug("yeni item eklendi");
    }

    public void deleteLine(ShipmentItem item) {
        manualFlush();
        if (entity == null) {
            return;
        }
        log.debug("Kayıt Silinecek : {0}", item);
        entity.getItems().remove(item);
    }

	@Override
	public void deleteLine(Integer ix) {
		log.info("Deleting line. Line number :{0}", ix.intValue());
		manualFlush();
	
		TekirShipmentNoteDetail item = getEntity().getItems().get(ix.intValue());

		if (item.getProductType().equals(ProductType.Product) || 
				item.getProductType().equals(ProductType.Service)) {
			for(int i=item.getChildList().size() - 1;i>=0;i--) {
				entity.getItems().remove(ix.intValue() + 1);
			}
			entity.getItems().remove(ix.intValue());

			if (item.getReferenceDocumentId() != null) {
				TekirOrderNote ton = null;
				for (OrderShipmentLink ol : entity.getOrderLinks()) {
					ton = ol.getOrderNote();
					
					for (TekirOrderNoteDetail ond : ton.getItems()) {
						if (item.getReferenceDocumentId().equals(ond.getId())) {
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

	@Override
	public void createDiscountLine(Integer ix) {
    	log.info("Creating discount line for index:{0}",ix);
    	manualFlush();
    	
    	selectedIndex = ix;
    	
    	selectedDetail = new TekirShipmentNoteDetail();
    	selectedDetail.setOwner(entity);
    	selectedDetail.setProductType(ProductType.Discount);
	}


    public void addDiscountToItem() {
        log.info("Adding discount to line...");
    	manualFlush();

    	TekirShipmentNoteDetail detail = new TekirShipmentNoteDetail();
    	detail.setProductType(ProductType.Discount);
    	DiscountOrExpense discount = null;
    	if (selectedDiscountExpense != null) {

    		discount = selectedDiscountExpense.getDiscountOrExpense();

    		detail.getDiscount().setPercentage(discount.getPercentage());
    		detail.getDiscount().setRate(discount.getRate());
    		detail.getDiscount().setValue(discount.getValue());

    		
    		TekirShipmentNoteDetail selectedItem = entity.getItems().get(selectedIndex);
    		
    		if (selectedItem != null) {
    			selectedItem.getChildList().add(detail);
    			detail.setParent(selectedItem);
    			
    			entity.getItems().add(selectedIndex + 1, detail);

    			detail.setOwner(entity);
    		}
    	}
    	
    }

	public void selectProductWithBarcode(Integer ix) {
		log.info("Product selected with barcode on line :{0}", ix.intValue());
		manualFlush();
		
		try {
			TekirShipmentNoteDetail detail = entity.getItems().get(ix);

			Product product = generalSuggestion.findProductWithBarcode( addZerosToBarcode(detail.getBarcode()) );
			
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

	private void prepareSelectedLine(int indexOfDetail, boolean refreshLine) throws Exception {
		TekirShipmentNoteDetail aDetail = entity.getItems().get(indexOfDetail);
		Product aProduct = aDetail.getProduct();
		
		if (aProduct == null) return;

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

		if (priceItemDetail.getHasDiscount()) {
			addDiscountLine(priceItemDetail, indexOfDetail);
		} 
		
	}

	/**
	 * Parent detaya fiyat listesi üzerinden gelen indirimi ekler.
	 * @param priceItemDetail
	 * @param parentDetail
	 */
	private void addDiscountLine(PriceItemDetail priceItemDetail,int indexOfDetail) {
		TekirShipmentNoteDetail parentDetail = entity.getItems().get(indexOfDetail);
		
		MoneySet discountPrice = priceItemDetail.getDiscountPrice();

		TekirShipmentNoteDetail discountDetail = new TekirShipmentNoteDetail();
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
	public void addDocumentDiscountToItems() {
    	log.info("Adding document discount to line...");
    	manualFlush();

    	UserDiscountRightHome userDiscountRight = (UserDiscountRightHome)Component.getInstance("userDiscountRight",true);
    	try {
			
	    	TekirShipmentNoteDetail detail = new TekirShipmentNoteDetail();
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

    public void toggleMiniTable() {
    	if (showMiniTable) {
    		showMiniTable = Boolean.FALSE;
    	} else {
    		showMiniTable = Boolean.TRUE;
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

    public void manualFlush() {
        Conversation.instance().changeFlushMode(org.jboss.seam.annotations.FlushModeType.MANUAL);
    }

	public void print() {
		try {

			log.info("Shipment Print");

			Map<Object,Object> params = new HashMap<Object,Object>();
			params.put("pShipmentId", entity.getId());

			NumberToText ntt = new NumberToTextTR();
			String totalAsText = ntt.convert(entity.getTotalAmount().getLocalAmount().doubleValue(), 
											 BaseConsts.SYSTEM_CURRENCY_CODE, 
											 BaseConsts.SYSTEM_CURRENCYDEC_CODE);

			params.put("pTotalAsText", totalAsText);

            String path =  systemProperties.getProperties().get("folder.templates") + "/" ;
            params.put("SUBREPORT_DIR", path );
			
			jasperReport.compileAndRunReportToPdf("Satis_Irsaliyesi(" + entity.getReference()+ ")", "satis_irsaliyesi", params);

		} catch (Exception ex) {
			log.error("İrsaliye yazdırılamadı. Sebebi #0", ex);
			facesMessages.add(Utils.getExceptionMessage(ex));
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

	@Override
	public void createDocumentDiscountLine() {
        log.info("Creating new document discount line...");
    	manualFlush();
    	
        selectedDetail = new TekirShipmentNoteDetail();
    	selectedDetail.setOwner(entity);
    	selectedDetail.setProductType(ProductType.DocumentDiscount);

    	authorizedUser = new User();
	}

    @SuppressWarnings("unchecked")
	public List<TekirOrderNote> findOrders() {
    	try {
	    	if (entity.getContact() != null) {
	    		
				List<TekirOrderNote> resultList = entityManager.createQuery("select sn from TekirOrderNote sn where " +
																				  "sn.tradeAction=:tradeAction and " + 
																				  "sn.containsService=false and " + 
																				  "(sn.status =:openStatus or sn.status =:processingStatus) and " + 
																				  "sn.contact=:contact ")
																				  .setParameter("contact", entity.getContact())
																				  .setParameter("tradeAction", TradeAction.Sale)
																				  .setParameter("openStatus", OrderStatus.Open)
																				  .setParameter("processingStatus", OrderStatus.Processing)
																				  .getResultList();
		
		        for (OrderShipmentLink ol : entity.getOrderLinks()) {
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

        TekirOrderNote on = orderList.get(ix);

        OrderShipmentLink ol = new OrderShipmentLink();
        ol.setShipmentNote(entity);
        ol.setOrderNote(on);
        
        entity.getOrderLinks().add(ol);

        entity.setClerk(on.getClerk());
        entity.setWarehouse(on.getWarehouse());
        
     // siparişin iş takip numarasını irsaliyeye setle.
        if (entity.getWorkBunch() == null || !entity.getWorkBunch().equals(on.getWorkBunch())){
        	entity.setWorkBunch(on.getWorkBunch());
        }
        
        orderList.remove(ix);

    	addOrderLines(on);
    	
    	calculateEverything();
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
		    			TekirShipmentNoteDetail detail = new TekirShipmentNoteDetail();
		    			
		    			moveOrderFieldsToShipment(item, detail);
		    			detail.setReferenceDocumentId(item.getId());
		    			detail.setReferenceDocumentType(tsn.getDocumentType());
		    			
		    			
		    			entity.getItems().add(detail);
		    			
		    			detail.setOwner(entity);
		    			for (TekirOrderNoteDetail childItem : item.getChildList()) {
		    				TekirShipmentNoteDetail childDetail = new TekirShipmentNoteDetail();
		    				moveOrderFieldsToShipment(childItem, childDetail);
		    				
		    				detail.getChildList().add(childDetail);
		    				childDetail.setParent(detail);
		    				
		    				childDetail.setOwner(entity);
		    				childDetail.setReferenceDocumentId(childItem.getId());
		    				childDetail.setReferenceDocumentType(tsn.getDocumentType());
		    				
		    				entity.getItems().add(childDetail);
		    			}
		    			
		    			if (item.getOrderDiscount().getValue().compareTo(BigDecimal.ZERO) != 0) {
		    				TekirShipmentNoteDetail childDetail = new TekirShipmentNoteDetail();
		    				childDetail.setProductType(ProductType.Discount);
		    				
		    				MoneySet orderDiscount = item.getOrderDiscount();
		    				
		    				DiscountOrExpense discount = new DiscountOrExpense();
		    				discount.setPercentage(false);
		    				discount.setCurrency(orderDiscount.getCurrency());
		    				

		    				BigDecimal quantityValue = BigDecimal.valueOf(item.getQuantity().getValue());
		    				discount.setLocalAmount(orderDiscount.getLocalAmount().divide(quantityValue, 2, RoundingMode.HALF_UP));
		    				discount.setValue(orderDiscount.getValue().divide(quantityValue, 2, RoundingMode.HALF_UP));

		    				
		    				discount.setLocalAmount(orderDiscount.getLocalAmount());
		    				discount.setValue(orderDiscount.getValue());
		    				
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
	
	private void moveOrderFieldsToShipment(TekirOrderNoteDetail fromDetail, TekirShipmentNoteDetail toDetail) {

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

	public void removeOrderNote(int ix) {
    	log.info("Removing order note. Index :{0}", ix);
    	manualFlush();
    	
        
    	TekirOrderNote sn = entity.getOrderLinks().get(ix).getOrderNote();

    	for (TekirOrderNoteDetail ondet : sn.getItems()) {

    		ondet.setClosedQuantity(0d);
    		ondet.setReferenceDocumentId(null);
    		ondet.setReferenceDocumentType(null);

    		TekirShipmentNoteDetail shipmentDetail = null;
    		for (int j= entity.getItems().size() -1;j>=0;j--) {
    			shipmentDetail = entity.getItems().get(j);
    			
    			if (shipmentDetail.getReferenceDocumentId() != null && 
    					shipmentDetail.getReferenceDocumentId().equals(ondet.getId())) {

    				entity.getItems().remove(shipmentDetail);
    			}
    			
    		}
    	}
    	
        entity.getOrderLinks().remove(ix);

        orderList.remove(sn);
	}

	@Override
	public void setId(Long id) {
        if (entity != null) {
            return;
        }

		super.setId(id);

		updateOldQuantities();
	}

	private void updateOldQuantities() {
		for (TekirShipmentNoteDetail item : entity.getProductItems()) {
			if (!oldQuantities.containsKey(item.getProduct())) {
				oldQuantities.put(item.getProduct(), item.getQuantity().getValue());
			} else {
				Double productQuantity = oldQuantities.get(item.getProduct());

				productQuantity = productQuantity + item.getQuantity().getValue();
			}
		}
	}

	@Override
	public void updateDetailCurrencies() {
		for (int i=0;i<entity.getItems().size();i++) {
			calculateUnitPrice(i);
			setTaxExcludedAmount(i);
		}
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

	public List<TekirOrderNote> getOrderList() {
		return orderList;
	}

	public void setOrderList(List<TekirOrderNote> orderList) {
		this.orderList = orderList;
	}

	public TekirShipmentNoteDetail getSelectedDetail() {
		return selectedDetail;
	}

	public void setSelectedDetail(TekirShipmentNoteDetail selectedDetail) {
		this.selectedDetail = selectedDetail;
	}

	public Product getSelectedDiscountExpense() {
		return selectedDiscountExpense;
	}

	public void setSelectedDiscountExpense(Product selectedDiscountExpense) {
		this.selectedDiscountExpense = selectedDiscountExpense;
	}

	public List<String> getZeroLineAmountWarnings() {
		return zeroLineAmountWarnings;
	}

}
