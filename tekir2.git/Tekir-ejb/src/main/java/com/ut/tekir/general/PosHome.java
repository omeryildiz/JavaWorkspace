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

import java.util.List;

import com.ut.tekir.entities.BankCard;
import com.ut.tekir.entities.Pos;
import com.ut.tekir.framework.IEntityHome;

/**
 *
 * @author sinan.yumak
 */
public interface PosHome<E> extends IEntityHome<E>{
    
	void init();
	
    Pos getPos();
    void setPos(Pos pos);
    
    void initPosList();
    
    void createNewLine();
    void deleteLine(Integer ix);
    
    void addSelectedCardsToList(List<BankCard> cardList);
    
    void openBankCardPopup(Integer selectedIndex);
    
    void createNewBankLine();
    void deleteBankLine(Integer ix);
    
}