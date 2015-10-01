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

import com.ut.tekir.entities.Option;

/**
 * Seri numaraları option ekranında kullanacağımız view modeli...
 * @author sinan.yumak
 *
 */
public class SerialNumberOptionViewModel {

	private Option option;
	private long sequence;

	public Option getOption() {
		return option;
	}

	public void setOption(Option option) {
		this.option = option;
	}

	public String sequenceWithSuffix() {
		if (option != null) return option.getKey() + "." + option.getValue();
		return null;
	}
	
	public long getSequence() {
		return sequence;
	}

	public void setSequence(long sequence) {
		this.sequence = sequence;
	}

}
