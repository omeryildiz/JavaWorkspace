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
import org.jboss.seam.annotations.Transactional;
import org.jboss.seam.core.Events;
import org.jboss.seam.log.Log;

import com.ut.tekir.annotations.OptionPane;
import com.ut.tekir.entities.Option;
import com.ut.tekir.framework.OptionManager;

/**
 *
 * Rounding Option editor
 * 
 * @author sinan.yumak
 */
@Name("amountLimitationOptionPane")
@OptionPane(OptionPaneType.System)
public class AmountLimitationOptionPane extends AbstractOptionPane {
    @Logger
    protected Log log;
    @In(create=true)
    OptionManager optionManager;
    @In
    protected Events events;
    
    public Option getOption(AmountLimitationOptionKey key) {
    	return optionManager.getOption( key, true);
    }

    @Transactional
    public void save(){
    	for (OptionKey key : AmountLimitationOptionKey.values()) {
    		optionManager.updateOption(key.getValue());
    	}
        events.raiseTransactionSuccessEvent("refreshOptions");
    }

    public String getName() {
        return "amountLimitationOptionPane";
    }

    public void cancel() {
        //TODO: editten önceki hale dönmeli...
    }

}
