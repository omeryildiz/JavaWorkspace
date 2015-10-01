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

import org.jboss.seam.ScopeType;
import org.jboss.seam.annotations.AutoCreate;
import org.jboss.seam.annotations.Create;
import org.jboss.seam.annotations.In;
import org.jboss.seam.annotations.Logger;
import org.jboss.seam.annotations.Name;
import org.jboss.seam.annotations.Scope;
import org.jboss.seam.core.Events;
import org.jboss.seam.log.Log;
import org.jboss.seam.theme.ThemeSelector;

import com.ut.tekir.annotations.OptionPane;
import com.ut.tekir.framework.OptionManager;
import com.ut.tekir.framework.ThemeManager;

/**
 * User Theme Option Pane
 * @author haky
 */
@Name("themeOptionPane")
@OptionPane(OptionPaneType.User)
@Scope(ScopeType.CONVERSATION)
@AutoCreate
public class ThemeOptionPane extends AbstractOptionPane{

    @Logger
    protected Log log;
    @In 
    OptionManager optionManager;
    @In
    protected Events events;
    @In 
    ThemeManager themeManager;
    @In
    ThemeSelector themeSelector;
    
    private List<SelectItem> skinNames = new ArrayList<SelectItem>();

    private List<ThemeViewModel> themeList = new ArrayList<ThemeViewModel>();
    	
    public List<ThemeViewModel> getThemeList() {
    	if (themeList.size() == 0) {
    		for (String theme : themeSelector.getAvailableThemes()) {
    			ThemeViewModel model = new ThemeViewModel();
    			model.setThemeName(theme);
    			if (themeSelector.getTheme().equals(theme)) {
    				model.setSelected(true);
    			}
    			themeList.add(model);
    		}
    	}
    	return themeList;
    }
    
    @Create
    public void init(){
        getSkinNames().add( new SelectItem("glassX"));
        getSkinNames().add(new SelectItem("blueSky"));
        getSkinNames().add(new SelectItem("emeraldTown"));
        getSkinNames().add(new SelectItem("wine"));
        getSkinNames().add(new SelectItem("japanCherry"));
        getSkinNames().add(new SelectItem("ruby"));
        getSkinNames().add(new SelectItem("classic"));
        getSkinNames().add(new SelectItem("deepMarine"));
        getSkinNames().add(new SelectItem("plain"));
    }

    public String getName() {
        return "themeOptionPane";
    }

    private String getSelectedTheme() {
    	for (ThemeViewModel model : themeList) {
    		if (model.isSelected()) return model.getThemeName();
    	}
    	return null;
    }
    
    public void save() {
    	String themeName = getSelectedTheme();
    	if (themeName != null) {
    		themeSelector.setTheme( themeName );
    		themeSelector.select();
    		events.raiseTransactionSuccessEvent("refreshOptions");
    	}
    }

    public void cancel() {
        //TODO: editten önceki hale dönmeli...
    }

    /**
     * @return the skinNames
     */
    public List<SelectItem> getSkinNames() {
        return skinNames;
    }

    /**
     * @param skinNames the skinNames to set
     */
    public void setSkinNames(List<SelectItem> skinNames) {
        this.skinNames = skinNames;
    }

}
