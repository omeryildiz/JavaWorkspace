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
import org.jboss.seam.core.Events;
import org.jboss.seam.log.Log;

import com.ut.tekir.annotations.OptionPane;
import com.ut.tekir.entities.Option;
import com.ut.tekir.framework.OptionManager;
import com.ut.tekir.framework.SequenceManager;


//FIXME: OptionKey ile daha jenerik bi hale sokmalı.

/**
 *
 *  Seri numaraları için kullandığımız option editörü...
 * 
 * @author sinan.yumak
 */
@Name("serialNumberOptionPane")
@OptionPane(OptionPaneType.System)
public class SerialNumberOptionPane extends AbstractOptionPane {
    @Logger
    protected Log log;
    @In
    protected Events events;
    @In
    OptionManager optionManager;
    @In
    SequenceManager sequenceManager;

    private List<SerialNumberOptionViewModel> optionList = new ArrayList<SerialNumberOptionViewModel>();

    public Option getOption(SerialNumberOptionKey key) {
    	return optionManager.getOption( key, true);
    }

    public List<SerialNumberOptionViewModel> getOptionList() {
    	if ( optionList == null || optionList.size() == 0 ) {

    		for (OptionKey key : SerialNumberOptionKey.values()) {
    			SerialNumberOptionViewModel model = new SerialNumberOptionViewModel();
    			model.setOption( optionManager.getOption(key, true) );
    			model.setSequence( sequenceManager.getCurrenctNumber( model.sequenceWithSuffix() ) );

    			optionList.add( model );
    		}
    	}
    	return optionList;
    }
    
    public void save() {
    	for (SerialNumberOptionViewModel model : optionList) {
    		optionManager.updateOption( model.getOption() );
    		sequenceManager.setCurrenctNumber( model.sequenceWithSuffix() , model.getSequence() );
    	}
        events.raiseTransactionSuccessEvent("refreshOptions");
    }

    public String getName() {
        return "serialNumberOptionPane";
    }

    public void cancel() {
        //TODO: editten önceki hale dönmeli...
    }

}
