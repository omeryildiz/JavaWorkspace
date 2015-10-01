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

package com.ut.tekir.dashboard;

import javax.faces.component.UIComponent;
import javax.faces.context.FacesContext;
import org.jboss.seam.Component;
import org.jboss.seam.annotations.Name;
import org.jboss.seam.annotations.faces.Converter;
import org.jboss.seam.annotations.intercept.BypassInterceptors;

/**
 * Dashlet List shuttle için. Aslında gerçek bir converter değil
 * ama kullanmazsak loga sürekli hata geliyor
 * @author Hakan Uygun
 */
@Name("dashletShuttleConverter")
@BypassInterceptors
@Converter
public class DashletShuttleConverter implements javax.faces.convert.Converter{

    public Object getAsObject(FacesContext fc, UIComponent uic, String string) {
        return string;
    }

    public String getAsString(FacesContext fc, UIComponent uic, Object o) {
        return o.toString();
    }

}
