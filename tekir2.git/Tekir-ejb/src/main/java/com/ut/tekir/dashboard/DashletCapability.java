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

/**
 * Dashlet Capabilities
 * @author Hakan Uygun
 */
public enum DashletCapability {
    canHide,
    canMinimize,
    canMaximize,
    canExecute,
    canRefresh,
    canEdit,
    isSystem,
    isMandetory,
    canMultiInstance,
    hasIcon,
    hasCaptionHandler,
    needInit
}
