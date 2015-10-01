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

package com.ut.tekir.docmatch.mapper;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import javax.ejb.Remove;
import javax.ejb.Stateful;
import javax.ejb.TransactionAttribute;
import javax.ejb.TransactionAttributeType;
import javax.persistence.EntityManager;

import org.jboss.seam.ScopeType;
import org.jboss.seam.annotations.Destroy;
import org.jboss.seam.annotations.In;
import org.jboss.seam.annotations.Logger;
import org.jboss.seam.annotations.Name;
import org.jboss.seam.annotations.Scope;
import org.jboss.seam.faces.FacesMessages;
import org.jboss.seam.log.Log;

import com.ut.tekir.configuration.SystemConfiguration;
import com.ut.tekir.docmatch.DocumentMatchModel;
import com.ut.tekir.docmatch.DocumentMatchProvider;
import com.ut.tekir.docmatch.MatchProviderRegistry;
import com.ut.tekir.docmatch.PaymentCurrencyHelper;
import com.ut.tekir.entities.DocumentMatch;
import com.ut.tekir.entities.DocumentType;
import com.ut.tekir.entities.FinanceAction;
import com.ut.tekir.entities.MoneySet;
import com.ut.tekir.entities.Payment;
import com.ut.tekir.entities.PaymentItem;
import com.ut.tekir.entities.TradeAction;
import com.ut.tekir.entities.inv.TekirInvoice;
import com.ut.tekir.finance.InvoiceSuggestion;
import com.ut.tekir.finance.PaymentSuggestion;
import com.ut.tekir.finance.PaymentSuggestionFilterModel;
import com.ut.tekir.framework.CurrencyManager;
import com.ut.tekir.invoice.yeni.InvoiceSuggestionFilterModel;

/**
 * Açık kalan faturalar ile açık kalan tahsilatları kapatan sınıftır.
 * 
 * @author sinan.yumak
 *
 */
@Stateful
@Name("invoicePaymentMatcher")
@Scope(ScopeType.CONVERSATION)
public class InvoicePaymentMapperHome implements InvoicePaymentMapper {
	private static final long serialVersionUID = 1L;

	@Logger
	private Log log;
	@In
	EntityManager entityManager;
	@In
	InvoiceSuggestion invoiceSuggestion;
	@In
	PaymentSuggestion paymentSuggestion;
	@In
	CurrencyManager currencyManager;
	@In
	FacesMessages facesMessages;
	
	private DocumentMatchProvider invMatchProvider;
	private DocumentMatchProvider paymentMatchProvider;
	private PaymentCurrencyHelper paymentCurrencyHelper;
	
	private MapperModel model;
	
	private List<TekirInvoice> findInvoices() {
		return invoiceSuggestion.suggestInvoice( getInvoiceFilterModel() );
	}

	private List<Payment> findPayments() {
		return paymentSuggestion.suggestPayment( getPaymentFilterModel() );
	}
	
	private InvoiceSuggestionFilterModel getInvoiceFilterModel() {
		InvoiceSuggestionFilterModel fm = new InvoiceSuggestionFilterModel();
		fm.setBeginDate(getModel().getBeginDate());
		fm.setEndDate(getModel().getEndDate());
		fm.setContact(getModel().getContact());
		if (FinanceAction.Credit.equals(getModel().getAction())) {
			fm.setTradeAction(TradeAction.Sale);
		} else {
			fm.setTradeAction(TradeAction.Purchase);
		}
		fm.setMatchingFinished(false);
		return fm;
	}

	private PaymentSuggestionFilterModel getPaymentFilterModel() {
		PaymentSuggestionFilterModel fm = new PaymentSuggestionFilterModel();
		fm.setBeginDate(getModel().getBeginDate());
		fm.setEndDate(getModel().getEndDate());
		fm.setContact(getModel().getContact());
		fm.setAction(getModel().getAction());
		fm.setMatchingFinished(false);
		return fm;
	}
	
	public void initialize() {
		getModel().clear();
		for (TekirInvoice inv : findInvoices()) {
			getModel().add(new MapperInvoiceModel(inv, getInvoiceMatchProvider().findUsedAmountInMatchesBefore(inv)));
		}
		log.debug("Size of invoices: #0", getModel().getInvoices().size());

		for (Payment py : findPayments()) {
			getModel().add( createMapperPaymentModel(py) );
		}
		log.info("Size of payments: #0", getModel().getPayments().size());
	}

	private MapperPaymentModel createMapperPaymentModel(Payment py) {
		MapperPaymentModel mpm = new MapperPaymentModel(py);
		
		for (PaymentItem pi : py.getItems()) {
			MapperPaymentItemModel item = new MapperPaymentItemModel(pi, usedAmountInMatchesBefore(pi));

			mpm.getItems().add(item);
		}
		return mpm;
	}

	private BigDecimal usedAmountInMatchesBefore(PaymentItem pi) {
		return getMatchLocaleAmtTotal(getPaymentMatchProvider().findMatches(pi));
	}
	
	private BigDecimal getMatchLocaleAmtTotal(List<DocumentMatch> matches) {
		BigDecimal total = BigDecimal.ZERO;
		for (DocumentMatch dm : matches) {
			total = total.add(dm.getAmount().getLocalAmount());
		}
		return total;
	}
	
	@TransactionAttribute(TransactionAttributeType.REQUIRES_NEW)
	public void mapSelected() {
		log.info("Mapping selected invoices to payments...");
		List<DocumentMatchModel> matchModels = new ArrayList<DocumentMatchModel>();
		for (MapperInvoiceModel mim : getModel().getInvoices()) {
			if (!mim.isSelected()) continue;
			if (mim.hasRemainingAmount()) {
				List<DocumentMatchModel> createdMatches = createMatchesFor(mim);
				log.info("Appending #0 matches for invoice: #1", createdMatches.size(), mim.getInvoice().getSerial());
				addToUserMessages(mim, createdMatches);
				matchModels.addAll(createdMatches);
			}
		}
		getPaymentMatchProvider().save(matchModels);
		
		initialize();
	}

	private List<DocumentMatchModel> createMatchesFor(MapperInvoiceModel mim) {
		List<DocumentMatchModel> matchModels = new ArrayList<DocumentMatchModel>();
		
		for (MapperPaymentModel mpm : getModel().getPayments()) {
			if (!mpm.hasRemainingAmount() || !mim.getContact().equals(mpm.getContact())) continue;

			for (MapperPaymentItemModel pim : mpm.getItems()) {
				if (!pim.isMatchingFinished()) {
					matchModels.add( createMatchModel(mim, pim) );
					
					setRemainingAmounts(mim, pim);

					if (!mim.hasRemainingAmount()) {
						setMatchingFinishedFlag(mim);
						return matchModels;
					}
					if (!mpm.hasRemainingAmount()) break;
				}
			}
		}
		return matchModels;
	}

	private void setMatchingFinishedFlag(MapperInvoiceModel mim) {
		mim.getInvoice().setMatchingFinished(true);
		entityManager.merge(mim.getInvoice());
		entityManager.flush();
	}

	private void setRemainingAmounts(MapperInvoiceModel mim, MapperPaymentItemModel pim) {
		BigDecimal matchAmount = getMatchAmount(mim, pim);
		mim.setRemainingAmount(mim.getRemainingAmount().subtract( matchAmount ));

		BigDecimal convertedToPaymentAmount = getPaymentCurrencyHelper(pim.getPaymentItem().getOwner()).convertToLocale(matchAmount, mim.getInvoice().getDocCurrency());
		pim.getRemainingAmount().setLocalAmount(pim.getRemainingAmount().getLocalAmount().subtract( convertedToPaymentAmount ));
	}
	
	private BigDecimal getMatchAmount(MapperInvoiceModel mim, MapperPaymentItemModel pim) {
		BigDecimal convertedPaymentAmt = getPaymentCurrencyHelper(pim.getPaymentItem().getOwner()).convertToCurrency(pim.getRemainingAmount().getLocalAmount(), 
																													 SystemConfiguration.CURRENCY_CODE, 
																													 mim.getInvoice().getDocCurrency());
		
		if (mim.getRemainingAmount().compareTo( convertedPaymentAmt ) >= 0) {
			return convertedPaymentAmt;
		} else if (mim.getRemainingAmount().compareTo( convertedPaymentAmt ) == -1){
			return mim.getRemainingAmount();
		}
		return BigDecimal.ZERO;
	}
	
	private DocumentMatchModel createMatchModel(MapperInvoiceModel mim, MapperPaymentItemModel pim) {
		log.info("Creating match for invoice: #0", mim.getInvoice().getSerial());
		DocumentMatchModel matchModel = new DocumentMatchModel(pim.getPaymentItem());
		matchModel.setMatches(getPaymentMatchProvider().findMatches(pim.getPaymentItem()));
		
		DocumentMatch match = new DocumentMatch();
		match.setDocumentSerial(pim.getPaymentItem().getOwner().getSerial());
		match.setDocumentType(pim.getPaymentItem().getOwner().getDocumentType());
		match.setDocumentId(pim.getPaymentItem().getOwner().getId());
		match.setMatchedDocumentSerial(mim.getInvoice().getSerial());
		match.setMatchedDocumentId(mim.getInvoice().getId());
		match.setMatchedDocumentType(mim.getInvoice().getDocumentType());
		match.setMatchDate(new Date());
		
		MoneySet amount = new MoneySet(mim.getInvoice().getDocCurrency());
		amount.setValue(getMatchAmount(mim, pim));
		getPaymentCurrencyHelper(pim.getPaymentItem().getOwner()).setLocalAmountOf(amount);
		match.setAmount(amount);
		
		matchModel.getMatches().add(match);
		log.info("Created match for invoice #0. Match amount: #1, serial: #2, id: #3", mim.getInvoice().getSerial(), match.getAmount(),
																					   match.getDocumentSerial(), match.getDocumentId());
		return matchModel;
	}

	public MapperModel getModel() {
		if (model == null) {
			model = new MapperModel();
		}
		return model;
	}

	public void setModel(MapperModel model) {
		this.model = model;
	}

	private void addToUserMessages(MapperInvoiceModel mim, List<DocumentMatchModel> models) {
		//TODO: mesajları properties dosyasından almalı.
		if (models.size() == 0) return;
		facesMessages.add("#0 no lu fatura için;",mim.getInvoice().getSerial());
		for (DocumentMatchModel model : models) {
			DocumentMatch dm = model.getMatches().get(0);
			facesMessages.add("#0 no lu, #1 tutarlı tahsilat eşlemesi ", dm.getDocumentSerial(), dm.getAmount());
		}
		facesMessages.add("oluşturuldu.");
	}
	
    @Remove
    @Destroy
    public void destroy() {
    }

	public DocumentMatchProvider getInvoiceMatchProvider() {
		if (invMatchProvider == null) {
			invMatchProvider = MatchProviderRegistry.getProvider(DocumentType.SaleInvoice);
		}
		return invMatchProvider;
	}

	public DocumentMatchProvider getPaymentMatchProvider() {
		if (paymentMatchProvider == null) {
			paymentMatchProvider = MatchProviderRegistry.getProvider(DocumentType.Payment);
		}
		return paymentMatchProvider;
	}
	
	public PaymentCurrencyHelper getPaymentCurrencyHelper(Payment payment) {
		if (paymentCurrencyHelper == null) {
			paymentCurrencyHelper = PaymentCurrencyHelper.instance();
		} 
		paymentCurrencyHelper.setPayment(payment);
		return paymentCurrencyHelper;
	}
	
}
