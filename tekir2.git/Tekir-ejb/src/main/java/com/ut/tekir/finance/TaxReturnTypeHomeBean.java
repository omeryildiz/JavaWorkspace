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

package com.ut.tekir.finance;

import java.util.List;

import javax.ejb.Stateful;

import org.jboss.seam.ScopeType;
import org.jboss.seam.annotations.Begin;
import org.jboss.seam.annotations.Create;
import org.jboss.seam.annotations.Factory;
import org.jboss.seam.annotations.FlushModeType;
import org.jboss.seam.annotations.In;
import org.jboss.seam.annotations.Name;
import org.jboss.seam.annotations.Out;
import org.jboss.seam.annotations.Scope;
import org.jboss.seam.annotations.datamodel.DataModel;
import org.jboss.seam.core.Conversation;


import com.ut.tekir.entities.DocumentType;
import com.ut.tekir.entities.TaxReturnType;
import com.ut.tekir.framework.BaseConsts;
import com.ut.tekir.framework.EntityHome;
import com.ut.tekir.general.GeneralSuggestion;
import com.ut.tekir.util.Utils;

/**
 * @author rustem
 *
 */
@Stateful
@Name("taxReturnTypeHome")
@Scope(ScopeType.CONVERSATION)
public class TaxReturnTypeHomeBean extends EntityHome<TaxReturnType> implements TaxReturnTypeHome<TaxReturnType> {

	@In
	GeneralSuggestion generalSuggestion;
	
	@DataModel("taxReturnTypeList")
    private List<TaxReturnType> entityList;

	private DocumentType[] documentTypeArray;
	
    @Create
	@Begin(join=true,flushMode= FlushModeType.MANUAL)
    public void init() {
    }

    public DocumentType[] getDocumentTypeArray() {
		return documentTypeArray;
	}

	public void setDocumentTypeArray(DocumentType[] documentTypeArray) {
		this.documentTypeArray = documentTypeArray;
	}

	@SuppressWarnings("unchecked")
	@Factory("taxReturnTypeList")
    public void initTaxReturnTypeList() {
        log.debug("Beyanname Listesi hazırlanıyor...");

        entityList = getEntityManager().createQuery("select c from TaxReturnType c")
                .getResultList();
    }
	
	@Override
	public void edit(TaxReturnType e) {
		manualFlush();
		super.edit(e);
		documentTypeArray = getEntity().getDocumentTypesAsArray();
	}

	@Override
	public String save() {
		String result = BaseConsts.SUCCESS;
		manualFlush();
		try {
			makeEntityValidations();

			getEntity().setDocumentTypesAsString(getDocumentTypeArray());
			result = super.save();
		} catch (Exception e) {
			facesMessages.add(Utils.getExceptionMessage(e));
			result = BaseConsts.FAIL;
		}
		return result;
	}

	@Override
	public String saveAndNew() {
		manualFlush();
		String result = BaseConsts.SUCCESS;
		try {
			makeEntityValidations();
			result = super.saveAndNew();
			documentTypeArray = new DocumentType[]{};
		} catch (Exception e) {
			facesMessages.add(Utils.getExceptionMessage(e));
			result = BaseConsts.FAIL;
		}
		return result;
	}

	private void makeEntityValidations() {
		if( getDocumentTypeArray().length == 0 ){
			throw new RuntimeException("Evrak tipi boş bırakılamaz. En az bir tane belge seçilmelidir.");
		}
	}
	
	@Out(required = false)
    public TaxReturnType getTaxReturnType() {
        return getEntity();
    }

    @In(required = false)
    public void setTaxReturnType(TaxReturnType taxReturnType) {
        setEntity(taxReturnType);
    }
    
    @Override
    public void createNew() {
        entity = new TaxReturnType();
        entity.setActive(true);
    }

    @Override
    public List<TaxReturnType> getEntityList() {
        return entityList;
    }

    public void setEntityList(List<TaxReturnType> entityList) {
        this.entityList = entityList;
    }

	public GeneralSuggestion getGeneralSuggestion() {
		return generalSuggestion;
	}

	public void setGeneralSuggestion(GeneralSuggestion generalSuggestion) {
		this.generalSuggestion = generalSuggestion;
	}

	public void manualFlush() {
        Conversation.instance().changeFlushMode(org.jboss.seam.annotations.FlushModeType.MANUAL);
    }

}
