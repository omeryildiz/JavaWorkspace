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

/**
 *
 * @author haky
 */
public enum FinanceAction {
    // para çıkıyorsa alacaklı, para giriyorsa borçlu
    Debit, //borç
    Credit; //alacak
    
	public static FinanceAction fromString(String method) {
		for (FinanceAction action : values()) {
			if (action.name().equalsIgnoreCase(method))
				return action;
		}
		return null;
	}

}
