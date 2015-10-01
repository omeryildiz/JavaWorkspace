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
import java.io.FileNotFoundException;
import java.util.Map;
import java.util.HashMap;
import net.sf.jasperreports.engine.JRException;


import javax.ejb.Stateful;

import org.jboss.seam.ScopeType;
import org.jboss.seam.annotations.Begin;
import org.jboss.seam.annotations.Create;
import org.jboss.seam.annotations.In;
import org.jboss.seam.annotations.Name;
import org.jboss.seam.annotations.Out;
import org.jboss.seam.annotations.Scope;
import org.jboss.seam.core.Conversation;

import com.ut.tekir.entities.ProductTransfer;
import com.ut.tekir.entities.ProductTransferItem;
import com.ut.tekir.entities.ProductType;
import com.ut.tekir.entities.PosCommision;
import com.ut.tekir.entities.PosCommisionDetail;
import com.ut.tekir.framework.BaseConsts;
import com.ut.tekir.framework.CalendarManager;
import com.ut.tekir.framework.EntityBase;
import com.ut.tekir.framework.JasperHandlerBean;
import com.ut.tekir.framework.SequenceManager;
import com.ut.tekir.framework.SequenceType;

/**
 *
 * @author rustem
 */
@Stateful
@Name("posCommisionHome")
@Scope(value = ScopeType.CONVERSATION)
public class PosCommisionHomeBean extends EntityBase<PosCommision> implements PosCommisionHome<PosCommision> {



    @In(create = true)
    SequenceManager sequenceManager;
    @In
    CalendarManager calendarManager;

    @In(create=true)
    GeneralSuggestion generalSuggestion;

    
    @In
	JasperHandlerBean jasperReport;

    @Create
    @Begin(join=true, flushMode = org.jboss.seam.annotations.FlushModeType.MANUAL)
    public void init() {

    }

    @Out(required = false)
    public PosCommision getPosCommision() {
        return getEntity();
    }

    @In(required = false)
    public void setPosCommision(PosCommision posCommision) {
        setEntity(posCommision);
    }

    @Override
    public void createNew() {
        log.debug("Yeni PosCommision");

        entity = new PosCommision();
        entity.setActive(true);
        entity.setStartDate(calendarManager.getCurrentDate());
        entity.setEndDate(calendarManager.getCurrentDate());
        
    }

    @Override
    public String save() {
        
    	String res = null;

        // Kontrol yapılacak

        res = super.save();
		return res;
    }


   

    public void createNewLine() {
        manualFlush();
        if (entity == null) {
            return;
        }

        log.debug("EntityManager.FlushMode : #0", entityManager.getFlushMode());

        PosCommisionDetail pcd = new PosCommisionDetail();
        pcd.setOwner(entity);
        entity.getDetailList().add(pcd);
        log.debug("yeni detay eklendi");
    }


     @Override
    public String delete() {

        entityManager.createQuery("delete Pos_Commision where " +
        						  "ID = :Id")
			        			 .setParameter("Id", entity.getId())
			        			 .executeUpdate();

        return super.delete();
    }

    public void deleteLine(PosCommisionDetail pcd) {
        manualFlush();
        if (entity == null) {
            return;
        }
        log.debug("Kayıt Silinecek : {0}", pcd);
        entity.getDetailList().remove(pcd);
    }

    public void deleteLine(Integer ix) {
        manualFlush();
        if (entity == null) {
            return;
        }
        log.debug("Kayıt Silinecek IX : {0}", ix);
        log.debug("Toplam Kayıt : {0}", entity.getDetailList().size());
        Object o = entity.getDetailList().remove(ix.intValue());
        log.debug("Silinen : {0}", o);
        log.debug("Toplam Kayıt : {0}", entity.getDetailList().size());
    }

    

    public void manualFlush() {
        Conversation.instance().changeFlushMode(org.jboss.seam.annotations.FlushModeType.MANUAL);
    }

	@SuppressWarnings("unchecked")
	public void print() {
//		try {
//
//			log.info("Product Transfer Print");
//
//			Map params = new HashMap();
//			params.put("invoice", entity.getId());
//			jasperReport.printObjectToPDF( "Depo_Sevk_Irsaliyesi_" + entity.getReference(), "depo_sevk_irsaliyesi", params, entity.getItems());
//
//		} catch (JRException ex) {
//			log.error("Invoice print error", ex);
//			facesMessages.add("İrsaliye yazdırılamadı!");
//		} catch (FileNotFoundException e) {
//			log.error("Invoice template not found", e);
//			facesMessages.add("İrsaliye yazdırma şablonu bulunamadı!");
//		}
	}

}
