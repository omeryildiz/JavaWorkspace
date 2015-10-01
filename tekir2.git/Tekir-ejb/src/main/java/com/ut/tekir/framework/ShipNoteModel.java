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

package com.ut.tekir.framework;

import java.math.BigInteger;
import java.util.Date;

/**
*
*	İrsaliye Listesini doldurmak için kullanılan alanlardan oluşan model.
*
* @author yigit
*/

public class ShipNoteModel {

	private String serial;
	private String reference;
	private String code;
	private Long contactId;
	private String contactName;
	private Long id;
	private Date date;
	private String info;
	private int expiringDay;
	
	public ShipNoteModel(){}
	
	public ShipNoteModel(Object[] obj) {
		this.contactId = Long.valueOf(((BigInteger)obj[0]).toString());
		this.id = Long.valueOf(((BigInteger)obj[1]).toString());
		this.serial = (String) obj[2];
		this.reference = (String) obj[3];
		this.code = (String) obj[4];
		this.info = (String) obj[5];
		this.date = new Date( ((java.sql.Date)obj[6]).getTime());
		this.expiringDay = Integer.valueOf(((BigInteger)obj[7]).toString());
		this.contactName = (String) obj[8];
	}

	public String getSerial() {
		return serial;
	}

	public void setSerial(String serial) {
		this.serial = serial;
	}

	public String getReference() {
		return reference;
	}

	public void setReference(String reference) {
		this.reference = reference;
	}

	public String getCode() {
		return code;
	}

	public void setCode(String code) {
		this.code = code;
	}

	public Long getContactId() {
		return contactId;
	}

	public void setContactId(Long contactId) {
		this.contactId = contactId;
	}

	public Long getId() {
		return id;
	}

	public void setId(Long id) {
		this.id = id;
	}

	public Date getDate() {
		return date;
	}

	public void setDate(Date date) {
		this.date = date;
	}

	public String getInfo() {
		return info;
	}

	public void setInfo(String info) {
		this.info = info;
	}

	public int getExpiringDay() {
		return expiringDay;
	}

	public void setExpiringDay(int expiringDay) {
		this.expiringDay = expiringDay;
	}

	public String getContactName() {
		return contactName;
	}

	public void setContactName(String contactName) {
		this.contactName = contactName;
	}
	
}
