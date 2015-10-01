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

package com.ut.tekir.invoice;

import java.io.FileNotFoundException;
import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import net.sf.jasperreports.engine.JRException;

import org.hibernate.Criteria;
import org.hibernate.criterion.Restrictions;
import org.jboss.seam.ScopeType;
import org.jboss.seam.annotations.Begin;
import org.jboss.seam.annotations.Create;
import org.jboss.seam.annotations.In;
import org.jboss.seam.annotations.Out;
import org.jboss.seam.annotations.Scope;
import org.jboss.seam.core.Conversation;
import org.jboss.seam.persistence.HibernateSessionProxy;

import com.ut.tekir.entities.CurrencyPair;
import com.ut.tekir.entities.CurrencyRate;
import com.ut.tekir.entities.Invoice;
import com.ut.tekir.entities.InvoiceCurrencyRate;
import com.ut.tekir.entities.InvoiceCurrencySummary;
import com.ut.tekir.entities.InvoiceServiceItem;
import com.ut.tekir.entities.InvoiceTaxSummary;
import com.ut.tekir.entities.Money;
import com.ut.tekir.entities.MoneySet;
import com.ut.tekir.entities.ShipmentItem;
import com.ut.tekir.entities.ShipmentNote;
import com.ut.tekir.entities.Tax;
import com.ut.tekir.entities.TradeAction;
import com.ut.tekir.finance.AccountTxnAction;
import com.ut.tekir.finance.FinanceTxnAction;
import com.ut.tekir.framework.BaseConsts;
import com.ut.tekir.framework.CalendarManager;
import com.ut.tekir.framework.CurrencyManager;
import com.ut.tekir.framework.EntityBase;
import com.ut.tekir.framework.JasperHandlerBean;
import com.ut.tekir.framework.SequenceManager;
import com.ut.tekir.framework.SequenceType;
import com.ut.tekir.invoice.DiscountLineModel.LineType;
import com.ut.tekir.invoice.InvoiceItemModel.ItemType;
import com.ut.tekir.invoice.print.InvoicePrintModelBuilder;
import com.ut.tekir.stock.ProductTxnAction;

/**
 * TODO: Alış ile birleşik bir kod yazılsın. Bu hali ile çok saçma. Nerde DRY.
 * 
 * @author haky
 */
//@Stateful
//@Name("saleInvoiceHome")
@Scope(value = ScopeType.CONVERSATION)
public class SaleInvoiceHomeBean extends EntityBase<Invoice> implements
		SaleInvoiceHome<Invoice> {

	private List<InvoiceItemModel> items;
	private List<String> ccyList;
	private Map<String, InvoiceCurrencyRate> ccyRates = new HashMap<String, InvoiceCurrencyRate>();
	private Map<String, Money> ccyTotals = new HashMap<String, Money>();
	private Map<String, Money> localTotals = new HashMap<String, Money>();
	private Map<Tax, BigDecimal> taxRates;
	private List<ShipmentNote> shipmentNotes;
	private Boolean shipmentInvoice = false;
	private Integer invoiceType = 0;
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
	@In
	JasperHandlerBean jasperReport;

	@Create
	@Begin(join=true,flushMode = org.jboss.seam.annotations.FlushModeType.MANUAL)
	public void init() {

	}

	@Out(required = false)
	public Invoice getSaleInvoice() {
		return getEntity();
	}

	@In(required = false)
	public void setSaleInvoice(Invoice invoice) {
		setEntity(invoice);
	}

	@Override
	public void createNew() {
		log.debug("Yeni Invoice");
		log.debug("Invoice Type : #0", invoiceType);

		shipmentInvoice = invoiceType == 1;

		entity = new Invoice();
		entity.setAction(TradeAction.Sale);
		entity.setActive(true);
		entity.setClosed(false);
		entity.setSerial(sequenceManager
				.getNewSerialNumber(SequenceType.SERIAL_INVOICE_SALE));
		// entity.setReference(sequenceManager.getNewSerialNumber(SequenceType.REFERENCE_INVOICE_SALE));
		entity.setDate(calendarManager.getCurrentDate());
		entity.setShippingDate(calendarManager.getCurrentDate());
		entity.setTime(new java.util.Date());
		entity.setShipmentInvoice(shipmentInvoice);

		if (!shipmentInvoice) {
			ShipmentNote sn = new ShipmentNote();

			sn.setAction(TradeAction.Sale);
			sn.setActive(true);
			sn.setInvoice(entity);
			entity.getShipments().add(sn);
		}

		items = null;
	}

    //FIXME: item service ise serviceTxn e kayıt atılmalı. bu haliyle serviceTxn kaydı atılmıyor.
	@Override
	public String save() {

		// TODO: Hatalara dil desteği eklenecek
		Boolean hata = false;
		boolean shipmentEvent = false;

		try {
			if (items.size() == 0) {
				facesMessages.add("En az bir detay girmelisiniz!");
				hata = true;
			}

			// TODO: Metinlere dildesteği eklenecek
			for (InvoiceItemModel it : items) {

				Object o = it.getType() == ItemType.ServiceItem ? it
						.getService() : it.getProduct();

				if (o == null) {
					facesMessages.add("Stok - Hizmet seçmelisiniz!");
					hata = true;
				}

				if (it.getQuantity().getValue() <= 0) {
					facesMessages.add("Sıfırdan büyük bir miktar girmelisiniz");
					hata = true;
				}

				if (it.getAmount().getValue().compareTo(BigDecimal.ZERO) <= 0) {
					facesMessages.add("Sıfırdan büyük bir tutar girmelisiniz");
					hata = true;
				}
			}
			if (hata) {
				throw new RuntimeException("Hata!");
			}
		} catch (Exception e) {
			log.error("Hata :", e);
			return BaseConsts.FAIL;
		}

		recalculate();

		if (!entity.getShipmentInvoice()) {
			if (getShipmentNote().getItems().size() > 0) {
				getShipmentNote().setActive(entity.getActive());
				getShipmentNote().setContact(entity.getContact());
				getShipmentNote().setCode(entity.getCode());
				getShipmentNote().setDate(entity.getShippingDate());
				getShipmentNote().setInfo(entity.getInfo());
				getShipmentNote().setReference(entity.getReference());
				getShipmentNote().setSerial(entity.getSerial());
				getShipmentNote().setWarehouse(entity.getWarehouse());

				entityManager.persist(getShipmentNote());

				shipmentEvent = true;
			} else {
				ShipmentNote n = getShipmentNote();
				entity.getShipments().clear();
				entityManager.remove(n);
			}
		}

		String res = super.save();

		if (BaseConsts.SUCCESS.equals(res)) {
			if (!entity.getShipmentInvoice()) {
				// Otomatik irsaliye açılışı
				productTxnAction.saveShipmentNote(getShipmentNote());
			}
			financeTxnAction.saveInvoice(entity);

			events
					.raiseEvent("refreshBrowser:com.ut.tekir.entities.ShipmentNote");
		}

		return res;

	}

	@Override
	public String delete() {

		financeTxnAction.deleteInvoice(entity);

		if (!entity.getShipmentInvoice()) {
			ShipmentNote n = getShipmentNote();

			productTxnAction.deleteShipmentNote(n);

			entity.getShipments().clear();
			entityManager.remove(n);
		} else {

			for (ShipmentNote sn : entity.getShipments()) {
				sn.setInvoice(null);
				entityManager.merge(sn);
			}

			entity.getShipments().clear();
			entityManager.merge(entity);
			entityManager.flush();
		}

		return super.delete();
	}

	public void createNewServiceLine() {
		manualFlush();
		if (entity == null) {
			return;
		}

		recalculate();

		log.debug("EntityManager.FlushMode : #0", entityManager.getFlushMode());

		// GUI
		InvoiceItemModel item = new InvoiceItemModel(ItemType.ServiceItem);
		items.add(item);

		// Model
		item.getServiceItem().setInvoice(entity);
		item.getServiceItem().getAmount().setCurrency(
				BaseConsts.SYSTEM_CURRENCY_CODE);
		entity.getServiceItems().add(item.getServiceItem());

		log.debug("yeni service item eklendi");

	}

	public void createNewShipmentLine() {
		manualFlush();
		if (entity == null) {
			return;
		}

		recalculate();

		log.debug("EntityManager.FlushMode : #0", entityManager.getFlushMode());

		// GUI
		InvoiceItemModel item = new InvoiceItemModel(
				ItemType.InvoiceShipmentItem);
		items.add(item);

		// Model
		// Bu fatura türünde otomatik olarak açılan bir irsaliye var...
		getShipmentNote().getItems().add(item.getShipmentItem());
		item.getShipmentItem().setOwner(getShipmentNote());

		log.debug("yeni invoice shipment item eklendi");

	}

	public void createNewDiscountLine() {
		manualFlush();
		if (entity == null) {
			return;
		}

		log.debug("EntityManager.FlushMode : #0", entityManager.getFlushMode());

		InvoiceItemModel item = new InvoiceItemModel(ItemType.InvoiceDiscount);
		item.getDiscount().setType(LineType.Invoice);
		item.getDiscount().setDiscount(true);
		item.getDiscount().setRate(0d);

		items.add(item);

		log.debug("yeni invoice discount item eklendi");

	}

	public void createNewExpenseLine() {
		manualFlush();
		if (entity == null) {
			return;
		}

		log.debug("EntityManager.FlushMode : #0", entityManager.getFlushMode());

		InvoiceItemModel item = new InvoiceItemModel(ItemType.InvoiceDiscount);

		item.getDiscount().setType(LineType.Invoice);
		item.getDiscount().setDiscount(false);
		item.getDiscount().setRate(0d);

		items.add(item);

		log.debug("yeni invoice discount item eklendi");

	}

	public void createNewSubDiscountLine(Integer ix) {

		manualFlush();
		if (entity == null) {
			return;
		}
		log.debug("İşlenecek IX : {0}", ix);

		InvoiceItemModel item = items.get(ix);
		DiscountLineModel dis = new DiscountLineModel();

		dis.setDiscount(true);
		dis.setRate(0d);
		dis.setType(LineType.Line);

		item.getDiscounts().add(dis);

	}

	public void createNewSubExpenseLine(Integer ix) {

		manualFlush();
		if (entity == null) {
			return;
		}
		log.debug("İşlenecek IX : {0}", ix);

		InvoiceItemModel item = items.get(ix);
		DiscountLineModel dis = new DiscountLineModel();

		dis.setDiscount(false);
		dis.setRate(0d);
		dis.setType(LineType.Line);

		item.getDiscounts().add(dis);

	}

	public void deleteLine(InvoiceItemModel item) {
		manualFlush();
		if (entity == null) {
			return;
		}
		log.debug("Kayıt Silinecek : {0}", item);
		items.remove(item);
		// entity.getItems().remove(item);
	}

	public void deleteLine(Integer ix) {
		manualFlush();
		if (entity == null) {
			return;
		}
		log.debug("Kayıt Silinecek IX : {0}", ix);
		InvoiceItemModel it = items.remove(ix.intValue());

		log.debug("Silinen Kayıt : {0}", it);

		// Gerçek veri modelinden de siliniyor...
		if (it.getType() == ItemType.ServiceItem) {
			entity.getServiceItems().remove(it.getServiceItem());
		} else {
			getShipmentNote().getItems().remove(it.getShipmentItem());
		}

		recalculate();
		// Object o = entity.getItems().remove(ix.intValue());
		// log.debug("Silinen : {0}", o);
		// log.debug("Toplam Kayıt : {0}", entity.getItems().size());
	}

	public void selectLine(Integer ix) {
		manualFlush();
		if (entity == null) {
			return;
		}
		log.debug("İşlenecek IX : {0}", ix);

		InvoiceItemModel o = items.get(ix);
		if (o.getType() == ItemType.InvoiceDiscount) {
			return;
		}
		Object oo = o.getType() == ItemType.ServiceItem ? o.getService() : o
				.getProduct();
		if (oo != null) {
			o.getQuantity().setUnit(
					o.getType() == ItemType.ServiceItem ? o.getService()
							.getUnit() : o.getProduct().getUnit());
			if (o.getType() == ItemType.ServiceItem) {
				o
						.setTaxRate(o.getService().getSellTax() != null ? o
								.getService().getSellTax().getRates().get(0)
								.getRate() : BigDecimal.ZERO);
			} else {
				o.setTaxRate(o.getProduct().getSellTax() != null ? o
						.getProduct().getSellTax().getRates().get(0).getRate()
						: BigDecimal.ZERO);
			}
		}

	}

	protected InvoiceCurrencyRate findCurrencyRate(CurrencyPair cp) {
		for (InvoiceCurrencyRate icr : entity.getCurrencyRates()) {
			if (icr.getCurrencyPair().equals(cp)) {
				return icr;
			}
		}
		return null;
	}

	protected InvoiceTaxSummary findTaxSummary(Tax tax) {
		for (InvoiceTaxSummary itr : entity.getTaxSummaries()) {
			if (itr.getTax().equals(tax)) {
				return itr;
			}
		}
		return null;
	}

	protected InvoiceCurrencySummary findCurrencySummary(String ccy) {
		for (InvoiceCurrencySummary icr : entity.getCurrencySummaries()) {
			if (icr.getCurrency().equals(ccy)) {
				return icr;
			}
		}
		return null;
	}

	protected void calculateCcy(InvoiceItemModel it) {
		String ccy = it.getAmount().getCurrency();
		InvoiceCurrencyRate icr = null;

		// Kolaylık olsun döviz listesi hazırlanıyor
		if (!ccyList.contains(ccy)) {
			ccyList.add(ccy);
		}

		// Kurlar toplanıyor
		if (!ccy.equals(BaseConsts.SYSTEM_CURRENCY_CODE)) {
			
			it.getUnitPrice().setCurrency(ccy);
			it.getTaxExcludedTotalAmount().setCurrency(ccy);
			
			// Eğer lokal değer değilse unit price'ın dövizini setle.
			// Bu kod çalışmazsa lokal deger set edilmiş olarak kalıyor.
			

			CurrencyPair cp = currencyManager.getCurrencyPair(ccy,
					BaseConsts.SYSTEM_CURRENCY_CODE);

			icr = findCurrencyRate(cp);
			if (icr == null) {

				CurrencyRate cr = currencyManager.getCurrencyRate(entity
						.getDate(), cp);
				icr = new InvoiceCurrencyRate();
				icr.setCurrencyPair(cp);
				icr.setInvoice(entity);
				icr.setAsk(cr != null ? cr.getAsk() : BigDecimal.ZERO);
				icr.setAsk(cr != null ? cr.getBid() : BigDecimal.ZERO);

				entity.getCurrencyRates().add(icr);

				ccyRates.put(ccy, icr);
			}

		}

		
		// Döviz toplamları alınıyor
		InvoiceCurrencySummary ics = findCurrencySummary(ccy);

		if (ics == null) {
			ics = new InvoiceCurrencySummary();
			ics.setCurrency(ccy);
			ics.setInvoice(entity);
			ics.getAmount().setCurrency(ccy);
			ics.getAmount().setValue(BigDecimal.ZERO);
			ics.getAmount().setLocalAmount(BigDecimal.ZERO);
			ics.setCurrency(ccy);
			entity.getCurrencySummaries().add(ics);
		}

				
		Boolean taxIncluded = it.getType() == ItemType.ServiceItem ? it
				.getService().getTaxIncluded() : it.getProduct()
				.getTaxIncluded();


		// FIXME: Burda aslında currencyManager üzerinde doğru bir fonksiyon
		// olacak ve o kullanılacak...
		// FIXME: MoneySet Karışıklığı Düeltile

		if (taxIncluded) {
			ics.getAmount().setValue( ics.getAmount().getValue().add(it.getAmount().getValue()) );
		} else {
			ics.getAmount().setValue( ics.getAmount().getValue().add(it.getAmount().getValue())
					.add(it.getTaxAmount().getValue())
					);
		}				
				
		if (ccy.equals(BaseConsts.SYSTEM_CURRENCY_CODE)) {
			it.getAmount().setLocalAmount(it.getAmount().getValue());
			it.getUnitPrice().setLocalAmount(it.getUnitPrice().getValue());
			it.getTaxExcludedTotalAmount().setLocalAmount(it.getTaxExcludedTotalAmount().getValue());
			ics.getAmount().setLocalAmount(ics.getAmount().getValue());			
		} else {
			it.getAmount().setLocalAmount(it.getAmount().getValue().multiply(icr.getAsk()));
			it.getUnitPrice().setLocalAmount(it.getUnitPrice().getValue().multiply(icr.getAsk()));
			it.getTaxExcludedTotalAmount().setLocalAmount(it.getTaxExcludedTotalAmount().getValue().multiply(icr.getAsk()));
			ics.getAmount().setLocalAmount(ics.getAmount().getValue().multiply(icr.getAsk()));
		} 

	}
	
	protected void calculateTaxes(InvoiceItemModel it) {
		// Tax Hesaplanıyor
		Tax tax = it.getType() == ItemType.ServiceItem ? 
				it.getService().getSellTax() : it.getProduct().getSellTax();
		Boolean taxIncluded = it.getType() == ItemType.ServiceItem ? 
				it.getService().getTaxIncluded() : it.getProduct().getTaxIncluded();

		log.debug("Tax Satır #0 #1; TaxIncluede #2", it.getAmount(), it.getTaxRate(), taxIncluded);
		
		if (it.getTaxRate().compareTo(BigDecimal.ZERO) == 0 && tax != null) {
			it.setTaxRate(tax.getRates().get(0).getRate());
		}
		
		if (taxIncluded) {
			BigDecimal divisior = BigDecimal.ONE.add( 
					it.getTaxRate().divide(BigDecimal.valueOf(100))
					);
			// Verginin local degeri
			BigDecimal subtrahend = new BigDecimal(it.getAmount().getLocalAmount().doubleValue() / divisior.doubleValue());
			BigDecimal taxLocalAmount = it.getAmount().getLocalAmount().subtract(subtrahend);
			it.getTaxAmount().setLocalAmount(taxLocalAmount);
			// Verginin degeri
			BigDecimal taxAmount = it.getAmount().getValue().subtract(
					new BigDecimal(it.getAmount().getValue().doubleValue() / divisior.doubleValue())
					);
			it.getTaxAmount().setValue(taxAmount);
		} else {
			BigDecimal taxLocalAmount = (it.getAmount().getLocalAmount().multiply(it.getTaxRate())).divide(BigDecimal.valueOf(100));
			it.getTaxAmount().setLocalAmount(taxLocalAmount);
			BigDecimal taxAmount = (it.getAmount().getValue().multiply(it.getTaxRate())).divide(BigDecimal.valueOf(100));
			it.getTaxAmount().setValue(taxAmount);
		}

		if (taxIncluded) {
			it.getTaxExcludedTotalAmount().setValue(
					it.getAmount().getValue().subtract(it.getTaxAmount().getValue())
					);
		} else {
			it.getTaxExcludedTotalAmount().setValue(
					it.getAmount().getValue()
					);
		}
		
	}
	
	protected void fillTaxSummary(InvoiceItemModel it) {

		Tax taxVar = it.getType() == ItemType.ServiceItem ? it.getService()
				.getSellTax() : it.getProduct().getSellTax();
				// TODO: TaxSummary ayrıca yapılmalı...
		if (taxVar != null) {

			InvoiceTaxSummary its = findTaxSummary(taxVar);

			if (its == null) {

				its = new InvoiceTaxSummary();
				its.setInvoice(entity);
				its.setTax(taxVar);
				its.setAmount(new MoneySet());
				its.setRate(it.getTaxRate());
				entity.getTaxSummaries().add(its);

			}

			// Döviz toplamları yerel birim üzerinde olacak...
			its.getAmount().setValue( its.getAmount().getValue().add(it.getTaxAmount().getLocalAmount()) );
			its.getAmount().setCurrency(BaseConsts.SYSTEM_CURRENCY_CODE);
			
			log.debug("Tax : #0 #1 #2", its, its.getAmount(), it.getTaxAmount());
		}
	}
	
	protected void calculateTotals(InvoiceItemModel it) {
		it.setTotalAmaount(new MoneySet());
		
		Boolean taxIncluded = it.getType() == ItemType.ServiceItem ? 
				it.getService().getTaxIncluded() : it.getProduct().getTaxIncluded();
				
		if (taxIncluded) {
			it.getTotalAmaount().setValue(it.getTotalAmaount().getValue().add(it.getAmount().getLocalAmount()));
		} else {
			it.getTotalAmaount().setValue(it.getTotalAmaount().getValue().add(it.getTaxAmount().getLocalAmount()));
			it.getTotalAmaount().setValue(it.getTotalAmaount().getValue().add(it.getAmount().getLocalAmount()));
		}
		
		if (taxIncluded) {
			entity.getBeforeTax().setValue(entity.getBeforeTax().getValue().add(it.getAmount().getLocalAmount()));
			entity.getBeforeTax().setValue(entity.getBeforeTax().getValue().subtract(it.getTaxAmount().getLocalAmount()));
			
			entity.getTotalTax().setValue(entity.getTotalTax().getValue().add(it.getTaxAmount().getLocalAmount()));
			
			entity.getInvoiceTotal().setValue(entity.getInvoiceTotal().getValue().add(it.getTotalAmaount().getValue()));
			
		} else {
			
			entity.getBeforeTax().setValue(entity.getBeforeTax().getValue().add(it.getAmount().getLocalAmount()));
			
			entity.getTotalTax().setValue(entity.getTotalTax().getValue().add(it.getTaxAmount().getLocalAmount()));
			
			entity.getInvoiceTotal().setValue(entity.getInvoiceTotal().getValue().add(it.getTotalAmaount().getValue()));
		}
		
		entity.getTotalTax().setCurrency(BaseConsts.SYSTEM_CURRENCY_CODE);
		entity.getBeforeTax().setCurrency(BaseConsts.SYSTEM_CURRENCY_CODE);
		entity.getInvoiceTotal().setCurrency(BaseConsts.SYSTEM_CURRENCY_CODE);
	}


	/**
	 * Fatura satırlarını tek tek dolaşarak tüm fatura toplamlarını yeniden
	 * hesaplar...
	 */
	public void recalculate() {

		ccyList = new ArrayList<String>();
		ccyTotals = new HashMap<String, Money>();
		localTotals = new HashMap<String, Money>();
		ccyRates.clear();
		entity.getTaxSummaries().clear();
		entity.getCurrencySummaries().clear();
		entity.setBeforeTax(new Money());
		entity.setTotalTax(new Money());
		entity.setInvoiceTotal(new Money());

		// Kur ve Döviz toplamları hesaplanıyor
		for (InvoiceItemModel it : items) {

			Object o = it.getType() == ItemType.ServiceItem ? it.getService() : it.getProduct();
			if (o != null) {
				// Eer null ise satn hemen hereyi bo demektir...
				
				calculateCcy(it); // Local değerleri hesaplanıyor
				calculateTaxes(it); // Vergiler hesaplanıyor
				fillTaxSummary(it); // Vergiler dolduruluyor
				calculateTotals(it); // Toplamları alınıyor
			}

		}

	}
	
	public Double calculateAmountValue(){
		Double amountValue = 0.0;
		
		for (int i = 0; i < items.size(); i++) {
			
			amountValue +=items.get(i).getAmount().getValue().doubleValue();
			
			
		}
		return amountValue;
	}

	public void manualFlush() {
		Conversation.instance().changeFlushMode(
				org.jboss.seam.annotations.FlushModeType.MANUAL);
	}

	public void buildItems() {
		items = new ArrayList<InvoiceItemModel>();

		for (ShipmentNote sn : entity.getShipments()) {
			for (ShipmentItem sh : sn.getItems()) {
				items.add(new InvoiceItemModel(sh));
			}
		}

		for (InvoiceServiceItem si : entity.getServiceItems()) {
			items.add(new InvoiceItemModel(si));
		}
	}

	public List<InvoiceItemModel> getItems() {

		if (items == null) {
			buildItems();
		}

		return items;
	}

	public void setItems(List<InvoiceItemModel> items) {
		this.items = items;
	}

	public Map<String, InvoiceCurrencyRate> getCcyRates() {
		return ccyRates;
	}

	public void setCcyRates(Map<String, InvoiceCurrencyRate> ccyRates) {
		this.ccyRates = ccyRates;
	}

	public Map<String, Money> getCcyTotals() {
		return ccyTotals;
	}

	public void setCcyTotals(Map<String, Money> ccyTotals) {
		this.ccyTotals = ccyTotals;
	}

	public List<String> getCcyList() {
		return ccyList;
	}

	public void setCcyList(List<String> ccyList) {
		this.ccyList = ccyList;
	}

	public Map<String, Money> getLocalTotals() {
		return localTotals;
	}

	public void setLocalTotals(Map<String, Money> localTotals) {
		this.localTotals = localTotals;
	}

	public ShipmentNote getShipmentNote() {
		if (entity.getShipments().size() == 0) {
			ShipmentNote sn = new ShipmentNote();

			sn.setAction(TradeAction.Purchase);
			sn.setActive(true);
			sn.setInvoice(entity);
			entity.getShipments().add(sn);

		}
		return entity.getShipments().get(0);
	}

	// ʩrsaliye seçim ileri...
	public void refreshShipmentLines() {
		buildItems();
		recalculate();
	}

	@SuppressWarnings("unchecked")
	public void buildShipmentNotes() {

		log.debug("Sorgu yapcez...");
		log.debug("Action : #0, Contact : #1 #2", TradeAction.Sale, entity
				.getContact(), entity.getContact() != null ? entity
				.getContact().getId() : "null");

		// DetachedCriteria crit = DetachedCriteria.forClass( ShipmentNote.class
		// );
		HibernateSessionProxy session = (HibernateSessionProxy) getEntityManager()
				.getDelegate();
		Criteria crit = session.createCriteria(ShipmentNote.class);

		crit.add(Restrictions.eq("action", TradeAction.Sale));
		crit.add(Restrictions.eq("contact", entity.getContact()));
		crit.add(Restrictions.isNull("invoice"));

		shipmentNotes = crit.list();

		log.debug("İrsaliyeler #0", shipmentNotes);

		// Hali hazırda faturaya bağlı olanlar listeden çıkartılıyorlar...
		for (ShipmentNote sn : entity.getShipments()) {
			if (shipmentNotes.contains(sn)) {
				shipmentNotes.remove(sn);
			}
		}

	}

	public void selectShipmentNote(int ix) {
		// TODO: Burdaki seçim için kullnılan index değeri kontrol edilmeli.
		// rowKey sadece aktif sayfanın indexini getiriyor olabilir...
		ShipmentNote sn = shipmentNotes.get(ix);

		sn.setInvoice(entity);
		entity.getShipments().add(sn);

		shipmentNotes.remove(sn);

		refreshShipmentLines();
	}

	public void removeShipmentNote(int ix) {
		// TODO: Burda irsaliyeyi veri tabanından gerçekten silmeye kalmaz
		// umarım...
		ShipmentNote sn = entity.getShipments().get(ix);

		sn.setInvoice(null);
		entity.getShipments().remove(sn);

		shipmentNotes.add(sn);

		refreshShipmentLines();
	}

	public List<ShipmentNote> getShipmentNotes() {
		if (shipmentNotes == null) {
			buildShipmentNotes();
		}
		return shipmentNotes;
	}

	public void setShipmentNotes(List<ShipmentNote> shipmentNotes) {
		this.shipmentNotes = shipmentNotes;
	}

	public void beginShipmentInvoice() {
		log.debug("Shipment Faturası Kesecez!");
		shipmentInvoice = true;
	}

	public Integer getInvoiceType() {
		return invoiceType;
	}

	public void setInvoiceType(Integer invoiceType) {
		this.invoiceType = invoiceType;
	}

	@SuppressWarnings("unchecked")
	public void print() {
		try {

			//TODO: fatura tutarını yazı olarak parametreye geçmeli.
			
			log.info("Invoice Print");

			InvoicePrintModelBuilder builder = new InvoicePrintModelBuilder();

			builder.begin(entity);

			for (InvoiceItemModel lm : getItems()) {
				builder.addLine(lm);
			}

			Map params = new HashMap();
			params.put("invoice", entity.getId());
			
			if (!entity.getShipmentInvoice()) {
				jasperReport.printObjectToPDF( "irsaliyeli_satis_faturasi_" + entity.getReference(), "irsaliyeli_satis_faturasi", params, builder
					.getModel());
			} else {
				jasperReport.printObjectToPDF( "satis_faturasi_" + entity.getReference(), "satis_faturasi", params, builder
						.getModel());
			}

		} catch (JRException ex) {
			log.error("Invoice print error", ex);
		} catch (FileNotFoundException e) {
			log.error("Invoice template not found", e);
			facesMessages.add("Fatura şablonu bulunamadı!");
		}
	}
}
