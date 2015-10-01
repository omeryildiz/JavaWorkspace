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

package com.ut.tekir.framework;

import java.util.Calendar;
import java.util.Date;

import javax.ejb.Remove;
import javax.ejb.Stateful;

import org.jboss.seam.ScopeType;
import org.jboss.seam.annotations.AutoCreate;
import org.jboss.seam.annotations.Create;
import org.jboss.seam.annotations.Destroy;
import org.jboss.seam.annotations.Logger;
import org.jboss.seam.annotations.Name;
import org.jboss.seam.annotations.Scope;
import org.jboss.seam.log.Log;

/**
 * Uygulama içinde Sistem tarihini tutar ve tarihle ilgili hesaplama işlerini yapacak fonksiyon setini taşır.
 * @author haky
 */
@Stateful()
@Name( "calendarManager")
@Scope(value=ScopeType.APPLICATION)
@AutoCreate
public class CalendarManagerBean implements CalendarManager {
    @Logger
    protected Log log;

    private Date currentDate;
    
    @Create
    public void initComponent(){
        //TODO: Aslında bu değer bir şekilde veri tabanından okunmalı... Mesela OptionManager üzerinden alınabilir mi?
        currentDate = Calendar.getInstance().getTime();
    }
    
    public Date getCurrentDate(){
    	
    	//TODO: Sistem tarihi açısından düzeltme yapılacak.
//        return currentDate;
    	return new Date();
    }
    
    public void setCurrentDate(Date date){
        //TODO: Tabii ki bu değişince bir yerlere yazmak lazım. Ayrıca bir de burada hak kontrolü süper olur :)
        currentDate = date;
    }
    
    public Integer getCurrentYear(){
        return Calendar.getInstance().get(Calendar.YEAR);
    }

    /**
     * 
     * İçinde bulunulan haftanın ilk günü döner
     * 
     * @return Haftanın ilk günü
     */
    public Date getCurrentWeekFirtDay(){
        //TODO: Şu anda günün tarihinden 3 gün önce veriyor... JODA-TIME ile daha doğru hale getirmeli..
        Calendar c = Calendar.getInstance();
        c.setTime(getCurrentDate());
        c.add(Calendar.DAY_OF_MONTH, -7 );

        return c.getTime();
    }
    
    /**
     * İçinde bulunulan haftanın son gününü döner
     * @return
     */
    public Date getCurrentWeekLastDay(){
        //TODO: Şu anda günün tarihinden 3 gün önce veriyor... JODA-TIME ile daha doğru hale getirmeli..
        Calendar c = Calendar.getInstance();
        c.setTime(getCurrentDate());
        c.add(Calendar.DAY_OF_MONTH, 7 );
        
        return c.getTime();
    }


    /**
     * Geçerli ayın indeks değerini geri döndürür (Ocak = 0)...
     * @return
     */
    public Integer getCurrentMonthIndex(){
        //TODO: Ocak ayı indeks olarak sıfır geldiğinden kullanımda dikkat edilmeli.
        return Calendar.getInstance().get(Calendar.MONTH);
    }

    /**
     * Geçerli ayın, yılın kaçıncı ayı oduğunu geri döndürür (Ocak = 1)...
     * @return
     */
    public Integer getCurrentMonth(){
        //TODO: Ocak ayı indeks olarak sıfır geldiğinden kullanımda dikkat edilmeli.
        return getCurrentMonthIndex()+1;
    }

    /**
     * Geçerli yılın ilk gününü geri döndürür.
     * @return
     */
    public Date getFirstDayOfYear(){
        Calendar c = Calendar.getInstance();
        c.set(getCurrentYear(), 0, 1);

        return c.getTime();
    }

    /**
     * Geçerli yılın son gününü geri döndürür.
     * @return
     */
    public Date getLastDayOfYear(){
        Calendar c = Calendar.getInstance();
        c.set(getCurrentYear(), 11, 31);

        return c.getTime();
    }

    private Boolean isLeapYear (Integer year){
        return(year % 4 == 0);
    }

    /**
     * Geçerli ayın ilk gününü geri döndürür.
     * @return
     */
    public Date getFirstDayOfMonth(){
        Calendar c = Calendar.getInstance();
        c.set(getCurrentYear(), getCurrentMonthIndex(), 1);

        return c.getTime();
    }

    /**
     * Ayın son gününü geri dündürür.
     * @return
     */
    public Date getLastDayOfMonth(){

        Calendar c = Calendar.getInstance();
        int month = getCurrentMonth();
        int monthInd = getCurrentMonthIndex();
        log.info("ay ={0}", getCurrentMonth());
        log.info("indeks ={0}", monthInd);
        if(month == 1 || month == 3 ||month == 5 ||month == 7 || month == 8 || month == 10 || month == 12){
            c.set(getCurrentYear(), monthInd, 31);
            return c.getTime();
        }else if(month ==2){
            if(isLeapYear(getCurrentYear())){
                c.set(getCurrentYear(), monthInd, 29);
            }else{
                c.set(getCurrentYear(), monthInd, 28);
            }

            return c.getTime();

        }else{
            c.set(getCurrentYear(), monthInd, 30);
            return c.getTime();
        }

    }


    /**
     * Aktif günden 10 gün öncesini döner...
     * @return
     */
    public Date getLastTenDay(){
        //TODO: Şu anda günün tarihinden 3 gün önce veriyor... JODA-TIME ile daha doğru hale getirmeli..
        Calendar c = Calendar.getInstance();
        c.setTime(getCurrentDate());
        c.add(Calendar.DAY_OF_MONTH, -10 );
        
        return c.getTime();
    }
    
    public Date getLastMonthDate(){
    	Calendar c = Calendar.getInstance();
    	c.setTime(getCurrentDate());
    	
    	if(c.get(Calendar.MONTH) != Calendar.DECEMBER){
    		c.add(Calendar.MONTH, -1);
    	} else{
    		c.add(Calendar.MONTH, 10);
    	}
    	
    	return c.getTime();
    }
    
    
    @Remove @Destroy
    public void destroy() {
    }
}
