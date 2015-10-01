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

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import javax.faces.model.SelectItem;

import org.jboss.seam.ScopeType;
import org.jboss.seam.annotations.In;
import org.jboss.seam.annotations.Logger;
import org.jboss.seam.annotations.Name;
import org.jboss.seam.annotations.Scope;
import org.jboss.seam.log.Log;

import com.ut.tekir.dashboard.model.DashboardTab;

/**
 * Kullanıcı için dashboard bilgilerini edit edilmesi için gereken arayüzü sağlar.
 * @author Hakan Uygun
 */
@Name("dashboardEditor")
@Scope(ScopeType.SESSION)
public class DashboardEditor {

    @Logger
    Log log;

    @In
    DashboardManager dashboardManager;

    @In
    Map<Object, String> messages;

    private DashboardTab selectedTab;
    private List<String> availDashlets;
    private Integer tabIndex = 0;

    public List<DashboardTab> getTabs(){
        return dashboardManager.getData().getTabs();
    }


    public void newTab(){
        DashboardTab tab = new DashboardTab();
        tab.setName( messages.get("dashboard.label.NewTab") + getTabs().size());
        tab.setLayoutIndex(1);
        dashboardManager.getData().getTabs().add(tab);
        setSelectedTab(tab);
    }

    public void deleteTab(){
        //FIXME: delete tab implemente edilecek.
        if( tabIndex == 0 ) return; //ilk tab silinemez...
        getTabs().remove( tabIndex.intValue() );
        dashboardManager.saveDashboard();

        setDefaultTab();
    }

    private void setDefaultTab() {
    	setSelectedTab( getTabs().get(0) );
    	tabIndex = 0;
    }
    
    public void save(){
        dashboardManager.saveDashboard();
    }

    public List<String> getAvailDashlets() {
        log.info("avilDashlets: #0", availDashlets);
        if( availDashlets == null ){
            availDashlets = new ArrayList<String>();
            for( String s : dashboardManager.getAvailDashlets() ){
                if( !getSelectedTab().getDashlets().contains(s)){
                    availDashlets.add(s);
                }
            }
        }
        log.info("avilDashlets: #0", availDashlets);
        return availDashlets;
    }

    public void setAvailDashlets(List<String> availDashlets) {
        this.availDashlets = availDashlets;
    }

    public DashboardTab getSelectedTab() {
        if( selectedTab == null ){
            selectedTab = getTabs().get(0);
        }
        return selectedTab;
    }

    public void setSelectedTab(DashboardTab selectedTab) {
        if( this.selectedTab == null || !this.selectedTab.equals( selectedTab)){
            availDashlets = null;
        }
        this.selectedTab = selectedTab;
        log.info("SelectedTab:#0", selectedTab);
    }

    public Integer getTabIndex() {
        return tabIndex;
    }

    public void setTabIndex(Integer tabIndex) {
       log.info("TabIndex : #0", tabIndex);
        if( tabIndex.equals(-1) ){
            newTab();
            this.tabIndex = getTabs().size() - 1;
        } else {
            setSelectedTab( getTabs().get(tabIndex.intValue()));
            this.tabIndex = tabIndex;
        }
       log.info("TabIndex : #0", this.tabIndex);
    }

    /**
     * Geriye tab menüsünü döndürür.
     * Yeni tab için -1 değerli bir item ekler.
     * @return
     */
    public List<SelectItem> getTabMenu(){
        List<SelectItem> ls = new ArrayList<SelectItem>();
        for( int i= 0; i < getTabs().size(); i++ ){
            DashboardTab t = getTabs().get(i);
            ls.add(new SelectItem(new Integer( i ), t.getName()));
        }
        ls.add(new SelectItem(new Integer( -1 ), messages.get("dashboard.label.AddNewTab")));
        return ls;
    }

}
