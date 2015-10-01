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

import javax.persistence.AttributeOverride;
import javax.persistence.AttributeOverrides;
import javax.persistence.Column;
import javax.persistence.Embedded;
import javax.persistence.EnumType;
import javax.persistence.Enumerated;
import javax.persistence.MappedSuperclass;

import org.hibernate.validator.Valid;

/**
 * Ortak olarak kullanılan vergi alanlarını tutan model sınıfıdır.
 * @author sinan.yumak
 *
 */
@MappedSuperclass
public class TaxBase implements Serializable {

	private static final long serialVersionUID = 1L;

	@Column(name="TYPE")
    @Enumerated(EnumType.ORDINAL)
    private TaxType type;

    /**
     * Verginin yüzde mi değer mi kesir mi olduğu...
     */
    @Column(name="KIND")
	@Enumerated(EnumType.ORDINAL)
    private TaxKind kind = TaxKind.Rate;

    /**
     * Buradaki rate ve amount sadece vergilerin aynı
     * olup olmadığını kontrol etmek için tutulacak.
     */
    @Column(name="SOURCE_RATE",precision=10,scale=2)
    private BigDecimal sourceRate = BigDecimal.ZERO;

    // vergi tutar olarak tutulacaksa kullanılacak alan..
    @Embedded
    @Valid
    @AttributeOverrides({
        @AttributeOverride(name="value", column=@Column(name="SOURCE_VAL")),
        @AttributeOverride(name="currency", column=@Column(name="SOURCE_CCY")),
        @AttributeOverride(name="localAmount", column=@Column(name="SOURCE_LCYVAL"))
    })
    private MoneySet sourceAmount = new MoneySet();

    /**
     * Matrah bilgisidir.
     */
    @Embedded
    @Valid
    @AttributeOverrides({
        @AttributeOverride(name="value", column=@Column(name="BASE_VAL")),
        @AttributeOverride(name="currency", column=@Column(name="BASE_CCY")),
        @AttributeOverride(name="localAmount", column=@Column(name="BASE_LCYVAL"))
    })
    private MoneySet base = new MoneySet();

    /**
     * Vergi tutarı bilgisidir.
     */
    @Embedded
    @AttributeOverrides( {
        @AttributeOverride(name="currency", column=@Column(name="CCY")),
        @AttributeOverride(name="value",    column=@Column(name="CCYVAL")),
        @AttributeOverride(name="localAmount", column=@Column(name="LCYVAL"))
    })
    private MoneySet amount = new MoneySet();

	public TaxType getType() {
		return type;
	}

	public void setType(TaxType type) {
		this.type = type;
	}

	public TaxKind getKind() {
		return kind;
	}

	public void setKind(TaxKind kind) {
		this.kind = kind;
	}

	public MoneySet getBase() {
		return base;
	}

	public void setBase(MoneySet base) {
		this.base = base;
	}

	public MoneySet getAmount() {
		return amount;
	}

	public void setAmount(MoneySet amount) {
		this.amount = amount;
	}

	public BigDecimal getSourceRate() {
		return sourceRate;
	}

	public void setSourceRate(BigDecimal sourceRate) {
		this.sourceRate = sourceRate;
	}

	public MoneySet getSourceAmount() {
		return sourceAmount;
	}

	public void setSourceAmount(MoneySet sourceAmount) {
		this.sourceAmount = sourceAmount;
	}

}
