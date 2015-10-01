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

import java.util.Calendar;
import java.util.HashMap;
import java.util.Map;

import javax.ejb.Stateful;

import org.hibernate.criterion.DetachedCriteria;
import org.hibernate.criterion.MatchMode;
import org.hibernate.criterion.Order;
import org.hibernate.criterion.Projections;
import org.hibernate.criterion.Restrictions;
import org.hibernate.transform.Transformers;
import org.jboss.seam.ScopeType;
import org.jboss.seam.annotations.In;
import org.jboss.seam.annotations.Name;
import org.jboss.seam.annotations.Observer;
import org.jboss.seam.annotations.Scope;

import com.ut.tekir.entities.TradeAction;
import com.ut.tekir.entities.shp.TekirShipmentNote;
import com.ut.tekir.entities.PosCommision;
import com.ut.tekir.entities.PosCommisionDetail;
import com.ut.tekir.framework.BrowserBase;
import com.ut.tekir.framework.CalendarManager;
import com.ut.tekir.stock.ShipmentFilterModel;


/**
 *
 * @author rustem
 */
@Stateful
@Name("posCommisionBrowse")
@Scope(ScopeType.SESSION)
public class PosCommisionBrowseBean extends BrowserBase<PosCommision, PosCommisionFilterModel> implements PosCommisionBrowse<PosCommision, PosCommisionFilterModel> {


    @In
    CalendarManager calendarManager;

    @Override
    public PosCommisionFilterModel newFilterModel() {
        PosCommisionFilterModel fm = new PosCommisionFilterModel();
        fm.setStartDate(calendarManager.getLastTenDay());
        fm.setEndDate(calendarManager.getCurrentDate());
        return fm;
    }

    @Override
    public DetachedCriteria buildCriteria() {

        DetachedCriteria crit = DetachedCriteria.forClass(PosCommision.class);

        //crit.createAlias("detailList", "detailList");
        crit.createAlias("pos", "pos");
        crit.createAlias("pos.bank", "bank");
        crit.createAlias("pos.bankBranch", "bankBranch");
        crit.createAlias("pos.bankAccount", "bankAccount");
        crit.setProjection(Projections.projectionList()
//                .add(Projections.property("detailList.month"), "month")
//        		.add(Projections.property("detailList.rate"), "rate")
//                .add(Projections.property("detailList.valor"), "valor")
//                .add(Projections.property("detailList"), "detailList")
                .add(Projections.property("pos"), "pos")
                .add(Projections.property("id"), "id")
                .add(Projections.property("pos.bank"), "bank")
                .add(Projections.property("pos.bankBranch"), "bankBranch")
                .add(Projections.property("pos.bankAccount"), "bankAccount")
        		.add(Projections.property("startDate"), "startDate")
                .add(Projections.property("endDate"), "endDate")


        ).setResultTransformer(Transformers.aliasToBean(PosCommisionFilterModel.class));




        if (filterModel.getBank()!=null) {
            crit.add(Restrictions.eq("pos.bank", filterModel.getBank()));
        }

        if (filterModel.getBankBranch()!=null) {
            crit.add(Restrictions.eq("pos.bankBranch", filterModel.getBankBranch()));
        }

        if (filterModel.getBankAccount()!=null) {
            crit.add(Restrictions.eq("pos.bankAccount", filterModel.getBankAccount()));
        }

        if(filterModel.getPos() != null){
            crit.add(Restrictions.eq("this.pos", filterModel.getPos()));
        }

//        if (filterModel.getMonth() != null) {
//            crit.add(Restrictions.eq("detailList.month", filterModel.getMonth()));
//        }

        if (filterModel.getStartDate() != null) {
            crit.add(Restrictions.ge("this.startDate", filterModel.getStartDate()));
        }

        if (filterModel.getEndDate() != null) {
            crit.add(Restrictions.le("this.endDate", filterModel.getEndDate()));
        }

        crit.addOrder(Order.desc("this.id"));

        return crit;
    }

    @Observer("refreshBrowser:com.ut.tekir.entities.PosCommision")
    public void refreshResults() {
        log.debug("Uyarı geldi resultSet tazeleniyor");
        if (getEntityList() == null || getEntityList().isEmpty()) {
            return;
        }
        search();
    }

    @SuppressWarnings("unchecked")
	public void pdf() {
        Map params = new HashMap();


        execPdf("irsaliye_listesi", "Aliş İrsaliye Listesi", params);

    }

    @SuppressWarnings("unchecked")
	public void detailedPdf() {
        Map params = new HashMap();


        execPdf("alis-satis-irsaliye", "Aliş İrsaliye Detayi", params);
    }

    public void clearBankAccount() {
		filterModel.setBankBranch(null);
		filterModel.setBankAccount(null);
	}

}
