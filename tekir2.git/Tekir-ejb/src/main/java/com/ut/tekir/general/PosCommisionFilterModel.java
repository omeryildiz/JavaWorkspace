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



import java.io.Serializable;
import java.util.Date;
import com.ut.tekir.framework.DefaultDocumentFilterModel;
import com.ut.tekir.entities.Pos;
import com.ut.tekir.entities.PosCommisionDetail;
import com.ut.tekir.entities.PosCommision;
import com.ut.tekir.entities.Bank;
import com.ut.tekir.entities.BankBranch;
import com.ut.tekir.entities.BankAccount;


/**
 *
 * @author rustem
 */
public class PosCommisionFilterModel  extends DefaultDocumentFilterModel implements Serializable{
	private static final long serialVersionUID = 1L;

	private Long id;
    private Boolean active;
    private Date startDate;
    private Date endDate;
    private Integer month;
    private Integer rate;
    private Integer valor;
    private PosCommision posCommision;
    private Pos pos;
    private PosCommisionDetail detailList;
    private Bank bank;
    private BankBranch bankBranch;
    private BankAccount bankAccount;
    private String name;

    public BankAccount getBankAccount() {
        return bankAccount;
    }

    public void setBankAccount(BankAccount bankAccount) {
        this.bankAccount = bankAccount;
    }

    public BankBranch getBankBranch() {
        return bankBranch;
    }

    public void setBankBranch(BankBranch bankBranch) {
        this.bankBranch = bankBranch;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }
    
    public Bank getBank() {
        return bank;
    }

    public void setBank(Bank bank) {
        this.bank = bank;
    }

    public PosCommision getPosCommision() {
        return posCommision;
    }

    public void setPosCommision(PosCommision posCommision) {
        this.posCommision = posCommision;
    }


    public PosCommisionDetail getDetailList() {
        return detailList;
    }

    public void setDetailList(PosCommisionDetail detailList) {
        this.detailList = detailList;
    }

    
    public Integer getMonth() {
        return month;
    }

    public void setMonth(Integer month) {
        this.month = month;
    }

    

    public Pos getPos() {
        return pos;
    }

    public void setPos(Pos pos) {
        this.pos = pos;
    }

    public Boolean getActive() {
        return active;
    }

    public void setActive(Boolean active) {
        this.active = active;
    }

     public Date getEndDate() {
        return endDate;
    }

    public void setEndDate(Date endDate) {
        this.endDate = endDate;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public Integer getRate() {
        return rate;
    }

    public void setRate(Integer rate) {
        this.rate = rate;
    }

    public Date getStartDate() {
        return startDate;
    }

    public void setStartDate(Date startDate) {
        this.startDate = startDate;
    }

    public Integer getValor() {
        return valor;
    }

    public void setValor(Integer valor) {
        this.valor = valor;
    }



}
