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

package com.ut.tekir.contact;

public class StatusCell {

    private String ccy;
    private Double debit = 0d;
    private Double credit = 0d;
    private Double balance = 0d;
   
    public StatusCell(String key) {
        super();
        this.ccy = key;
    }

    public String getCcy() {
        return ccy;
    }

    public void setCcy(String ccy) {
        this.ccy = ccy;
    }

    public Double getDebit() {
        return debit;
    }

    public void setDebit(Double debit) {
        this.debit = debit;
    }

    public Double getCredit() {
        return credit;
    }

    public void setCredit(Double credit) {
        this.credit = credit;
    }

    public Double getBalance() {
        return balance;
    }

    public void setBalance(Double balance) {
        this.balance = balance;
    }

    public void addCredit(Double amt) {
        credit = credit + amt;
        calcBalance();
    }

    public void addDebit(Double amt) {
        debit = debit + amt;
        calcBalance();
    }

    public void calcBalance() {
        balance = debit - credit;
    }
}
