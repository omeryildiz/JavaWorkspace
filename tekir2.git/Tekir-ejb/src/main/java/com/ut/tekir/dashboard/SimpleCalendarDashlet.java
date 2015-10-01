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

import com.ut.tekir.annotations.Dashlet;
import java.io.Serializable;
import java.util.Date;
import org.jboss.seam.ScopeType;
import org.jboss.seam.annotations.AutoCreate;
import org.jboss.seam.annotations.Name;
import org.jboss.seam.annotations.Scope;

/**
 * Sistem tarihini gösteren basit bir takvim
 * @author Hakan Uygun
 */
@Name("simpleCalendarDashlet")
@Scope(ScopeType.SESSION)
@AutoCreate
@Dashlet(capabilities={DashletCapability.canHide, DashletCapability.canMinimize, DashletCapability.hasIcon, DashletCapability.needInit})
public class SimpleCalendarDashlet implements Serializable{

    public Date getDate(){
        return new Date();
    }
}
