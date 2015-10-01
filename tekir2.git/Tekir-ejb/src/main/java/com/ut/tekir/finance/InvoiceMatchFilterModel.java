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

import com.ut.tekir.entities.Contact;
import com.ut.tekir.entities.TradeAction;
import com.ut.tekir.framework.DefaultDocumentFilterModel;
import java.math.BigDecimal;
import java.util.Date;

/**
 *
 * pusuladan aktarıldı , pusulada InvoiceFilterModel adında
 * @author yigit
 */
public class InvoiceMatchFilterModel extends DefaultDocumentFilterModel {

    private Long id;
	private String integrationSerial;
	private Date date;
	private Contact contact;
	private String info;
	private String beforeCurrency;
	private BigDecimal beforeValue;
	private String totalCurrency;
	private BigDecimal totalValue;
	private String invoiceCurrency;
	private BigDecimal invoiceValue;
	private Boolean shipmentInvoice;
    private String contactCode;
    private String contactName;
    private TradeAction action;
    private Long matchedDocumentId;

    /**
     * @return the id
     */
    public Long getId() {
        return id;
    }

    /**
     * @param id the id to set
     */
    public void setId(Long id) {
        this.id = id;
    }

    /**
     * @return the integrationSerial
     */
    public String getIntegrationSerial() {
        return integrationSerial;
    }

    /**
     * @param integrationSerial the integrationSerial to set
     */
    public void setIntegrationSerial(String integrationSerial) {
        this.integrationSerial = integrationSerial;
    }

    /**
     * @return the date
     */
    public Date getDate() {
        return date;
    }

    /**
     * @param date the date to set
     */
    public void setDate(Date date) {
        this.date = date;
    }

    /**
     * @return the contact
     */
    public Contact getContact() {
        return contact;
    }

    /**
     * @param contact the contact to set
     */
    public void setContact(Contact contact) {
        this.contact = contact;
    }

    /**
     * @return the info
     */
    public String getInfo() {
        return info;
    }

    /**
     * @param info the info to set
     */
    public void setInfo(String info) {
        this.info = info;
    }

    /**
     * @return the beforeCurrency
     */
    public String getBeforeCurrency() {
        return beforeCurrency;
    }

    /**
     * @param beforeCurrency the beforeCurrency to set
     */
    public void setBeforeCurrency(String beforeCurrency) {
        this.beforeCurrency = beforeCurrency;
    }

    /**
     * @return the beforeValue
     */
    public BigDecimal getBeforeValue() {
        return beforeValue;
    }

    /**
     * @param beforeValue the beforeValue to set
     */
    public void setBeforeValue(BigDecimal beforeValue) {
        this.beforeValue = beforeValue;
    }

    /**
     * @return the totalCurrency
     */
    public String getTotalCurrency() {
        return totalCurrency;
    }

    /**
     * @param totalCurrency the totalCurrency to set
     */
    public void setTotalCurrency(String totalCurrency) {
        this.totalCurrency = totalCurrency;
    }

    /**
     * @return the totalValue
     */
    public BigDecimal getTotalValue() {
        return totalValue;
    }

    /**
     * @param totalValue the totalValue to set
     */
    public void setTotalValue(BigDecimal totalValue) {
        this.totalValue = totalValue;
    }

    /**
     * @return the invoiceCurrency
     */
    public String getInvoiceCurrency() {
        return invoiceCurrency;
    }

    /**
     * @param invoiceCurrency the invoiceCurrency to set
     */
    public void setInvoiceCurrency(String invoiceCurrency) {
        this.invoiceCurrency = invoiceCurrency;
    }

    /**
     * @return the invoiceValue
     */
    public BigDecimal getInvoiceValue() {
        return invoiceValue;
    }

    /**
     * @param invoiceValue the invoiceValue to set
     */
    public void setInvoiceValue(BigDecimal invoiceValue) {
        this.invoiceValue = invoiceValue;
    }

    /**
     * @return the shipmentInvoice
     */
    public Boolean getShipmentInvoice() {
        return shipmentInvoice;
    }

    /**
     * @param shipmentInvoice the shipmentInvoice to set
     */
    public void setShipmentInvoice(Boolean shipmentInvoice) {
        this.shipmentInvoice = shipmentInvoice;
    }

    /**
     * @return the contactCode
     */
    public String getContactCode() {
        return contactCode;
    }

    /**
     * @param contactCode the contactCode to set
     */
    public void setContactCode(String contactCode) {
        this.contactCode = contactCode;
    }

    /**
     * @return the contactName
     */
    public String getContactName() {
        return contactName;
    }

    /**
     * @param contactName the contactName to set
     */
    public void setContactName(String contactName) {
        this.contactName = contactName;
    }

    /**
     * @return the action
     */
    public TradeAction getAction() {
        return action;
    }

    /**
     * @param action the action to set
     */
    public void setAction(TradeAction action) {
        this.action = action;
    }

    /**
     * @return the matchedDocumentId
     */
    public Long getMatchedDocumentId() {
        return matchedDocumentId;
    }

    /**
     * @param matchedDocumentId the matchedDocumentId to set
     */
    public void setMatchedDocumentId(Long matchedDocumentId) {
        this.matchedDocumentId = matchedDocumentId;
    }
}
