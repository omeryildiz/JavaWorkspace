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

import javax.faces.model.SelectItem;

import org.jboss.seam.annotations.Create;
import org.jboss.seam.annotations.Logger;
import org.jboss.seam.annotations.Name;
import org.jboss.seam.log.Log;

import com.ut.tekir.annotations.OptionPane;

/**
 * Dil ayarları
 * @author sinan.yumak
 */
@Name("localeOptionPane")
@OptionPane(OptionPaneType.User)
public class LocaleOptionPane extends AbstractOptionPane {

    @Logger
    protected Log log;
    
    private List<SelectItem> localeNames = new ArrayList<SelectItem>();

    @Create
    public void init(){
    	localeNames.add( new SelectItem("tr", "Turkish") );
    	localeNames.add( new SelectItem("en","English") );
    }

    public String getName() {
        return "localeOptionPane";
    }

    public void cancel() {
        //TODO: editten önceki hale dönmeli...
    }

    public List<SelectItem> getLocaleNames() {
        return localeNames;
    }

    public void setLocaleNames(List<SelectItem> localeNames) {
        this.localeNames = localeNames;
    }

}
