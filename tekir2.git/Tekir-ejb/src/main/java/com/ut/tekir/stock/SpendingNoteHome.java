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

package com.ut.tekir.stock;

import javax.ejb.Local;

import com.ut.tekir.entities.SpendingNote;
import com.ut.tekir.entities.SpendingNoteItem;
import com.ut.tekir.framework.IEntityBase;

/**
 *
 * @author sinan.yumak
 */
@Local
public interface SpendingNoteHome<E> extends IEntityBase<E> {
    
	SpendingNote getSpendingNote();

    void setSpendingNote(SpendingNote SpendingNote);

    public void createNewLine();
    
    public void deleteLine( SpendingNoteItem item );
    
    public void deleteLine( Integer ix );
    
    void init();
    
    void manualFlush();
    
    void selectLine(Integer ix);
}
