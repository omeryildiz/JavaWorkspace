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
import java.util.List;
import java.util.Map;

import javax.ejb.Stateful;

import org.hibernate.criterion.DetachedCriteria;
import org.hibernate.criterion.MatchMode;
import org.hibernate.criterion.Order;
import org.hibernate.criterion.Projections;
import org.hibernate.criterion.Restrictions;
import org.hibernate.transform.Transformers;
import org.jboss.seam.ScopeType;
import org.jboss.seam.annotations.AutoCreate;
import org.jboss.seam.annotations.In;
import org.jboss.seam.annotations.Name;
import org.jboss.seam.annotations.Scope;
import org.jboss.seam.core.Events;

import com.ut.tekir.entities.Contact;
import com.ut.tekir.entities.DocumentMatch;
import com.ut.tekir.entities.DocumentType;
import com.ut.tekir.entities.Invoice;
import com.ut.tekir.entities.TradeAction;
import com.ut.tekir.framework.BrowserBase;
import com.ut.tekir.framework.CalendarManager;
import com.ut.tekir.framework.DefaultDocumentFilterModel;

/**
 * Fatura eşlemelerinde kullanılacak home sınıfı...
 * @author sinan.yumak
 *
 */
@Name("invoiceMatchHome")
@Scope(ScopeType.CONVERSATION)
@Stateful
@AutoCreate
public class InvoiceMatchHomeBean extends BrowserBase<Invoice, DefaultDocumentFilterModel> implements InvoiceMatchHome <Invoice, DefaultDocumentFilterModel>{
    @In
    protected Events events;
    @In
    CalendarManager calendarManager;

    private Contact contact;
    private TradeAction tradeAction;
    private Boolean disableContactSelect = Boolean.FALSE;
	private String invoiceMatchObserver;

    @Override
    public DefaultDocumentFilterModel newFilterModel() {
    	log.debug("Creating filter model...");
        DefaultDocumentFilterModel fm = new DefaultDocumentFilterModel();
        fm.setBeginDate( calendarManager.getLastTenDay());
        fm.setEndDate( calendarManager.getCurrentDate());
        return fm;
    }

	@Override
    public DetachedCriteria buildCriteria() {
		DetachedCriteria crit = DetachedCriteria.forClass(Invoice.class);

        crit.setProjection(Projections.projectionList()
            .add(Projections.property("this.serial"), "serial")
            .add(Projections.property("this.contact"), "contact")
            .add(Projections.property("this.reference"), "reference")
            .add(Projections.property("this.action"), "action")
            .add(Projections.property("this.invoiceTotal.currency"), "invoiceCurrency")
            .add(Projections.property("this.invoiceTotal.value"), "invoiceValue")
            .add(Projections.property("this.date"), "date")
            .add(Projections.property("this.id"),"matchedDocumentId"));

        crit.add(Restrictions.ne("this.matchingFinished", Boolean.TRUE));

        crit.setResultTransformer(Transformers.aliasToBean(InvoiceMatchFilterModel.class));

        if (filterModel.getSerial() != null && filterModel.getSerial().length() > 0) {
            crit.add( Restrictions.ilike("this.serial", filterModel.getSerial(),MatchMode.START));
        }

        if( filterModel.getReference() != null && filterModel.getReference().length() > 0){
            crit.add( Restrictions.ilike("this.reference", filterModel.getReference(),MatchMode.START ));
        }

        if( filterModel.getCode() != null && filterModel.getCode().length() > 0){
            crit.add( Restrictions.ilike("this.code", filterModel.getCode(),MatchMode.START));
        }

        if( getContact() != null ){
        	crit.add( Restrictions.eq("this.contact", getContact()));
        }

        if( filterModel.getBeginDate() != null ){
            crit.add( Restrictions.ge("this.date", filterModel.getBeginDate() ));
        }

        if( filterModel.getEndDate() != null ){
            crit.add( Restrictions.le("this.date", filterModel.getEndDate() ));
        }

        if( tradeAction != null ){
        	crit.add( Restrictions.eq("this.action", tradeAction ));
        }

        crit.addOrder(Order.desc("this.serial"));

        return crit;
	}

	private BigDecimal findUsedAmountOfInvoice(String documentSerial, DocumentType documentType) {
		BigDecimal result = null;

		result = (BigDecimal)entityManager.createQuery("select sum(m.amount) from DocumentMatch m where " +
                                                      "m.matchedDocumentSerial =:documentSerial and " +
                                                      "m.matchedDocumentType =:documentType")
                                                      .setParameter("documentSerial", documentSerial)
                                                      .setParameter("documentType", documentType)
                                                      .getSingleResult();
		if (result == null) {
			result = BigDecimal.ZERO;
		}
		return result;
	}

	private void setMatchingFinishedFlag(String serial) {
		entityManager.createQuery("update Invoice inv " +
                               "set inv.matchingFinished = true where " +
                               "inv.serial =:serial")
                               .setParameter("serial", serial)
                               .executeUpdate();
	}

	public boolean updateInvoiceDocumentMatch(Map<String, BigDecimal> updateList,
											  DocumentType documentType,
											  List<DocumentMatch> matchList) {

		boolean result = true;
		BigDecimal usedAmount = null;
		TradeAction tradeAction = null;
		for(String invoiceSerial :updateList.keySet()) {
			usedAmount = findUsedAmountOfInvoice(invoiceSerial, documentType);

			if (updateList.get(invoiceSerial).add(usedAmount).compareTo(findInvoiceTotal(matchList, invoiceSerial)) > 0) {
				result = false;
				facesMessages.add("#0 #{messages['invoiceMatch.message.InvoiceTotalExceeded']}",invoiceSerial);
			} else if (updateList.get(invoiceSerial).add(usedAmount).compareTo(findInvoiceTotal(matchList, invoiceSerial)) == 0){
				log.info("fatura tutari tamamen kullanildi #0", invoiceSerial);
				setMatchingFinishedFlag(invoiceSerial);
			}
		}
		return result;
	}

	/**
	 * Verilen seri no ya göre fatura tutarını bulur.
	 * @param matchList
	 * @param serial
	 * @return
	 */
	private BigDecimal findInvoiceTotal(List<DocumentMatch> matchList, String serial) {
		BigDecimal result = BigDecimal.ZERO;
    	//FIXME: bu kodları document match provider yapısına geçirmek gerek.
//		for(DocumentMatch match : matchList) {
//			if ( match.getMatchedDocumentSerial().equals(serial)) {
//				return match.getTotalAmount();
//			}
//		}
		return result;
	}

	public void selectInvoice(int index) {
		if (invoiceMatchObserver != null && invoiceMatchObserver.length() > 0) {
    		events.raiseTransactionSuccessEvent(invoiceMatchObserver, entityList.get(index));
		}
	}

	public String getInvoiceMatchObserver() {
		return invoiceMatchObserver;
	}

	public void setInvoiceMatchObserver(String invoiceMatchObserver) {
		this.invoiceMatchObserver = invoiceMatchObserver;
	}

	public Contact getContact() {
		return contact;
	}

	public void setContact(Contact contact) {
		this.contact = contact;
	}

	public TradeAction getTradeAction() {
		return tradeAction;
	}

	public void setTradeAction(TradeAction tradeAction) {
		this.tradeAction = tradeAction;
	}

	public Boolean getDisableContactSelect() {
		return disableContactSelect;
	}

	public void setDisableContactSelect(Boolean disableContactSelect) {
		this.disableContactSelect = disableContactSelect;
	}

}
