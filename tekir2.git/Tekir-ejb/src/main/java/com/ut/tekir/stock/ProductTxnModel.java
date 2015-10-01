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

package com.ut.tekir.stock;

import java.math.BigDecimal;
import java.sql.Date;
import java.text.NumberFormat;

import com.ut.tekir.entities.DocumentType;
import com.ut.tekir.entities.FinanceAction;
import com.ut.tekir.entities.Product;
import com.ut.tekir.entities.ProductGroup;
import com.ut.tekir.entities.Tax;
import com.ut.tekir.entities.TradeAction;
import com.ut.tekir.entities.WorkBunch;


/**
 * @author bilga
 * 
 */
public class ProductTxnModel {

	private DocumentType documentType;
	private String serial;
	private String reference;
	private String code;
	private Date date;
	private String wareName;
	private Long prodId;
	private String prodName;
	private String prodCode;
	private String adverseCode;
	private String adverseName;
	private String info;
	private TradeAction action;
	private Long documentId;
	private Long productId;

	private String quantityUnit;
	private Double quantityValue;
	
	private String unitPriceCurrency;
    private BigDecimal unitPriceValue;
    private FinanceAction financeAction;
    private ProductGroup group;
    private String barcode;
    private String barcode2;
    private String barcode3;
    private String labelName;
    private Tax buyTax1;
    private Tax buyTax2;
    private Tax buyTax3;
    private Tax buyTax4;
    private Tax buyTax5;
    private Tax sellTax1;
    private Tax sellTax2;
    private Tax sellTax3;
    private Tax sellTax4;
    private Tax sellTax5;
    private BigDecimal netPriceValue;
    private String netPriceCurrency;
    private BigDecimal grossPriceValue;
    private String grossPriceCurrency;
    private String ownerCode;
    private Date ownerBeginDate;
    private Date ownerEndDate;
    private Boolean ownerIsActive;
    private String priceListName;
    private Product product;
    private String workBunchCode ;
    private WorkBunch workBunch;
    
    
    public String getBarcode() {
        return barcode;
    }

    public void setBarcode(String barcode) {
        this.barcode = barcode;
    }

    public ProductGroup getGroup() {
        return group;
    }

    public void setGroup(ProductGroup group) {
        this.group = group;
    }

	public DocumentType getDocumentType() {
		return documentType;
	}

	public void setDocumentType(DocumentType documentType) {
		this.documentType = documentType;
	}

	public String getQuantity() {
        NumberFormat f = NumberFormat.getInstance();
        f.setMaximumFractionDigits(2);
        f.setMinimumFractionDigits(2);

        return f.format(getQuantityValue()) + " " + getQuantityUnit();
	}
	
	public String getUnitPrice() {
		NumberFormat f = NumberFormat.getInstance();
		f.setMaximumFractionDigits(2);
		f.setMinimumFractionDigits(2);
		
		return f.format(getUnitPriceValue()) + " " + getUnitPriceCurrency();
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

	public Date getDate() {
		return date;
	}

	public void setDate(Date date) {
		this.date = date;
	}

	public String getProdName() {
		return prodName;
	}

	public void setProdName(String prodName) {
		this.prodName = prodName;
	}

	public String getProdCode() {
		return prodCode;
	}

	public void setProdCode(String prodCode) {
		this.prodCode = prodCode;
	}

	public String getAdverseCode() {
		return adverseCode;
	}

	public void setAdverseCode(String adverseCode) {
		this.adverseCode = adverseCode;
	}

	public String getAdverseName() {
		return adverseName;
	}

	public void setAdverseName(String adverseName) {
		this.adverseName = adverseName;
	}

	public String getInfo() {
		return info;
	}

	public void setInfo(String info) {
		this.info = info;
	}

	public TradeAction getAction() {
		return action;
	}

	public void setAction(TradeAction action) {
		this.action = action;
	}

	public String getQuantityUnit() {
		return quantityUnit;
	}

	public void setQuantityUnit(String quantityUnit) {
		this.quantityUnit = quantityUnit;
	}

	public Double getQuantityValue() {
		return quantityValue;
	}

	public void setQuantityValue(Double quantityValue) {
		this.quantityValue = quantityValue;
	}

	public String getUnitPriceCurrency() {
		return unitPriceCurrency;
	}

	public void setUnitPriceCurrency(String unitPriceCurrency) {
		this.unitPriceCurrency = unitPriceCurrency;
	}

	public BigDecimal getUnitPriceValue() {
		return unitPriceValue;
	}

	public void setUnitPriceValue(BigDecimal unitPriceValue) {
		this.unitPriceValue = unitPriceValue;
	}

	public void setWareName(String wareName) {
		this.wareName = wareName;
	}

	public String getWareName() {
		return wareName;
	}

	public void setDocumentId(Long documentId) {
		this.documentId = documentId;
	}

	public Long getDocumentId() {
		return documentId;
	}

	public void setProductId(Long productId) {
		this.productId = productId;
	}

	public Long getProductId() {
		return productId;
	}

	public void setCode(String code) {
		this.code = code;
	}

	public String getCode() {
		return code;
	}

    /**
     * @return the financeAction
     */
    public FinanceAction getFinanceAction() {
        return financeAction;
    }

    /**
     * @param financeAction the financeAction to set
     */
    public void setFinanceAction(FinanceAction financeAction) {
        this.financeAction = financeAction;
    }

	public String getBarcode2() {
		return barcode2;
	}

	public void setBarcode2(String barcode2) {
		this.barcode2 = barcode2;
	}

	public String getBarcode3() {
		return barcode3;
	}

	public void setBarcode3(String barcode3) {
		this.barcode3 = barcode3;
	}

	public String getLabelName() {
		return labelName;
	}

	public void setLabelName(String labelName) {
		this.labelName = labelName;
	}

	public Tax getBuyTax1() {
		return buyTax1;
	}

	public void setBuyTax1(Tax buyTax1) {
		this.buyTax1 = buyTax1;
	}

	public Tax getBuyTax2() {
		return buyTax2;
	}

	public void setBuyTax2(Tax buyTax2) {
		this.buyTax2 = buyTax2;
	}

	public Tax getBuyTax3() {
		return buyTax3;
	}

	public void setBuyTax3(Tax buyTax3) {
		this.buyTax3 = buyTax3;
	}

	public Tax getBuyTax4() {
		return buyTax4;
	}

	public void setBuyTax4(Tax buyTax4) {
		this.buyTax4 = buyTax4;
	}

	public Tax getBuyTax5() {
		return buyTax5;
	}

	public void setBuyTax5(Tax buyTax5) {
		this.buyTax5 = buyTax5;
	}

	public Tax getSellTax1() {
		return sellTax1;
	}

	public void setSellTax1(Tax sellTax1) {
		this.sellTax1 = sellTax1;
	}

	public Tax getSellTax2() {
		return sellTax2;
	}

	public void setSellTax2(Tax sellTax2) {
		this.sellTax2 = sellTax2;
	}

	public Tax getSellTax3() {
		return sellTax3;
	}

	public void setSellTax3(Tax sellTax3) {
		this.sellTax3 = sellTax3;
	}

	public Tax getSellTax4() {
		return sellTax4;
	}

	public void setSellTax4(Tax sellTax4) {
		this.sellTax4 = sellTax4;
	}

	public Tax getSellTax5() {
		return sellTax5;
	}

	public void setSellTax5(Tax sellTax5) {
		this.sellTax5 = sellTax5;
	}

	public Long getProdId() {
		return prodId;
	}

	public void setProdId(Long prodId) {
		this.prodId = prodId;
	}

	public BigDecimal getNetPriceValue() {
		return netPriceValue;
	}

	public void setNetPriceValue(BigDecimal netPriceValue) {
		this.netPriceValue = netPriceValue;
	}

	public String getNetPriceCurrency() {
		return netPriceCurrency;
	}

	public void setNetPriceCurrency(String netPriceCurrency) {
		this.netPriceCurrency = netPriceCurrency;
	}

	public String getOwnerCode() {
		return ownerCode;
	}

	public void setOwnerCode(String ownerCode) {
		this.ownerCode = ownerCode;
	}

	public Date getOwnerBeginDate() {
		return ownerBeginDate;
	}

	public void setOwnerBeginDate(Date ownerBeginDate) {
		this.ownerBeginDate = ownerBeginDate;
	}

	public Date getOwnerEndDate() {
		return ownerEndDate;
	}

	public void setOwnerEndDate(Date ownerEndDate) {
		this.ownerEndDate = ownerEndDate;
	}

	public Boolean getOwnerIsActive() {
		return ownerIsActive;
	}

	public void setOwnerIsActive(Boolean ownerIsActive) {
		this.ownerIsActive = ownerIsActive;
	}

	public String getPriceListName() {
		return priceListName;
	}

	public void setPriceListName(String priceListName) {
		this.priceListName = priceListName;
	}

	public Product getProduct() {
		return product;
	}

	public void setProduct(Product product) {
		this.product = product;
	}

	public BigDecimal getGrossPriceValue() {
		return grossPriceValue;
	}

	public void setGrossPriceValue(BigDecimal grossPriceValue) {
		this.grossPriceValue = grossPriceValue;
	}

	public String getGrossPriceCurrency() {
		return grossPriceCurrency;
	}

	public void setGrossPriceCurrency(String grossPriceCurrency) {
		this.grossPriceCurrency = grossPriceCurrency;
	}

	public String getWorkBunchCode() {
		return workBunchCode;
	}

	public void setWorkBunchCode(String workBunchCode) {
		this.workBunchCode = workBunchCode;
	}

	public WorkBunch getWorkBunch() {
		return workBunch;
	}

	public void setWorkBunch(WorkBunch workBunch) {
		this.workBunch = workBunch;
	}

}
