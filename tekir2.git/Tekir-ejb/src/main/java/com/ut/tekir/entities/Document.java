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

package com.ut.tekir.entities;

import java.util.Date;

/**
 * Document Interface used for Document definitions like Invoice, Payment etc.
 * @author haky
 */
public interface Document {
    /**
     * Returns document status.
     * 
     * @return True if active
     */
    Boolean getActive();

    /**
     * Returns document special code. Use for reporting.
     * 
     * @return Document Code
     */
    String getCode();

    /**
     * Returns document date. Transaction date.
     * 
     * @return Document date
     */
    Date getDate();

    /**
     * Resturns document description.
     * 
     * @return Document descrition
     */
    String getInfo();

    /**
     * Returns Document external reference. Usualy legal references, like Invoice Number.
     * 
     * @return Document external reference
     */
    String getReference();

    /**
     * Returns Document Serial Number
     * 
     * @return Serial Number as String
     */
    String getSerial();

    /**
     * Set document status.
     * 
     * @param active Document activity status
     */
    void setActive(Boolean active);

    /**
     * Sets document code.
     * 
     * @param code Document code
     */
    void setCode(String code);

    /**
     * Sets document date.
     * 
     * @param date document date
     */
    void setDate(Date date);

    /**
     * Set description
     * 
     * @param info Description
     */
    void setInfo(String info);

    /**
     * Sets reference number. Usualy legal references, like Invoice Number.
     * 
     * @param reference Document external reference number.
     */
    void setReference(String reference);

    /**
     * Sets document serial number
     * 
     * @param serial Serial number
     */
    void setSerial(String serial);
    
}
