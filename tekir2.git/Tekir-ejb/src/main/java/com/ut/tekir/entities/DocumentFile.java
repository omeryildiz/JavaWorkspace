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
import java.util.Date;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.EnumType;
import javax.persistence.Enumerated;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.Table;
import javax.persistence.Temporal;
import javax.persistence.TemporalType;


/**
 *
 * @author haky
 */
@Entity
@Table(name="DOCUMENT_FILE")
public class DocumentFile implements  Serializable {
    
	private static final long serialVersionUID = 1L;

	@Id 
	@GeneratedValue(strategy = GenerationType.TABLE,generator="genericSeq")
    private Long id = 0L;

    @ManyToOne
	@JoinColumn(name="CONTACT_ID")
	private Contact contact;

    @Column(name="NAME")
    private String name;
    
    @Column(name="UPDATE_DATE")
    @Temporal(value = TemporalType.DATE)
    private Date updateDate;
    
    @Column(name="TITLE")
    private String title;
    
    @Column(name="DOC_TYPE")
    @Enumerated(EnumType.ORDINAL)
    private DocumentFileType docType;
    
    @Column(name="FORMAT")
    private String format; // Upload sırasında MIME gelecek. 
    
    @Column(name="INFO")
    private String info;
    
    @Column(name="USER") // dosyayı upload eden user
    private String user;
    
    @Column(name="URL")
    private String url;
    
    @Column(name="ACTIVE")
    private Boolean active;

    @Column(name="FILE_SIZE")
    private Integer fileSize;
    
    @Column(name="DOC_QUANTITY")
    private Integer quantity;

    @Column(name="OWNER_TYPE")
    @Enumerated(EnumType.ORDINAL)
    private OwnerType ownerType;
    
    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }
    
    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getFormat() {
        return format;
    }

    public void setFormat(String format) {
        this.format = format;
    }

    public String getInfo() {
        return info;
    }

    public void setInfo(String info) {
        this.info = info;
    }

    public String getUrl() {
        return url;
    }

    public void setUrl(String url) {
        this.url = url;
    }

    public Boolean getActive() {
        return active;
    }

    public void setActive(Boolean active) {
        this.active = active;
    }
    
    public Date getUpdateDate() {
        return updateDate;
    }

    public void setUpdateDate(Date updateDate) {
        this.updateDate = updateDate;
    }

    public Integer getFileSize() {
        return fileSize;
    }

    public void setFileSize(Integer fileSize) {
        this.fileSize = fileSize;
    }

    /**
     * @return the quantity
     */
    public Integer getQuantity() {
        return quantity;
    }

    /**
     * @param quantity the quantity to set
     */
    public void setQuantity(Integer quantity) {
        this.quantity = quantity;
    }

    /**
     * @return the docType
     */
    public DocumentFileType getDocType() {
        return docType;
    }

    /**
     * @param docType the docType to set
     */
    public void setDocType(DocumentFileType docType) {
        this.docType = docType;
    }
       /**
     * Returns a hash code value for the object.  This implementation computes 
     * a hash code value based on the id fields in this object.
     * @return a hash code value for this object.
     */
    @Override
    public int hashCode() {
        int hash = 0;
        hash += (this.getId() != null ? this.getId().hashCode() : 0);
        return hash;
    }

    /**
     * Determines whether another object is equal to this Contact.  The result is 
     * <code>true</code> if and only if the argument is not null and is a Customer object that 
     * has the same id field values as this object.
     * @param object the reference object with which to compare
     * @return <code>true</code> if this object is the same as the argument;
     * <code>false</code> otherwise.
     */
    @Override
    public boolean equals(Object object) {
        // TODO: Warning - this method won't work in the case the id fields are not set
        if (!(object instanceof DocumentFile)) {
            return false;
        }
        DocumentFile other = (DocumentFile)object;
        if (this.getId() != other.getId() && (this.getId() == null || !this.id.equals(other.id))) return false;
        return true;
    }

    /**
     * Returns a string representation of the object.  This implementation constructs 
     * that representation based on the id fields.
     * @return a string representation of the object.
     */
    @Override
    public String toString() {
        return "com.ut.ttdoc.entities.Document[id=" + getId() + "]";
    }

    /**
     * @return the contact
     */
    public Contact getContact() {
        return contact;
    }

    /**
     * @param contact the contact to set
     */
    public void setContact(Contact contact) {
        this.contact = contact;
    }

    public OwnerType getOwnerType() {
        return ownerType;
    }

    public void setOwnerType(OwnerType ownerType) {
        this.ownerType = ownerType;
    }

	public String getUser() {
		return user;
	}

	public void setUser(String user) {
		this.user = user;
	}
}
