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

package com.ut.tekir.dashboard.model;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

/**
 * Dashboard üzerinde bulunan bilgileri tutar
 * @author Hakan Uygun
 */
public class DashboardData implements Serializable{

    private List<DashboardTab> tabs = new ArrayList<DashboardTab>();

    public List<DashboardTab> getTabs() {
        return tabs;
    }

    public void setTabs(List<DashboardTab> tabs) {
        this.tabs = tabs;
    }
    
    

}
