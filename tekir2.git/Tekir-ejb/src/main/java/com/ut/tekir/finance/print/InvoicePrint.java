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

package com.ut.tekir.finance.print;

import java.math.BigDecimal;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.jboss.seam.Component;

import com.google.common.base.Joiner;
import com.ut.tekir.configuration.SystemConfiguration;
import com.ut.tekir.entities.inv.TekirInvoice;
import com.ut.tekir.general.GeneralSuggestion;
import com.ut.tekir.options.CompanyOptionKey;
import com.ut.tekir.report.ReportParameters;
import com.ut.tekir.util.NumberToTextEN;
import com.ut.tekir.util.NumberToTextTR;

/**
 * @author sinan.yumak
 *
 */
public class InvoicePrint extends DocumentPrint {
	private static final long serialVersionUID = 1L;
	private TekirInvoice doc;

	private GeneralSuggestion generalSuggestion;
	
	public InvoicePrint() {
	}

	public InvoicePrint(TekirInvoice doc) {
		this.doc = doc;
	}

	@Override
	public Map<Object, Object> getParams() {
		Map<Object,Object> params = new HashMap<Object,Object>();
		params.put("pInvoiceId", doc.getId());
		params.put("pTotalAsText", getTotalAsTextTR());
		params.put("pTotalAsTextEN", getTotalAsTextEN());
		params.put("pShipmentReferences", getShipmentNoteReferences());
		params.put("SUBREPORT_DIR", ReportParameters.getTemplateDirPath() );

		params.put("pLogopath", getSystemProperties().getProperty("report.logo.file") );				
		params.put("pCompanyName", getOptionManager().getOption(CompanyOptionKey.COMPANY_TITLE, true).getAsString());
		params.put("pCompanyAddress", getOptionManager().getOption(CompanyOptionKey.COMPANY_ADDRESS, true).getAsString());
		params.put("pCompanyTaxOffice", getOptionManager().getOption(CompanyOptionKey.COMPANY_TAXOFFICE, true).getAsString());
		params.put("pCompanyTaxNumber", getOptionManager().getOption(CompanyOptionKey.COMPANY_TAX_NUMBER, true).getAsString());
		params.put("pCompanyPhone", getOptionManager().getOption(CompanyOptionKey.COMPANY_PHONE, true).getAsString());
		params.put("pCompanyFax", getOptionManager().getOption(CompanyOptionKey.COMPANY_FAX, true).getAsString());
		params.put("pCompanyEmail", getOptionManager().getOption(CompanyOptionKey.COMPANY_EMAIL, true).getAsString());
		params.put("pCompanyWeb", getOptionManager().getOption(CompanyOptionKey.COMPANY_WEB, true).getAsString());

        return params;
	}

	@SuppressWarnings("unchecked")
	private String getShipmentNoteReferences() {
		StringBuilder result = new StringBuilder();
		List<String> referenceList = getEntityManager().createQuery("select reference from TekirShipmentNote sh where " +
																		     "invoice.id = :invoiceId")
																		     .setParameter("invoiceId", doc.getId())
																		     .getResultList();
		
		
		Joiner joiner = Joiner.on(", ");
		joiner.appendTo(result, referenceList);
		
		return result.toString();
	}
	
	private String getTotalAsTextTR() {
		return new NumberToTextTR().convert(getGrandTotal().doubleValue(), doc.getDocCurrency(), getDecimalCurrencyCode());
	}

	private String getTotalAsTextEN() {
		return new NumberToTextEN().convert(getGrandTotal().doubleValue(), doc.getDocCurrency(), getDecimalCurrencyCode());
	}
	
	private BigDecimal getGrandTotal() {
		return doc.getDocCurrency().equals(SystemConfiguration.CURRENCY_CODE) ? doc.getGrandTotal().getLocalAmount()
																				 : doc.getGrandTotal().getValue();
	}

	private String getDecimalCurrencyCode() {
		String currencyCode = getGeneralSuggestion().findCurrencyDecimalCode(doc.getDocCurrency());
		return currencyCode;
	}

	private GeneralSuggestion getGeneralSuggestion() {
		if (generalSuggestion == null) {
			generalSuggestion = (GeneralSuggestion)Component.getInstance("generalSuggestion");
		}
		return generalSuggestion;
	}
	
	@Override
	public void print(String fileName) throws Exception {
		getJasperReport().compileAndRunReportToPdf(fileName + "(" + doc.getReference()+")", fileName, getParams());
	}

	public static DocumentPrint instance(TekirInvoice obj) {
		return new InvoicePrint(obj);
	}

}
