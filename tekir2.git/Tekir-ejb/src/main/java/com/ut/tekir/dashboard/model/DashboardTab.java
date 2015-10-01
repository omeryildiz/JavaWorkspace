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
 * Dashboard Tab bilgileri
 * @author Hakan Uygun
 */
public class DashboardTab implements Serializable {

    /**
     * Tabın kullanıcı tarafından verilmiş adı
     */
    private String name;
    /**
     * Tabın kolon sayısına göre layout tipi
     */
    private LayoutType layoutType;
    /**
     * Tabın hangi layout'u kullanacağı. layoutTipi+index biçminde
     */
    private Integer layoutIndex;
    /**
     * Birinci kolonda bulunan dashlet listesi
     */
    private List<String> column1 = new ArrayList<String>();
    /**
     * İkinci kolonda bulunan dashlet listesi
     */
    private List<String> column2 = new ArrayList<String>();
    /**
     * Üçüncü kolonda bulunan dashlet listesi
     */
    private List<String> column3 = new ArrayList<String>();
    /**
     * Dördücü kolonda bulunan dashlet listesi
     */
    private List<String> column4 = new ArrayList<String>();

    public List<String> getColumn1() {
        return column1;
    }

    public void setColumn1(List<String> column1) {
        this.column1 = column1;
    }

    public List<String> getColumn2() {
        return column2;
    }

    public void setColumn2(List<String> column2) {
        this.column2 = column2;
    }

    public List<String> getColumn3() {
        return column3;
    }

    public void setColumn3(List<String> column3) {
        this.column3 = column3;
    }

    public List<String> getColumn4() {
        return column4;
    }

    public void setColumn4(List<String> column4) {
        this.column4 = column4;
    }

    public Integer getLayoutIndex() {
        return layoutIndex;
    }

    public void setLayoutIndex(Integer layoutIndex) {
        this.layoutIndex = layoutIndex;
    }

    public LayoutType getLayoutType() {
        return layoutType;
    }

    public void setLayoutType(LayoutType layoutType) {
        this.layoutType = layoutType;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }


    /**
     * Dashborad Tab üzerinde tanımlı bütün dashletleri geriye döner.
     * @return
     */
    public List<String> getDashlets(){
        List<String> ls = new ArrayList<String>();

        ls.addAll(getColumn1());
        ls.addAll(getColumn2());
        ls.addAll(getColumn3());
        ls.addAll(getColumn4());

        return ls;
    }

    /**
     * Liste olarak verilen dashletleri colonlara atar.
     * @param ls
     */
    public void setDashlets(List<String> ls){

        //FIXME: layout değiştiğinde ortadan kalkan kolonlar varsa onların bilgisini de diğer kolonlara aktarmak lazım...
        clearColumn( ls, column1 );
        clearColumn( ls, column2 );
        clearColumn( ls, column3 );
        clearColumn( ls, column4 );

        addNewDashlets(ls);
    }

    /**
     * Colon içindeki dashlet eğer listede yoksa siler.
     * @param ls
     * @param col
     */
    private void clearColumn(List<String> ls, List<String> col){
        List<String> cls = new ArrayList<String>();

        for( String s : col){
            if( !ls.contains(s)){
                cls.add(s);
            }
        }

        for( String s : cls ){
            col.remove(s);
        }
    }

    /**
     * Listede yeni olanları column1'e ekler.
     * @param ls
     */
    private void addNewDashlets( List<String> ls ){
        List<String> news = new ArrayList<String>();

        for( String s : ls ){
            if( !column1.contains(s) &&
                !column2.contains(s) &&
                !column3.contains(s) &&
                !column4.contains(s)){
                news.add(s);
            }
        }

        column1.addAll(news);
    }

    @Override
    public String toString() {
        return getName();
    }


}
