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

import javax.ejb.Stateless;
import javax.persistence.EntityManager;

import org.jboss.seam.ScopeType;
import org.jboss.seam.annotations.AutoCreate;
import org.jboss.seam.annotations.In;
import org.jboss.seam.annotations.Logger;
import org.jboss.seam.annotations.Name;
import org.jboss.seam.annotations.Scope;
import org.jboss.seam.log.Log;

import com.ut.tekir.entities.DiscountOrExpense;
import com.ut.tekir.entities.User;

/**
 * Kullanıcının indirim bilgilerinin sorgulandığı home sınıfımızdır.
 * @author sinan.yumak
 *
 */
@Stateless
@Name("userDiscountRight")
@Scope(ScopeType.EVENT)
@AutoCreate
public class UserDiscountRightHomeBean implements UserDiscountRightHome {

	private static final long serialVersionUID = 1L;
	
	@Logger
	Log log;
	@In
	EntityManager entityManager;
	@In
	User activeUser;
	
	@Override
	public boolean hasAuthorizedUserRight(User aUser, DiscountOrExpense aDiscount) {
		User authorizedUser = authenticate(aUser);
		
		if (authorizedUser != null) {
			return hasUserRight(authorizedUser, aDiscount);
		} else {
			throw new RuntimeException("#{messages['general.message.login.InvalidUser']}");
		}
	}

	@Override
	public boolean hasLoggedUserRight(DiscountOrExpense aDiscount) {
		return hasUserRight(activeUser, aDiscount);
	}
	
	public boolean hasUserRight(User aUser, DiscountOrExpense aDiscount) {
		if (aUser.getHasDiscount()) {
			if (aDiscount.getPercentage()) {
				if (aDiscount.getRate().compareTo( aUser.getDiscount().getRate() ) == 1) {
					throw new RuntimeException("'" + aUser.getUserName() + "' kullanıcısının en fazla " +
											   "%" + aUser.getDiscount().getRate() + " indirim yapma " +
											   "hakkı bulunmaktadır.");
				} else {
					return true;
				} 
				
			} else {
				if (aDiscount.getValue().compareTo( aUser.getDiscount().getValue() ) ==1) {
					throw new RuntimeException("'" + aUser.getUserName() + "' kullanıcısının en fazla " +
							aUser.getDiscount().getValue() + " " + BaseConsts.SYSTEM_CURRENCY_CODE + " indirim yapma " +
							   "hakkı bulunmaktadır.");
					
				} else {
					return true;
				}
			}
			
		} else {
			throw new RuntimeException("'" + aUser.getUserName() + "' kullanıcısının indirim" +
									   " yapmaya hakkı bulunmamaktadır.");

		}
	}

	private User authenticate(User aUser) {
		User result = null;
	    Hash hash = new Hash();
		try {
            result = (User) entityManager.createQuery("from User u where " +
            									      "u.userName = :username and " +
            									      "u.pass = :password and " +
            									      "u.active = :active")
            									      .setParameter("username", aUser.getUserName())
            									      .setParameter("password", hash.md5(aUser.getPass()))
            									      .setParameter("active", true)
            									      .getSingleResult();
		} catch (Exception e) {
			log.error("Kullanıcı sorgulanırken hata meydana geldi. Sebebi :{0}", e);
		}
		return result;
	}
	
}
