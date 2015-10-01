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
import java.util.Date;
import java.util.List;

import javax.persistence.CascadeType;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.EnumType;
import javax.persistence.Enumerated;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.OneToMany;
import javax.persistence.Table;
import javax.persistence.Temporal;
import javax.persistence.TemporalType;
import javax.persistence.Transient;

import org.hibernate.annotations.Cascade;
import org.hibernate.validator.Length;
import org.hibernate.validator.NotNull;

@Entity
@Table(name="SECURITY")
public class Security implements Serializable {
	
	private static final long serialVersionUID = 1L;
	
    @Id
    @GeneratedValue(strategy = GenerationType.TABLE,generator="genericSeq")
    @Column(name="ID")
	private Long id;

    @Column(name="CODE", length=20, unique=true, nullable=false)
    @Length(max=20, min=1)
    @NotNull
	private String code;
	
    @Column(name="ISIN", length=12, unique=true)
    @Length(max=12)
	private String isin;
	
    @Column(name="INFO")
	private String info;
	
    @Column(name="ISSUE_DATE")
	@Temporal(value=TemporalType.DATE)
	private Date issueDate;
	
    @Column(name="MATURITY_DATE")
	@Temporal(value = TemporalType.DATE)
	private Date maturityDate;
	
    @Column(name="UNIT")
	private Integer unit;
	
    @Column(name="RATE", precision=10, scale=2)
	private BigDecimal rate;
	
    @Column(name="COUPON_COUNT")
	private Integer couponCount;
	
    @Column(name="SECURITY_TYPE")
	@Enumerated(EnumType.ORDINAL)
	private SecurityType securityType;
	
	@Column(name="CCY", length=3)
	@Length(max=3)
    private String currency;
    
    @Column(name="ISACTIVE")
    private Boolean active = Boolean.TRUE;
	
	@OneToMany(mappedBy="security", cascade =CascadeType.ALL)
	@Cascade(org.hibernate.annotations.CascadeType.DELETE_ORPHAN)
	private List<SecurityCoupon> coupons = new ArrayList<SecurityCoupon>();
	
	public Long getId() {
		return id;
	}
	
	public void setId(Long id) {
		this.id = id;
	}
	
	public String getCode() {
		return code;
	}
	
	public void setCode(String code) {
		this.code = code;
	}
	
	public String getIsin() {
		return isin;
	}
	
	public void setIsin(String isin) {
		this.isin = isin;
	}
	
	public String getInfo() {
		return info;
	}
	
	public void setInfo(String info) {
		this.info = info;
	}
	
	public Date getIssueDate() {
		return issueDate;
	}
	
	public void setIssueDate(Date issueDate) {
		this.issueDate = issueDate;
	}
	
	public Date getMaturityDate() {
		return maturityDate;
	}
	
	public void setMaturityDate(Date maturityDate) {
		this.maturityDate = maturityDate;
	}
	
	public Integer getUnit() {
		return unit;
	}
	
	public void setUnit(Integer unit) {
		this.unit = unit;
	}
	
	public BigDecimal getRate() {
		return rate;
	}
	
	public void setRate(BigDecimal rate) {
		this.rate = rate;
	}
	
	public Integer getCouponCount() {
		return couponCount;
	}
	
	public void setCouponCount(Integer couponCount) {
		this.couponCount = couponCount;
	}
	
	public SecurityType getSecurityType() {
		return securityType;
	}
	
	public void setSecurityType(SecurityType securityType) {
		this.securityType = securityType;
	}
	
	public String getCurrency() {
		return currency;
	}
	
	public void setCurrency(String currency) {
		this.currency = currency;
	}

	public Boolean getActive() {
		return active;
	}

	public void setActive(Boolean active) {
		this.active = active;
	}

	public List<SecurityCoupon> getCoupons() {
		return coupons;
	}

	public void setCoupons(List<SecurityCoupon> coupons) {
		this.coupons = coupons;
	}
	
    @Transient
    public String getCaption(){
        return "[" + getCode() + "] " + getIsin();
    }
	
	@Override
    public int hashCode() {
        int hash = 0;
        hash += (this.id != null ? this.id.hashCode() : 0);
        return hash;
    }
	
	@Override
    public boolean equals(Object object) {
        // TODO: Warning - this method won't work in the case the id fields are not set
        if (!(object instanceof Security)) {
            return false;
        }
        Security other = (Security)object;
        if (this.id != other.id && (this.id == null || !this.id.equals(other.id))) return false;
        return true;
    }
	
	@Override
    public String toString() {
        return "com.ut.tekir.entities.Security[id=" + id + "]";
    }

}
