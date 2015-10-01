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

import java.io.File;
import org.jboss.seam.ScopeType;
import org.jboss.seam.annotations.AutoCreate;
import org.jboss.seam.annotations.In;
import org.jboss.seam.annotations.Logger;
import org.jboss.seam.annotations.Name;
import org.jboss.seam.annotations.Scope;
import org.jboss.seam.log.Log;

/**
 * Şirket Logosunun kullanılıp kullnılmayacağı...
 * @author haky
 */
@Name("logoManager")
@Scope(value = ScopeType.APPLICATION)
@AutoCreate
public class LogoManagerBean {

    @Logger
    protected Log log;
    @In
    private SystemProperties systemProperties;


    public boolean getUseDefaultLogo() {
        return systemProperties.getProperties().getProperty("branding.logo.file") == null;
    }

    public File getLogo() {
        String s = systemProperties.getProperties().getProperty("branding.logo.file");
        File f = null;
        if (s != null) {
            f = new File(s);
        }
        return f;
    }
}
