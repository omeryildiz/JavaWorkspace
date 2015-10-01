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

import org.jboss.seam.annotations.In;
import org.jboss.seam.annotations.Logger;
import org.jboss.seam.annotations.Name;
import org.jboss.seam.annotations.TransactionPropagationType;
import org.jboss.seam.annotations.Transactional;
import org.jboss.seam.core.Events;
import org.jboss.seam.faces.FacesMessages;
import org.jboss.seam.log.Log;

import com.ut.tekir.annotations.OptionPane;
import com.ut.tekir.entities.Option;
import com.ut.tekir.framework.OptionManager;

/**
 * 
 * @author sinan.yumak
 */
@Name("generalOptionPane")
@OptionPane(OptionPaneType.System)
public class GeneralOptionPane extends AbstractOptionPane {

	@Logger
    protected Log log;
    
	@In(create=true)
    OptionManager optionManager;
    
	@In
    protected Events events;

	@In
    FacesMessages facesMessages;
    
    public Option getOption(GeneralOptionKey key) {
    	return optionManager.getOption( key, true);
    }

    @Transactional(TransactionPropagationType.REQUIRED)
    public void save(){
    	try {
    		for (OptionKey key : GeneralOptionKey.values()) {
    			optionManager.updateOption(key.getValue());
    		}
    		facesMessages.add("#{messages['general.message.record.SaveSuccess']}");
    		events.raiseTransactionSuccessEvent("refreshOptions");
		} catch (Exception e) {
			facesMessages.add("#{messages['general.message.record.SaveFail']}");
			log.info("Kaydederken hata meydana geldi. Sebebi #0", e);
		}
    }

    public String getName() {
        return "generalOptionPane";
    }

    public void cancel() {
        //TODO: editten önceki hale dönmeli...
    }

}
