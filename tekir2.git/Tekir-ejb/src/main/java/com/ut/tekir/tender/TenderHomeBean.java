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

package com.ut.tekir.tender;


import java.util.ArrayList;
import java.util.List;

import javax.ejb.Stateful;

import org.jboss.seam.ScopeType;
import org.jboss.seam.annotations.Begin;
import org.jboss.seam.annotations.Create;
import org.jboss.seam.annotations.FlushModeType;
import org.jboss.seam.annotations.In;
import org.jboss.seam.annotations.Name;
import org.jboss.seam.annotations.Out;
import org.jboss.seam.annotations.Scope;
import org.jboss.seam.annotations.Transactional;

import com.ut.tekir.entities.ProductDetail;
import com.ut.tekir.entities.ProductType;
import com.ut.tekir.entities.Tender;
import com.ut.tekir.entities.TenderCurrencyRate;
import com.ut.tekir.entities.TenderCurrencySummary;
import com.ut.tekir.entities.TenderDetail;
import com.ut.tekir.entities.TenderDetailBase;
import com.ut.tekir.entities.TenderTaxSummary;
import com.ut.tekir.framework.BaseConsts;
import com.ut.tekir.framework.CalendarManager;
import com.ut.tekir.framework.SequenceManager;
import com.ut.tekir.framework.SequenceType;

/*
 * Notlar:
 
 * Teklif sayfasında bir satır için düzenleme popupu 
   açıldığında o satırın yedeği 
   alınarak yedek üzerinde işlemler yapılır. Böylece 
   eleman vazgeçmek isterse hiçbir sorun olmaz.

 * Her popunun geçerlilik kontrolünü yapan makeValidations() metotları var.
 */

/**
 * @author sinan.yumak
 *
 */
@Stateful
@Name("tenderHome")
@Scope(ScopeType.CONVERSATION)
public class TenderHomeBean extends TenderCalculationHomeBean<Tender> implements TenderHome<Tender>{
    @In
    SequenceManager sequenceManager;
	@In
	CalendarManager calendarManager;
	
	private int selectedIndex;
	private Boolean isEdit = Boolean.TRUE;
	private TenderDetail selectedDetail = new TenderDetail();

	private int selectedDiscountOrExpenseIndex;
	private Boolean isDiscountOrExpenseEdit = Boolean.TRUE;
	private TenderDetail selectedDiscountOrExpense = new TenderDetail();

	private List<TenderDetail> selectedChildList = new ArrayList<TenderDetail>();
	
	@Create
	@Begin(join=true,flushMode= FlushModeType.MANUAL)
	public void init() {
		//FIXME: seçili detayı burada scope a bağlamak gerek.
	}

	@Out(required=false)
	public Tender getTender() {
		return getEntity();
	}

	@In(required=false)
	public void setTender(Tender tender) {
		setEntity(tender);
	}

	@Override
	public void createNew() {
		log.debug("#{messages['logMessages.CreatedEntity']}");
		
		entity = new Tender();
		entity.setActive(true);
        entity.setSerial(sequenceManager.getNewSerialNumber(SequenceType.SERIAL_TENDER));
        entity.setDate(calendarManager.getCurrentDate());
	}
	
	@Override
	@Transactional
	public String save() {
		String result = null;
		boolean error = false;

		try {
			error = makeEntityValidations();
			
			calculateEverything();

			setParentOfLists();
		
			if (error) {
				result = BaseConsts.FAIL;
			} else {
				persistParents();
				
				result = super.save();
			}
		} catch (Exception e) {
			facesMessages.add(e.getMessage());
			log.error("Hata :", e);
		}
		return result;
	}

	private void persistParents() {
		TenderDetail item = null;
		for (int i=0;i<entity.getItems().size();i++) {
			item = entity.getItems().get(i);
		
			if (item.getChildList() != null && item.getChildList().size() != 0) {
				if (item.getId() == null) {
					entityManager.persist(item);

					item.setParent(item);
					for (TenderDetail child : item.getChildList()) {
						child.setParent(item);
					}
				}
			}
		}
	}

	private boolean makeEntityValidations() {
		boolean result = false;
		if (entity.getItems().size() == 0 ) {
			facesMessages.add("#{messages['beanMessages.AtLeastOneItemRequired']}");
			log.error("#{messages['beanMessages.AtLeastOneItemRequired']}");
			result = true;
		}

		TenderDetail detail = null;
		for(int i=0;i<entity.getItems().size();i++) {
			detail = entity.getItems().get(i);
			
			//FIXME: gerekli kontrolleri eklemeli.
		}
		return result;
	}

	private void setParentOfLists() {
		for(TenderTaxSummary item : entity.getTaxSummaryList()) {
			item.setOwner(entity);
		}
		for(TenderCurrencySummary item : entity.getCurrencySummaryList()) {
			item.setOwner(entity);
		}
		for(TenderCurrencyRate item : entity.getCurrencyRateList()) {
			item.setOwner(entity);
		}
	}
	
	public void selectProduct() {
        manualFlush();

        if (selectedDetail != null && selectedDetail.getProduct() != null) {
			selectedDetail.setProductType(selectedDetail.getProduct().getProductType());
        	selectedDetail.getQuantity().setUnit(selectedDetail.getProduct().getUnit());

        	selectedChildList.clear();
        	
    		for (ProductDetail pd : selectedDetail.getProduct().getDetailList()) {
    			TenderDetail expenseOrDiscountLine = new TenderDetail();
    			expenseOrDiscountLine.setProductType(pd.getProduct().getProductType());
    			
    			if (expenseOrDiscountLine.getProductType().equals(ProductType.Discount)) {
    				expenseOrDiscountLine.getDiscount().moveFieldsOf(pd.getProduct().getDiscountOrExpense());
    			} else {
    				expenseOrDiscountLine.getExpense().moveFieldsOf(pd.getProduct().getDiscountOrExpense());
    			}
    			selectedChildList.add(expenseOrDiscountLine);
    			expenseOrDiscountLine.setParent(selectedDetail);
    			expenseOrDiscountLine.setOwner(entity);
    		}
        }
	}
	
    public Boolean calculateEverything() {
    	return calculateEverything(entity);
    }
    
    public Boolean calculateSelectedDetail() {
    	calculateEverythingForItem(selectedDetail);
    	return false;
    }

    public void refreshCurrencyRate() {
    	fillCurrencyRateForItem(selectedDetail);
    }

    public void createNewDetail() {
        manualFlush();

        setSelectedDetail(new TenderDetail());
        selectedDetail.setOwner(entity);

        setSelectedChildList(new ArrayList<TenderDetail>());
        selectedDetail.setChildList(selectedChildList);
        
        isEdit = Boolean.FALSE;
        
        log.debug("#{messages['logMessages.NewItemAdded']}");
    }

    public void createNewItemExpense() {
    	manualFlush();
    	
    	selectedDiscountOrExpense = new TenderDetail();
    	selectedDiscountOrExpense.setOwner(entity);
    	selectedDiscountOrExpense.setProductType(ProductType.Expense);
    	selectedDiscountOrExpense.setParent(selectedDetail);
    	
    	isDiscountOrExpenseEdit = Boolean.FALSE;

    	log.debug("#{messages['logMessages.NewItemAdded']}");
    }

    public void createNewItemFee() {
    	manualFlush();
    	
    	selectedDiscountOrExpense = new TenderDetail();
    	selectedDiscountOrExpense.setOwner(entity);
    	selectedDiscountOrExpense.setProductType(ProductType.Fee);
    	selectedDiscountOrExpense.setParent(selectedDetail);
    	
    	isDiscountOrExpenseEdit = Boolean.FALSE;
    	
    	log.debug("#{messages['logMessages.NewItemAdded']}");
    }

    public void createNewItemDiscount() {
    	manualFlush();
    	
    	selectedDiscountOrExpense = new TenderDetail();
    	selectedDiscountOrExpense.setOwner(entity);
    	selectedDiscountOrExpense.setProductType(ProductType.Discount);
    	selectedDiscountOrExpense.setParent(selectedDetail);
    	
    	isDiscountOrExpenseEdit = Boolean.FALSE;
    	
    	log.debug("#{messages['logMessages.NewItemAdded']}");
    }
	
    public void createDocumentDiscountLine() {
        manualFlush();
    	
        selectedDiscountOrExpense = new TenderDetail();
    	selectedDiscountOrExpense.setOwner(entity);
    	selectedDiscountOrExpense.setProductType(ProductType.DocumentDiscount);
        
    	isDiscountOrExpenseEdit  = Boolean.FALSE;
    }

    public void createDocumentFeeLine() {
    	manualFlush();
    	
    	selectedDiscountOrExpense = new TenderDetail();
    	selectedDiscountOrExpense.setOwner(entity);
    	selectedDiscountOrExpense.setProductType(ProductType.DocumentFee);
    	
    	isDiscountOrExpenseEdit  = Boolean.FALSE;
    }

    public void createDocumentExpenseLine() {
        manualFlush();
    	
        selectedDiscountOrExpense = new TenderDetail();
    	selectedDiscountOrExpense.setOwner(entity);
    	selectedDiscountOrExpense.setProductType(ProductType.DocumentExpense);
        
    	isDiscountOrExpenseEdit  = Boolean.FALSE;
    }
    
	public void editDetail(Integer index) {
		manualFlush();

		selectedIndex = index.intValue();
		
		TenderDetail originalDetail = entity.getItems().get(selectedIndex);

		setSelectedDetail(new TenderDetail());

		moveChildsToDummyList(originalDetail);

		moveFieldsToDummyDetail(originalDetail, selectedDetail);

		selectedDetail.setChildList(selectedChildList);
		
		isEdit = Boolean.TRUE;
		log.debug("#{messages['logMessages.NewItemAdded']}");
	}

	private void moveChildsToDummyList(TenderDetail selectedDetail) {
		setSelectedChildList(new ArrayList<TenderDetail>());

		TenderDetail newChild = null;
		for (TenderDetail child : selectedDetail.getChildList()) {
			newChild = new TenderDetail();
			
			moveFieldsToDummyDetail(child, newChild);

			selectedChildList.add(newChild);
		}
		
//		removeParentDetailFromChildList(selectedDetail);
	}

	private void removeParentDetailFromChildList(TenderDetail parentDetail) {
		TenderDetail child = null;
		for (int i=0;i<selectedChildList.size();i++) {
			child = selectedChildList.get(i);
			if (child.getId() != null && child.getParent() != null && 
					child.getParent().getId().equals(child.getId())) {
				selectedChildList.remove(i);
				break;
			}
		}
	}

	public void addToDiscountOrExpenseItems() {
		manualFlush();

		boolean error = makeDiscountOrExpenseValidations();
		
		if (!error) {
			selectedChildList.add(selectedDiscountOrExpense);
		}
	}

	public void addDocumentDiscountOrExpenseToItems() {
		manualFlush();
		
		boolean error = makeDiscountOrExpenseValidations();
		
		if (!error) {
			entity.getItems().add(selectedDiscountOrExpense);
		}
	}

	public void editDocumentDiscountOrExpenseItem(Integer rowKey) {
		log.info("Document Discount or expense line rowKey: {0}", rowKey);
		manualFlush();
		
		selectedDiscountOrExpenseIndex = rowKey.intValue();
		
		TenderDetail originalDetail = entity.getItems().get(selectedDiscountOrExpenseIndex);
		
		selectedDiscountOrExpense = new TenderDetail();

		moveFieldsToDummyDetail(originalDetail, selectedDiscountOrExpense);
		
		isDiscountOrExpenseEdit = Boolean.TRUE;
	}

	public void editDiscountOrExpenseItem(Integer rowKey) {
		log.info("Discount or expense line rowKey: {0}", rowKey);
		manualFlush();
		
		selectedDiscountOrExpenseIndex = rowKey.intValue();
		
		TenderDetail originalDetail = selectedChildList.get(selectedDiscountOrExpenseIndex);
		
		selectedDiscountOrExpense = new TenderDetail();
		
		moveFieldsToDummyDetail(originalDetail, selectedDiscountOrExpense);
		
		isDiscountOrExpenseEdit = Boolean.TRUE;
	}

	public void endDiscountOrExpenseEdit() {
		boolean error = makeValidations();
		
		if (!error) {
			moveFieldsToDummyDetail(selectedDiscountOrExpense, selectedChildList.get(selectedDiscountOrExpenseIndex));
		}
	}

	public void endDocumentDiscountOrExpenseEdit() {
		boolean error = makeValidations();
		
		if (!error) {
			moveFieldsToDummyDetail(selectedDiscountOrExpense, entity.getItems().get(selectedDiscountOrExpenseIndex));
		}
	}
	
	public void deleteDiscountOrExpenseLine(Integer rowKey) {
		log.info("Deleting discount or expense line:{0}", rowKey);
		manualFlush();

		log.info("Childlist size before deletion:{0} ", selectedChildList.size());	
		selectedChildList.remove(rowKey.intValue());
		log.info("Childlist size after deletion:{0} ", selectedChildList.size());	
	}
	
	private boolean makeDiscountOrExpenseValidations() {
		boolean result = false;
		
		return result;
	}
	
	public void addToItems() {
		manualFlush();

		boolean error = makeValidations();
		
		if (!error) {
//			selectedDetail.setParent(selectedDetail);

			TenderDetail newDetail = new TenderDetail();
			
			moveFieldsToDummyDetail(selectedDetail, newDetail);
			
			entity.getItems().add(newDetail);

			selectedIndex = entity.getItems().size() - 1;

			insertChildListToItems(true);
		}
	}
	
	private void insertChildListToItems(boolean isNew) {
		int detailIndex = entity.getItems().size();
		if (!isNew) {
			detailIndex = selectedIndex + 1;
		}
		TenderDetail originalDetail = entity.getItems().get(selectedIndex);
//		if (originalDetail.getId() != null) {
//			originalDetail.getChildList().clear();
//			originalDetail.getChildList().add(originalDetail);
//		}
		originalDetail.getChildList().clear();
		
		TenderDetail item = null;
		for (TenderDetailBase child : selectedChildList) {
			item = (TenderDetail)child;
			
			entity.getItems().add(detailIndex, item);
		
			originalDetail.getChildList().add(item);
			detailIndex++;
		}
	}
	
	private boolean makeValidations() {
		boolean result = false;
		
		return result;
	}

	public void endDetailEdit() {
		//FIXME: döviz tür kontrolleri vb.
		boolean error = makeValidations();
		
		if (!error) {
			TenderDetail detail = entity.getItems().get(selectedIndex);

			int lastIndex = detail.getChildList().size();
			
			for (int i=0;i<lastIndex;i++) {
				entity.getItems().remove(selectedIndex + 1);
			}

			moveFieldsToDummyDetail(selectedDetail, detail);

			insertChildListToItems(false);
		}
		
	}
	
	private Integer getLastIndex(TenderDetail detail) {
		if (detail.getId() != null) return detail.getChildList().size() - 1;
		return detail.getChildList().size();
	}
	
	private void moveFieldsToDummyDetail(TenderDetail fromDetail, TenderDetail toDetail) {
		toDetail.getAmount().moveFieldsOf(fromDetail.getAmount());
		toDetail.getBeforeTax().moveFieldsOf(fromDetail.getBeforeTax());
		toDetail.getFee().moveFieldsOf(fromDetail.getFee());
		toDetail.getGrandTotal().moveFieldsOf(fromDetail.getGrandTotal());
		
		
		toDetail.setProductType(fromDetail.getProductType());
		toDetail.setLineCode(fromDetail.getLineCode());
		toDetail.setInfo(fromDetail.getInfo());
		toDetail.setId(fromDetail.getId());
		toDetail.setDiscountStyle(fromDetail.getDiscountStyle());
		toDetail.setExpenseStyle(fromDetail.getExpenseStyle());
		toDetail.setOwner(fromDetail.getOwner());
		toDetail.setParent(fromDetail.getParent());
		toDetail.setProduct(fromDetail.getProduct());
//		toDetail.setChildList(fromDetail.getChildList());

		
		toDetail.getQuantity().moveFieldsOf(fromDetail.getQuantity());

		if (fromDetail.getInvoiceDiscount() !=  null) toDetail.getInvoiceDiscount().moveFieldsOf(fromDetail.getInvoiceDiscount());
		if (fromDetail.getInvoiceExpense() !=  null) toDetail.getInvoiceExpense().moveFieldsOf(fromDetail.getInvoiceExpense());
		if (fromDetail.getTenderDiscount() !=  null) toDetail.getTenderDiscount().moveFieldsOf(fromDetail.getTenderDiscount());
		if (fromDetail.getTenderExpense() !=  null) toDetail.getTenderExpense().moveFieldsOf(fromDetail.getTenderExpense());
		if (fromDetail.getDiscount() !=  null) toDetail.getDiscount().moveFieldsOf(fromDetail.getDiscount());
		if (fromDetail.getExpense() !=  null) toDetail.getExpense().moveFieldsOf(fromDetail.getExpense());
		if (fromDetail.getShipmentDiscount() !=  null) toDetail.getShipmentDiscount().moveFieldsOf(fromDetail.getShipmentDiscount());
		if (fromDetail.getShipmentExpense() !=  null) toDetail.getShipmentExpense().moveFieldsOf(fromDetail.getShipmentExpense());

		
		if (fromDetail.getTaxExcludedTotal() !=  null) toDetail.getTaxExcludedTotal().moveFieldsOf(fromDetail.getTaxExcludedTotal());
		if (fromDetail.getTaxTotalAmount() !=  null) toDetail.getTaxTotalAmount().moveFieldsOf(fromDetail.getTaxTotalAmount());
		if (fromDetail.getTotalAmount() !=  null) toDetail.getTotalAmount().moveFieldsOf(fromDetail.getTotalAmount());
		if (fromDetail.getUnitPrice() !=  null) toDetail.getUnitPrice().moveFieldsOf(fromDetail.getUnitPrice());

		//embeddable vergiler...
		if (fromDetail.getTax1() !=  null) toDetail.getTax1().moveFieldsOf(fromDetail.getTax1());
		if (fromDetail.getTax2() !=  null) toDetail.getTax2().moveFieldsOf(fromDetail.getTax2());
		if (fromDetail.getTax3() !=  null) toDetail.getTax3().moveFieldsOf(fromDetail.getTax3());
		if (fromDetail.getTax4() !=  null) toDetail.getTax4().moveFieldsOf(fromDetail.getTax4());
		if (fromDetail.getTax5() !=  null) toDetail.getTax5().moveFieldsOf(fromDetail.getTax5());
	}

	//FIXME: bu metodu düzenlemek gerek!
	public void deleteLine(Integer ix) {
		manualFlush();

        TenderDetail deletedItem = entity.getItems().get(ix);
        if (deletedItem.hasChildren()) {
            deleteChildrenFromMainList(deletedItem, ix);
        	deletedItem.clearChildList();
        }
        entity.getItems().remove(ix.intValue());
        log.debug("#{messages['logMessages.ItemDeleted']} : {0}", ix.intValue());
    }

	private void deleteChildrenFromMainList(TenderDetail deletedItem, Integer ix) {
		for(int i=0;i<deletedItem.getChildList().size();i++) {
			entity.getItems().remove(ix + 1);
		}
	}

	public Boolean getIsEdit() {
		return isEdit;
	}

	public Boolean getIsDiscountOrExpenseEdit() {
		return isDiscountOrExpenseEdit;
	}
	
	public TenderDetail getSelectedDetail() {
		return selectedDetail;
	}

	public void setSelectedDetail(TenderDetail selectedDetail) {
		this.selectedDetail = selectedDetail;
	}
	
	public TenderDetail getSelectedDiscountOrExpense() {
		return selectedDiscountOrExpense;
	}

	public void setSelectedDiscountOrExpense(TenderDetail selectedDiscountOrExpense) {
		this.selectedDiscountOrExpense = selectedDiscountOrExpense;
	}

	public List<TenderDetail> getSelectedChildList() {
		return selectedChildList;
	}

	public void setSelectedChildList(List<TenderDetail> selectedChildList) {
		this.selectedChildList = selectedChildList;
	}

}
