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

import com.ut.tekir.entities.PromissoryNote;

public class PromissoryParamModel extends ChequePromParamModelBase {

	private PromissoryNote promissory;

	public PromissoryNote getPromissory() {
		return promissory;
	}
	
	public void setPromissory(PromissoryNote promissory) {
		this.promissory = promissory;
	}
	
}
