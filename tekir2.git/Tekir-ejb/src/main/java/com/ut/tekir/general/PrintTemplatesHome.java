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

import java.util.List;

import javax.ejb.Local;

import com.ut.tekir.framework.IEntityHome;

/**
 * @author cenk.canarslan
 *
 * @param <PrintTemplates>
 */
@Local
public interface PrintTemplatesHome<PrintTemplates> extends IEntityHome<PrintTemplates> {
	void initPrintTemplateList();
	
	PrintTemplates getPrintTemplates();
	void setPrintTemplates(PrintTemplates printTemplates);
	List<String> getTemplateFilez();
}
