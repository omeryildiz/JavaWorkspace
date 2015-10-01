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

import java.util.HashMap;
import java.util.Map;

import com.ut.tekir.configuration.SystemConfiguration;
import com.ut.tekir.entities.DocumentType;
import com.ut.tekir.entities.Payment;

/**
 * Tahsilat-tediye fişlerinin yazdırma işlemmlerinin yapıldığı sınıftır.
 * 
 * @author sinan.yumak, volkan
 *
 */
public class PaymentPrint extends DocumentPrint {
	private static final long serialVersionUID = 1L;
	private Payment doc;

	public PaymentPrint() {
	}

	public PaymentPrint(Payment doc) {
		this.doc = doc;
	}

	public static DocumentPrint instance(Payment doc) {
		return new PaymentPrint(doc);
	}
	
	@Override
	public Map<Object, Object> getParams() {
		Map<Object, Object> params = new HashMap<Object, Object>();
		params.put("payment", doc.getId());
		params.put("noteName", doc.getDocumentType().equals(DocumentType.Collection) ? "TAHSİLAT MAKBUZU" : "TEDİYE MAKBUZU");

		params.put("pCompanyName", getOptionManager().getOption("company.Title").getAsString());
        params.put("pCompanyAddress", getOptionManager().getOption("company.Address").getAsString());
        params.put("pCompanyTaxOffice", getOptionManager().getOption("company.TaxOffice").getAsString());
        params.put("pCompanyTaxNumber", getOptionManager().getOption("company.TaxNumber").getAsString());
        params.put("pCompanyPhone", getOptionManager().getOption("company.Phone").getAsString());
        params.put("pCompanyFax", getOptionManager().getOption("company.Fax").getAsString());
        params.put("pCompanyEmail", getOptionManager().getOption("company.Email").getAsString());
        params.put("pCompanyWeb", getOptionManager().getOption("company.Web").getAsString());
        params.put("pLogopath", getSystemProperties().getProperty("report.logo.file") );	
        params.put("pProductLabel", SystemConfiguration.PRODUCTLABEL); 
		return params;
	}

	@Override
	public void print() throws Exception {
		getLog().info("Collection Note Print");

		PaymentPrintModelBuilder builder = new PaymentPrintModelBuilder();
		builder.begin(doc);
		
		getJasperReport().printObjectToPDF( getReportName(), "tahsilat", getParams(), builder.getModel());
	}

	public String getReportName() {
		//TODO:stringleri properties dosyasından okumak lazım!
		StringBuilder result = new StringBuilder();
		result.append(doc.getDocumentType().equals(DocumentType.Collection) ? "tahsilat" : "tediye");
		result.append("_");
		result.append(doc.getReference());
		return result.toString();
	}
}
