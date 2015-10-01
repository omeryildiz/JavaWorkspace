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

import java.util.Date;

/**
 *
 * @author haky
 */
public interface CalendarManager {
    Date getCurrentDate();

    Integer getCurrentYear();
    
    void initComponent();

    void setCurrentDate(Date date);

    Date getLastTenDay();
    Date getLastMonthDate();
    Date getCurrentWeekLastDay();
    Date getCurrentWeekFirtDay();

    Integer getCurrentMonthIndex();
    Integer getCurrentMonth();
    Date getFirstDayOfYear();
    Date getLastDayOfYear();
    Date getFirstDayOfMonth();
    Date getLastDayOfMonth();
    
    public void destroy();
}
