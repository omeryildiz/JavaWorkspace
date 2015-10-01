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

import org.hibernate.annotations.Cascade;

@Entity
@Table(name="PORTF_TO_PORTF_TRANSFER")
public class PortfolioToPortfolioTransfer extends DocumentBase implements Serializable{

	private static final long serialVersionUID = 1L;
	
    @Id
    @GeneratedValue(strategy = GenerationType.TABLE,generator="genericSeq")
    @Column(name="ID")
    private Long id;
	
	@ManyToOne
	@JoinColumn(name="FROM_PORTFOLIO_ID")
	private Portfolio fromPortfolio;
	
	@ManyToOne
	@JoinColumn(name="TO_PORTFOLIO_ID")
	private Portfolio toPortfolio;
	
	@OneToMany(mappedBy = "owner", cascade=CascadeType.ALL )
    @Cascade(org.hibernate.annotations.CascadeType.DELETE_ORPHAN)
    private List<PortfolioToPortfolioTransferItem> items = new ArrayList<PortfolioToPortfolioTransferItem>();
	
    @Override
	public DocumentType getDocumentType() {
		return DocumentType.PorfolioToPortfolioTransfer;
	}

	public Long getId() {
		return id;
	}
	
	public void setId(Long id) {
		this.id = id;
	}

	public void setFromPortfolio(Portfolio fromPortfolio) {
		this.fromPortfolio = fromPortfolio;
	}

	public Portfolio getFromPortfolio() {
		return fromPortfolio;
	}

	public void setToPortfolio(Portfolio toPortfolio) {
		this.toPortfolio = toPortfolio;
	}

	public Portfolio getToPortfolio() {
		return toPortfolio;
	}

	public List<PortfolioToPortfolioTransferItem> getItems() {
        return items;
    }

    public void setItems(List<PortfolioToPortfolioTransferItem> items) {
        this.items = items;
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
        if (!(object instanceof PortfolioToPortfolioTransfer)) {
            return false;
        }
        PortfolioToPortfolioTransfer other = (PortfolioToPortfolioTransfer)object;
        if (this.id != other.id && (this.id == null || !this.id.equals(other.id))) return false;
        return true;
    }
	
	@Override
    public String toString() {
        return "com.ut.tekir.entities.PortfolioToPortfolioTransfer[id=" + id + "]";
    }

}
