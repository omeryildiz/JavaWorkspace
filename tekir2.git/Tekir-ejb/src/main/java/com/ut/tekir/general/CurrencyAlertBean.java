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

import javax.ejb.Remove;
import javax.ejb.Stateful;
import javax.persistence.EntityManager;

import org.jboss.seam.ScopeType;
import org.jboss.seam.annotations.Destroy;
import org.jboss.seam.annotations.In;
import org.jboss.seam.annotations.Name;
import org.jboss.seam.annotations.Scope;

import com.ut.tekir.entities.Option;
import com.ut.tekir.framework.OptionManager;

/**
 * @author sinan.yumak
 *
 */
@Stateful
@Name("currencyAlertBean")
@Scope(value=ScopeType.SESSION)
public class CurrencyAlertBean implements CurrencyAlert {
	@In
	EntityManager entityManager;
	@In
	OptionManager optionManager;
	
    private Boolean firstTime = Boolean.TRUE;
    
	/**
	 * Finds the flag which makes currency popup visible to user or not.
	 */
	public Boolean showCurrencyAlertPopupProperty() {
		Option o = optionManager.getOption("currencyAlertPopup.isVisible");
		
		return o.getAsBoolean() == null ? Boolean.TRUE: o.getAsBoolean();
	}
	
	public void setFirstTimeToFalse() {
		firstTime = Boolean.FALSE;
	}

	public Boolean getFirstTime() {
		return firstTime;
	}

	@Remove @Destroy
    public void destroy() {
    }
	
}
