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

import com.ut.tekir.entities.Product;
import com.ut.tekir.entities.ProductType;
import com.ut.tekir.framework.CurrencyManager;
import com.ut.tekir.framework.EntityBase;
import com.ut.tekir.framework.SequenceManager;
import com.ut.tekir.framework.SequenceType;

/**
 *
 * @author bilga
 */
@Stateful
@Name("expenseAndDiscountHome")
@Scope(value=ScopeType.CONVERSATION)
public class ExpenseAndDiscountHomeBean extends EntityBase<Product> implements ExpenseAndDiscountHome<Product>{

	@In(create = true)
    SequenceManager sequenceManager;
    @In
    CurrencyManager currencyManager;

	private ProductType type;
	
    @Create @Begin(join=true,flushMode = org.jboss.seam.annotations.FlushModeType.MANUAL)
    public void init() {
        log.debug("ExpenseAndDiscountHome Init");
    }
    
    @Out(required=false) 
    public Product getProduct() {
        return getEntity();
    }

    @In(required=false)
    public void setProduct(Product product) {
        setEntity( product );
    }
    
    @Override
    public void createNew() {
        log.debug("Yeni Masraf");
        entity = new Product();
        entity.setActive(true);
        
        if(type == ProductType.Expense || type == ProductType.ExpenseAddition){
        	entity.setCode(sequenceManager.getNewSerialNumber(SequenceType.SERIAL_CARDS_EXPENSE));
        } else if (type == ProductType.Discount || type == ProductType.DiscountAddition) {
        	entity.setCode(sequenceManager.getNewSerialNumber(SequenceType.SERIAL_CARDS_DISCOUNT));
        } 
        entity.setProductType(type);
    }
    
    @Override
    public String save() {
    	
    	if(entity.getProductType() != null && entity.getProductType() == ProductType.Expense){
    		entity.setBuyTax(entity.getSellTax());
    		entity.setBuyTax2(entity.getSellTax2());
    		entity.setBuyTax3(entity.getSellTax3());
    		entity.setBuyTax4(entity.getSellTax4());
    		entity.setBuyTax5(entity.getSellTax5());
    	}
    	
		BigDecimal convertedAmount = currencyManager.convertToLocal(entity.getDiscountOrExpense().getValue(),
				entity.getDiscountOrExpense().getCurrency(), 
				entity.getCreateDate());
		
		entity.getDiscountOrExpense().setLocalAmount(convertedAmount);

    	String res = super.save();
    	return res;
    	
    }
    
    public void manualFlush() {
        Conversation.instance().changeFlushMode(org.jboss.seam.annotations.FlushModeType.MANUAL);
    } 

	public void setType(ProductType type) {
		this.type = type;
	}

	public ProductType getType() {
		return type;
	}
    
}
