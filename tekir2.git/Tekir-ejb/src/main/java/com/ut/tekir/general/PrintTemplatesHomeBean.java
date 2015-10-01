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

package com.ut.tekir.general;

import java.io.File;
import java.io.FilenameFilter;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import javax.ejb.Stateful;

import org.jboss.seam.ScopeType;
import org.jboss.seam.annotations.Factory;
import org.jboss.seam.annotations.In;
import org.jboss.seam.annotations.Name;
import org.jboss.seam.annotations.Out;
import org.jboss.seam.annotations.Scope;
import org.jboss.seam.annotations.datamodel.DataModel;

import com.ut.tekir.entities.DocumentType;
import com.ut.tekir.entities.PrintTemplates;
import com.ut.tekir.framework.BaseConsts;
import com.ut.tekir.framework.EntityHome;
import com.ut.tekir.framework.SystemProperties;
import com.ut.tekir.report.ReportParameters;

/**
 * @author cenk.canarslan
 *
 */
@Stateful
@Name("printTemplatesHome")
@Scope(value = ScopeType.CONVERSATION)
public class PrintTemplatesHomeBean extends EntityHome<PrintTemplates> implements PrintTemplatesHome<PrintTemplates> {

	@DataModel("printTemplateList")
    private List<PrintTemplates> entityList;
	
	@In
	SystemProperties systemProperties;
	private static final String suffix = ".jrxml";
	boolean err = false;
	
	public List<String> getTemplateFilez(){
		List<String> fileList = new ArrayList<String>();
		for (String file : getTemplateDir().list(getJrxmlFilter())) {
			fileList.add( file.replace(suffix, "") );
		}
		Collections.sort(fileList);
		return fileList;
	}

	private File getTemplateDir() {
		return new File(ReportParameters.getTemplateDirPath());
	}
	
	private FilenameFilter getJrxmlFilter() {
		return new FilenameFilter() {
			@Override
			public boolean accept(File dir, String name) {
				return name.endsWith(suffix);// && name.contains("fatura");
			}
		};
	}
	
    @SuppressWarnings("unchecked")
	@Factory("printTemplateList")
    public void initPrintTemplateList() {
        log.debug("PrintTemplate Listesi hazırlanıyor...");

        entityList = getEntityManager().createQuery("select p from PrintTemplates p") //.setMaxResults(100)
                .getResultList();
    }
    
    @Override
	public String save() {
   		checkVariousFields();
   		if (err){
   			return BaseConsts.FAIL;
   		}
   		if (entity.getDefaultTemplate()){
   			resetDefaultStatuses();
   		}
    	return super.save();
	}

	@Override
	public String saveAndNew() {
   		checkVariousFields();
   		if (err){
   			return BaseConsts.FAIL;
   		}
   		if (entity.getDefaultTemplate()){
   			resetDefaultStatuses();
   		}
		return super.saveAndNew();
	}

	private void resetDefaultStatuses() {
		entityManager.createQuery("update PrintTemplates pt " +
									"set pt.defaultTemplate = :defTempl " +
									"where pt.documentType = :docType " +
									"and pt.code <> :code")
									.setParameter("defTempl", false)
									.setParameter("docType", entity.getDocumentType())
									.setParameter("code", entity.getCode())
									.executeUpdate();
	}

	private void checkVariousFields() {
		if (entity.getDocumentType().equals(DocumentType.Unknown)){
            facesMessages.add("#{messages['printTemplates.message.chooseDoc']}");
            err = true;
    	}
   		if ("Unknown".equals(entity.getTemplateName())){
            facesMessages.add("#{messages['printTemplates.message.chooseTempl']}");
            err = true;
   		}
	}

	@Out(required = false)
	public PrintTemplates getPrintTemplates() {
		return getEntity();
	}

    @In(required = false)
	public void setPrintTemplates(PrintTemplates printTemplates) {
		setEntity(printTemplates);
	}

	@Override
	public void createNew() {
		entity = new PrintTemplates();
		entity.setActive(true);
	}

	@Override
	public List<PrintTemplates> getEntityList() {
		return entityList;
	}

	public void setEntityList(List<PrintTemplates> entityList) {
		this.entityList = entityList;
	}

}
