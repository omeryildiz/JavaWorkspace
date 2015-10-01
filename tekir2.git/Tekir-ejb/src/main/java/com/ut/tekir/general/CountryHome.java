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

package com.ut.tekir.general;

import com.ut.tekir.framework.IEntityHome;
import javax.ejb.Local;

/**
 *
 * @author haky
 */
@Local
public interface CountryHome<Country> extends IEntityHome<Country> {
    
    void initCountryList();

    Country getCountry();
    void setCountry(Country country);

}
