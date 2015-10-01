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

package com.ut.tekir.docmatch;

import java.util.ArrayList;
import java.util.List;

import javax.ejb.Remove;
import javax.ejb.Stateful;

import org.jboss.seam.ScopeType;
import org.jboss.seam.annotations.Destroy;
import org.jboss.seam.annotations.In;
import org.jboss.seam.annotations.Name;
import org.jboss.seam.annotations.Scope;

import com.ut.tekir.entities.AdvanceProcessType;
import com.ut.tekir.entities.BankToContactTransfer;
import com.ut.tekir.entities.DocumentType;
import com.ut.tekir.entities.FinanceAction;
import com.ut.tekir.entities.MoneySet;
import com.ut.tekir.entities.PaymentItem;
import com.ut.tekir.entities.TradeAction;
import com.ut.tekir.entities.inv.TekirInvoice;
import com.ut.tekir.finance.BankToContactSuggestion;
import com.ut.tekir.finance.BankToContactSuggestionFilterModel;
import com.ut.tekir.finance.InvoiceSuggestion;
import com.ut.tekir.finance.PaymentSuggestion;
import com.ut.tekir.finance.PaymentSuggestionFilterModel;
import com.ut.tekir.framework.BrowserBase;
import com.ut.tekir.invoice.yeni.InvoiceSuggestionFilterModel;

/**
 * Döküman eşlemeleri popupunda gelecek olan dökümanları listeleyen browse bileşenidir.
 * Eğer avans işlem tipi olarak normal seçildi ise faturalar,
 * avans iade seçildi ise avans tipindeki tediye satırları döner.
 * Eğer avans seçildi ise hiç bir şey dönmeyecektir.
 * 
 * @author sinan.yumak
 *
 */
@Stateful
@Name("docMatchBrowse")
@Scope(ScopeType.SESSION)
public class DocumentMatchBrowseBean  extends BrowserBase<DocumentMatchResultModel, DocumentMatchFilterModel> implements DocumentMatchBrowse<DocumentMatchResultModel, DocumentMatchFilterModel>   {
	@In
	private InvoiceSuggestion invoiceSuggestion;
	@In
	private PaymentSuggestion paymentSuggestion;
	@In
	private BankToContactSuggestion bankToContactSuggestion;

	private DocumentMatchProvider invoiceMatchProvider;
	private DocumentMatchProvider paymentMatchProvider;
	private DocumentMatchProvider bankToContactMatchProvider;

    @Override
    public DocumentMatchFilterModel newFilterModel() {
    	log.debug("Creating filter model...");
    	return new DocumentMatchFilterModel();
    }

	@Override
	public void search() {
		getEntityList().clear();

		appendInvoices();
		appendPaymentItems();
		appendBankToContactTransfers();
	}
	
	private void appendInvoices() {
		if (!getFilterModel().getProcessType().equals(AdvanceProcessType.Normal)) return;
		List<TekirInvoice> invoices = findInvoices();
		log.info("Appending #0 invoices to list", invoices.size());
		for (TekirInvoice inv : invoices) {
			getEntityList().add(createResultModel(inv));
		}
	}

	private DocumentMatchResultModel createResultModel(TekirInvoice inv) {
		DocumentMatchResultModel model = new DocumentMatchResultModel();
		model.setId(inv.getId());
		model.setDocumentType(inv.getDocumentType());
		model.setSerial(inv.getSerial());
		model.setReference(inv.getReference());
		model.setDate(inv.getDate());
		model.setContact(inv.getContact());
		model.setAmount(inv.getGrandTotal());
		model.setUsedAmount(getInvoiceMatchProvider().findUsedAmountInMatchesBefore(inv));
		return model;
	}
	
	private void appendPaymentItems() {
		if (!getFilterModel().getProcessType().equals(AdvanceProcessType.AdvanceReturn)) return;
		
		List<PaymentItem> pitems = findPaymentItems();
		log.info("Appending #0 payment items to list", pitems.size());
		for (PaymentItem pi : pitems) {
			getEntityList().add(createResultModel(pi));
		}
	}

	private DocumentMatchResultModel createResultModel(PaymentItem pi) {
		DocumentMatchResultModel model = new DocumentMatchResultModel();
		model.setId(pi.getId());
		model.setDocumentType(pi.getOwner().getDocumentType().equals(DocumentType.Collection) ? DocumentType.CollectionItem:DocumentType.PaymentItem);
		model.setSerial(pi.getOwner().getSerial());
		model.setReference(pi.getOwner().getReference());
		model.setDate(pi.getOwner().getDate());
		model.setContact(pi.getOwner().getContact());
		model.setAmount(new MoneySet(pi.getAmount()));
		model.setUsedAmount(getPaymentMatchProvider().findUsedAmountInMatchesBefore(pi));
		return model;
	}

	private void appendBankToContactTransfers() {
		if (!getFilterModel().getProcessType().equals(AdvanceProcessType.AdvanceReturn)) return;

		List<BankToContactTransfer> transfers = findBankToContactTransfers();
		log.info("Appending #0 bank to contact transfers to list", transfers.size());
		for (BankToContactTransfer btc : transfers) {
			getEntityList().add(createResultModel(btc));
		}
	}

	private DocumentMatchResultModel createResultModel(BankToContactTransfer btc) {
		DocumentMatchResultModel model = new DocumentMatchResultModel();
		model.setId(btc.getId());
		model.setDocumentType(btc.getDocumentType());
		model.setSerial(btc.getSerial());
		model.setReference(btc.getReference());
		model.setDate(btc.getDate());
		model.setContact(btc.getContact());
		model.setAmount(btc.getAmount());
		model.setUsedAmount(getBankToContactMatchProvider().findUsedAmountInMatchesBefore(btc));
		return model;
	}

	private List<TekirInvoice> findInvoices() {
		return invoiceSuggestion.suggestInvoice( getInvoiceFilterModel() );
	}

	private List<PaymentItem> findPaymentItems() {
		return paymentSuggestion.suggestPaymentItem( getPaymentFilterModel() );
	}

	private List<BankToContactTransfer> findBankToContactTransfers() {
		return bankToContactSuggestion.suggest( getBankToContactFilterModel() );
	}
	
	private InvoiceSuggestionFilterModel getInvoiceFilterModel() {
		InvoiceSuggestionFilterModel fm = new InvoiceSuggestionFilterModel();
		fm.setBeginDate(getFilterModel().getBeginDate());
		fm.setEndDate(getFilterModel().getEndDate());
		fm.setContact(getFilterModel().getContact());
		fm.setTradeAction(getFilterModel().getAction());
		fm.setSerial(getFilterModel().getSerial());
		fm.setReference(getFilterModel().getReference());
		fm.setMatchingFinished(false);
		return fm;
	}

	private PaymentSuggestionFilterModel getPaymentFilterModel() {
		PaymentSuggestionFilterModel fm = new PaymentSuggestionFilterModel();
		fm.setBeginDate(getFilterModel().getBeginDate());
		fm.setEndDate(getFilterModel().getEndDate());
		fm.setContact(getFilterModel().getContact());
		fm.setSerial(getFilterModel().getSerial());
		fm.setReference(getFilterModel().getReference());
		if (TradeAction.Purchase.equals(getFilterModel().getAction())) {
			fm.setAction(FinanceAction.Credit);
		} else {
			fm.setAction(FinanceAction.Debit);
		}
		fm.setMatchingFinished(false);

		if (AdvanceProcessType.AdvanceReturn.equals(getFilterModel().getProcessType())) {
			fm.setProcessType(AdvanceProcessType.Advance);
		}
		return fm;
	}

	private BankToContactSuggestionFilterModel getBankToContactFilterModel() {
		BankToContactSuggestionFilterModel fm = new BankToContactSuggestionFilterModel();
		fm.setBeginDate(getFilterModel().getBeginDate());
		fm.setEndDate(getFilterModel().getEndDate());
		fm.setContact(getFilterModel().getContact());
		fm.setSerial(getFilterModel().getSerial());
		fm.setReference(getFilterModel().getReference());
		if (TradeAction.Purchase.equals(getFilterModel().getAction())) {
			fm.setAction(FinanceAction.Credit);
		} else {
			fm.setAction(FinanceAction.Debit);
		}
		fm.setMatchingFinished(false);
		return fm;
	}
	
    @Override
	@Remove
    @Destroy
    public void destroy() {
    }

	@Override
	public List<DocumentMatchResultModel> getEntityList() {
		if (entityList == null)  {
			entityList = new ArrayList<DocumentMatchResultModel>();
		}
		return entityList;
	}
    
	private DocumentMatchProvider getInvoiceMatchProvider() {
		if (invoiceMatchProvider == null) {
			invoiceMatchProvider = MatchProviderRegistry.getProvider(DocumentType.SaleInvoice);
		}
		return invoiceMatchProvider;
	}

	private DocumentMatchProvider getPaymentMatchProvider() {
		if (paymentMatchProvider == null) {
			paymentMatchProvider = MatchProviderRegistry.getProvider(DocumentType.Payment);
		}
		return paymentMatchProvider;
	}

	private DocumentMatchProvider getBankToContactMatchProvider() {
		if (bankToContactMatchProvider == null) {
			bankToContactMatchProvider = MatchProviderRegistry.getProvider(DocumentType.BankToContactTransfer);
		}
		return bankToContactMatchProvider;
	}
	
}
