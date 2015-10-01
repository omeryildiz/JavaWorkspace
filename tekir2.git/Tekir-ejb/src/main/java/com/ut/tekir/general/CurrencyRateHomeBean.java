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

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import javax.ejb.Stateful;
import javax.persistence.TemporalType;

import org.jboss.seam.ScopeType;
import org.jboss.seam.annotations.Begin;
import org.jboss.seam.annotations.Create;
import org.jboss.seam.annotations.In;
import org.jboss.seam.annotations.Logger;
import org.jboss.seam.annotations.Name;
import org.jboss.seam.annotations.Scope;
import org.jboss.seam.faces.FacesMessages;
import org.jboss.seam.log.Log;

import com.ut.tekir.entities.CurrencyPair;
import com.ut.tekir.entities.CurrencyRate;
import com.ut.tekir.external.TcmbRateReader;
import com.ut.tekir.framework.BaseConsts;
import com.ut.tekir.framework.CalendarManager;
import com.ut.tekir.framework.EntityHome;

/**
 * Currency Rate Home Bean
 *
 * @author selman
 */
@Stateful
@Name("currencyRateHome")
@Scope(value = ScopeType.CONVERSATION)
public class CurrencyRateHomeBean extends EntityHome<CurrencyRate> implements CurrencyRateHome {

	@Logger
	Log log;
	@In
	TcmbRateReader tcmbRateReader;

	@In
	CalendarManager calendarManager;
	@In
	FacesMessages facesMessages;

	private Date rateDate;//hangi tarihe ait kur bilgisi olacağı
	private List<CurrencyRate> currencyRateList;
	private Boolean islemYapilmisMi = Boolean.FALSE;
	@SuppressWarnings("unused")
	private Date today;

	@Create
	@Begin(join = true)
	public void init() {
		setRateDate(getToday());
		showRates();
	}

	/**
	 * Girilmiş olan tarihe ait rate'leri toplar. Eğer kayıt yoksa boş alanlar olarak açar.
	 */
	@SuppressWarnings("unchecked")
	public void showRates() {
		FacesMessages.afterPhase();
		facesMessages.clear();
		log.debug("Currency Rate Listesi hazırlanıyor. Tarih : {0}",
				getRateDate());

		if (getRateDate() == null){
			facesMessages.add("#{messages['currencyRate.message.chooseDate']}");
			return;
		}
		//TODO:tatil günü kontrolü de eklenebilir
		if (getRateDate().compareTo(getToday())>0){
			facesMessages.add("#{messages['currencyRate.message.wrongDate']}");
			return;
		}

		setCurrencyRateList(getEntityManager().createQuery(
				"select c from CurrencyRate c where date = :date")
				.setParameter("date", getRateDate(), TemporalType.DATE)
				.getResultList());

		if(currencyRateList.size() == 0)
			facesMessages.add("#{messages['currencyRate.message.noDefinitionFound']}");

		//aktif ve zayıf dövizi TL olan döviz çiftlerini bul
		List<CurrencyPair> cpl = getEntityManager().createQuery(
				"select c from CurrencyPair c where active = :active and weakCurrency.code=:weakCurrency")
				.setParameter("active", true)
				.setParameter("weakCurrency", BaseConsts.SYSTEM_CURRENCY_CODE)
				.getResultList();

		//cpl icin degerleri 0, tarihlerini bugün olarak setle ki ekrana gelsin
		for (CurrencyPair cp : cpl) {
			boolean b = false;
			for (CurrencyRate cr : currencyRateList) {
				if (cr.getCurrencyPair().equals(cp)) {
					b = true;
					break;
				}
			}

			if (!b) {
				CurrencyRate cr = new CurrencyRate();
				cr.setCurrencyPair(cp);
				cr.setDate(getRateDate());
				cr.setBid(BigDecimal.ZERO);
				cr.setAsk(BigDecimal.ZERO);
				getCurrencyRateList().add(cr);
			}
		}
	}

	@SuppressWarnings("unchecked")
	public String saveTCMB() {
		List<CurrencyRate> cRateList;
		List<CurrencyPair> cpl = getEntityManager().createQuery(
				"select c from CurrencyPair c where active = :active and weakCurrency.code=:weakCurrency")
				.setParameter("active", true)
				.setParameter("weakCurrency", BaseConsts.SYSTEM_CURRENCY_CODE)
				// .setMaxResults(100)
				// .setHint("org.hibernate.cacheable", true)
				.getResultList();

		// Tanimli aktif ve TL nin zayif kur oldugu doviz cifti var mi ki
		if (cpl.size() == 0) {
			facesMessages.add("#{messages['currencyRate.message.NoDefinition']}");
			return BaseConsts.FAIL;
		}

		// aktif doviz ciftleri icin xml den oku
		try {
			cRateList = tcmbRateReader.getCurrencies(cpl);
		} catch (Exception e) {
			log.info("Kur kayit etmede hata olustu", e);
			facesMessages.add("#{messages['genel.mesaj.kayit.beklenmeyenHata']}");
			return BaseConsts.FAIL;
		}

		log.debug("TCMB günlük kur listesi saklanacak...");
		if (cRateList.size() > 0) {
			try {

				//TODO: Geçici çözüm. Düzeltilmeli ( Hakan )
				getEntityManager().createQuery("delete CurrencyRate where date = :date")
					.setParameter("date", getToday(), TemporalType.DATE)
					.executeUpdate();
				//Hakan

				for (CurrencyRate cr : cRateList) {
						getEntityManager().persist(cr);
				}
				getEntityManager().flush();
			} catch (Exception e) {
				log.info("Kur kayit etmede hata olustu", e);
				facesMessages.add("#{messages['genel.mesaj.kayit.beklenmeyenHata']}");
				return BaseConsts.FAIL;
			}
			setRateDate(getToday());
			showRates();
			facesMessages.add("#{messages['general.message.record.TCMBSaveSuccess']}");
			return BaseConsts.SUCCESS;
		}

		else {
			facesMessages.add("#{messages['genel.mesaj.kayit.beklenmeyenHata']}");
			return BaseConsts.FAIL;

		}
	}

    @Override
    public String save() {

		log.debug("CurrencyRate Listesi saklanacak...");

		if (currencyRateList == null) {
			facesMessages.add("#{messages['currencyRate.message.NoDefinition']}");
			return BaseConsts.SUCCESS;
		}

		for (CurrencyRate cr : currencyRateList) {

			if (cr.getId() == null) {
				getEntityManager().persist(cr);
			} else {
				getEntityManager().merge(cr);
			}
		}
		getEntityManager().flush();
		facesMessages.add("#{messages['general.message.record.SaveSuccess']}");
		return BaseConsts.SUCCESS;
	}

	@Override
    public String delete() {

		log.debug("CurrencyRate Listesi silinecek...");

		if (currencyRateList == null) {
			facesMessages
					.add("#{messages['currencyRate.message.NoDefinition']}");
			return BaseConsts.SUCCESS;
		}

		try {
			for (CurrencyRate cr : currencyRateList) {
				if (cr.getId() != null) {
					getEntityManager().remove(cr);
				}
			}
			getEntityManager().flush();
		} catch (Exception e) {
			log.debug("Hata : #0", e);
			facesMessages.add("#{messages['general.message.record.DeleteFaild']}");
			return BaseConsts.FAIL;
		}
		setRateDate(null);

		facesMessages.add("#{messages['general.message.record.DeleteSuccess']}");
		return BaseConsts.SUCCESS;
	}

	// Bugun e ait kurlar daha onceden kaydedilmis mi
	@SuppressWarnings("unchecked")
	public Boolean kurKontrol() {
		List<CurrencyRate> curRateList = new ArrayList<CurrencyRate>();
		curRateList = getEntityManager().createQuery("select cr from CurrencyRate cr where date=:date")
				.setParameter("date", getToday(), TemporalType.DATE)
				.getResultList();

			if (curRateList != null && curRateList.size() > 0)
				return true;//bugünün kurları alınmış

		return false;
	}

	public Date getRateDate() {
		return rateDate;
	}

	public void setRateDate(Date rateDate) {
		this.rateDate = rateDate;
	}

	public List<CurrencyRate> getCurrencyRateList() {
		return currencyRateList;
	}

	public void setCurrencyRateList(List<CurrencyRate> currencyRateList) {
		this.currencyRateList = currencyRateList;
	}

	public Boolean getIslemYapilmisMi() {
		return islemYapilmisMi;
	}

	public void setIslemYapilmisMi(Boolean islemYapilmisMi) {
		this.islemYapilmisMi = islemYapilmisMi;
	}

	public Date getToday() {
		return calendarManager.getCurrentDate();
	}

	public void setToday(Date today) {
		this.today = today;
	}


}
