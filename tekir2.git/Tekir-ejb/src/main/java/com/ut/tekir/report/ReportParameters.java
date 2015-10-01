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

package com.ut.tekir.report;

import java.util.Calendar;
import java.util.Date;

import com.ut.tekir.framework.SystemProperties;
import com.ut.tekir.util.Utils;

/**
 * @author sinan.yumak
 *
 */
public class ReportParameters {

    public static Date getDefaultBeginDate() {
        Calendar c = Calendar.getInstance();
        c.set(1900, 1, 1);
    	return c.getTime();
    }

    public static Date getDefaultEndDate() {
    	Calendar c = Calendar.getInstance();
		c.set(2100, 12, 31);
    	return c.getTime();
    }

    public static String getTemplateDirPath() {
    	return SystemProperties.instance().getProperties().get("folder.templates") + Utils.getFileSeperator();
    }
}
