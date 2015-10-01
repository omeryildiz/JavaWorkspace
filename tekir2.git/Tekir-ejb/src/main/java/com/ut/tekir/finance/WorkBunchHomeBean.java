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

import java.math.BigInteger;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import javax.ejb.Stateful;
import javax.persistence.Query;

import org.jboss.seam.ScopeType;
import org.jboss.seam.annotations.Begin;
import org.jboss.seam.annotations.Create;
import org.jboss.seam.annotations.In;
import org.jboss.seam.annotations.Name;
import org.jboss.seam.annotations.Out;
import org.jboss.seam.annotations.Scope;
import org.jboss.seam.core.Conversation;

import com.ut.tekir.entities.DocumentType;
import com.ut.tekir.entities.FinanceAction;
import com.ut.tekir.entities.TradeAction;
import com.ut.tekir.entities.WorkBunch;
import com.ut.tekir.framework.CalendarManager;
import com.ut.tekir.framework.EntityBase;
import com.ut.tekir.framework.SequenceManager;



//FIXME: buradaki kur hesaplama ve toplam hesaplama rutinlerini 
//diğer sınıflara (tahsilat, tediye, hareket fişleri vb. yerlere aktarmalı.)

/**
 * Kasa çıkış fişi home bileşenidir.
 * @author sinan.yumak
 */
@Stateful
@Name("workBunchHome")
@Scope(value = ScopeType.CONVERSATION)
public class WorkBunchHomeBean extends EntityBase<WorkBunch> implements WorkBunchHome<WorkBunch> {

    @In(create = true)
    SequenceManager sequenceManager;
    @In
    CalendarManager calendarManager;
    
    /* (non-Javadoc)
     * @see com.ut.tekir.finance.WorkBunchHome#workBunchHistory(java.lang.Long)
     */
    @SuppressWarnings("unchecked")
	public List<WorkBunchViewModel> workBunchHistory(Long wbid){
    	
    	List<WorkBunchViewModel> wbvm = new ArrayList<WorkBunchViewModel>();
    	StringBuilder txnFinance = new StringBuilder();
    	
    	txnFinance.append("select " +
    						"	f_txn.DOCUMENTID, f_txn.SERIAL, f_txn.REFERENCE, " +
    						"	f_txn.INFO, f_txn.DOCUMENT_TYPE, f_txn.FINANCE_ACTION, ti.TRADE_ACTION, " +
    						"	f_txn.TXN_DATE, f_txn.CODE " +
    						"from FINANCE_TXN f_txn " +
    						"left join TEKIR_INVOICE ti on f_txn.SERIAL = ti.SERIAL " +
    						"where " +
    						"	ti.SERIAL is not null " +
    						"	and f_txn.WORK_BUNCH_ID = " + wbid);
    	
    	
    	StringBuilder txnProduct = new StringBuilder();
    	txnProduct.append("select " +
    						"	pt.DOCUMENTID, pt.SERIAL, pt.REFERENCE, " +
    						"	pt.INFO, pt.DOCUMENT_TYPE, pt.FINANCE_ACTION, pt.TRADE_ACTION, " +
    						"	pt.TXN_DATE, pt.CODE  " +
    						"from PRODUCT_TXN pt " +
    						"where " +
    						"	pt.WORK_BUNCH_ID = " + wbid);
    	
    	
    	StringBuilder txnBank = new StringBuilder();
    	txnBank.append("select " +
    					"	bt.DOCUMENTID, bt.SERIAL, bt.REFERENCE, " +
    					"	bt.INFO, bt.DOCUMENT_TYPE, null, null, " +
    					"	bt.TXNDATE, bt.CODE " +
    					"from BANK_TXN bt " +
						"where " +
						"	bt.WORK_BUNCH_ID = " + wbid);
    	
    	
    	StringBuilder queryString = new StringBuilder();
    	queryString.append(txnFinance.toString() + " UNION ALL " + txnProduct.toString() + " UNION ALL " + txnBank);
    	
    	Query query = entityManager.createNativeQuery(queryString.toString());
    	
    	List<Object[]> resultList = query.getResultList();
    	
    	for (Object[] item : resultList){
    		WorkBunchViewModel model = new WorkBunchViewModel();
    		model.setDocId(((BigInteger) item[0]).longValue());
    		model.setSerial((String) item[1]);
    		model.setReference((String) item[2]);
    		model.setInfo((String) item[3]);
    		model.setDocumentType(findProperEnumValue(DocumentType.class, (Integer) item[4]));
    		if (item[5] != null){
    			model.setFinanceAction(findProperEnumValue(FinanceAction.class, (Integer) item[5]));
    		}
    		if (item[6] != null){
    			model.setTradeAction(findProperEnumValue(TradeAction.class, (Integer) item[6]));
    		}
    		model.setDate((Date) item[7]);
    		model.setCode((String) item[8]);
    		wbvm.add(model);
    	}
    	
    	
    	
    	return wbvm;
    }
    
    
    
    /**
     * parametre olarak gecilen DocumentType, FinanceAction veya TradeAction 
     * enum'larinin icinde donerek, database'den gelen val parametresine denk gelen 
     * ordinal degere sahip olanini geri doner
     * 
     * @param <E>
     * @param enumType
     * @param val
     * @return 
     */
    private <E extends Enum<E>> E findProperEnumValue(Class<E> enumType, Integer val){
    	if (enumType == null || !enumType.isEnum()){
    		return null;
    	}
    	
    	E[] elements = enumType.getEnumConstants();
    	
    	for (E element : elements){
    		if (val.equals(element.ordinal()))
    			return element;
    	}
    	return null;
    }
    
	@Create 
    @Begin(join=true,flushMode = org.jboss.seam.annotations.FlushModeType.MANUAL)
    public void init() {

    }

    @Out(required = false)
	public WorkBunch getWorkBunch() {
		return getEntity();
	}

    @In(required = false)
	public void setWorkBunch(WorkBunch workBunch) {
    	setEntity(workBunch);
	}

    @Override
    public void createNew() {
        log.debug("New WorkBunch");

        entity = new WorkBunch();
        entity.setActive(true);
    }
/*
    @Override
    @Transactional
    public String save() {
		log.info("Saving WorkBunch.");
		String result = BaseConsts.FAIL;
    	try {
    		
            result = super.save();
            
//            if(result.equals(BaseConsts.SUCCESS)){
//            	accountTxnAction.saveDocument(entity);
//            }
            
    	} catch (Exception e) {
            facesMessages.add(Utils.getExceptionMessage(e));
            log.error("Hata :#0", e);
            return BaseConsts.FAIL;
        }
        return result;
    }
    
    @Override
    @Transactional
    public String delete() {
    	String result = BaseConsts.FAIL;
    	try {
//    		accountTxnAction.deleteDocument(entity);
    		result = super.delete();
		} catch (Exception e) {
			log.error("Döküman silinirken hata oluştu. Sebebi :{0}", e.getMessage());
			facesMessages.add(Severity.ERROR, "Döküman silinirken hata oluştu. Sebebi :{0}",e);
		}
        return result;
    }
*/

    public void manualFlush() {
        Conversation.instance().changeFlushMode(org.jboss.seam.annotations.FlushModeType.MANUAL);
    }

}
