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

package com.ut.tekir.entities;

import java.math.BigDecimal;
import java.util.Date;

import javax.persistence.AttributeOverride;
import javax.persistence.AttributeOverrides;
import javax.persistence.Column;
import javax.persistence.Embedded;
import javax.persistence.Entity;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.Table;
import javax.persistence.Temporal;
import javax.persistence.TemporalType;

import org.hibernate.annotations.ForeignKey;
import org.hibernate.validator.Valid;

/**
 * Kurum ile yapılan işlemler için transaction tablosu.
 * @author haky
 */
@Entity
@Table(name="FOUNDATION_TXN")
public class FoundationTxn extends TxnBase {
	private static final long serialVersionUID = 1L;

	/**
     * işlemin yapıldığı kurum bilgisidir.
     */
    @ManyToOne
    @JoinColumn(name="FOUNDATION_ID")
    @ForeignKey(name="FK_FOUNDATIONTXN_FOUNDATIONID")
    private Foundation foundation;
    
	/**
	 * komisyon oran bilgisini yüzde olarak tutar.
	 */
	@Column(name="RATE")
	private Double rate = 0.0d;
	
	/**
	 * komisyon valör bilgisini gün olarak tutar.
	 */
	@Column(name="VALOR")
	private Integer valor = 0;

    //siparis de alinan kaparo tutarlarinin takibi icin
    @Column(name="TRADEIN")
    private Boolean tradein = Boolean.FALSE;

    @Column(name="MATURITY_DATE")
    @Temporal(value=TemporalType.DATE)
    private Date maturityDate;

    /**
     * Matrah bilgisidir.
     */
    @Embedded
    @Valid
    @AttributeOverrides( {
        @AttributeOverride(name="currency", column=@Column(name="BASE_AMOUNT_CCY")),
        @AttributeOverride(name="value",    column=@Column(name="BASE_AMOUNT_VALUE")),
        @AttributeOverride(name="localAmount", column=@Column(name="BASE_AMOUNT_LCYVAL"))
    })
    private MoneySet baseAmount = new MoneySet();

	@Column(name="DISCOUNT", precision=10, scale=2)
	private BigDecimal discount = BigDecimal.ZERO;
	
	@Column(name="ADDED", precision=10, scale=2)
	private BigDecimal added = BigDecimal.ZERO;
	
	@ManyToOne
	@JoinColumn(name="PAYMENT_ACTION_TYPE_ID")
	@ForeignKey(name="FK_FOUNDATIONTXN_PAYMENTACTIONTYPEID")
    private PaymentActionType paymentActionType;
    
	/**
	 * Satırı oluşturan masraf veya indirim bilgisini tutar.
	 */
	@ManyToOne
	@JoinColumn(name="PRODUCT_ID")
	@ForeignKey(name="FK_FOUNDATIONTXN_PRODUCTID")
	private Product product;
	
	@ManyToOne
    @JoinColumn(name="WORK_BUNCH_ID")
    @ForeignKey(name="FK_FOUNDATIONTXN_WORKBUNCHID")
	private WorkBunch workBunch;
	
    @Override
    public int hashCode() {
        int hash = 0;
        hash += (this.getId() != null ? this.getId().hashCode() : 0);
        return hash;
    }

    @Override
    public boolean equals(Object object) {
        // TODO: Warning - this method won't work in the case the id fields are not set
        if (!(object instanceof FoundationTxn)) {
            return false;
        }
        FoundationTxn other = (FoundationTxn)object;
        if (this.getId() != other.getId() && (this.getId() == null || !this.getId().equals(other.getId()))) return false;
        return true;
    }

    @Override
    public String toString() {
        return "com.ut.tekir.entities.FoundationTxn[id=" + getId() + "]";
    }
    
	public Double getRate() {
		return rate;
	}

	public void setRate(Double rate) {
		this.rate = rate;
	}

	public Integer getValor() {
		return valor;
	}

	public void setValor(Integer valor) {
		this.valor = valor;
	}

    public Boolean getTradein() {
        return tradein;
    }

    public void setTradein(Boolean tradein) {
        this.tradein = tradein;
    }

	public Date getMaturityDate() {
		return maturityDate;
	}

	public void setMaturityDate(Date maturityDate) {
		this.maturityDate = maturityDate;
	}

	public MoneySet getBaseAmount() {
		return baseAmount;
	}

	public void setBaseAmount(MoneySet baseAmount) {
		this.baseAmount = baseAmount;
	}

	public PaymentActionType getPaymentActionType() {
		return paymentActionType;
	}

	public void setPaymentActionType(PaymentActionType paymentActionType) {
		this.paymentActionType = paymentActionType;
	}

	public Foundation getFoundation() {
		return foundation;
	}

	public void setFoundation(Foundation foundation) {
		this.foundation = foundation;
	}

	public BigDecimal getDiscount() {
		return discount;
	}

	public void setDiscount(BigDecimal discount) {
		this.discount = discount;
	}

	public BigDecimal getAdded() {
		return added;
	}

	public void setAdded(BigDecimal added) {
		this.added = added;
	}

	public Product getProduct() {
		return product;
	}

	public void setProduct(Product product) {
		this.product = product;
	}

	public WorkBunch getWorkBunch() {
		return workBunch;
	}

	public void setWorkBunch(WorkBunch workBunch) {
		this.workBunch = workBunch;
	}

}
