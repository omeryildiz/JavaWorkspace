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

import java.math.BigDecimal;
import java.util.List;

import com.ut.tekir.docmatch.DocumentMatchFilterModel;
import com.ut.tekir.docmatch.DocumentMatchResultModel;
import com.ut.tekir.entities.Cheque;
import com.ut.tekir.entities.DocumentMatch;
import com.ut.tekir.entities.PaymentItem;
import com.ut.tekir.entities.PaymentItemCheque;
import com.ut.tekir.entities.PaymentItemPromissoryNote;
import com.ut.tekir.entities.PromissoryNote;
import com.ut.tekir.framework.IEntityBase;


/**
 * @author sinan.yumak
 *
 */
public interface PaymentHomeBase<E> extends IEntityBase<E>{
	void init();

	void createNew();

	String save();

	String delete();

    void print();

	void createNewLine();

	void popupPromissorySelect(int clientOrFirm);

	void popupChequeSelect(int clientOrFirm);

	void popupChequeCreate();

	void popupPromissoryCreate();

	void selectClientCheque(Cheque cheque);

	void cekSatirininDetaylariniAta(PaymentItemCheque itemCheque, Cheque cheque);

	Boolean cekMi(int rowKey);

	Boolean senetMi(int rowKey);

	void selectClientPromissory(PromissoryNote promissory);

	void senetSatirininDetaylariniAta(PaymentItemPromissoryNote promissoryNote, PromissoryNote promissory);

	void findCheque(int rowKey);

	void findPromissory(int rowKey);

	void deleteLine(Integer ix);

	void manualFlush();

	PaymentItem getSelectedItem();
	void setSelectedItem(PaymentItem selectedItem);

	int getLineIndex();
	void setLineIndex(int lineIndex);

	Boolean isFirmaCekiMi();

	Boolean getFirmaCekiMi();

	void setFirmaCekiMi(Boolean firmaCekiMi);

	Boolean getFirmaSenediMi();

	void setFirmaSenediMi(Boolean firmaSenediMi);
	
	Boolean getIsEditable();

	void setIsEditable(Boolean isEditable);
	
	void recalculate();
	
	void calculateTotals(PaymentItem item);
	
	DocumentMatchFilterModel openMatchPopup(int index);
    void deleteMatchLine(int index);
    void selectMatch(DocumentMatchResultModel matchModel);
    List<DocumentMatch> getSelectedItemMatches();   
 
    BigDecimal getRemainingMatchTotal();   
    List<DocumentMatch> getMatches(int index);

}
