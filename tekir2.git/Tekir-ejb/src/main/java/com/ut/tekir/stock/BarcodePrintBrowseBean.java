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

import java.util.ArrayList;
import java.util.List;

import javax.ejb.Stateful;

import org.hibernate.Criteria;
import org.hibernate.criterion.DetachedCriteria;
import org.hibernate.criterion.Restrictions;
import org.jboss.seam.ScopeType;
import org.jboss.seam.annotations.In;
import org.jboss.seam.annotations.Name;
import org.jboss.seam.annotations.Observer;
import org.jboss.seam.annotations.Scope;
import org.jboss.seam.core.Conversation;
import org.jboss.seam.international.StatusMessage.Severity;
import org.jboss.seam.persistence.HibernateSessionProxy;

import com.ut.tekir.entities.BarcodeTxn;
import com.ut.tekir.entities.BarcodeTxnFilterModel;
import com.ut.tekir.entities.MoneySet;
import com.ut.tekir.entities.PriceItemDetail;
import com.ut.tekir.framework.BaseConsts;
import com.ut.tekir.framework.BrowserBase;
import com.ut.tekir.tender.PriceProvider;

//FIXME: madem fiyat listelerini buluyoruz, bir daha sorgu çekmeden
//yazdırılmaları için gereken düzenlemeler yapılmalı.


/**
 * @author sinan.yumak
 *
 */
@Stateful
@Name("barcodePrintBrowse")
@Scope(ScopeType.SESSION)
public class BarcodePrintBrowseBean extends BrowserBase<BarcodeTxnFilterModel, BarcodeTxn> implements BarcodePrintBrowse<BarcodeTxnFilterModel, BarcodeTxn> {

	@In(create=true)
	private BarcodePrinterHome barcodePrinterHome;
	@In
	PriceProvider priceProvider;

	@Override
    public BarcodeTxn newFilterModel() {
    	BarcodeTxn fm = new BarcodeTxn();
    	fm.setActive(Boolean.TRUE);
        return fm;
    }

    @Override
    public DetachedCriteria buildCriteria() {

        DetachedCriteria crit = DetachedCriteria.forClass(BarcodeTxn.class);

        if (filterModel.getActive() != null) {
            crit.add(Restrictions.eq("active", filterModel.getActive()));
        }

        if (filterModel.getProduct() != null) {
        	crit.add(Restrictions.eq("product", filterModel.getProduct()));
        }

        return crit;
    }
    
	@Override
	public void search() {
		manualFlush();
		
        log.debug("Search Execute");
        
        HibernateSessionProxy session = (HibernateSessionProxy) getEntityManager().getDelegate();
        
        Criteria ecrit =  buildCriteria().getExecutableCriteria( session );
        ecrit.setCacheable(false);
        ecrit.setMaxResults( 100 );

        entityList = new ArrayList<BarcodeTxnFilterModel>();
        
		BarcodeTxnFilterModel fm = null;
		for (BarcodeTxn txn : (List<BarcodeTxn>)ecrit.list()) {
			PriceItemDetail item = null;
			fm = new BarcodeTxnFilterModel();
			fm.setItem(txn);
			try {
				item = priceProvider.findSalePriceItemDetailForProduct(txn.getProduct());
			} catch (Exception e) {
				log.info("Fiyat bulunamadı! Sebebi ", e);
			} 

			if (item == null) {
				fm.setHasPrice(false);
			} else {
				MoneySet grossPrice = new MoneySet();
				grossPrice.moveFieldsOf(item.getGrossPrice());
				fm.setGrossPrice(grossPrice);
//				fm.setGrossPrice(item.getGrossPrice());
			}
			entityList.add(fm);
		}

	}
    
    
    public void sendToBarcodePrinter() {
    	try {
    		List<BarcodeTxn> txnList = new ArrayList<BarcodeTxn>();
    		
    		for (BarcodeTxnFilterModel fm : getEntityList()) {
    			txnList.add(fm.getItem());
    		}
			barcodePrinterHome.sendToBarcodePrinter(txnList);

			deleteSuccesfullySentList(txnList);

			search();
    	} catch (Exception e) {
			log.error("Yazıcıya gönderirken hata oluştu. Sebebi \n:{0}", e);
			facesMessages.add(Severity.ERROR,"Yazıcıya gönderirken hata oluştu. Sebebi :{0}\n",e.getMessage());
		}
    }
    
    private void deleteSuccesfullySentList(List<BarcodeTxn> barcodeList) {
    	log.info("Deleting succesfully sent barcode txn records.");
    	manualFlush();
    	for (BarcodeTxn txn : barcodeList) {
    		if (txn.getActive()) {
    			entityManager.createQuery("delete from BarcodeTxn where id =:id")
    						 .setParameter("id", txn.getId())
    						 .executeUpdate();
    		}
    	}
    	entityManager.flush();
    }

    public void deleteLine(int rowKey) {
    	log.debug("Deleting line. Index: {0}", rowKey);
    	
    	manualFlush();
        if (entityList == null) {
            return;
        }

        BarcodeTxnFilterModel txn = entityList.get(rowKey);
        
        entityManager.createQuery("delete from BarcodeTxn where id=:id")
        						  .setParameter("id", txn.getItem().getId())
        						  .executeUpdate();
        
        entityList.remove(rowKey);
    }

    @Observer("refreshBrowser:com.ut.tekir.entities.BarcodeTxn")
    public void refreshResults() {
        log.debug("Uyarı geldi resultSet tazeleniyor");
        if (getEntityList() == null || getEntityList().isEmpty()) {
            return;
        }
        search();
    }

	private void manualFlush() {
		Conversation.instance().changeFlushMode(org.jboss.seam.annotations.FlushModeType.MANUAL);
	}    
    
    public String close() {
    	return BaseConsts.SUCCESS;
    }

}
