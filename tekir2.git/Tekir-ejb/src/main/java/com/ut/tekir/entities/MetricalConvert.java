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

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.Table;

/**
 * @author bilga
 *
 */
@Entity
@Table(name="METRICAL_CONVERT")
public class MetricalConvert implements Serializable {
	
    private static final long serialVersionUID = 1L;

    @Id
    @GeneratedValue(strategy = GenerationType.TABLE,generator="genericSeq")
    @Column(name="ID")
    private Long id;
    
    @Column(name="CHANGE_UNIT")
    private String changeUnit;
 
    @Column(name="CHANGE_UNIT_VALUE")
    private BigDecimal changeUnitValue;
    
    @Column(name="MAIN_UNIT")
    private String mainUnit;
    
    @Column(name="MAIN_UNIT_VALUE")
    private BigDecimal mainUnitValue;
    
    @Column(name="METRICAL_TYPE")
    private Integer metricalType;
    
    @Override
    public int hashCode() {
        int hash = 0;
        hash += (this.getId() != null ? this.getId().hashCode() : 0);
        return hash;
    }

    @Override
    public boolean equals(Object object) {
        // TODO: Warning - this method won't work in the case the id fields are not set
        if (!(object instanceof MetricalConvert)) {
            return false;
        }
        MetricalConvert other = (MetricalConvert)object;
        if (this.getId() != other.getId() && (this.getId() == null || !this.id.equals(other.id))) return false;
        return true;
    }

    @Override
    public String toString() {
        return getChangeUnit();
    }

	public void setId(Long id) {
		this.id = id;
	}

	public Long getId() {
		return id;
	}

	public void setMainUnit(String mainUnit) {
		this.mainUnit = mainUnit;
	}

	public String getMainUnit() {
		return mainUnit;
	}

	public void setChangeUnit(String changeUnit) {
		this.changeUnit = changeUnit;
	}

	public String getChangeUnit() {
		return changeUnit;
	}

	public void setChangeUnitValue(BigDecimal changeUnitValue) {
		this.changeUnitValue = changeUnitValue;
	}

	public BigDecimal getChangeUnitValue() {
		return changeUnitValue;
	}

	public void setMainUnitValue(BigDecimal mainUnitValue) {
		this.mainUnitValue = mainUnitValue;
	}

	public BigDecimal getMainUnitValue() {
		return mainUnitValue;
	}

	public void setMetricalType(Integer metricalType) {
		this.metricalType = metricalType;
	}

	public Integer getMetricalType() {
		return metricalType;
	}

}
