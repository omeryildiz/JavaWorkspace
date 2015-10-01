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

import com.ut.tekir.entities.DocumentType;
import com.ut.tekir.entities.MoneySet;

/**
 * Döküman için eşleme sırasında kullanılacak olan elzem 
 * bilgileri tutar.
 * 
 * @author sinan.yumak
 *
 */
public class DocumentMatchMetaModel {
	private DocumentType type;
	private String serial;
	private MoneySet amount;//dökümanın tutarı bilgisidir.
	private Long id;

	public DocumentMatchMetaModel() {
		super();
	}

	public DocumentMatchMetaModel(Long id) {
		super();
		this.id = id;
	}

	public String getCurrency() {
		return amount.getCurrency();
	}
	
	@Override
	public boolean equals(Object object) {
        if (!(object instanceof DocumentMatchMetaModel)) {
            return false;
        }
        DocumentMatchMetaModel other = (DocumentMatchMetaModel)object;
        if (this.id != other.id || this.type != other.type) return false;
        return true;
	}

	@Override
	public int hashCode() {
		return type.hashCode() + serial.hashCode() + id.hashCode();
	}

	@Override
	public String toString() {
		return "id:"+getId() + ", serial:" + getSerial() + ", type:"+ getType();
	}

	public DocumentType getType() {
		return type;
	}

	public void setType(DocumentType type) {
		this.type = type;
	}

	public String getSerial() {
		return serial;
	}

	public void setSerial(String serial) {
		this.serial = serial;
	}

	public Long getId() {
		return id;
	}

	public void setId(Long id) {
		this.id = id;
	}

	public MoneySet getAmount() {
		return amount;
	}

	public void setAmount(MoneySet amount) {
		this.amount = amount;
	}

}
