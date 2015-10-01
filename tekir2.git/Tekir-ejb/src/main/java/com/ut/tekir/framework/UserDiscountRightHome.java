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

import java.io.Serializable;

import com.ut.tekir.entities.DiscountOrExpense;
import com.ut.tekir.entities.User;

/**
 * @author sinan.yumak
 *
 */
public interface UserDiscountRightHome extends Serializable {

	/**
	 * Oturum açmış kullanıcının söz konusu indirim için hakkı olup olmadığına bakar.
	 * @return
	 */
	boolean hasLoggedUserRight(DiscountOrExpense aDiscount);
	
	/**
	 * Yetkili kullanıcının söz konusu indirim için hakkı olup olmadığına bakar.
	 * @return
	 */
	boolean hasAuthorizedUserRight(User user, DiscountOrExpense aDiscount);
	
}
