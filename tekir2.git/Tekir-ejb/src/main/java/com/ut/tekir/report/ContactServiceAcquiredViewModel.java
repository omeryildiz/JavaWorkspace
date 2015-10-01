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
import com.ut.tekir.entities.DocumentType;
import com.ut.tekir.entities.ProductType;
import com.ut.tekir.entities.TradeAction;
import com.ut.tekir.entities.User;

/**
 * @author rustem
 *
 */
public class ContactServiceAcquiredViewModel {
	
	private Date date;
	private Contact contact;
	private DocumentType documentType;
	private ProductType productType;
	private String fis_no;
	private String urun_adi;
	private Double adet;
	private Double birim_fiyat;
	private BigDecimal kapora;
	private BigDecimal net_tutar;
	private String cari_adi;
	private String company;
	private Boolean person;
	private Integer acttype;
	private Long con_id;
	private String cari_kodu;
	private String belge_no;
	private String barcode;
	private String urun_kodu;
	private String kasiyer;
	private User tezgahtar;
	private Long clerk_id;
	private Date deliveryDate;
	private Long fisId;
	private Long urunId;
	private TradeAction tradeAction;
	private BigDecimal value;
	private String doviz;
	
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
	public String getFis_no() {
		return fis_no;
	}
	public void setFis_no(String fisNo) {
		fis_no = fisNo;
	}
	public String getUrun_adi() {
		return urun_adi;
	}
	public void setUrun_adi(String urunAdi) {
		urun_adi = urunAdi;
	}
	public Double getAdet() {
		return adet;
	}
	public void setAdet(Double adet) {
		this.adet = adet;
	}
	public Double getBirim_fiyat() {
		return birim_fiyat;
	}
	public void setBirim_fiyat(Double item) {
		birim_fiyat = item;
	}
	public BigDecimal getKapora() {
		return kapora;
	}
	public void setKapora(BigDecimal kapora) {
		this.kapora = kapora;
	}
	public BigDecimal getNet_tutar() {
		return net_tutar;
	}
	public void setNet_tutar(BigDecimal netTutar) {
		net_tutar = netTutar;
	}
	public String getCari_adi() {
		return cari_adi;
	}
	public void setCari_adi(String cariAdi) {
		cari_adi = cariAdi;
	}
	public String getCompany() {
		return company;
	}
	public void setCompany(String company) {
		this.company = company;
	}
	public Boolean getPerson() {
		return person;
	}
	public void setPerson(Boolean person) {
		this.person = person;
	}
	public Integer getActtype() {
		return acttype;
	}
	public void setActtype(Integer acttype) {
		this.acttype = acttype;
	}
	public Long getCon_id() {
		return con_id;
	}
	public void setCon_id(Long conId) {
		con_id = conId;
	}
	public String getCari_kodu() {
		return cari_kodu;
	}
	public void setCari_kodu(String cariKodu) {
		cari_kodu = cariKodu;
	}
	public String getBelge_no() {
		return belge_no;
	}
	public void setBelge_no(String belgeNo) {
		belge_no = belgeNo;
	}
	public String getBarcode() {
		return barcode;
	}
	public void setBarcode(String barcode) {
		this.barcode = barcode;
	}
	public String getUrun_kodu() {
		return urun_kodu;
	}
	public void setUrun_kodu(String urunKodu) {
		urun_kodu = urunKodu;
	}
	public String getKasiyer() {
		return kasiyer;
	}
	public void setKasiyer(String kasiyer) {
		this.kasiyer = kasiyer;
	}
	public User getTezgahtar() {
		return tezgahtar;
	}
	public void setTezgahtar(User tezgahtar) {
		this.tezgahtar = tezgahtar;
	}
	public Long getClerk_id() {
		return clerk_id;
	}
	public void setClerk_id(Long clerkId) {
		clerk_id = clerkId;
	}
	public Date getDeliveryDate() {
		return deliveryDate;
	}
	public void setDeliveryDate(Date deliveryDate) {
		this.deliveryDate = deliveryDate;
	}
	public Long getFisId() {
		return fisId;
	}
	public void setFisId(Long fisId) {
		this.fisId = fisId;
	}
	public Long getUrunId() {
		return urunId;
	}
	public void setUrunId(Long urunId) {
		this.urunId = urunId;
	}
	public DocumentType getDocumentType() {
		return documentType;
	}
	public void setDocumentType(DocumentType documentType) {
		this.documentType = documentType;
	}
	public ProductType getProductType() {
		return productType;
	}
	public void setProductType(ProductType productType) {
		this.productType = productType;
	}
	public TradeAction getTradeAction() {
		return tradeAction;
	}
	public void setTradeAction(TradeAction tradeAction) {
		this.tradeAction = tradeAction;
	}
	public BigDecimal getValue() {
		return value;
	}
	public void setValue(BigDecimal value) {
		this.value = value;
	}
	public String getDoviz() {
		return doviz;
	}
	public void setDoviz(String doviz) {
		this.doviz = doviz;
	}
	
}
