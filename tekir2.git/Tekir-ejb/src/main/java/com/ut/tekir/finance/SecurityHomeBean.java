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

import java.math.BigDecimal;

import javax.ejb.Stateful;

import org.jboss.seam.ScopeType;
import org.jboss.seam.annotations.Begin;
import org.jboss.seam.annotations.Create;
import org.jboss.seam.annotations.In;
import org.jboss.seam.annotations.Name;
import org.jboss.seam.annotations.Out;
import org.jboss.seam.annotations.Scope;
import org.jboss.seam.core.Conversation;

import com.ut.tekir.entities.Security;
import com.ut.tekir.entities.SecurityCoupon;
import com.ut.tekir.entities.SecurityType;
import com.ut.tekir.framework.BaseConsts;
import com.ut.tekir.framework.CalendarManager;
import com.ut.tekir.framework.EntityBase;

@Stateful
@Name("securityHome")
@Scope(value = ScopeType.CONVERSATION)
public class SecurityHomeBean extends EntityBase<Security> implements SecurityHome<Security> {
	@In
    CalendarManager calendarManager;
	
    @Create
	@Begin(flushMode = org.jboss.seam.annotations.FlushModeType.MANUAL)
	public void init() {
		// TODO Auto-generated method stub
		
	}
	
	@Out(required = false)
	public Security getSecurity() {
		// TODO Auto-generated method stub
		return getEntity();
	}

	
	@In(required = false)
	public void setSecurity(Security security) {
		// TODO Auto-generated method stub
		setEntity(security);
	}
	
	@Override
    public void createNew() {
        log.debug("Yeni Bono");

        entity = new Security();
        entity.setCouponCount(new Integer(0));
        entity.setRate(BigDecimal.ZERO);
        entity.setSecurityType(SecurityType.DiscountBond);
        entity.setUnit(new Integer(0));
        entity.setIssueDate(calendarManager.getCurrentDate());
        entity.setMaturityDate(calendarManager.getCurrentDate());
        entity.setCurrency(BaseConsts.SYSTEM_CURRENCY_CODE);
    }
	
	public void createNewLine() {
        manualFlush();
        if (entity == null) {
            return;
        }
        SecurityCoupon sc = new SecurityCoupon();
        sc.setRate(entity.getRate());
        sc.setSecurity(entity);
        entity.getCoupons().add(sc);
        log.debug("yeni item eklendi");
        entity.setCouponCount(entity.getCouponCount()+1);
    }
	public void deleteLine(SecurityCoupon coupon) {
        manualFlush();
        if (entity == null) {
            return;
        }
        log.debug("Kayıt Silinecek : {0}", coupon);
        entity.getCoupons().remove(coupon);
        entity.setCouponCount(entity.getCouponCount()-1);
    }
    public void deleteLine(Integer ix) {
        manualFlush();
        if (entity == null) {
            return;
        }
        log.debug("Kayıt Silinecek IX : {0}", ix);
        log.debug("Toplam Kayıt : {0}", entity.getCoupons().size());
        Object o = entity.getCoupons().remove(ix.intValue());
        log.debug("Silinen : {0}", o);
        log.debug("Toplam Kayıt : {0}", entity.getCoupons().size());
        entity.setCouponCount(entity.getCouponCount()-1);
    }
    public void resetCoupon(){
		if(entity.getCoupons().size()!=0){
			for(int i= entity.getCoupons().size();i>0;i--)
			{
				deleteLine(i-1);
			}
		}
	}
    //TODO:islevi nedir, security coupon tanimi nerede yapilir ?
    public void initCouponRate(){
    	if(entity.getCoupons().size()!=0){
    		for(int i=0;i<entity.getCoupons().size();i++){
    			entity.getCoupons().get(i).setRate(entity.getRate());
    		}
    	}
    }
	
	@Override
	public String save(){
		Boolean hata = false;
		try{			
			if(entity.getCouponCount()>=1){
				int coupons = entity.getCouponCount();
				for(int i=0;i<coupons;i++){
					if(entity.getCoupons().get(i).getBeginDate() == null && entity.getCoupons().get(i).getEndDate() == null){
						facesMessages.add("Kuponların Başlangıç ve Bitiş Tarihi Girilmelidir!");
						hata = true;
					}
					if(entity.getCoupons().get(i).getBeginDate().after(entity.getCoupons().get(i).getEndDate())){
						facesMessages.add((i+1)+". Kuponun Başlangıç Tarihi Bitiş Tarihinden Önce Olmalıdır!");
						hata = true;
					}
				}
				for(int i=0;i<coupons-1;i++){
					if(entity.getCoupons().get(i).getEndDate().after(entity.getCoupons().get(i+1).getBeginDate())){
						facesMessages.add((i+1)+". Kuponun Başlangıç tarihi ve"+(i+2)+". Kuponun Bitiş Tarihleri Tutarsız!");
						hata = true;
					}
				}
				if(!(entity.getCoupons().get(0).getBeginDate().equals(entity.getIssueDate()))){
					facesMessages.add("İhrac Tarihi ile İlk Kuponun Başlangıç Tarihi Aynı Olmalıdır!");
					hata = true;
				}
				if(!(entity.getCoupons().get(entity.getCoupons().size()-1).getEndDate().equals(entity.getMaturityDate()))){
					facesMessages.add("İtfa Tarihi İle Son Kuponun Bitiş Tarihi Aynı Olmalıdır!");
					hata = true;
				}
			}
			if(hata){
				throw new RuntimeException("Hata!");
			}
			
		}catch (Exception e) {
            log.error("Hata :", e);
            return BaseConsts.FAIL;
        }
		String res = super.save();
		return res;
	}
	
	public void manualFlush() {
        Conversation.instance().changeFlushMode(org.jboss.seam.annotations.FlushModeType.MANUAL);
    }

	
}
