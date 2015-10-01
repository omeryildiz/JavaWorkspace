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
import com.ut.tekir.entities.DocumentType;
import com.ut.tekir.entities.Product;
import com.ut.tekir.entities.ProductType;
import com.ut.tekir.entities.ShipmentItem;
import com.ut.tekir.entities.TradeAction;
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
import com.ut.tekir.general.GeneralSuggestion;
import com.ut.tekir.options.LimitationOptionKey;
import com.ut.tekir.stock.ProductTxnAction;
import com.ut.tekir.stock.StockSuggestion;
import com.ut.tekir.tender.TenderCalculationHomeBean;
import com.ut.tekir.util.NumberToText;
import com.ut.tekir.util.NumberToTextTR;
import com.ut.tekir.util.Utils;

/**
 * Yeni irsaliye yapısına uygun home bileşenimiz.
 * @author sinan.yumak
 */
@Stateful
@Name("purchaseShipmentHome")
@Scope(value = ScopeType.CONVERSATION)
public class PurchaseShipmentHomeBean extends TenderCalculationHomeBean<TekirShipmentNote> implements PurchaseShipmentHome<TekirShipmentNote> {

	@In
	OptionManager optionManager;
    @In
    Map<Object, String> messages;
	@In
	JasperHandlerBean jasperReport;
    @In(create = true)
    SequenceManager sequenceManager;
    @In
    CalendarManager calendarManager;
    @In
    ProductTxnAction productTxnAction;
    @In(create=true)
    StockSuggestion stockSuggestion;
    @In
    GeneralSuggestion generalSuggestion;
    @In
    private SystemProperties systemProperties;

    private ContactAddress selectedAddress;

    private List<String> zeroLineAmountWarnings = new ArrayList<String>();

    @Create 
    @Begin(join=true,flushMode = org.jboss.seam.annotations.FlushModeType.MANUAL)
    public void init() {

    }

    @Out(required = false)
    public TekirShipmentNote getPurchaseShipment() {
        return getEntity();
    }

    @In(required = false)
    public void setPurchaseShipment(TekirShipmentNote shipmentNote) {
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
        entity.setTradeAction(TradeAction.Purchase);
		entity.setDocumentType(DocumentType.PurchaseShipmentNote);
        entity.setActive(true);
        entity.setSerial(sequenceManager.getNewSerialNumber(SequenceType.SERIAL_SHIPMENT_PURCHASE));

        entity.setDate(calendarManager.getCurrentDate());
        entity.setTime(new java.util.Date());

        //FIXME:burada ne olması gerekiyor? Hata yı engellemek için 
        //ataması yapıldı.
        stockSuggestion.setProductType(ProductType.Product);
        stockSuggestion.setDisableProductCombo(true);
        
        //ekran açılır açılmaz yeni bir detay eklenmesini sağlıyoruz.
        createNewLine();
    }

    @Override
    @Transactional
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
        	
        	if (result.equals(BaseConsts.SUCCESS)) {
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
		entity.setDeliverySsn(contact.getSsn());
		entity.setDeliveryFullname(contact.getFullname());
		entity.setDeliveryTaxNumber(contact.getTaxNumber());
		entity.setDeliveryTaxOffice(contact.getTaxOffice());
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
			}
		}
	}
	
	public boolean hasZeroLineAmountWarning() {
		return zeroLineAmountOption().equals(ControlType.Warning) ? true : false;
	}
	
	private ControlType zeroLineAmountOption() {
		return optionManager.getOption(LimitationOptionKey.PURCHASESHIPMENT_ZERO_LINEAMOUNT, true).getAsEnum(ControlType.class);
	}

	@Override
    @Transactional
    public String delete() {
    	String result = BaseConsts.SUCCESS;
    	try {
    		productTxnAction.deleteProductTxnRecordsForShipmentNote(entity);

    		result = super.delete();
		} catch (Exception e) {
			log.error("irsaliye silinirken hata oluştu. Sebebi :{0}", e.getMessage());
			facesMessages.add(Severity.ERROR, "İrsaliye silinirken hata oluştu. Sebebi :{0}",e);
		}
		return result;
    }

	public void selectProductWithBarcode(Integer ix) {
		log.info("Product selected with barcode on line :{0}", ix.intValue());
		manualFlush();
		
		try {
			TekirShipmentNoteDetail detail = entity.getItems().get(ix);

			Product product = generalSuggestion.findProductWithBarcode(detail.getBarcode());
			
			if (product != null) {
				detail.setProduct(product);
				detail.setProductType(ProductType.Product);
				detail.getQuantity().setValue(1.0);
				detail.getQuantity().setUnit(product.getUnit());

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

    public void selectProduct(Integer ix) {
        manualFlush();
        if (entity == null) {
            return;
        }
        log.debug("İşlenecek IX : {0}", ix);

        TekirShipmentNoteDetail o = entity.getItems().get(ix.intValue());
        if (o != null && o.getProduct() != null) {
            o.getQuantity().setUnit(o.getProduct().getUnit());
            o.getQuantity().setValue(1.0);
            o.setProductType(ProductType.Product);
        }
    }

    public void manualFlush() {
        Conversation.instance().changeFlushMode(org.jboss.seam.annotations.FlushModeType.MANUAL);
    }

    public void setAddress() {
		log.info("Updating address list. Contact code :{0}",entity.getContact().getCaption());
		if (selectedAddress != null) {
			entity.setDeliveryAddress(selectedAddress.getAddress());
		} else {
			entity.setDeliveryAddress(null);
		}
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
			
			jasperReport.compileAndRunReportToPdf("Alis_Irsaliyesi(" + entity.getReference()+ ")", "alis_irsaliyesi", params);

		} catch (Exception ex) {
			log.error("İrsaliye yazdırılamadı. Sebebi #0", ex);
			facesMessages.add(Utils.getExceptionMessage(ex));
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

	@Override
	public List<String> getZeroLineAmountWarnings() {
		return zeroLineAmountWarnings;
	}

}
