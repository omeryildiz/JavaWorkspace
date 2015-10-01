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

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import org.jboss.seam.ScopeType;
import org.jboss.seam.annotations.AutoCreate;
import org.jboss.seam.annotations.Create;
import org.jboss.seam.annotations.In;
import org.jboss.seam.annotations.Logger;
import org.jboss.seam.annotations.Name;
import org.jboss.seam.annotations.Scope;
import org.jboss.seam.annotations.remoting.WebRemote;
import org.jboss.seam.annotations.web.RequestParameter;
import org.jboss.seam.log.Log;

import com.google.common.base.Joiner;
import com.ut.tekir.dashboard.model.DashboardData;
import com.ut.tekir.dashboard.model.DashboardTab;
import com.ut.tekir.dashboard.model.LayoutType;
import com.ut.tekir.entities.Option;
import com.ut.tekir.framework.OptionManager;
import com.ut.tekir.framework.SystemProperties;

/**
 * Kullanıcı dashboard'unu yönetir.
 * @author Hakan Uygun
 */
@Name("dashboardManager")
@Scope(ScopeType.SESSION)
@AutoCreate
public class DashboardManager implements Serializable {

    @Logger
    Log log;

    @RequestParameter
    private Integer tab;
    private Integer currentTab = 0;

    @In
    OptionManager optionManager;
    @In
    SystemProperties systemProperties;
    @In
    DashletRegistery dashletRegistery;
    private DashboardData data;
    /**
     * Seçili olan aktif tabı gösterir.
     */
    private DashboardTab dashboard;

    /**
     * Dashboard'u oluşturur. Oturum başına çalışır.
     */
    @Create
    public void init() {
        loadDashboard();
    }

    /**
     * Varsayılan geliştirme dashboardunu oluşturur. test amaçlı...
     * @return
     */
    protected DashboardData defaultDashboard() {
        log.info("Dashboard Loaded form Code");
        DashboardData d = new DashboardData();
        DashboardTab t = new DashboardTab();
        t.setName("Ana Sayfa");
        t.setLayoutType(LayoutType.SingleColumn);
        t.setLayoutIndex(1);
        t.getColumn1().add("helloDashlet");
        d.getTabs().add(t);
        return d;
    }

    /**
     * Uygulama properties dosyasını okuyarak dashboard oluşturur. Eğer uygulama propertylerinde bilgi yoksa koddan bir tene oluşturur.
     * @return
     */
    protected DashboardData systemDashboard() {
        DashboardData d = null;


        String s = systemProperties.getProperty("dashboard.tab.count", "0");
        if( "0".equals(s)){
            log.info("Dashboard not found in System Properties : #0", s);
            //SystemProperties'de tanımlı dashboard yok o zaman koddan bir tane üreteceğiz.
            return defaultDashboard();
        } else {
            log.info("Dashboard Loaed from System Properties");
            d = new DashboardData();

            int tc = Integer.parseInt(s);
            for( int i = 0; i < tc; i++ ){
                DashboardTab t = new DashboardTab();
                t.setName(systemProperties.getProperty("dashboard.tab." +i+ ".name", "Dashtab"));
                s = systemProperties.getProperty("dashboard.tab." +i+ ".layoutIndex", "1");
                t.setLayoutIndex(Integer.parseInt(s));

                setColumnDashets( t.getColumn1(), s = systemProperties.getProperty("dashboard.tab." +i+ ".column1", ""));
                setColumnDashets( t.getColumn2(), s = systemProperties.getProperty("dashboard.tab." +i+ ".column2", ""));
                setColumnDashets( t.getColumn3(), s = systemProperties.getProperty("dashboard.tab." +i+ ".column3", ""));
                setColumnDashets( t.getColumn4(), s = systemProperties.getProperty("dashboard.tab." +i+ ".column4", ""));

                d.getTabs().add(t);

            }
        }
        return d;
    }

    public DashboardData getData() {
        return data;
    }

    public void setData(DashboardData data) {
        this.data = data;
    }

    /**
     * Dshleterin colon düzenleri string olraka gelir
     * ve bu metod içerisinde hem aktif dashboarda yerleştirilir hem de veri tabanına kaydedilir.
     *
     * format:
     * column1=d1,d2;column2=d3,d3;column3=
     * @param layout
     */
    @WebRemote
    public void saveDashletPositions(String layout) {
        String[] cols = layout.split(";");
        for (String col : cols) {
            String[] colpart = col.split("=");
            if (colpart[0].equals("column1")) {
                setColumnDashets(dashboard.getColumn1(), colpart[1]);
            } else if (colpart[0].equals("column2")) {
                setColumnDashets(dashboard.getColumn2(), colpart[1]);
            } else if (colpart[0].equals("column3")) {
                setColumnDashets(dashboard.getColumn3(), colpart[1]);
            } else if (colpart[0].equals("column4")) {
                setColumnDashets(dashboard.getColumn4(), colpart[1]);
            }
        }
        //Değişiklikleri veri tabanına da kaydedelim...
        saveDashboard();
    }


    /**
     * virgüllerle ayrılmış dashletleri verilen colon listesine ekler.
     * @param column
     * @param dashlets
     */
    private void setColumnDashets(List<String> column, String dashlets) {
        column.clear();
        if (!"".equals(dashlets) && dashlets != null) {
            String[] ds = dashlets.split(",");
            column.addAll(Arrays.asList(ds));
        }
    }

    /**
     * Dashboard üzerindeki tüm veriyi veri tabanına kaydeder.
     */
    public void saveDashboard(){

        List<Option> ls = new ArrayList<Option>();

        Option o = optionManager.getOption("dashboard.tab.count", "", true);
        o.setAsInteger(data.getTabs().size());
        ls.add(o);
        for( int i = 0; i < data.getTabs().size(); i++ ){
            //Tab ismi setleniyor
            o = optionManager.getOption("dashboard.tab." + i + ".name", "", true);
            o.setAsString(data.getTabs().get(i).getName());
            ls.add(o);

            //Tab layoutIndex setleniyor
            o = optionManager.getOption("dashboard.tab." + i + ".layout", "", true);
            o.setAsInteger(data.getTabs().get(i).getLayoutIndex());
            ls.add(o);

            //Tab colonları setleniyor
            o = optionManager.getOption("dashboard.tab." + i + ".col1", "", true);
            String s = Joiner.on(",").join(data.getTabs().get(i).getColumn1());
            o.setAsString(s);
            ls.add(o);

            o = optionManager.getOption("dashboard.tab." + i + ".col2", "", true);
            s = Joiner.on(",").join(data.getTabs().get(i).getColumn2());
            o.setAsString(s);
            ls.add(o);

            o = optionManager.getOption("dashboard.tab." + i + ".col3", "", true);
            s = Joiner.on(",").join(data.getTabs().get(i).getColumn3());
            o.setAsString(s);
            ls.add(o);

            o = optionManager.getOption("dashboard.tab." + i + ".col4", "", true);
            s = Joiner.on(",").join(data.getTabs().get(i).getColumn4());
            o.setAsString(s);
            ls.add(o);
        }

        optionManager.updateOptions(ls);
    }

    /**
     * Dashboard'u veri tabanından yükler.
     * Eğer kullanıcı için mevcut bir dashboard yoksa varsayılan dashboardu yükleyecek ve kullanıcı için kaydecektir.
     */
    public void loadDashboard(){

        Option o = optionManager.getOption("dashboard.tab.count");
        if( o == null ){
           //Kullanıcı için kayıtlı dashboard yok default Yüklenecek.
            data = systemDashboard();
        } else {
            log.info("Dashboard Loaded form DB : #0.#1:#2" , o.getUser(), o.getKey(), o.getValue());
            data = new DashboardData();
            int tc = o.getAsInteger();
            for( int i = 0; i < tc; i++ ){
                DashboardTab t = new DashboardTab();
                o = optionManager.getOption("dashboard.tab." + i + ".name", "", true);
                t.setName(o.getAsString());
                o = optionManager.getOption("dashboard.tab." + i + ".layout", "", true);
                t.setLayoutIndex(o.getAsInteger());

                o = optionManager.getOption("dashboard.tab." + i + ".col1", "", true);
                String col = o.getAsString();
                setColumnDashets( t.getColumn1(), col );

                o = optionManager.getOption("dashboard.tab." + i + ".col2", "", true);
                col = o.getAsString();
                setColumnDashets( t.getColumn2(), col );

                o = optionManager.getOption("dashboard.tab." + i + ".col3", "", true);
                col = o.getAsString();
                setColumnDashets( t.getColumn3(), col );

                o = optionManager.getOption("dashboard.tab." + i + ".col4", "", true);
                col = o.getAsString();
                setColumnDashets( t.getColumn4(), col );

                data.getTabs().add(t);
            }
        }

    }

    /**
     * Dshborad üzerinde tanımlı bütün dashletleri geriye döner.
     * @return
     */
    public List<String> getDashlets() {
        List<String> ls = new ArrayList<String>();

        ls.addAll(getDashboard().getColumn1());
        ls.addAll(getDashboard().getColumn2());
        ls.addAll(getDashboard().getColumn3());
        ls.addAll(getDashboard().getColumn4());

        return ls;
    }

    /**
     * Henüz kullanılmamış ama kullanılabilir itemları alır.
     * @return
     */
    public List<String> getAvailDashlets() {
        //TODO: Burada kullanıcı hak kontrolü yapılacak.
        List<String> ls = new ArrayList<String>();
        for (String s : dashletRegistery.getDashletList()) {
            ls.add(s);
        }
        return ls;
    }

    /**
     * Tüm colonlarda bulunan dashletleri s:webremote içinde kullanmak üzere virgüllerle ayrılmış string olarak listeler.
     * @return
     */
    public String getDashletWebRemoteList() {
        //FIXME: Burası edit yeteneği olan tüm dashletleri çevirmeli...
        return "helloDashlet,myTasksDashlet";
    }

    /**
     * Dashborad üzerindeki dashletlerin bilgilerini JSON olarak geriye döner.
     * @return
     */
    public String getDashletProperties() {

        StringBuilder sb = new StringBuilder();
        sb.append("{");

        boolean f = true;
        for (String s : getDashlets()) {
            if( f ){
               f = false;
            } else {
               sb.append(","); 
            }
            sb.append(s).append(": {");

            sb.append("editable:").append(dashletRegistery.hasCapability(s, DashletCapability.canEdit)).append(",");
            //Hide seçeneğini kaldırıyoruz...
            //sb.append("removable:").append(dashletRegistery.hasCapability(s, DashletCapability.canHide)).append(",");
            sb.append("removable:false").append(",");
            sb.append("refreshable:").append(dashletRegistery.hasCapability(s, DashletCapability.canRefresh)).append(",");
            sb.append("collapsible:").append(dashletRegistery.hasCapability(s, DashletCapability.canMinimize));
            sb.append("}");
        }

        sb.append("}");
        log.info("Properties : #0", sb.toString());
        return sb.toString();
    }

    private void checkDashboard() {

        if (currentTab.equals(tab)) {
            if (dashboard == null) {
                if (tab == null) {
                    dashboard = data.getTabs().get(0);
                } else {
                    dashboard = data.getTabs().get(tab.intValue());
                }
            }
        } else {
            if (tab != null) {
                currentTab = tab;
                dashboard = data.getTabs().get(tab.intValue());
            }
        }

        //Eğer hala dashboard yoksa
        if (dashboard == null) {
            dashboard = data.getTabs().get(0);
        }
    }

    public DashboardTab getDashboard() {
        checkDashboard();

        return dashboard;
    }

    public void setDashboard(DashboardTab dashboard) {
        this.dashboard = dashboard;
    }
}
