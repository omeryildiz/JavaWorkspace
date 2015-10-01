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
import java.text.DateFormat;
import java.util.ArrayList;
import java.util.List;

import javax.ejb.TransactionAttribute;
import javax.ejb.TransactionAttributeType;

import org.jboss.seam.annotations.Begin;
import org.jboss.seam.annotations.Create;
import org.jboss.seam.annotations.In;
import org.jboss.seam.annotations.Observer;
import org.jboss.seam.annotations.Transactional;
import org.jboss.seam.core.Conversation;
import org.jboss.seam.faces.FacesMessages;
import org.jboss.seam.international.StatusMessage.Severity;

import com.ut.tekir.docmatch.DocumentMatchFilterModel;
import com.ut.tekir.docmatch.DocumentMatchModel;
import com.ut.tekir.docmatch.DocumentMatchProvider;
import com.ut.tekir.docmatch.DocumentMatchResultModel;
import com.ut.tekir.docmatch.MatchProviderRegistry;
import com.ut.tekir.docmatch.PaymentCurrencyHelper;
import com.ut.tekir.entities.Cheque;
import com.ut.tekir.entities.ChequeHistory;
import com.ut.tekir.entities.ChequeStatus;
import com.ut.tekir.entities.DocumentMatch;
import com.ut.tekir.entities.DocumentType;
import com.ut.tekir.entities.Money;
import com.ut.tekir.entities.MoneySet;
import com.ut.tekir.entities.Payment;
import com.ut.tekir.entities.PaymentItem;
import com.ut.tekir.entities.PaymentItemCheque;
import com.ut.tekir.entities.PaymentItemPromissoryNote;
import com.ut.tekir.entities.PaymentType;
import com.ut.tekir.entities.PromissoryNote;
import com.ut.tekir.finance.print.DocumentPrint;
import com.ut.tekir.finance.print.PaymentPrint;
import com.ut.tekir.framework.BaseConsts;
import com.ut.tekir.framework.CalendarManager;
import com.ut.tekir.framework.EntityBase;
import com.ut.tekir.framework.JasperHandlerBean;
import com.ut.tekir.framework.OptionManager;
import com.ut.tekir.framework.SequenceManager;
import com.ut.tekir.general.GeneralSuggestion;

/**
 * Tahsilat-tediye home bileşenlerinin ortak olarak kullanacakları
 * sınıftır.
 * 
 * @author sinan.yumak
 *
 */
public class PaymentHomeBeanBase<E> extends EntityBase<Payment> implements PaymentHomeBase<Payment>{
	private PaymentItem selectedItem;
	protected Boolean duzenlemeMi = Boolean.FALSE;
	protected Boolean firmaCekiMi = Boolean.FALSE;
	protected Boolean firmaSenediMi = Boolean.FALSE;
	protected Boolean isEditable = Boolean.TRUE;
	
    private int lineIndex;

	@In(create = true)
	SequenceManager sequenceManager;
	@In(create=true)
	CalendarManager calendarManager;
	@In
	AccountTxnAction accountTxnAction;
	@In
	FinanceTxnAction financeTxnAction;
	@In
	ChequeSuggestion chequeSuggestion;
	@In
	PromissorySuggestion promissorySuggestion;
	@In
	ChequeAction chequeAction;
	@In
	ChequePopupHome chequePopupHome;
	@In
	PromissoryPopupHome promissoryPopupHome;
	@In
	PromissoryAction promissoryAction;
	@In
	JasperHandlerBean jasperReport;
    @In
    OptionManager optionManager;
    @In
    GeneralSuggestion generalSuggestion;

    private DocumentMatchProvider matchProvider;
    private PaymentCurrencyHelper currencyHelper;
    
    private List<List<DocumentMatch>> matches;
    
	@Create
	@Begin(join = true, flushMode = org.jboss.seam.annotations.FlushModeType.MANUAL)
	public void init() {
	}

	@Override
	public void createNew() {
		log.debug("Yeni Payment");
		entity = new Payment();
		entity.setActive(true);
		entity.setDate(calendarManager.getCurrentDate());
		isEditable = true;
		// TODO: Default Kasa eklenecek.
	}

	@Override
	@TransactionAttribute(TransactionAttributeType.REQUIRED)
	public String save() {
		String res = BaseConsts.FAIL;
		manualFlush();
		recalculate();
		// TODO: Çoklu dil desteği.

		try {
			makeValidations();

			res = super.save();
			
			if(res.equals(BaseConsts.SUCCESS)){

				saveMatches();

				//FIXME: bu kodlar burada ne arıyor?
				for (PaymentItem pItem : entity.getItems()) {
	
					if (pItem instanceof PaymentItemCheque) {
	
						Cheque pic = ((PaymentItemCheque) pItem).getCheque();
						ChequeParamModel cpm;
	
						if (pic != null) {
	
							pic.setContact(entity.getContact());
	
							if (! chequeAction.alreadySaved(pic, DocumentType.Collection, entity.getId())) {
	
								cpm = new ChequeParamModel();
								cpm.setCheque(pic);
								cpm.setDocumentId(entity.getId());
								cpm.setDocumentType(DocumentType.Collection);
								cpm.setNewStatus(ChequeStatus.Portfoy);
								cpm.setWorkBunch(pItem.getWorkBunch());
								cpm.setDocumentSerial(entity.getSerial());
								cpm.setToAccount(entity.getAccount());
								cpm.setToContact(entity.getContact());
								chequeAction.changePosition(cpm);
	
								entityManager.merge(pic);
							} 
	
						}
	
					} else if (pItem instanceof PaymentItemPromissoryNote) {
	
						PromissoryNote pipn = ((PaymentItemPromissoryNote) pItem).getPromissoryNote();
						PromissoryParamModel cpm;
	
						if (pipn != null) {
		        			
							pipn.setContact(entity.getContact());
	
							if (! promissoryAction.alreadySaved(pipn, DocumentType.Collection, entity.getId())) {
	
								cpm = new PromissoryParamModel();
								cpm.setPromissory(pipn);
								cpm.setDocumentId(entity.getId());
								cpm.setDocumentType(DocumentType.Collection);
								cpm.setNewStatus(ChequeStatus.Portfoy);
								cpm.setWorkBunch(pItem.getWorkBunch());
								cpm.setDocumentSerial(entity.getSerial());
								cpm.setToContact(entity.getContact());
								cpm.setToAccount(entity.getAccount());
								promissoryAction.changePosition(cpm);
	
								entityManager.merge(pipn);
							} 
		        			
						}
	
					}
	
				}

				financeTxnAction.savePayment(entity);
				accountTxnAction.savePayment(entity);

				if (entity.getItems() != null && entity.getItems().size() != 0) {

					for (PaymentItem pItem : entity.getItems()) {

						if (pItem.getLineType() != PaymentType.Cash) {

							financeTxnAction.savePaymentItem(pItem);
						}
					} 
				}
				entityManager.flush();
			}
		
		} catch (Exception e) {
			FacesMessages.afterPhase();
			facesMessages.clear();
			facesMessages.add(e.getMessage());
			log.error("Hata :", e);
			res = BaseConsts.FAIL;
		}
		return res;
	}

	public void makeValidations() {
		if (entity.getItems().size() == 0) {
			facesMessages.add("En az bir detay girmelisiniz!");
			throw new RuntimeException("Hatalar Var! Kayıt gerçekleşmedi!");
		}

		for (PaymentItem item : entity.getItems()) {
			if (item.getAmount().getValue().compareTo(BigDecimal.ZERO) < 0) {
				facesMessages.add("Sıfırdan büyük bir değer girmelisiniz");
				throw new RuntimeException("Hatalar Var! Kayıt gerçekleşmedi!");
			}
        }
	}

	private boolean dailyRatesDefinedValidation(MoneySet money) {
		//günün kurları alınmış mı kontrolü
		if (!money.getCurrency().equals(BaseConsts.SYSTEM_CURRENCY_CODE)) {
			if (!generalSuggestion.kurKontrol(entity.getDate())) {
				throw new RuntimeException("Günlük kurlar tanımlanmamış!");
			}
		}
		return true;
	}

	protected void saveMatches() {
		log.info("Saving matches...");
		getMatchProvider().save( getMatchModels() );
	}

	private List<DocumentMatchModel> getMatchModels() {
		List<DocumentMatchModel> matchModels = new ArrayList<DocumentMatchModel>();
		for (int i=0;i<entity.getItems().size();i++) {
			DocumentMatchModel mm = new DocumentMatchModel();
			mm.setActualDoc(getReference().getItems().get(i));
			mm.setMatches(getMatches(i));
			matchModels.add(mm);
		}
		return matchModels;
	}
	
    public DocumentMatchFilterModel openMatchPopup(int index) {
    	log.info("Opening match popup for index: #0", index);
    	return getMatchProvider().getDocumentMatchPopupFilters(entity.getItems().get(index));
    }
    
	public void deleteMatchLine(int index) {
		DocumentMatch dm = getMatches(lineIndex).get(index);
		
		getMatchProvider().deleteMatch(dm);
		getMatches(lineIndex).remove(index);
	}

    @Observer("paymentHome:selectInvoice")
    public void selectMatch(DocumentMatchResultModel matchModel) {
    	manualFlush();

    	DocumentMatch match = getMatchProvider().createDocumentMatch(matchModel);
    	getSelectedItemMatches().add(match);
    }

	//FIXME:bu ne biçim kod ya? Bunları tahsilat-tediye ortak çalışabilecek bi yapıya çekmek
	//lazım.
    /**
     * Satırları tek tek dolaşarak tüm fiş toplamlarını yeniden hesaplar...
     */
    public void recalculate() {
    	manualFlush();
    	try {
    		entity.setTotalAmount(new Money());
    		
    		getCurrencyHelper().fillCurrencyRates();
    		
    		//Kur ve Döviz toplamları hesaplanıyor
    		for (PaymentItem it : entity.getItems()) {
    			calculateTotals(it);
    		}
		} catch (Exception e) {
			log.error("Hesaplamalar yapılırken hata meydana geldi. Sebebi :{0}", e);
			facesMessages.add(Severity.ERROR, "{0}",e);
		}
    }
    
	public void calculateTotals(PaymentItem item){
    	BigDecimal totalAmount = BigDecimal.ZERO;

        if (item.getAmount().getValue().compareTo(BigDecimal.ZERO) < 0) {
            facesMessages.add("Sıfırdan büyük bir değer girmelisiniz");
            throw new RuntimeException("Hatalar Var! Kayıt gerçekleşmedi!");
        }

        dailyRatesDefinedValidation(item.getAmount());

        getCurrencyHelper().setLocalAmountOf(item.getAmount());

        totalAmount = entity.getTotalAmount().getValue().add( item.getAmount().getLocalAmount() );
        entity.getTotalAmount().setValue(totalAmount);
    }

	public void controllItem() {
		if (getEntity() == null)
			return;

		try {
			if (getEntity().getId() != null) {
				if (entity.getItems() != null && entity.getItems().size() != 0) {
					for (PaymentItem item : entity.getItems()) {

						if(item.getId() == null){
							return;
						}
						if (item instanceof PaymentItemCheque) {
							
							Cheque cheque = ((PaymentItemCheque) item).getCheque();

							if (cheque.getLastStatus() != cheque.getPreviousStatus()) {

								isEditable = false;
								break;
							}
							
						} else if (item instanceof PaymentItemPromissoryNote) {
							
							PromissoryNote promissory = ((PaymentItemPromissoryNote) item).getPromissoryNote();

							if (promissory.getLastStatus() != promissory.getPreviousStatus()) {

								isEditable = false;
								break;
							}
						}
					}
				} else {
					isEditable = true;
				}

			}
		} catch (Exception e) {
			log.error("Hata :", e);
		}
	}

	@Override
	//@Transactional
	public String delete() {

		financeTxnAction.deletePayment(entity);
		accountTxnAction.deletePayment(entity);

		getMatchProvider().delete( getMatchModels() );
		
 		for (int i=0; i < entity.getItems().size(); i++) {
 			deleteItem(i);
 		}
 			
		if (entity == null) {
			return BaseConsts.FAIL;
		}

		try {
			getEntityManager().remove(getReference());
			getEntityManager().flush();
		} catch (Exception e) {
			log.debug("Hata : #0", e);
			facesMessages.add("#{messages['general.message.record.DeleteFaild']}");
			return BaseConsts.FAIL;
		}

		log.debug("Entity Removed : {0} ", entity);
		entity = null;
		facesMessages.add("#{messages['general.message.record.DeleteSuccess']}");

		raiseRefreshBrowserEvent();
		endCurrentConversation();
		return BaseConsts.SUCCESS;
	}
	
	public void deleteItem(int i){
		
		PaymentItem item = entity.getItems().get(i);
		
		try {
			//if (item instanceof PaymentItemCheque) {
			if(item.getLineType().equals(PaymentType.Cheque)){
        		Cheque cheque = ((PaymentItemCheque) item).getCheque();
        		entityManager.remove(entityManager.getReference(PaymentItem.class, item.getId()));
        		entityManager.remove(entityManager.getReference(Cheque.class, cheque.getId()));

			}
			//else if(item instanceof PaymentItemPromissoryNote) {
			else if(item.getLineType().equals(PaymentType.PromissoryNote)){
				PromissoryNote promissory = ((PaymentItemPromissoryNote) item).getPromissoryNote();
				
				entityManager.remove(entityManager.getReference(PaymentItem.class, item.getId()));
        		entityManager.remove(entityManager.getReference(PromissoryNote.class, promissory.getId()));
			}
			else{
				entityManager.remove(entityManager.getReference(PaymentItem.class, item.getId()));

			}
			
			
			
		} catch (Exception e) {
			facesMessages.add("Silmede hata!");
			//log.debug("silme hatası #0", e.getMessage());
			log.info("silme hatası #0 ", e);
			log.debug("hata sebeo #0 :",e);
		}
	}

	public void createNewLine() {
		manualFlush();
		if (entity == null) {
			return;
		}
		PaymentItem it = new PaymentItem();
		it.setOwner(entity);
		it.setLineType(PaymentType.Cash);
		it.getAmount().setCurrency(BaseConsts.SYSTEM_CURRENCY_CODE);

		entity.getItems().add(it);

		log.debug("yeni item eklendi");
	}

	/**
	 * 0 -> Müşteri Senedi 1 -> Firma Senedi
	 */
	public void popupPromissorySelect(int clientOrFirm) {

		if (clientOrFirm == 0) {

			promissorySuggestion
					.setCallerObserveString("paymentHome:popupNotice:client.promissory");
			promissorySuggestion.setClientPromissory(Boolean.TRUE);
			promissorySuggestion.setTargetStatus(ChequeStatus.Portfoy); // Portföy
																		// e
																		// almak
																		// istiyoruz

			if (promissorySuggestion.getPromissoryList() != null
					&& promissorySuggestion.getPromissoryList().size() != 0) {
				promissorySuggestion.getPromissoryList().clear();
			}

			log.debug("Client promissory selected.");

		} else {

			promissorySuggestion
					.setCallerObserveString("paymentHome:popupNotice:firm.promissory");
			promissorySuggestion.setClientPromissory(Boolean.FALSE);
			promissorySuggestion.setTargetStatus(ChequeStatus.Portfoy); // Portföy
																		// e
																		// almak
																		// istiyoruz

			if (promissorySuggestion.getPromissoryList() != null
					&& promissorySuggestion.getPromissoryList().size() != 0) {
				promissorySuggestion.getPromissoryList().clear();
			}

			log.debug("Firm promissory selected.");
		}

	}

	/**
	 * 0 -> Müşteri Çeki 1 -> Firma Çeki
	 */
	public void popupChequeSelect(int clientOrFirm) {

		if (clientOrFirm == 0) {

			chequeSuggestion
					.setCallerObserveString("paymentHome:popupNotice:client.cheque");
			chequeSuggestion.setClientCheque(Boolean.TRUE);
			chequeSuggestion.setTargetStatus(ChequeStatus.Portfoy); // Portföy e
																	// almak
																	// istiyoruz

			if (chequeSuggestion.getChequeList() != null
					&& chequeSuggestion.getChequeList().size() != 0) {
				chequeSuggestion.getChequeList().clear();
			}

			log.debug("Client cheque selected.");

		} else {

			chequeSuggestion.setCallerObserveString("paymentHome:popupNotice:firm.cheque");
			chequeSuggestion.setClientCheque(Boolean.FALSE);
			chequeSuggestion.setTargetStatus(ChequeStatus.Portfoy); // Portföy e
																	// almak
																	// istiyoruz

			if (chequeSuggestion.getChequeList() != null
					&& chequeSuggestion.getChequeList().size() != 0) {
				chequeSuggestion.getChequeList().clear();
			}

			log.debug("Firm cheque selected.");
		}
	}

	public void popupChequeCreate() {

		chequePopupHome.setCallerObserveString("paymentHome:popupNotice:client.cheque");
		chequePopupHome.createNew();
		chequePopupHome.getCheque().setContact(entity.getContact());
		duzenlemeMi = Boolean.FALSE;
	}

	public void popupPromissoryCreate() {

		promissoryPopupHome.setCallerObserveString("paymentHome:popupNotice:client.promissory");
		promissoryPopupHome.createNew();
		promissoryPopupHome.getPromissory().setContact(entity.getContact());
		duzenlemeMi = Boolean.FALSE;
	}

	@Observer("paymentHome:popupNotice:client.cheque")
	public void selectClientCheque(Cheque cheque) {

		if (entity == null) {
			return;
		}

		PaymentItemCheque pitc = null;

		if (duzenlemeMi == Boolean.FALSE) {

			pitc = new PaymentItemCheque();

			cekSatirininDetaylariniAta(pitc, cheque);

			entity.getItems().add(pitc);
			log.debug("yeni item eklendi");
		} else {

			pitc = (PaymentItemCheque) entity.getItems().get(lineIndex);
			cekSatirininDetaylariniAta(pitc, cheque);
			duzenlemeMi = Boolean.FALSE;
		}

	}

	public void cekSatirininDetaylariniAta(PaymentItemCheque itemCheque,Cheque cheque) {

		itemCheque.setOwner(entity);
		itemCheque.getAmount().setCurrency(cheque.getMoney().getCurrency());
		itemCheque.setCheque(cheque);
		itemCheque.setAmount(new MoneySet(cheque.getMoney()));
		itemCheque.getAmount().setValue(cheque.getMoney().getValue());
		itemCheque.getAmount().setCurrency(cheque.getMoney().getCurrency());

		String date = DateFormat.getDateInstance().format(cheque.getMaturityDate());
		
		String info = "Çek No: " + cheque.getReferenceNo()
						+" ,Banka: "+cheque.getBankName()+"/"+cheque.getBankBranch()                        
                        +" ,Hesap No: "+ cheque.getAccountNo()
						+" ,Sahibi: "+ cheque.getChequeOwner()
						+" ,Vade: "+ date;
		
		if (cheque.getInfo().length() > 0) {
			info = info + ", " + cheque.getInfo();
		}
		
		if (info.length() > 255) {
			info.substring(0, 255);
		}
		
		itemCheque.setInfo(info);

	}

	public Boolean cekMi(int rowKey) {

		PaymentItem item = entity.getItems().get(rowKey);
		
		if (item instanceof PaymentItemCheque) {

			PaymentItemCheque paymentItemCheque = (PaymentItemCheque) item;

			if (paymentItemCheque.getCheque().getClientCheque() != Boolean.TRUE) {

				firmaCekiMi = Boolean.FALSE;
			} else {
				firmaCekiMi = Boolean.TRUE;
			}

			return Boolean.TRUE;
		}

		return Boolean.FALSE;
	}

	public Boolean senetMi(int rowKey) {

		PaymentItem item = entity.getItems().get(rowKey);
		
		if (item instanceof PaymentItemPromissoryNote) {

			PaymentItemPromissoryNote paymentItemPromissory = (PaymentItemPromissoryNote) item;

			if (paymentItemPromissory.getPromissoryNote().getClientPromissoryNote() != Boolean.TRUE) {

				firmaSenediMi = Boolean.FALSE;
			} else {
				firmaSenediMi = Boolean.TRUE;
			}

			return Boolean.TRUE;
		}

		return Boolean.FALSE;
	}

	@Observer("paymentHome:popupNotice:client.promissory")
	public void selectClientPromissory(PromissoryNote promissory) {

		if (entity == null) {
			return;
		}

		PaymentItemPromissoryNote pipn = null;

		if (duzenlemeMi == Boolean.FALSE) {

			pipn = new PaymentItemPromissoryNote();

			senetSatirininDetaylariniAta(pipn, promissory);

			entity.getItems().add(pipn);
			
			log.debug("yeni item eklendi");
		} else {

			pipn = (PaymentItemPromissoryNote) entity.getItems().get(lineIndex);
			senetSatirininDetaylariniAta(pipn, promissory);
			duzenlemeMi = Boolean.FALSE;
		}

	}

	public void senetSatirininDetaylariniAta(PaymentItemPromissoryNote promissoryNote, PromissoryNote promissory) {

		promissoryNote.setOwner(entity);
		promissoryNote.getAmount().setCurrency(promissory.getMoney().getCurrency());
		promissoryNote.setPromissoryNote(promissory);
		promissoryNote.setAmount(new MoneySet(promissory.getMoney()));
		promissoryNote.getAmount().setValue(promissory.getMoney().getValue());

		String date = DateFormat.getDateInstance().format(promissory.getMaturityDate());

        String info = "Senet No: " + promissory.getReferenceNo() 
        				+" ,Keşide Yeri: "+ promissory.getPaymentPlace()
						+" ,Sahibi: "+ promissory.getPromissorynoteOwner()
						+" ,Vade: "+ date;

		if (promissory.getInfo().length() > 0) {
			info = info + ", " + promissory.getInfo();
		}
		
		if (info.length() > 255) {
			info.substring(0, 255);
		}
		
		promissoryNote.setInfo(info);
		
	}

	public void findCheque(int rowKey) {

		lineIndex = rowKey;
		PaymentItem item = entity.getItems().get(rowKey);
		
		PaymentItemCheque pic = (PaymentItemCheque) item;
		chequePopupHome.setCheque(pic.getCheque());
		chequePopupHome.setCallerObserveString("paymentHome:popupNotice:client.cheque");
		duzenlemeMi = Boolean.TRUE;
	}

	public void findPromissory(int rowKey) {
		log.info("Finding promissory with rowKey:#0", rowKey);
		lineIndex = rowKey;

		PaymentItem item = entity.getItems().get(rowKey);
		
		PaymentItemPromissoryNote pipn = (PaymentItemPromissoryNote) item;
		promissoryPopupHome.setPromissory(pipn.getPromissoryNote());
		promissoryPopupHome.setCallerObserveString("paymentHome:popupNotice:client.promissory");
		duzenlemeMi = Boolean.TRUE;
	}

	public void deleteLine(Integer ix) {
		manualFlush();
		if (entity == null) {
			return;
		}
		
		getMatchProvider().deleteMatch( getMatches(ix.intValue()) );
		
		if(entity.getId() != null){
			
			deleteItem(ix.intValue());
		}

		log.debug("Kayıt Silinecek IX : {0}", ix);
		log.debug("Toplam Kayıt : {0}", entity.getItems().size());
		Object o = entity.getItems().remove(ix.intValue());
		log.debug("Silinen : {0}", o);
		log.debug("Toplam Kayıt : {0}", entity.getItems().size());
	}

	public void print() {
		try {
			DocumentPrint dp = PaymentPrint.instance(getEntity());
			dp.print();
		} catch (Exception ex) {
            log.error("Hata oluştu : {0} ", ex );
            facesMessages.add("Hata : #0", ex.getMessage());
        }
	}

	public void manualFlush() {
		Conversation.instance().changeFlushMode(org.jboss.seam.annotations.FlushModeType.MANUAL);
	}

	public PaymentItem getSelectedItem() {
		if (entity != null && entity.getItems().size() != 0) return entity.getItems().get(lineIndex);
		return null;
	}

	public void setSelectedItem(PaymentItem selectedItem) {
		this.selectedItem = selectedItem;
	}

	@Override
	public void setEntity(Payment entity) {
		super.setEntity(entity);
		controllItem();
	}

	@Override
	public void setId(Long id) {
        if (entity != null) {
            return;
        }
		super.setId(id);

		initDocumentMatches();
	}

	private PaymentCurrencyHelper getCurrencyHelper() {
		if (currencyHelper == null) {
			currencyHelper = PaymentCurrencyHelper.instance(getEntity(), getMatches());
		}
		return currencyHelper;
	}

	public BigDecimal getRemainingMatchTotal() {
		if (getSelectedItem() == null) return null;
		BigDecimal itemAmount = getSelectedItem().getAmount().getValue();
		return itemAmount.subtract( getMatchTotal() );
	}

	public BigDecimal getMatchTotal() {
		return getCurrencyHelper().convertToCurrency(getMatchLocaleAmtTotal(), 
												BaseConsts.SYSTEM_CURRENCY_CODE, 
												getSelectedItem().getAmount().getCurrency());
	}
	
	private BigDecimal getMatchLocaleAmtTotal() {
		BigDecimal total = BigDecimal.ZERO;
		for (DocumentMatch dm : getSelectedItemMatches()) {
			getCurrencyHelper().setLocalAmountOf(dm.getAmount());
			total = total.add(dm.getAmount().getLocalAmount());
		}
		return total;
	}
	
	private void initDocumentMatches() {
		log.info("Initializing document matches...");
		if (getEntity() == null || getEntity().getId() == null) return;
		for (PaymentItem pi : entity.getItems()) {
			getMatches().add(getMatchProvider().findMatches(pi));
		}
	}

	public List<List<DocumentMatch>> getMatches() {
		if (matches == null) {
			matches = new ArrayList<List<DocumentMatch>>(); 
		}
		return matches;
	}

	public List<DocumentMatch> getMatches(int index) {
		List<DocumentMatch> result;
		if (getMatches().size() != index+1) {
			getMatches().add(new ArrayList<DocumentMatch>());
		}
		result = getMatches().get(index);
		log.debug("selected matches size: #0", result.size());
		return result;
	}
	
	public List<DocumentMatch> getSelectedItemMatches() {
		return getMatches(lineIndex);
	}
	
	public DocumentMatchProvider getMatchProvider() {
		if (matchProvider == null) {
			matchProvider = MatchProviderRegistry.getProvider(DocumentType.Collection);
		}
		return matchProvider;
	}
	
	public void setMatches(List<List<DocumentMatch>> matches) {
		this.matches = matches;
	}

	public int getLineIndex() {
		return lineIndex;
	}

	public void setLineIndex(int lineIndex) {
		this.lineIndex = lineIndex;
	}

	public Boolean isFirmaCekiMi() {
		return firmaCekiMi;
	}

	public Boolean getFirmaCekiMi() {
		return firmaCekiMi;
	}

	public void setFirmaCekiMi(Boolean firmaCekiMi) {
		this.firmaCekiMi = firmaCekiMi;
	}

	public Boolean getFirmaSenediMi() {
		return firmaSenediMi;
	}

	public void setFirmaSenediMi(Boolean firmaSenediMi) {
		this.firmaSenediMi = firmaSenediMi;
	}

	public Boolean getIsEditable() {
		return isEditable;
	}

	public void setIsEditable(Boolean isEditable) {
		this.isEditable = isEditable;
	}

}
