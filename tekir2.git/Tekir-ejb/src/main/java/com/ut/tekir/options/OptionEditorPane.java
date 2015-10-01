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

/**
 *
 * @author haky
 */
public interface OptionEditorPane {

    /**
     * Returns pane name. Must be unique.
     * 
     * @return unique pane name
     */
    public String getName();

    /**
     * Returns view id
     *
     * @return
     */
    public String getViewID();

    
    /**
     * Returns icon path
     * @return
     */
    public String getIcon();
}
