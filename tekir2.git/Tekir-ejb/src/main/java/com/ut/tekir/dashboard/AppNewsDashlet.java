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
import org.jboss.seam.ScopeType;
import org.jboss.seam.annotations.AutoCreate;
import org.jboss.seam.annotations.Name;
import org.jboss.seam.annotations.Scope;

/**
 * Uygulama haberlerini gösterir.
 * @author Hakan Uygun
 */
@Name("appNewsDashlet")
@Scope(ScopeType.SESSION)
@AutoCreate
@Dashlet(capabilities={DashletCapability.canHide, DashletCapability.canMinimize, DashletCapability.hasIcon})
public class AppNewsDashlet implements Serializable{

}
