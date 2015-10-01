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

package com.ut.tekir.report;

import java.math.BigDecimal;
import java.util.Date;

import com.ut.tekir.entities.Contact;

/**
 * @author rustem
 *
 */
public class RetrospectiveDebtorContactViewModel {
	
	private Date date;
	private Contact contact;
	private String ccy;
	private BigDecimal total;
	private BigDecimal otuzGuneKadar;
	private BigDecimal atmisGuneKadar;
	private BigDecimal doksanGuneKadar;
	private BigDecimal yuzyirmiGuneKadar;
	private BigDecimal yuzyirmiGunVeSonrasi;
	private String contactCode;
	private String contactFullName;
	private Boolean person;
	private String company;
	private BigDecimal totalOdenen;
	
	
	public Date getDate() {
		return date;
	}
	public void setDate(Date date) {
		this.date = date;
	}
	public Contact getContact() {
		return contact;
	}
	public void setContact(Contact contact) {
		this.contact = contact;
	}
	public String getCcy() {
		return ccy;
	}
	public void setCcy(String ccy) {
		this.ccy = ccy;
	}
	public BigDecimal getTotal() {
		return total;
	}
	public void setTotal(BigDecimal total) {
		this.total = total;
	}
	public BigDecimal getOtuzGuneKadar() {
		return otuzGuneKadar;
	}
	public void setOtuzGuneKadar(BigDecimal otuzGuneKadar) {
		this.otuzGuneKadar = otuzGuneKadar;
	}
	public BigDecimal getAtmisGuneKadar() {
		return atmisGuneKadar;
	}
	public void setAtmisGuneKadar(BigDecimal atmisGuneKadar) {
		this.atmisGuneKadar = atmisGuneKadar;
	}
	public BigDecimal getDoksanGuneKadar() {
		return doksanGuneKadar;
	}
	public void setDoksanGuneKadar(BigDecimal doksanGuneKadar) {
		this.doksanGuneKadar = doksanGuneKadar;
	}
	
	public BigDecimal getYuzyirmiGuneKadar() {
		return yuzyirmiGuneKadar;
	}
	public void setYuzyirmiGuneKadar(BigDecimal yuzyirmiGuneKadar) {
		this.yuzyirmiGuneKadar = yuzyirmiGuneKadar;
	}
	public BigDecimal getYuzyirmiGunVeSonrasi() {
		return yuzyirmiGunVeSonrasi;
	}
	public void setYuzyirmiGunVeSonrasi(BigDecimal yuzyirmiGunVeSonrasi) {
		this.yuzyirmiGunVeSonrasi = yuzyirmiGunVeSonrasi;
	}
	public String getContactCode() {
		return contactCode;
	}
	public void setContactCode(String contactCode) {
		this.contactCode = contactCode;
	}
	public String getContactFullName() {
		return contactFullName;
	}
	public void setContactFullName(String contactFullName) {
		this.contactFullName = contactFullName;
	}
	public Boolean getPerson() {
		return person;
	}
	public void setPerson(Boolean person) {
		this.person = person;
	}
	public String getCompany() {
		return company;
	}
	public void setCompany(String company) {
		this.company = company;
	}
	public BigDecimal getTotalOdenen() {
		return totalOdenen;
	}
	public void setTotalOdenen(BigDecimal totalOdenen) {
		this.totalOdenen = totalOdenen;
	}
	
}
