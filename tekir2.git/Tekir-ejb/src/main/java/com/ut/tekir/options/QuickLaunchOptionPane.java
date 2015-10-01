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

package com.ut.tekir.options;

import java.util.List;

import javax.persistence.EntityManager;

import org.jboss.seam.annotations.In;
import org.jboss.seam.annotations.Logger;
import org.jboss.seam.annotations.Name;
import org.jboss.seam.annotations.Transactional;
import org.jboss.seam.core.Events;
import org.jboss.seam.faces.FacesMessages;
import org.jboss.seam.log.Log;

import com.ut.tekir.annotations.OptionPane;
import com.ut.tekir.entities.QuickLaunch;
import com.ut.tekir.framework.OptionManager;
import com.ut.tekir.util.Utils;

/**
 *
 *  Company Informations Option editor
 * 
 * @author sinan.yumak
 */
@Name("quickLaunchOptionPane")
@OptionPane(OptionPaneType.System)
public class QuickLaunchOptionPane extends AbstractOptionPane {
    @Logger
    protected Log log;
    @In(create=true)
    OptionManager optionManager;
    @In
    EntityManager entityManager;
    @In
    FacesMessages facesMessages;
    @In
    protected Events events;

    private List<QuickLaunch> optionList;

    @SuppressWarnings("unchecked")
	public List<QuickLaunch> getOptionList(){
    	if (optionList == null || optionList.size() == 0) {
    		optionList = (List<QuickLaunch>)entityManager.createQuery("select c from QuickLaunch c").getResultList();
    	}
    	return optionList;
    }

    @Transactional
    public void save(){
    	try {
    		for (QuickLaunch item : optionList) {
    			entityManager.merge(item);
    		}
    		entityManager.flush();
		} catch (Exception e) {
			facesMessages.add(Utils.getExceptionMessage(e));
			log.error("Kısayollar kaydedilirken hata meydana geldi. Sebebi #0", e);
		}
    }

    public String getName() {
        return "quickLaunchOptionPane";
    }

    public void cancel() {
        //TODO: editten önceki hale dönmeli...
    }

}
