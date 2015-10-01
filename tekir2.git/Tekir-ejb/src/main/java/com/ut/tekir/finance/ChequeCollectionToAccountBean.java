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

import java.util.ArrayList;
import java.util.List;

import javax.ejb.Stateful;

import org.hibernate.criterion.DetachedCriteria;
import org.hibernate.criterion.Restrictions;
import org.jboss.seam.ScopeType;
import org.jboss.seam.annotations.In;
import org.jboss.seam.annotations.Name;
import org.jboss.seam.annotations.Observer;
import org.jboss.seam.annotations.Scope;
import org.jboss.seam.core.Conversation;

import com.ut.tekir.entities.Account;
import com.ut.tekir.entities.Cheque;
import com.ut.tekir.entities.ChequeStatus;
import com.ut.tekir.entities.DocumentType;
import com.ut.tekir.framework.BaseConsts;
import com.ut.tekir.framework.BrowserBase;
import com.ut.tekir.framework.CalendarManager;

/**
 *
 * @author huseyin
 */
@Stateful
@Name("chequeCollectionToAccount")
@Scope(ScopeType.CONVERSATION)
public class ChequeCollectionToAccountBean extends BrowserBase<Cheque, ChequeFilterModel> implements ChequeCollectionToAccount<Cheque, ChequeFilterModel> {
	
	private Account account;
	private List<Cheque> chequeList;
	@In
    ChequeSuggestion chequeSuggestion;
    @In
    CalendarManager calendarManager;
    @In 
    ChequeAction chequeAction;

    @Override
    public ChequeFilterModel newFilterModel() {
    	ChequeFilterModel fm = new ChequeFilterModel();
        fm.setBeginDate(calendarManager.getLastTenDay());
        fm.setEndDate(calendarManager.getCurrentDate());
        return fm;
    }
    
    public String savePosition() {

    	int counter = 0;
    	manualFlush();
    	
    	try {
    		if(getChequeList() == null){
        		facesMessages.add("Sonuç Listesi Boş. Kayıt Yapılamaz !");
        		throw new RuntimeException("Hatalar Var! Kayıt gerçekleşmedi!");
    		}
    		List <ChequeParamModel> cpml = new ArrayList<ChequeParamModel> ();
    		for(Cheque cheque : getChequeList()){
    			
    			if (cheque.getChecked()) {
    				ChequeParamModel cpm = new ChequeParamModel();
    				cpm.setCheque(cheque);
    				cpm.setDocumentId(cheque.getId());
    				cpm.setNewStatus(ChequeStatus.KasaTahsilat);
    				cpm.setDocumentType(DocumentType.ChequeCollection);
    				counter++;
    				
    				if (getAccount() != null) {
    					cpm.setToAccount(getAccount());
    				} else {
    					facesMessages.add("Kayıt Gerçekleşmedi. Paranın aktarılacağı kasayı seçmediniz.");
    		            throw new RuntimeException("Hatalar Var! Kayıt gerçekleşmedi!");
    				}
    				cpml.add(cpm);
    				cheque.setChecked(false);
    			}
    		}
    		
    		if(counter == 0){
    			
    			facesMessages.add("Çek Seçmelisiniz !");
				throw new RuntimeException("Hatalar Var! Kayıt gerçekleşmedi!");
				
    		}
    		
    		chequeAction.changePosition(cpml);

        	getEntityManager().flush();

    		refreshResults();
    		
		} catch (Exception e) {
			log.error("Hata :", e);
            return BaseConsts.FAIL;
		}
    	
        facesMessages.add("#{messages['general.message.record.SaveSuccess']}");	
        return BaseConsts.SUCCESS;
    }

    @Observer("refreshBrowser:com.ut.tekir.entities.ChequeFilterModel")
    public void refreshResults() {
        log.debug("Uyarı geldi resultSet tazeleniyor");
        if (getChequeList() == null || getChequeList().isEmpty()) {
            return;
        } 
        search();
    }
    
    public String close() {
        return BaseConsts.SUCCESS;
    }

	public Account getAccount() {
		return account;
	}

	public void setAccount(Account account) {
		this.account = account;
	}
    
    private void manualFlush() {
        Conversation.instance().changeFlushMode(org.jboss.seam.annotations.FlushModeType.MANUAL);
    }
    
    @Override
    public void search() {
    	
		chequeSuggestion.setClientCheque(Boolean.TRUE);
		chequeSuggestion.setTargetStatus(ChequeStatus.KasaTahsilat);
		
		chequeSuggestion.setReferenceNo(filterModel.getReferenceNo());
		chequeSuggestion.setBankName(filterModel.getBankName());
		chequeSuggestion.setBankBranch(filterModel.getBankBranch());
		chequeSuggestion.setContact(filterModel.getContact());
		chequeSuggestion.setHistoryBeginDate(filterModel.getBeginDate());
		chequeSuggestion.setHistoryEndDate(filterModel.getEndDate());
		chequeSuggestion.setChequeOwner(filterModel.getChequeOwner());
    	
    	setChequeList(chequeSuggestion.selectChequeList());
    }
    
	public List<Cheque> getChequeList() {
		return chequeList;
	}

	public void setChequeList(List<Cheque> chequeList) {
		this.chequeList = chequeList;
	}

}
