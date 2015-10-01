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

import java.math.BigDecimal;
import java.math.BigInteger;
import java.util.Date;


/**
*
*	Yükleme İrsaliye Listesini doldurmak için kullanılan alanlardan oluşan model.
*	İrsaliye modelinde totalValue alanı bulunmadığından bu model oluşturuldu.
*
* @author yigit
*/

public class TransShipNoteModel extends ShipNoteModel {

	private BigDecimal totalValue;
	
	public TransShipNoteModel(){}
	
	public TransShipNoteModel(Object[] obj){

		this.setContactId(Long.valueOf(((BigInteger)obj[0]).toString()));
		this.setId(Long.valueOf(((BigInteger)obj[1]).toString()));
		this.setSerial((String) obj[2]);
		this.setReference((String) obj[3]);
		this.setCode((String) obj[4]);
		this.setInfo((String) obj[5]);
		this.setDate(new Date( ((java.sql.Date)obj[6]).getTime()));
		this.setExpiringDay(Integer.valueOf(((BigInteger)obj[7]).toString()));
		this.totalValue = (BigDecimal)obj[8];
		this.setContactName((String) obj[9]);
	}
	
	public BigDecimal getTotalValue() {
		return totalValue;
	}
	public void setTotalValue(BigDecimal totalValue) {
		this.totalValue = totalValue;
	}
}
