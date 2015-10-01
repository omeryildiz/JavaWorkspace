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

package com.ut.tekir.stock;

import java.util.ArrayList;
import java.util.List;

import javax.ejb.Stateful;
import javax.ejb.TransactionAttribute;
import javax.ejb.TransactionAttributeType;

import org.jboss.seam.ScopeType;
import org.jboss.seam.annotations.Begin;
import org.jboss.seam.annotations.Create;
import org.jboss.seam.annotations.In;
import org.jboss.seam.annotations.Name;
import org.jboss.seam.annotations.Out;
import org.jboss.seam.annotations.Scope;
import org.jboss.seam.core.Conversation;
import org.jboss.seam.faces.FacesMessages;

import com.ut.tekir.entities.CountNote;
import com.ut.tekir.entities.CountNoteItem;
import com.ut.tekir.entities.CountStatus;
import com.ut.tekir.entities.MoneySet;
import com.ut.tekir.entities.Product;
import com.ut.tekir.entities.ProductCategory;
import com.ut.tekir.entities.ProductGroup;
import com.ut.tekir.entities.User;
import com.ut.tekir.framework.BaseConsts;
import com.ut.tekir.framework.CalendarManager;
import com.ut.tekir.framework.EntityBase;
import com.ut.tekir.framework.SequenceManager;
import com.ut.tekir.framework.SequenceType;
import com.ut.tekir.framework.TekirRuntimeException;
import com.ut.tekir.general.GeneralSuggestion;
import com.ut.tekir.tender.PriceProvider;
import com.ut.tekir.util.Utils;

//FIXME: yeni haliyle birlikte model kullanmaya(CountNoteCompareModel) gerek kalmadı.
//modeli koddan temizlemek lazım.

/**
 * Sayım fişi tanımı/girişi home bileşenidir.
 * @author sinan.yumak
 */
@Stateful
@Name("countNoteHome")
@Scope(value = ScopeType.CONVERSATION)
public class CountNoteHomeBean extends EntityBase<CountNote> implements CountNoteHome<CountNote> {

    @In(create = true)
    SequenceManager sequenceManager;
    @In
    CalendarManager calendarManager;
    @In
    GeneralSuggestion generalSuggestion;
    @In
    FacesMessages facesMessages;
    @In
    StockLevelFinder stockLevelFinder;
    @In
    PriceProvider priceProvider;
    @In
    ProductTxnAction productTxnAction;
    @In
    User activeUser;
    
    private CountNoteInputModel inputModel = new CountNoteInputModel();
    private List<CountNoteCompareModel> compareList;
    private int rowCount = 50;
    
    private ProductGroup group;
    private ProductCategory category;
    private boolean checkboxSelected = true;
    
    @Create 
    @Begin(join=true, flushMode = org.jboss.seam.annotations.FlushModeType.MANUAL)
    public void init() {
    }

    @Out(required = false)
    public CountNote getCountNote() {
        return getEntity();
    }

    @In(required = false)
    public void setCountNote(CountNote countNote) {
        setEntity(countNote);
    }

    @Override
    public void createNew() {
        log.debug("Yeni CountNote");

        entity = new CountNote();
        entity.setDate(calendarManager.getCurrentDate());
        entity.setSerial(sequenceManager.getNewSerialNumber(SequenceType.SERIAL_COUNT_NOTE));
    }

    public void toggleChecboxes() {
    	for (CountNoteCompareModel model : compareList) {
    		model.setProcess(checkboxSelected);
    	}
    }
    
    public void addUncountedProducts() {
    	log.info("Adding uncounted items...");
    	for (Product pr : getUncountedProducts()) {
    		double foundLevel = stockLevelFinder.findStockLevel(pr, entity.getWarehouse());

    		if (foundLevel != 0){
    			CountNoteItem item = new CountNoteItem(pr,entity);
    			entity.getItems().add( item );
    			compareList.add( new CountNoteCompareModel(foundLevel, pr, item) );

    			log.info("Added new line with product #0, quantity #1", pr.getCaption(), foundLevel);
    		}
    	}
    }
    
    @SuppressWarnings("unchecked")
	private List<Product> getUncountedProducts() {
    	log.info("Querying uncounted products...");
    	try {
    		return entityManager.createQuery("select p from Product p where " +
    															"p.id not in (:productIds)")
    															.setParameter("productIds", getCountedProductIds())
    															.getResultList();
			
		} catch (Exception e) {
			log.error("Sayılmayan ürün listesi alınırken hata meydana geldi. Sebebi #0", e);
		}
		return new ArrayList<Product>();
    }

    private List<Long> getCountedProductIds() {
    	List<Long> ids = new ArrayList<Long>();
    	for (CountNoteItem item : entity.getItems()) {
    		ids.add(item.getProduct().getId());
    	}
    	return ids;
    }
    
    @Override
    @TransactionAttribute(TransactionAttributeType.REQUIRES_NEW)
    public String save() {
		log.info("Saving count note. Serial :{0}", entity.getSerial());
		String result = BaseConsts.FAIL;

        try {
            result = super.save();
        } catch (Exception e) {
			facesMessages.add( Utils.getExceptionMessage(e) );
        	log.error("Hata :", e);
            return BaseConsts.FAIL;
        }
        return result;
    }

    public void makeUpTheDifference() {
    	log.info("Making up the difference...");
    	manualFlush();
    	try {
    		List<CountNoteCompareModel> compareList = getCompareList();
    		
    		List<CountNoteCompareModel> stepList =  new ArrayList<CountNoteCompareModel>(); 
    		
    		int i;
    		for (i=0;i<compareList.size();i=i+30) {
    			stepList.clear();
    			for (int j=i;j<i+30;j++) {
    				if (j == compareList.size()) break;

    				stepList.add(compareList.get(j));
    			}
    			productTxnAction.createProductTxnRecordsForCountNote(entity, stepList);
    			log.debug("step:" + i);
    		}
    		
    		entity.setStatus(CountStatus.Finished);
    		
    		entityManager.merge(entity);
    		entityManager.flush();
    		
    		facesMessages.add("Fark başarıyla kapatıldı.");
    		
    		applyFilter();
    	} catch (Exception e) {
			facesMessages.add("Fark kapatılırken hata meydana geldi. Sebebi {0}",e.getMessage());
			log.info("Fark kapatılırken hata meydana geldi. Sebebi #0", e);
		}
    }

    public void createNewLine() {
    	manualFlush();
        
        CountNoteItem it = new CountNoteItem();
        it.setOwner(entity);
        
        entity.getItems().add(it);
        log.debug("yeni item eklendi");
    }

    @Override
    @TransactionAttribute(TransactionAttributeType.REQUIRES_NEW)
    public String delete(){
		String result = BaseConsts.SUCCESS;
		try {
			productTxnAction.deleteCountNote(entity);

			result = super.delete();
		} catch (Exception e) {
			log.error("Pusula silinirken hata oluştu. Sebebi :#0", e);
			facesMessages.add(Utils.getExceptionMessage(e));
			result = BaseConsts.FAIL;			
		}
        return result;
    }
    
    public void deleteLine(Integer ix) {
    	manualFlush();
        if (entity == null) {
            return;
        }
        entity.getItems().remove(ix.intValue());
    }

    public void addItemByScanner() {
        setProductInput();
        addItemAction();
    }
    
    @Override
    public void setProductInput() {
    	try {
    		Product foundProduct = generalSuggestion.findProductWithBarcode( inputModel.getBarcode() );

    		if (foundProduct != null) {
    			inputModel.setProduct(foundProduct);

    			setPriceInput();
    		}
		} catch (Exception e) {
			facesMessages.add(Utils.getExceptionMessage(e));
			log.error("Hata", e);
		}
    }

    @Override
	public void setBarcodeInput() {
    	try {
			if ( inputModel.getProduct() != null ) setPriceInput();
		} catch (Exception e) {
			facesMessages.add(Utils.getExceptionMessage(e));
			log.error("Hata", e);
		}
    }

    private void setPriceInput() throws Exception {
		inputModel.setPrice( new MoneySet(priceProvider.findSalePriceItemDetailForProduct( inputModel.getProduct() ).getGrossPrice()) );
	}

	public void addItemAction() {
		manualFlush();
    	if ( inputModel.getPrice() == null) return;
    	try {
    		makeValidations();
    		
    		addItem();
    	} catch (Exception e) {
			facesMessages.add(Utils.getExceptionMessage(e));
			log.error("Hata", e);
		}
	}

	public void addWithBarcode() {
		try {
			setProductInput();

			addItem();
		} catch (Exception e) {
			facesMessages.add(Utils.getExceptionMessage(e));
			log.error("Hata", e);
		}
	}
	
    private void makeValidations() {
		if (inputModel.getQuantity() < 1 ) throw new TekirRuntimeException("Miktar 1'den küçük olamaz.");
	}

	private void addItem() {
		entity.addToItems(inputModel);

		productAddedMessage();

		inputModel.clearInputFields();
		
		entity.updateItemQuantities();
		
		super.save();
    }

    private void productAddedMessage() {
    	StringBuilder result = new StringBuilder();
    	result.append( inputModel.getProduct().getCaption() )
    		  .append( " kodlu ürün listeye " )
    		  .append( inputModel.getQuantity() )
    		  .append( " " )
    		  .append( inputModel.getProduct().getUnit() )
    		  .append( " eklendi." );
    	facesMessages.add( result.toString() );
    }

    public void applyFilter() {
		clearCompareList();
    	for ( CountNoteItem item : entity.getItems()) {
    		if (item.matches(group) && item.matches(category)) {
    			if (item.getExist() == null) {
    				item.setExist((int)stockLevelFinder.findStockLevel(item.getProduct(),entity.getWarehouse()));
    			}
    			compareList.add(new CountNoteCompareModel(item));
    		}
    	}
    }
    
    private void clearCompareList() {
    	getCompareList().clear();
    }
    
    public void openForEditing() {
    	try {
			entity.setStatus(CountStatus.Editing);
			entityManager.merge(entity);
			entityManager.flush();

			facesMessages.add( "Sayım fişi tekrar düzenlemeye açıldı.");
    	} catch (Exception e) {
			facesMessages.add(Utils.getExceptionMessage(e));
			log.error("Hata", e);
		}
    }
    
	@Override
	public void setId(Long id) {
        if (entity != null) return;
		super.setId(id);

		applyFilter();
	}

    public void manualFlush() {
        Conversation.instance().changeFlushMode(org.jboss.seam.annotations.FlushModeType.MANUAL);
    }

    public List<CountNoteCompareModel> getCompareList() {
    	if (compareList == null || compareList.size() == 0) {
    		compareList = new ArrayList<CountNoteCompareModel>();
    	}
    	return compareList;
	}

	public void setCompareList(List<CountNoteCompareModel> compareList) {
		this.compareList = compareList;
	}

	public CountNoteInputModel getInputModel() {
		return inputModel;
	}

	public void setInputModel(CountNoteInputModel inputModel) {
		this.inputModel = inputModel;
	}

	public int getRowCount() {
		return rowCount;
	}

	public void setRowCount(int rowCount) {
		this.rowCount = rowCount;
	}

	public ProductGroup getGroup() {
		return group;
	}

	public void setGroup(ProductGroup group) {
		this.group = group;
	}

	public ProductCategory getCategory() {
		return category;
	}

	public void setCategory(ProductCategory category) {
		this.category = category;
	}

	public boolean isCheckboxSelected() {
		return checkboxSelected;
	}

	public void setCheckboxSelected(boolean checkboxSelected) {
		this.checkboxSelected = checkboxSelected;
		toggleChecboxes();
	}

}