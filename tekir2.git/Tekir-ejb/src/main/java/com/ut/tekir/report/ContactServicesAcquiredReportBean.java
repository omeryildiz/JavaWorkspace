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

package com.ut.tekir.report;

import java.math.BigDecimal;
import java.math.BigInteger;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.ejb.Stateful;
import javax.persistence.Query;

import org.jboss.seam.ScopeType;
import org.jboss.seam.annotations.In;
import org.jboss.seam.annotations.Name;
import org.jboss.seam.annotations.Scope;

import com.ut.tekir.configuration.SystemConfiguration;
import com.ut.tekir.contact.ContactSuggestion;
import com.ut.tekir.entities.DocumentType;
import com.ut.tekir.entities.ProductType;
import com.ut.tekir.entities.TradeAction;
import com.ut.tekir.framework.BrowserBase;
import com.ut.tekir.general.GeneralSuggestion;

/**
 * @author rustem
 *
 */
@Stateful
@Name("contactServicesAcquiredReport")
@Scope(value=ScopeType.SESSION)
public class ContactServicesAcquiredReportBean extends BrowserBase<ContactServiceAcquiredViewModel,ContactServiceAcquiredFilterModel> implements ContactServiceAcquiredReport<ContactServiceAcquiredViewModel,ContactServiceAcquiredFilterModel> {

	@In
	ContactSuggestion contactSuggestion;
	@In
	GeneralSuggestion generalSuggestion;
	
	@Override
	public ContactServiceAcquiredFilterModel newFilterModel(){
		return new ContactServiceAcquiredFilterModel();
	}
	
	@Override
	@SuppressWarnings("unchecked")
	public void search(){
		if(entityList != null){
			entityList.clear();
		}
		else{
			entityList = new ArrayList<ContactServiceAcquiredViewModel>();
		}
		
		StringBuilder firstInnerQuery = new StringBuilder();
		
		firstInnerQuery.append("select ti.DOCUMENT_TYPE_ID as documentType, ti.SERIAL as fis_no, ti.REFERENCE as belge_no, " +
								"pro.BARCODE1, ti.TXNDATE as tarih, pro.NAME as urun_adi, tid.QUANTITY as adet, " +
								"tid.GRAND_TOTAL_VALUE/tid.QUANTITY as birim_fiyat, tid.GRAND_TOTAL_LCYVAL as net_tutar, " +
								"co.FULLNAME as cari_adi, co.COMPANY, co.PERSON,co.ID as con_id, ti.TRADE_ACTION as ACTTYPE, " +
								"co.code as cari_kodu, ti.ISACTIVE, pro.CODE as urun_kodu, ti.UPDATE_USER as kasiyer, " +
								"us.FULL_NAME as tezgahtar, us.ID as clerk_id, ti.DELIVERY_DATE as teslim_tarihi, " +
								"pro.id as urun_id, ti.ID as fisId, pro.PRODUCT_TYPE as productType,ti.TRADE_ACTION as tradeAction,tid.GRAND_TOTAL_VALUE as value, tid.GRAND_TOTAL_CCY as doviz, ti.WORKBUNCH_ID " +
								"from TEKIR_INVOICE_DETAIL tid "+
								"inner join TEKIR_INVOICE ti on ti.ID = tid.OWNER_ID "+
								"inner join CONTACT co on co.ID=ti.CONTACT_ID "+
								"inner join PRODUCT pro on pro.ID = tid.PRODUCT_ID "+
								"inner join USERS us on ti.CLERK_ID=us.ID " +
								"where (pro.PRODUCT_TYPE = 1 or pro.PRODUCT_TYPE = 2) ");
	
	
		log.debug("first innerquery :{0}", firstInnerQuery.toString());

		StringBuilder secondInnerQuery = new StringBuilder();
		
		secondInnerQuery.append("select ti.DOCUMENT_TYPE_ID as documentType, ti.SERIAL as fis_no, ti.REFERENCE as belge_no, " +
								"pro.BARCODE1, ti.TXNDATE as tarih, pro.NAME as urun_adi, tid.QUANTITY as adet, " +
								"tid.GRAND_TOTAL_VALUE/tid.QUANTITY as birim_fiyat, tid.GRAND_TOTAL_LCYVAL as net_tutar, " +
								"co.FULLNAME as cari_adi, co.COMPANY, co.PERSON,co.ID as con_id, ti.TRADE_ACTION as ACTTYPE, " +
								"co.code as cari_kodu, ti.ISACTIVE, pro.CODE as urun_kodu, ti.UPDATE_USER as kasiyer, " +
								"us.FULL_NAME as tezgahtar, us.ID as clerk_id, ti.DELIVERY_DATE as teslim_tarihi, " +
								"pro.id as urun_id, ti.ID as fisId, pro.PRODUCT_TYPE as productType,ti.TRADE_ACTION as tradeAction,tid.GRAND_TOTAL_VALUE as value, tid.GRAND_TOTAL_CCY as doviz, ti.WORKBUNCH_ID " +
								"from TEKIR_ORDER_NOTE_DETAIL tid "+
								"inner join TEKIR_ORDER_NOTE ti on ti.ID = tid.OWNER_ID "+
								"inner join CONTACT co on co.ID=ti.CONTACT_ID "+
								"inner join PRODUCT pro on pro.ID = tid.PRODUCT_ID "+								
								"inner join USERS us on ti.CLERK_ID=us.ID " +
								"left outer  join PAYMENT_TABLE_DETAIL ptd on ptd.OWNER_ID = ti.PAYMENT_TABLE_ID "+
								"where (pro.PRODUCT_TYPE = 1 or pro.PRODUCT_TYPE = 2) ");
		
		log.debug("second innerquery :{0}", secondInnerQuery.toString());
		
		StringBuilder outerQuery = new StringBuilder();
		
		outerQuery.append("select tablo.tarih, tablo.con_id, tablo.documentType, tablo.fis_no, tablo.urun_adi, tablo.adet, " +
				"tablo.birim_fiyat, tablo.net_tutar, tablo.cari_adi, tablo.COMPANY, tablo.PERSON, tablo.ACTTYPE, " +
				"tablo.cari_kodu, tablo.belge_no, tablo.BARCODE1, tablo.urun_kodu, tablo.kasiyer, tablo.tezgahtar, " +
				"tablo.clerk_id, tablo.teslim_tarihi, tablo.urun_id, tablo.fisId, tablo.productType, tablo.tradeAction, tablo.value, tablo.doviz from ( ( ");
		
		outerQuery.append(firstInnerQuery.toString());
		outerQuery.append(" ) union all ");
		outerQuery.append(" ( ");
		outerQuery.append(secondInnerQuery.toString());
		outerQuery.append(" ) ) as tablo ");
		
		log.debug("outerQuery :{0}", outerQuery.toString());
		
		if(filterModel.getBeginDate() != null || filterModel.getEndDate() != null || filterModel.getContact() !=null ){
			outerQuery.append(" where tablo.ISACTIVE=true ");
			
		}

		if(filterModel.getBeginDate() != null){
			outerQuery.append(" and tablo.tarih >=:beginDate ");
		}
		
		if(filterModel.getEndDate() != null){
			outerQuery.append(" and tablo.tarih <=:endDate ");
		}
		
		if(filterModel.getContact() != null){
			outerQuery.append(" and tablo.con_id =:conId ");
		}
		
		if(filterModel.getClerk() != null){
			outerQuery.append(" and tablo.clerk_id =:clerkId ");
		}
		
		if(isNotEmpty(filterModel.getBarcode1())){
			outerQuery.append(" and tablo.barcode1 =:barcode1 ");
		}

		if(filterModel.getProduct() != null){
			outerQuery.append(" and tablo.urun_id =:urunId ");
		}
		
		if(filterModel.getCashier() != null){
			outerQuery.append(" and tablo.kasiyer =:cashier ");
		}
		
		if(filterModel.getWorkBunch() != null){
			outerQuery.append(" and tablo.WORKBUNCH_ID =:workBunchId ");
		}
		
		outerQuery.append(" order by tablo.con_id desc  , tablo.tarih desc ");
		
		Query query = entityManager.createNativeQuery(outerQuery.toString());
		
		if(filterModel.getBeginDate() != null){
			query.setParameter("beginDate", filterModel.getBeginDate());
		}
		
		if(filterModel.getEndDate() != null){
			query.setParameter("endDate", filterModel.getEndDate());
		}
		
		if(filterModel.getContact() != null){
			query.setParameter("conId", filterModel.getContact().getId());
		}
		
		if(filterModel.getClerk() != null){
			query.setParameter("clerkId", filterModel.getClerk().getId());
		}
		
		if(filterModel.getCashier() != null){
			query.setParameter("cashier", filterModel.getCashier());
		}
		
		if(isNotEmpty(filterModel.getBarcode1())){
			query.setParameter("barcode1", filterModel.getBarcode1());
		}
		
		if(filterModel.getProduct() != null){
			query.setParameter("urunId", filterModel.getProduct().getId());
		}
		
		if(filterModel.getWorkBunch() != null){
			query.setParameter("workBunchId", filterModel.getWorkBunch().getId());
		}
		
		List<Object[]> resultList = query.getResultList();
		
		for (Object[] item : resultList){
			ContactServiceAcquiredViewModel model = new ContactServiceAcquiredViewModel();
			
			model.setDate((Date)item[0]);
			Long conId = new BigDecimal((BigInteger)item[1]).longValue();
			model.setContact(contactSuggestion.findContact(conId));
			model.setDocumentType(DocumentType.fromString((Integer)item[2]));
			model.setFis_no((String)item[3]);
			model.setUrun_adi((String)item[4]);
			model.setAdet((Double)item[5]);
			model.setBirim_fiyat((Double)item[6]);
			model.setNet_tutar((BigDecimal)item[7]);
			model.setCari_adi((String)item[8]);
			model.setCompany((String)item[9]);
			model.setPerson((Boolean)item[10]);
			model.setActtype((Integer)item[11]);
			model.setCari_kodu((String)item[12]);
			model.setBelge_no((String)item[13]);
			model.setBarcode((String)item[14]);
			model.setUrun_kodu((String)item[15]);
			model.setKasiyer((String)item[16]);
			Long clerkid = new BigDecimal((BigInteger)item[18]).longValue();
			model.setTezgahtar(generalSuggestion.findUser(clerkid));
			model.setClerk_id(clerkid);
			model.setDeliveryDate((Date)item[19]);
			model.setUrunId(new BigDecimal((BigInteger)item[20]).longValue());
			model.setFisId(new BigDecimal((BigInteger)item[21]).longValue());
			model.setProductType(ProductType.fromInteger((Integer)item[22]));
			model.setTradeAction(TradeAction.fromInteger((Integer)item[23]));
			model.setValue((BigDecimal)item[24]);
			model.setDoviz((String)item[25]);
			
			entityList.add(model);
		}
	}
	
	@SuppressWarnings("unchecked")
	public void pdf(){
		
		Map params = new HashMap();
		
		if(filterModel.getBeginDate() != null){
			params.put("pBDate", filterModel.getBeginDate());
		}
		
		if(filterModel.getEndDate() != null){
			params.put("pEDate", filterModel.getEndDate());
		}
		
		if(filterModel.getContact() != null){
			params.put("pConId", filterModel.getContact().getId());
		}
		
		if(isNotEmpty(filterModel.getCashier())){
			params.put("pCashier", filterModel.getCashier());
		}
		
		if(filterModel.getClerk()!=null){
			params.put("pClerkId", filterModel.getClerk().getId());
		}
		
		if(isNotEmpty(filterModel.getBarcode1())){
			params.put("pBarcode", filterModel.getBarcode1());
		}
		
		if(filterModel.getProduct() != null){
			params.put("pProductId", filterModel.getProduct().getId());
		}
		
		if(filterModel.getWorkBunch() != null){
			params.put("pWorkBunchId", filterModel.getWorkBunch().getId());
		}
		
		params.put("pProductLabel", SystemConfiguration.PRODUCTLABEL); 
		
		execPdf("cariye_verilen_hizmetler_listesi", "Cariye_Verilen_Hizmet_Raporu", params);
		
	}	
	
	@SuppressWarnings("unchecked")
	public void xls(){
		
		Map params = new HashMap();
		
		if(filterModel.getBeginDate() != null){
			params.put("pBDate", filterModel.getBeginDate());
		}
		
		if(filterModel.getEndDate() != null){
			params.put("pEDate", filterModel.getEndDate());
		}
		
		if(filterModel.getContact() != null){
			params.put("pConId", filterModel.getContact().getId());
		}
		
		if(isNotEmpty(filterModel.getCashier())){
			params.put("pCashier", filterModel.getCashier());
		}
		
		if(filterModel.getClerk()!=null){
			params.put("pClerkId", filterModel.getClerk().getId());
		}
		
		if(isNotEmpty(filterModel.getBarcode1())){
			params.put("pBarcode", filterModel.getBarcode1());
		}
		
		if(filterModel.getProduct() != null){
			params.put("pProductId", filterModel.getProduct().getId());
		}
		
		if(filterModel.getWorkBunch() != null){
			params.put("pWorkBunchId", filterModel.getWorkBunch().getId());
		}
		
		params.put("pProductLabel", SystemConfiguration.PRODUCTLABEL); 
		
		execXls("cariye_verilen_hizmetler_listesi_xls", "Cariye_Verilen_Hizmet_Raporu", params);
		
	}	
	
}
