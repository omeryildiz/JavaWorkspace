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

import java.io.Serializable;
import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;

import javax.persistence.AttributeOverride;
import javax.persistence.AttributeOverrides;
import javax.persistence.CascadeType;
import javax.persistence.Column;
import javax.persistence.Embedded;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.OneToMany;
import javax.persistence.Table;
import javax.persistence.Transient;

import org.hibernate.annotations.Cascade;
import org.hibernate.annotations.ForeignKey;
import org.hibernate.validator.Valid;

/**
 * Ödeme sözleşmesi detay bilgilerini tutar.
 * @author "sinan.yumak"
 *
 */
@Entity
@Table(name="PAYMENT_CONTRACT_PERIOD")
public class PaymentContractPeriod implements Serializable {
	private static final long serialVersionUID = 1L;

	@Id
    @GeneratedValue(strategy = GenerationType.TABLE,generator="genericSeq")
    @Column(name="ID")
	private Long id;

	@Column(name="PERIOD")
	private int period = 0;
	
	@ManyToOne
	@JoinColumn(name="OWNER_ID")
	@ForeignKey(name="FK_PAYMENTCONTRACTDETAIL_OWNERID")
	private PaymentContract owner;
	
    /**
     * false : tek seferde, peşin
     * true : vadeli
     */
	@Column(name="COLLECTION_TYPE")
    private Boolean collectionType = false;
    
    /**
     * false : islem tarihinden sonra ilave gun
     * true : islem tarihinden sonra belirli sabit bir gun
     */
	@Column(name="BLOCKING_TYPE")
    private Boolean blockingType = false;
    
	@Column(name="BLOCKING_DAY")
	private int blockingDay = 0;
	
    /**
     * false : blokaj tarihinden sonra ilave gun
     * true : blokaj tarihinden sonra belirli sabit bir gun
     */
	@Column(name="MATURITY_TYPE")
    private Boolean maturityType = false;

	@Column(name="MATURITY_DAY")
	private int maturityDay = 0;

	@Column(name="PAYMENT_DAY")
	private String paymentDay = "Mon;Tue;Wed;Thu;Fri;";
	
    @Column(name="IS_CAMPAIN")
	private Boolean campain = Boolean.FALSE;
    
    @Embedded
    @Valid
    @AttributeOverrides( {
        @AttributeOverride(name="currency", column=@Column(name="ADDING_AMOUNT_CCY")),
        @AttributeOverride(name="value",    column=@Column(name="ADDING_AMOUNT_VALUE")),
        @AttributeOverride(name="localAmount", column=@Column(name="ADDING_AMOUNT_LCYVAL"))
    })
    private MoneySet addingAmount = new MoneySet();
	
    @Column(name="ROUNDING_BASE", precision=10, scale=4)
    private BigDecimal roundingBase = BigDecimal.ZERO;
    
	@OneToMany(mappedBy="owner", cascade=CascadeType.ALL)
    @Cascade(org.hibernate.annotations.CascadeType.DELETE_ORPHAN)
    private List<PaymentContractCommision> commisionList = new ArrayList<PaymentContractCommision>();

	@Transient
	private boolean firstDay = true;

	@Transient
	private boolean secondDay = true;
	
	@Transient
	private boolean thirdDay = true;
	
	@Transient
	private boolean fourthDay = true;
	
	@Transient
	private boolean fifthDay = true;
	
	@Transient
	private boolean sixthDay = true;
	
	@Transient
	private boolean seventhDay = true;
	
	public List<Boolean> paymentDaysAsList() {
		List<Boolean> result = new ArrayList<Boolean>();

		//pazardan başlayarak...
		result.add(seventhDay);
		result.add(firstDay);
		result.add(secondDay);
		result.add(thirdDay);
		result.add(fourthDay);
		result.add(fifthDay);
		result.add(sixthDay);
		return result;
	}
	
    @Override
    public int hashCode() {
        int hash = 0;
        hash += (this.getId() != null ? this.getId().hashCode() : 0);
        return hash;
    }

    @Override
    public boolean equals(Object object) {
        // TODO: Warning - this method won't work in the case the id fields are not set
        if (!(object instanceof PaymentContractPeriod)) {
            return false;
        }
        PaymentContractPeriod other = (PaymentContractPeriod)object;
        if (this.getId() != other.getId() && (this.getId() == null || !this.getId().equals(other.getId()))) return false;
        return true;
    }

    @Override
    public String toString() {
        return "com.ut.tekir.entities.PaymentContractPeriod[id=" + getId() + "]";
    }

	public Long getId() {
		return id;
	}

	public void setId(Long id) {
		this.id = id;
	}

	public int getPeriod() {
		return period;
	}

	public void setPeriod(int period) {
		this.period = period;
	}

	public PaymentContract getOwner() {
		return owner;
	}

	public void setOwner(PaymentContract owner) {
		this.owner = owner;
	}

	public Boolean getCollectionType() {
		return collectionType;
	}

	public void setCollectionType(Boolean collectionType) {
		this.collectionType = collectionType;
	}

	public Boolean getBlockingType() {
		return blockingType;
	}

	public void setBlockingType(Boolean blockingType) {
		this.blockingType = blockingType;
	}

	public int getBlockingDay() {
		return blockingDay;
	}

	public void setBlockingDay(int blockingDay) {
		this.blockingDay = blockingDay;
	}

	public Boolean getMaturityType() {
		return maturityType;
	}

	public void setMaturityType(Boolean maturityType) {
		this.maturityType = maturityType;
	}

	public int getMaturityDay() {
		return maturityDay;
	}

	public void setMaturityDay(int maturityDay) {
		this.maturityDay = maturityDay;
	}

	public String getPaymentDay() {
		return paymentDay;
	}

	public void setPaymentDay(String paymentDay) {
		this.paymentDay = paymentDay;
	}

	public Boolean getCampain() {
		return campain;
	}

	public void setCampain(Boolean campain) {
		this.campain = campain;
	}

	public MoneySet getAddingAmount() {
		return addingAmount;
	}

	public void setAddingAmount(MoneySet addingAmount) {
		this.addingAmount = addingAmount;
	}

	public BigDecimal getRoundingBase() {
		return roundingBase;
	}

	public void setRoundingBase(BigDecimal roundingBase) {
		this.roundingBase = roundingBase;
	}

	public List<PaymentContractCommision> getCommisionList() {
		return commisionList;
	}

	public void setCommisionList(List<PaymentContractCommision> commisionList) {
		this.commisionList = commisionList;
	}

	public boolean isFirstDay() {
		return firstDay;
	}

	public void setFirstDay(boolean firstDay) {
		this.firstDay = firstDay;
	}

	public boolean isSecondDay() {
		return secondDay;
	}

	public void setSecondDay(boolean secondDay) {
		this.secondDay = secondDay;
	}

	public boolean isThirdDay() {
		return thirdDay;
	}

	public void setThirdDay(boolean thirdDay) {
		this.thirdDay = thirdDay;
	}

	public boolean isFourthDay() {
		return fourthDay;
	}

	public void setFourthDay(boolean fourthDay) {
		this.fourthDay = fourthDay;
	}

	public boolean isFifthDay() {
		return fifthDay;
	}

	public void setFifthDay(boolean fifthDay) {
		this.fifthDay = fifthDay;
	}

	public boolean isSixthDay() {
		return sixthDay;
	}

	public void setSixthDay(boolean sixthDay) {
		this.sixthDay = sixthDay;
	}

	public boolean isSeventhDay() {
		return seventhDay;
	}

	public void setSeventhDay(boolean seventhDay) {
		this.seventhDay = seventhDay;
	}

}
