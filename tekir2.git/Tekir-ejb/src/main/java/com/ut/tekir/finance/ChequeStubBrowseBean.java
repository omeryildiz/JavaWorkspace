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

import javax.ejb.Stateful;

import org.hibernate.criterion.DetachedCriteria;
import org.hibernate.criterion.MatchMode;
import org.hibernate.criterion.Restrictions;
import org.jboss.seam.ScopeType;
import org.jboss.seam.annotations.In;
import org.jboss.seam.annotations.Name;
import org.jboss.seam.annotations.Observer;
import org.jboss.seam.annotations.Scope;

import com.ut.tekir.entities.ChequeStub;
import com.ut.tekir.framework.BrowserBase;
import com.ut.tekir.framework.CalendarManager;

/**
 *
 * @author selman
 */
@Stateful
@Name("chequeStubBrowse")
@Scope(ScopeType.SESSION)
public class ChequeStubBrowseBean extends BrowserBase<ChequeStub, ChequeStubFilterModel> implements ChequeStubBrowse<ChequeStub, ChequeStubFilterModel> {

    @In
    CalendarManager calendarManager;

    @Override
    public ChequeStubFilterModel newFilterModel() {
    	ChequeStubFilterModel fm = new ChequeStubFilterModel();
        return fm;
    }

    @Override
    public DetachedCriteria buildCriteria() {

        DetachedCriteria crit = DetachedCriteria.forClass(ChequeStub.class);
        
        if (isNotEmpty(filterModel.getCode())) {
            crit.add(Restrictions.ilike("code", filterModel.getCode(),MatchMode.START));
        }
        
        if (isNotEmpty(filterModel.getBankName())) {
            crit.add(Restrictions.ilike("bankName", filterModel.getBankName() ,MatchMode.START));
        }

        if (isNotEmpty(filterModel.getBankBranch())) {
            crit.add(Restrictions.ilike("bankBranch", filterModel.getBankBranch(),MatchMode.START));
        }

        if (isNotEmpty(filterModel.getBankAccountName())) {
            crit.add(Restrictions.ilike("accountNo", filterModel.getBankAccountName(),MatchMode.START));
        }
        
        if (isNotEmpty(filterModel.getPaymentPlace())) {
            crit.add(Restrictions.ilike("paymentPlace", filterModel.getPaymentPlace() ,MatchMode.START));
        }
        
        return crit;
    }

    @Observer("refreshBrowser:com.ut.tekir.entities.ChequeStubFilterModel")
    public void refreshResults() {
        log.debug("Uyarı geldi resultSet tazeleniyor");
        if (getEntityList() == null || getEntityList().isEmpty()) {
            return;
        } 
        search();
    }

    public void pdf() {
//        Map params = new HashMap();
//
//        if (filterModel.getReferenceNo() != null && filterModel.getReferenceNo().length() > 0) {
//            params.put("pCode", filterModel.getReferenceNo());
//        }
//
//        Calendar c = Calendar.getInstance();
//        c.set(1900, 1, 1);
//        if (filterModel.getBeginDate() != null) {
//            params.put("pBDate", filterModel.getBeginDate());
//        }
//
//        c.set(2100, 12, 31);
//        if (filterModel.getEndDate() != null) {
//            params.put("pEDate", filterModel.getEndDate());
//        }
//
//        params.put("pAct", 0);
//
//
//        execPdf("cek_listesi", "Cek Listesi", params);
//
	}

    public void detailedPdf() {
//        Map params = new HashMap();
//
//
//        if (filterModel.getReferenceNo() != null && filterModel.getReferenceNo().length() > 0) {
//            params.put("pCode", filterModel.getReferenceNo());
//        }
//        
//        Calendar c = Calendar.getInstance();
//        c.set(1900, 1, 1);
//        if (filterModel.getBeginDate() != null) {
//            params.put("pBDate", filterModel.getBeginDate());
//        }
//
//        c.set(2100, 12, 31);
//        if (filterModel.getEndDate() != null) {
//            params.put("pEDate", filterModel.getEndDate());
//        }
//
//        params.put("pAct", 0);
//
//        execPdf("cek_detay", "Cek Listesi Detayi", params);
	}

}
