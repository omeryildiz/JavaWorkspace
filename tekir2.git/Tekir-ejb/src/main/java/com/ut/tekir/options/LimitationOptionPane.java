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

import java.util.ArrayList;
import java.util.List;

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
 *  Limitation Option editor
 * 
 * @author sinan.yumak
 */
@Name("limitationOptionPane")
@OptionPane(OptionPaneType.System)
public class LimitationOptionPane extends AbstractOptionPane {
    @Logger
    protected Log log;
    @In(create=true)
    OptionManager optionManager;
    @In
    protected Events events;

    private List<LimitationOptionViewModel> optionList = new ArrayList<LimitationOptionViewModel>();
    
    public Option getOption(LimitationOptionKey key) {
    	return optionManager.getOption( key, true );
    }

    @Transactional
    public void save(){
    	for (LimitationOptionViewModel model : optionList) {
    		optionManager.updateOption(model.getOption());
    	}
        events.raiseTransactionSuccessEvent("refreshOptions");
    }

    public String getName() {
        return "limitationOptionPane";
    }

    public void cancel() {
        //TODO: editten önceki hale dönmeli...
    }
    
    public List<LimitationOptionViewModel> getOptionList() {
    	if (optionList.size() == 0) {
    		for (LimitationOptionKey key : LimitationOptionKey.values()) {
    			LimitationOptionViewModel model = new LimitationOptionViewModel();
    			model.setOption( getOption(key) );
    			
    			optionList.add(model);
    		}
    	}
    	return optionList;
    }

}
