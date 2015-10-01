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
import java.util.ArrayList;
import java.util.List;

import javax.persistence.CascadeType;
import javax.persistence.Column;
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

/**
 * Çek kasa ödeme Bordrosu
 * 
 * @author sinan.yumak
 */
@Entity
@Table(name="CHEQUE_TO_ACC_PAY_PAYROLL")
public class ChequeToAccountPaymentPayroll extends DocumentBase implements Serializable {
	    
	private static final long serialVersionUID = 1L;

	@Id
    @GeneratedValue(strategy = GenerationType.TABLE,generator="genericSeq")
    @Column(name="ID")
	private Long id;
    
	@ManyToOne
	@JoinColumn(name="ACCOUNT_ID")
	private Account account;
	
    @OneToMany(mappedBy="owner", cascade=CascadeType.ALL)
    @Cascade(org.hibernate.annotations.CascadeType.DELETE_ORPHAN)
    private List<ChequeToAccountPaymentPayrollDetail> details = new ArrayList<ChequeToAccountPaymentPayrollDetail>();

	@Override
	public DocumentType getDocumentType() {
		return DocumentType.ChequeAccountPaymentPayroll;
	}

    public Long getId() {
		return id;
	}

	public void setId(Long id) {
		this.id = id;
	}

	public List<ChequeToAccountPaymentPayrollDetail> getDetails() {
		return details;
	}

	public void setDetails(List<ChequeToAccountPaymentPayrollDetail> details) {
		this.details = details;
	}
    
    public Account getAccount() {
		return account;
	}

	public void setAccount(Account account) {
		this.account = account;
	}

	@Transient
    public DocumentTotal getTotals(){
        DocumentTotal tot = new DocumentTotal();

        for( ChequeToAccountPaymentPayrollDetail item : getDetails() ){
        	tot.add( new MoneySet(item.getCheque().getMoney()));
        }

        return tot;
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
        if (!(object instanceof ChequeToAccountPaymentPayroll)) {
            return false;
        }
        ChequeToAccountPaymentPayroll other = (ChequeToAccountPaymentPayroll)object;
        if (this.getId() != other.getId() && (this.getId() == null || !this.getId().equals(other.getId()))) return false;
        return true;
    }

    @Override
    public String toString() {
        return "com.ut.tekir.entities.ChequeToAccountPaymentPayroll[id=" + getId() + "]";
    }
    
}
