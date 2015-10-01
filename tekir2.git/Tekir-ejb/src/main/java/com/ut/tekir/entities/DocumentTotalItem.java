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
 * TODO: Yeni MoneySet ile birlikte sanırım bu modelde değişmeli...
 * @author haky
 */
public class DocumentTotalItem {
    
    private String ccy;
    private Money amount;
    private Money localAmount;

    public DocumentTotalItem( String ccy, Money amount, Money localAmount ){
        this.ccy = ccy;
        this.amount = new Money( amount );
        this.localAmount = new Money( localAmount );
    }
    
    public String getCcy() {
        return ccy;
    }

    public void setCcy(String ccy) {
        this.ccy = ccy;
    }

    public Money getAmount() {
        return amount;
    }

    public void setAmount(Money amount) {
        this.amount = amount;
    }

    public Money getLocalAmount() {
        return localAmount;
    }

    public void setLocalAmount(Money localAmount) {
        this.localAmount = localAmount;
    }
}
