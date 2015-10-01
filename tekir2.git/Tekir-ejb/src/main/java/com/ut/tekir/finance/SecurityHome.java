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

package com.ut.tekir.finance;

import com.ut.tekir.entities.Security;
import com.ut.tekir.entities.SecurityCoupon;
import com.ut.tekir.framework.IEntityBase;

public interface SecurityHome<E> extends IEntityBase<E> {

	
	void createNewLine();
    void deleteLine(SecurityCoupon coupon);
    void deleteLine(Integer ix);
	
    Security getSecurity();
	void setSecurity(Security security);
	
	void resetCoupon();
	void initCouponRate();
	void init();
    void manualFlush();
}
