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

package com.ut.tekir.framework;

import com.ut.tekir.entities.Option;
import org.jboss.seam.ScopeType;
import org.jboss.seam.annotations.AutoCreate;
import org.jboss.seam.annotations.In;
import org.jboss.seam.annotations.Logger;
import org.jboss.seam.annotations.Name;
import org.jboss.seam.annotations.Scope;
import org.jboss.seam.log.Log;


/**
 * Manage Application Theme
 *
 * TODO: Theme Adına göre yüklenecek olan templateler ve css'ler değişiklik göstermei.
 * Tabii ki theme'lar da hazırlamak lazım... Ayrıca marka yönetimi ile de burası bir parça ilgili.
 * Theme desteği uygulamaya göre kapatılıyor olabilir.
 *
 * @author haky
 */
@Name("themeManager")
@Scope(ScopeType.SESSION)
@AutoCreate
public class ThemeManager {

    @Logger
    protected Log log;

    @In OptionManager optionManager;

    private Option skinName = null;
    private Option themeName = null;

    public static String OPTION_KEY_SKIN_NAME = "theme.skinName";
    public static String OPTION_KEY_THEME_NAME = "theme.themeName";

    /**
     * @return the rfSkinName
     */
    public String getSkinName() {
        if( skinName == null ){
            skinName = optionManager.getOption( OPTION_KEY_SKIN_NAME, "blueSky", true );
        }
        return skinName.getAsString();
    }

    /**
     * @param rfSkinName the rfSkinName to set
     */
    public void setSkinName(String skinName) {
        this.skinName.setAsString(skinName);
    }

    public String getThemeName() {
        if( themeName == null ){
            themeName = optionManager.getOption( OPTION_KEY_THEME_NAME, "DEFAULT", true );
        }
        return themeName.getAsString();
    }


    public void setThemeName(String themeName) {
        this.themeName.setAsString(themeName);
    }
}
