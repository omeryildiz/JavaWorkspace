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

import java.util.HashMap;
import java.util.Map;

import com.ut.tekir.entities.DocumentType;

/**
 * Döküman tiplerine karşılık gelen tablo adlarını ve toplam kolonu adlarını tutan sınıftır.
 * 
 * @author sinan.yumak
 * 
 */
public class DocumentTypeTableMatch {

	private static Map<DocumentType, DocumentTableInfo> matches = new HashMap<DocumentType, DocumentTableInfo>();

	static {
		matches.put(DocumentType.Collection, new DocumentTableInfo("Payment","totalAmount") );
		matches.put(DocumentType.Payment, new DocumentTableInfo("Payment","totalAmount") );
		matches.put(DocumentType.CollectionItem, new DocumentTableInfo("PaymentItem","amount") );
		matches.put(DocumentType.PaymentItem, new DocumentTableInfo("PaymentItem","amount") );
		matches.put(DocumentType.SaleInvoice, new DocumentTableInfo("TekirInvoice","grandTotal") );
		matches.put(DocumentType.SaleShipmentInvoice, new DocumentTableInfo("TekirInvoice","grandTotal") );
		matches.put(DocumentType.SaleProformaInvoice, new DocumentTableInfo("TekirInvoice","grandTotal") );
		matches.put(DocumentType.RetailSaleShipmentInvoice, new DocumentTableInfo("TekirInvoice","grandTotal") );
		matches.put(DocumentType.BankToContactTransfer, new DocumentTableInfo("BankToContactTransfer","amount") );
		matches.put(DocumentType.ContactToBankTransfer, new DocumentTableInfo("ContactToBankTransfer","amount") );
	}

	public static String getTableName(DocumentType type) {
		return matches.get(type).getTableName();
	}

	public static String getTableName(DocumentMatchMetaModel model) {
		return matches.get(model.getType()).getTableName();
	}

	public static String getTotalColumnName(DocumentType type) {
		return matches.get(type).getTotalColumnName();
	}
	
	public static String getTotalColumnName(DocumentMatchMetaModel model) {
		return matches.get(model.getType()).getTotalColumnName();
	}
}

class DocumentTableInfo {
	private String tableName;
	private String totalColumnName;

	public DocumentTableInfo() {
	}

	public DocumentTableInfo(String tableName, String totalColumnName) {
		super();
		this.tableName = tableName;
		this.totalColumnName = totalColumnName;
	}

	public String getTableName() {
		return tableName;
	}

	public void setTableName(String tableName) {
		this.tableName = tableName;
	}

	public String getTotalColumnName() {
		return totalColumnName;
	}

	public void setTotalColumnName(String totalColumnName) {
		this.totalColumnName = totalColumnName;
	}

}
