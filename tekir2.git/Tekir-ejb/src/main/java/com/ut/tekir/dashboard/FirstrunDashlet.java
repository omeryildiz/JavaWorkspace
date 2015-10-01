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
import org.jboss.seam.ScopeType;
import org.jboss.seam.annotations.AutoCreate;
import org.jboss.seam.annotations.Name;
import org.jboss.seam.annotations.Scope;

/**
 * Uygulama için hoş geldin mesaj panosu. Kullanıcıların bu dashleti hemen kaldırası bekleniyor :)
 * @author Hakan Uygun
 */
@Name("firstrunDashlet")
@Scope(ScopeType.SESSION)
@AutoCreate
@Dashlet(capabilities={DashletCapability.canHide, DashletCapability.canMinimize})
public class FirstrunDashlet {

}
