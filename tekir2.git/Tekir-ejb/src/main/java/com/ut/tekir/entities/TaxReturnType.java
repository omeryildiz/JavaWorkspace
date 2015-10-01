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

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.Lob;
import javax.persistence.Table;

import org.hibernate.validator.Length;
import org.hibernate.validator.NotNull;

/**
 * @author rustem
 *
 *  Beyanname türlerinin tutulduğu alan
 *
 */
@Entity
@Table(name="TAX_RETURN_TYPE")
public class TaxReturnType implements Serializable {
	
	private static final long serialVersionUID = 1L;

	@Id
	@GeneratedValue(strategy=GenerationType.TABLE)
	@Column(name="ID")
	private Long id;
	
	@Column(name="CODE", length=20, nullable=false, unique=true)
	@NotNull
	@Length(min=1, max=20)
	private String code;
	
	@Column(name="NAME", length=30)
	private String name;
	
	@Column(name="INFO", length=200)
	private String info;
	
	@Column(name="ACTIVE")
	private Boolean active = Boolean.TRUE;
	
	@Column(name="DOCUMENT_TYPES",length=510)
	private String documentTypes = "";

	public DocumentType[] getDocumentTypesAsArray() {
		List<DocumentType> types = new ArrayList<DocumentType>();

		String[] splittedTypes = documentTypes.split(",");
		
		if (splittedTypes.length == 1 && splittedTypes[0].equals("")) return new DocumentType[]{};
		
		for (String item : splittedTypes) {
			types.add( DocumentType.fromString( Integer.valueOf(item) ) );
		}
		return types.toArray(new DocumentType[]{});
	}

	public List<DocumentType> getDocumentTypesAsList() {
		List<DocumentType> types = new ArrayList<DocumentType>();
		
		String[] splittedTypes = documentTypes.split(",");
		
		if (splittedTypes.length == 1 && splittedTypes[0].equals("")) return types;
		
		for (String item : splittedTypes) {
			types.add( DocumentType.fromString( Integer.valueOf(item) ) );
		}
		return types;
	}

	public void setDocumentTypesAsString(DocumentType[] typeList) {
		StringBuilder result = new StringBuilder();

		if (typeList.length != 0) {
			for (int i=0;i<typeList.length - 1;i++) {
				DocumentType aType = typeList[i];
				
				result.append(aType.ordinal())
				.append(",");
			}
			result.append(typeList[typeList.length-1].ordinal());
		}

		this.documentTypes = result.toString();
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

	public Boolean getActive() {
		return active;
	}

	public void setActive(Boolean active) {
		this.active = active;
	}

	public String getDocumentTypes() {
		return documentTypes;
	}

	public void setDocumentTypes(String documentTypes) {
		this.documentTypes = documentTypes;
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
        if (!(object instanceof TaxReturnType)) {
            return false;
        }
        TaxReturnType other = (TaxReturnType)object;
        if (this.id != other.id && (this.id == null || !this.id.equals(other.id))) return false;
        return true;
    }

    @Override
    public String toString() {
        return "com.ut.tekir.entities.TaxReturnType[id=" + id + "]";
    }


}
