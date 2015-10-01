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

package com.ut.tekir.invoice.yeni;

import java.io.FileWriter;
import java.util.Calendar;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.ejb.Stateful;

import org.hibernate.criterion.DetachedCriteria;
import org.hibernate.criterion.MatchMode;
import org.hibernate.criterion.Order;
import org.hibernate.criterion.Restrictions;
import org.jboss.seam.Component;
import org.jboss.seam.ScopeType;
import org.jboss.seam.annotations.In;
import org.jboss.seam.annotations.Name;
import org.jboss.seam.annotations.Observer;
import org.jboss.seam.annotations.Scope;
import org.jboss.seam.annotations.Transactional;

import com.ut.tekir.entities.DocumentType;
import com.ut.tekir.entities.TradeAction;
import com.ut.tekir.entities.inv.TekirInvoice;
import com.ut.tekir.framework.BrowserBase;
import com.ut.tekir.framework.CalendarManager;
import com.ut.tekir.framework.InvoiceFilterModel;

/**
 *
 * @author sinan.yumak
 */
@Stateful
@Name("retailSaleInvoiceBrowse")
@Scope(ScopeType.SESSION)
public class RetailSaleInvoiceBrowseBean extends BrowserBase<TekirInvoice, InvoiceFilterModel> implements RetailSaleInvoiceBrowse<TekirInvoice, InvoiceFilterModel> {

    @In
    CalendarManager calendarManager;

    @Override
    public InvoiceFilterModel newFilterModel() {
    	InvoiceFilterModel fm = new InvoiceFilterModel();
        fm.setBeginDate(calendarManager.getLastTenDay());
        fm.setEndDate(calendarManager.getCurrentDate());
        return fm;
    }

    @Override
    public DetachedCriteria buildCriteria() {

        DetachedCriteria crit = DetachedCriteria.forClass(TekirInvoice.class);

        crit.createAlias("this.contact", "contact");
        
        if (isNotEmpty(filterModel.getSerial())) {
            crit.add(Restrictions.ilike("serial", filterModel.getSerial(),MatchMode.START));
        }

        if (isNotEmpty(filterModel.getReference())) {
            crit.add(Restrictions.ilike("reference", filterModel.getReference(),MatchMode.START));
        }

        if (isNotEmpty(filterModel.getCode())) {
            crit.add(Restrictions.ilike("code", filterModel.getCode(),MatchMode.START));
        }

        if (filterModel.getBeginDate() != null) {
            crit.add(Restrictions.ge("date", filterModel.getBeginDate()));
        }

        if (filterModel.getEndDate() != null) {
            crit.add(Restrictions.le("date", filterModel.getEndDate()));
        }

        /*
        if( filterModel.getWarehouse() != null ){
        crit.add( Restrictions.eq("warehouse", filterModel.getWarehouse() ));
        }
         */

        if( isNotEmpty(filterModel.getContactCode())){
        	crit.add( Restrictions.ilike("contact.code", filterModel.getContactCode(), MatchMode.START ));
        }

        if( isNotEmpty(filterModel.getContactName())){
        	crit.add( Restrictions.ilike("contact.fullname", filterModel.getContactName(), MatchMode.START));
        }

        crit.add(Restrictions.eq("tradeAction", TradeAction.Sale));
        crit.add(Restrictions.eq("documentType", DocumentType.RetailSaleShipmentInvoice));

        crit.addOrder(Order.desc("serial"));
        
        return crit;
    }

    @Observer("refreshBrowser:com.ut.tekir.entities.inv.TekirInvoice")
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



        if (filterModel.getSerial() != null && filterModel.getSerial().length() > 0) {
            params.put("pSeri", filterModel.getSerial());
        }

        if (filterModel.getReference() != null && filterModel.getReference().length() > 0) {
            params.put("pRef", filterModel.getReference());
        }

        if (filterModel.getCode() != null && filterModel.getCode().length() > 0) {
            params.put("pCode", filterModel.getCode());
        }


        Calendar c = Calendar.getInstance();
        c.set(1900, 1, 1);
        if (filterModel.getBeginDate() != null) {
            params.put("pBDate", filterModel.getBeginDate());
        }

        c.set(2100, 12, 31);
        if (filterModel.getEndDate() != null) {
            params.put("pEDate", filterModel.getEndDate());
        }

        params.put("pAct", 1);



        execPdf("fatura_listesi", "Satis Fatura Listesi", params);

    }

    @SuppressWarnings("unchecked")
	public void detailedPdf() {
        Map params = new HashMap();



        if (filterModel.getSerial() != null && filterModel.getSerial().length() > 0) {
            params.put("pSeri", filterModel.getSerial());
        }

        if (filterModel.getReference() != null && filterModel.getReference().length() > 0) {
            params.put("pRef", filterModel.getReference());
        }

        if (filterModel.getCode() != null && filterModel.getCode().length() > 0) {
            params.put("pCode", filterModel.getCode());
        }


        Calendar c = Calendar.getInstance();
        c.set(1900, 1, 1);
        if (filterModel.getBeginDate() != null) {
            params.put("pBDate", filterModel.getBeginDate());
        }

        c.set(2100, 12, 31);
        if (filterModel.getEndDate() != null) {
            params.put("pEDate", filterModel.getEndDate());
        }

        params.put("pAct", 1);


        execPdf("alis-satis-fatura", "Satis Fatura Detayi", params);
    }
    
    /**
     * Bu metot, eski faturaları düzeltmek için kullanılmaktadır.
     */
	@Transactional
	@SuppressWarnings("unchecked")
	public void recreateInvoices() {
		try {
			List<TekirInvoice>  invoiceList = null;

			RetailSaleInvoiceHome<TekirInvoice> rsih = (RetailSaleInvoiceHome<TekirInvoice>)Component.getInstance("retailSaleInvoiceHome",true);
			SaleInvoiceHome<TekirInvoice> sih = (SaleInvoiceHome<TekirInvoice>)Component.getInstance("saleInvoiceHome",true);
			PurchaseInvoiceHome<TekirInvoice> pih = (PurchaseInvoiceHome<TekirInvoice>)Component.getInstance("purchaseInvoiceHome",true);

			
			int islenenFaturaSayisi = 0;
			long lastId = 0;
			//199 tane fatura var.
			
			FileWriter fw = new FileWriter("/home/uygun/Desktop/sonuc.txt",true);

			for (int i=0;i<10;i+=3) {
				Thread.sleep(3000);
				invoiceList = entityManager.createQuery("select inv from TekirInvoice inv " +
														"where id >:id order by id")
														.setParameter("id", lastId)
														.setMaxResults(3)
														.getResultList();

				if (invoiceList != null && invoiceList.size() > 0) {
				
					System.out.println("SORGU BOYUTU = " + invoiceList.size() );

					for (TekirInvoice inv : invoiceList) {
						if (inv.getDocumentType().equals(DocumentType.RetailSaleShipmentInvoice)) {
							try {
								rsih.setEntity(inv);
								rsih.save();
								System.out.println("FATURA BAŞARIYLA KAYDEDILDI. ID ="+inv.getId());
								fw.append(inv.getId().toString() + "\n");
								fw.flush();
							} catch (Exception e) {
								log.info("Fatura kaydedilemedi. id = :{0}. Sebebi=:{1}", inv.getId(),e);
							}
						} else if (inv.getDocumentType().equals(DocumentType.SaleInvoice) || 
									inv.getDocumentType().equals(DocumentType.SaleShipmentInvoice)){
							
							try {
								sih.setEntity(inv);
								sih.save();
								System.out.println("FATURA BAŞARIYLA KAYDEDILDI. ID ="+inv.getId());
								fw.append(inv.getId().toString() + "\n");
								fw.flush();
							} catch (Exception e) {
								log.info("Fatura kaydedilemedi. id = :{0}. Sebebi=:{1}", inv.getId(),e);
							}
							
						} else if (inv.getDocumentType().equals(DocumentType.PurchaseInvoice) || 
									inv.getDocumentType().equals(DocumentType.PurchaseShipmentInvoice)) {
		
							try {
								pih.setEntity(inv);
								pih.save();
								System.out.println("FATURA BAŞARIYLA KAYDEDILDI. ID ="+inv.getId());
								fw.append(inv.getId().toString() + "\n");
								fw.flush();
							} catch (Exception e) {
								log.info("Fatura kaydedilemedi. id = :{0}. Sebebi=:{1}", inv.getId(),e);
							}
		
						}
						islenenFaturaSayisi++;
						lastId = inv.getId();
					}

				}
				
			}
			System.out.println("islenenFaturaSayisi = " + islenenFaturaSayisi);
			fw.flush();
			fw.close();
		} catch (Exception e) {
			System.out.println("Hata olustu: "+ e);
		}
	}

}
