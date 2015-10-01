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
import org.jboss.seam.Component;
import org.jboss.seam.ScopeType;
import org.jboss.seam.annotations.In;
import org.jboss.seam.annotations.Name;
import org.jboss.seam.annotations.Scope;

/**
 * Generic Option Editor Interface Bean
 *
 * @author haky
 */
@Name("optionEditor")
@Scope(value = ScopeType.CONVERSATION)
public class OptionEditorBean {

    private List<OptionEditorPane> panes = null;

    
    public List<OptionEditorPane> getSystemPanes() {
        if( panes == null ){
            OptionPaneManager opm = (OptionPaneManager) Component.getInstance( "optionPaneManager" );
            panes = opm.getSystemOptionPanes();
        }
        return panes;
    }

    public List<OptionEditorPane> getUserPanes() {
        if( panes == null ){
            OptionPaneManager opm = (OptionPaneManager) Component.getInstance( "optionPaneManager" );
            panes = opm.getUserOptionPanes();
        }
        return panes;
    }

    /**
     * Returns default pane name.
     *
     * currently returns first pane name
     * 
     * @return
     */
    public String getDefaultPane(){
        if( panes == null || panes.size() == 0 ){
            return "";
        }
        return panes.get(0).getName();
    }

    /**
     * Returns coma saparated pane name list
     * 
     * @return
     */
    public String getPaneNameList(){
        String s = "";

        for( OptionEditorPane p : panes ){
            s = s + p.getName() + ",";
        }

        return s;
    }
}
