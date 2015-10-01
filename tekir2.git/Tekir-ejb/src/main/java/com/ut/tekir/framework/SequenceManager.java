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

import javax.ejb.Local;

/**
 *
 * @author haky
 */
@Local
public interface SequenceManager {
    String getNewNumber(String serial, Integer len);
    String getNewTempNumber(String serial, Integer len);

    String getNewSerialNumber(String key);
    String getTempSerialNumber( String key );
    String getBarcodeNumber( String key , Integer len);

    Long getCurrenctNumber( String serial );
    void setCurrenctNumber( String serial, Long number );
    
    public void destroy();
}
