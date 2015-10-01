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

import org.hibernate.validator.Length;
import org.hibernate.validator.NotNull;

/**
 * @author cenk.canarslan
 *
 */
@Entity
@Table(name="PRINT_TEMPLATES")
public class PrintTemplates implements Serializable{

	private static final long serialVersionUID = 1L;

    @Id
    @GeneratedValue(strategy = GenerationType.TABLE,generator="genericSeq")
    @Column(name="ID")
    private Long id;

    @Column(name="CODE", nullable=false, unique=true, length=3)
    @NotNull
    @Length(max=3, min=1)
    private String code;

    @Column(name="NAME", length=30)
    @Length(max=30)
    private String name;

    @Column(name="TEMPLATE_NAME", length=100)
    @Length(max=100)
    private String templateName;

    @Column(name="INFO")
    private String info;

    @Column(name="DOC_TYPE")
    @Enumerated(EnumType.ORDINAL)
    private DocumentType documentType;

	@ManyToOne
    @JoinColumn(name="ORGANIZATION_ID")
    private Organization organization;

    @Column(name="ISACTIVE")
    private Boolean active = Boolean.TRUE;

    @Column(name="ISDEFAULT")
    private Boolean defaultTemplate = Boolean.FALSE;

	public Boolean getDefaultTemplate() {
		return defaultTemplate;
	}

	public void setDefaultTemplate(Boolean defaultTemplate) {
		this.defaultTemplate = defaultTemplate;
	}

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

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public String getInfo() {
		return info;
	}

	public void setInfo(String info) {
		this.info = info;
	}

	public Organization getOrganization() {
		return organization;
	}

	public void setOrganization(Organization organization) {
		this.organization = organization;
	}

	public Boolean getActive() {
		return active;
	}

	public void setActive(Boolean active) {
		this.active = active;
	}

	@Override
	public int hashCode() {
        int hash = 0;
        hash += (this.id != null ? this.id.hashCode() : 0);
        return hash;
	}

	@Override
	public String toString() {
        return "com.ut.tekir.entities.PrintTemplates[id=" + id + "]";
	}
    
	@Override
	public boolean equals(Object obj) {
        // TODO: Warning - this method won't work in the case the id fields are not set
        if (!(obj instanceof PrintTemplates)) {
            return false;
        }
        PrintTemplates other = (PrintTemplates)obj;
        if (this.id != other.id && (this.id == null || !this.id.equals(other.id))) return false;
        return true;
	}

	public void setTemplateName(String templateName) {
		this.templateName = templateName;
	}

	public String getTemplateName() {
		return templateName;
	}

    public DocumentType getDocumentType() {
		return documentType;
	}

	public void setDocumentType(DocumentType documentType) {
		this.documentType = documentType;
	}

	
}
