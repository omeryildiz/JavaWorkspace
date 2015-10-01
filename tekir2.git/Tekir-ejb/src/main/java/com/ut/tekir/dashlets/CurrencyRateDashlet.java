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

package com.ut.tekir.dashlets;

import com.ut.tekir.annotations.Dashlet;
import com.ut.tekir.dashboard.DashletCapability;
import java.io.Serializable;
import org.jboss.seam.ScopeType;
import org.jboss.seam.annotations.AutoCreate;
import org.jboss.seam.annotations.Name;
import org.jboss.seam.annotations.Scope;

/**
 * Sistem kur listesini gösterir.
 * @author Hakan Uygun
 */
@Name("currencyRateDashlet")
@Scope(ScopeType.SESSION)
@AutoCreate
@Dashlet(capabilities={DashletCapability.canHide, DashletCapability.canMinimize, DashletCapability.hasIcon})
public class CurrencyRateDashlet implements Serializable{

}
