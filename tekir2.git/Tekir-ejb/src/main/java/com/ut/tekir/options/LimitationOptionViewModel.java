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

package com.ut.tekir.options;

import com.ut.tekir.entities.ControlType;
import com.ut.tekir.entities.Option;

/**
 * Kısıtlama option ekranında kullanacağımız view modeli...
 * @author sinan.yumak
 * @param <T>
 *
 */
public class LimitationOptionViewModel {

	private Option option;
	private ControlType controlType;

	public Option getOption() {
		return option;
	}

	public void setOption(Option option) {
		this.option = option;
		
		controlType = option.getAsEnum(ControlType.class);
	}

	public ControlType getControlType() {
		return controlType;
	}

	public void setControlType(ControlType controlType) {
		this.controlType = controlType;
		if (option != null) {
			option.setAsEnum(controlType);
		}
	}
	
}
