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
import java.util.Date;
import java.util.List;

import javax.ejb.Stateful;

import org.jboss.seam.Component;
import org.jboss.seam.ScopeType;
import org.jboss.seam.annotations.Begin;
import org.jboss.seam.annotations.Create;
import org.jboss.seam.annotations.Factory;
import org.jboss.seam.annotations.In;
import org.jboss.seam.annotations.Name;
import org.jboss.seam.annotations.Observer;
import org.jboss.seam.annotations.Out;
import org.jboss.seam.annotations.Scope;
import org.jboss.seam.annotations.datamodel.DataModel;
import org.jboss.seam.core.Conversation;

import com.ut.tekir.entities.BankCard;
import com.ut.tekir.entities.Pos;
import com.ut.tekir.entities.PosBankList;
import com.ut.tekir.entities.PosCardList;
import com.ut.tekir.finance.BankCardSuggestion;
import com.ut.tekir.framework.EntityHome;

/**
 * Pos tanımlamalarının yapıldığı home sınıfı..
 * @author sinan.yumak
 */
@Stateful
@Name("posHome")
@Scope(value=ScopeType.CONVERSATION)
public class PosHomeBean extends EntityHome<Pos> implements PosHome<Pos> {
    
    @Create 
    @Begin(join=true,flushMode = org.jboss.seam.annotations.FlushModeType.MANUAL)
    public void init() {
    }

    @DataModel("posList")
    private List<Pos> entityList;
    
    private Integer selectedIndex;
    
    @SuppressWarnings("unchecked")
	@Factory("posList")
    public void initPosList() {
        log.debug("Pos Listesi hazırlanıyor...");
        
        setEntityList(getEntityManager().createQuery("select p from Pos p")
        			 .getResultList());
    }
    
    @Out(required=false)
    public Pos getPos() {
        return getEntity();
    }

    @In(required=false)
    public void setPos(Pos pos) {
        setEntity( pos );
    }

    @Override
    public void createNew() {
        log.debug("Yeni Pos");
        entity = new Pos();
        entity.setActive(true);
    }

    @Override
    public void createNewLine() {
    	manualFlush();
    	log.info("Creating new line...");
    	PosCardList pcl = new PosCardList();
    	pcl.setPos(entity);

    	Calendar calendar = Calendar.getInstance();
		calendar.add(Calendar.YEAR, 5);

		pcl.setBeginDate(new Date());
		pcl.setEndDate(calendar.getTime());
		
    	entity.getPosCardList().add(pcl);
    }

    @Override
    public void createNewBankLine() {
    	manualFlush();
    	log.info("Creating new bank line...");
    	PosBankList pbl = new PosBankList();
    	pbl.setOwner(entity);
    	
    	Calendar calendar = Calendar.getInstance();
    	calendar.add(Calendar.YEAR, 5);
    	
    	pbl.setBeginDate(new Date());
    	pbl.setEndDate(calendar.getTime());
    	
    	entity.getPosBankList().add(pbl);
    }
    
    public void deleteLine(Integer ix) {
        manualFlush();
        if (entity == null) {
            return;
        }
        log.debug("Kayıt Silinecek IX : {0}", ix);
        entity.getPosCardList().remove(ix.intValue());
    }

    public void deleteBankLine(Integer ix) {
    	manualFlush();
    	if (entity == null) {
    		return;
    	}
    	log.debug("Kayıt Silinecek IX : {0}", ix);
    	entity.getPosBankList().remove(ix.intValue());
    }

    @Override
    public List<Pos> getEntityList() {
        return entityList;
    }

    public void setEntityList(List<Pos> entityList) {
        this.entityList = entityList;
    }

    public void manualFlush() {
        Conversation.instance().changeFlushMode(org.jboss.seam.annotations.FlushModeType.MANUAL);
    }

	public void openBankCardPopup(Integer selectedIndex) {
		this.selectedIndex = selectedIndex;
		BankCardSuggestion bcs = (BankCardSuggestion)Component.getInstance("bankCardSuggestion",true);
		bcs.setObserverString("posHome.addSelectedCardsToList");
	}

	@Observer("posHome.addSelectedCardsToList")
    public void addSelectedCardsToList(List<BankCard> cardList) {
		log.info("Adding selected cards to list...");

		Calendar calendar = Calendar.getInstance();
		calendar.add(Calendar.YEAR, 5);
		
		if (cardList != null) {
			PosCardList pcl = entity.getPosCardList().get(selectedIndex);

			if ( cardList.size() >0 ) {
				pcl.setCard(cardList.get(0));
			}
			PosCardList newItem = null;
			for (int i=1;i<cardList.size();i++) {
				newItem = new PosCardList();
		    	newItem.setPos(entity);
		    	newItem.setCard(cardList.get(i));
		    	newItem.setBeginDate(new Date());
				newItem.setEndDate(calendar.getTime());
		    	
		    	entity.getPosCardList().add(newItem);
			}
		}
    }
    
}