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

package com.ut.tekir.invoice.yeni;

import java.io.Serializable;
import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.jboss.seam.Component;
import org.jboss.seam.ScopeType;
import org.jboss.seam.annotations.AutoCreate;
import org.jboss.seam.annotations.Logger;
import org.jboss.seam.annotations.Name;
import org.jboss.seam.annotations.Scope;
import org.jboss.seam.log.Log;

import com.ut.tekir.entities.InvoicePaymentPlanItem;
import com.ut.tekir.entities.MoneySet;
import com.ut.tekir.entities.PaymentPlan;
import com.ut.tekir.entities.PaymentPlanCalcType;
import com.ut.tekir.entities.PaymentPlanDestType;
import com.ut.tekir.entities.PaymentPlanItem;

//FIXME: ödeme planı tarih hesaplamalarında hafta başı metodu düzenlenmeli.

/**
 * Ödeme planı satırlarının oluşturulduğu sınıftır.
 * 
 * @author sinan.yumak
 *
 */
@Name("paymentPlanItemHome")
@Scope(value = ScopeType.EVENT)
@AutoCreate
public class PaymentPlanItemHome implements Serializable {
	private static final long serialVersionUID = 1L;

	@Logger
    private Log log;

	private PaymentPlanHolder planHolder;

	public void prepareItems() {
		planHolder.getPaymentPlanItems().clear();

		PaymentPlan plan = planHolder.getPaymentPlan();
		if (plan == null) {
			return;
		}
		
		Map<PaymentPlanDestType, ItemTotalModel> totalMap = prepareItemTotalMap();

		Calendar calendar = Calendar.getInstance();
		
		InvoicePaymentPlanItem invoiceItem = null;
		for ( PaymentPlanItem item : plan.getItems() ) {
			ItemTotalModel model = totalMap.get( item.getDestType() );
			ItemTotalModel totalModel = totalMap.get( PaymentPlanDestType.Total );

			BigDecimal calculatedAmount = BigDecimal.ZERO;
			if (item.getCalcType().equals(PaymentPlanCalcType.Amount)) {

				calculatedAmount = model.getCalculatedAmount().add(item.getRate());

			} else if (item.getCalcType().equals(PaymentPlanCalcType.Rate)) {
				
				BigDecimal hundred = BigDecimal.valueOf(100);
				calculatedAmount = model.getTotalAmount().multiply(item.getRate())
															  .divide(hundred, 2, RoundingMode.HALF_UP);

			} else {
				calculatedAmount = model.getRemainingAmount();
			}
			totalModel.setCalculatedAmount(totalModel.getCalculatedAmount().add(calculatedAmount));

			model.setCalculatedAmount( model.getCalculatedAmount().add(calculatedAmount) );

			
			invoiceItem = new InvoicePaymentPlanItem();

			calendar.setTime(planHolder.getDate());
			calendar.add(Calendar.DAY_OF_MONTH, item.getDay());

			checkPaymentWeek(calendar);
			
			invoiceItem.setDate(calendar.getTime());
			invoiceItem.setTotal(calculatedAmount);
			
			InvoicePaymentPlanItem foundLine = findSameDateLine(calendar.getTime());
			if (foundLine == null) {
				planHolder.getPaymentPlanItems().add(invoiceItem);
			} else {
				foundLine.setTotal(foundLine.getTotal().add(invoiceItem.getTotal()));
			}

			log.info("calculated amount: #0", calculatedAmount);
			log.info("ödeme planı boyutu: #0", planHolder.getPaymentPlanItems().size());
		}

		setLineNumbers();
		log.debug("elde edilen sonuçlar: #0",planHolder.getPaymentPlanItems());
	}

	public void checkPaymentWeek(Calendar calendar, PaymentPlan plan) {
		int weekOfMonth = calendar.get(Calendar.WEEK_OF_MONTH);

//		log.info("Checking payment day. Payment plan code:{0}, Week of Month:{1}", plan.getCode(), weekOfMonth);

		List<Boolean> paymentWeeks = plan.paymentWeeksAsList();

		boolean continueToSecondBlock = true;
		for (int i=weekOfMonth; i<paymentWeeks.size() + 1;i++) {
			if (paymentWeeks.get(i-1)) {
				Date beforeChange = calendar.getTime();
				
				checkPaymentDay(calendar, plan);

				if ( paymentWeeks.get(calendar.get(Calendar.WEEK_OF_MONTH) - 1)) {
					continueToSecondBlock = false;
					break;
				} else {
					calendar.setTime(beforeChange);
				}
			}

	        int currentDOW = calendar.get(Calendar.DAY_OF_WEEK);
	        calendar.add(Calendar.DAY_OF_YEAR, (currentDOW * -1) + 1);

			calendar.add(Calendar.WEEK_OF_MONTH, 1);
			calendar.get(Calendar.WEEK_OF_MONTH);
		}
		if (continueToSecondBlock) {
			for (int i=1; i<weekOfMonth;i++) {
				if (paymentWeeks.get(i)) {
					Date beforeChange = calendar.getTime();

					checkPaymentDay(calendar, plan);

					if ( paymentWeeks.get(calendar.get(Calendar.WEEK_OF_MONTH) - 1)) {
						break;
					} else {
						calendar.setTime(beforeChange);
					}
				}

		        int currentDOW = calendar.get(Calendar.DAY_OF_WEEK);
		        calendar.add(Calendar.DAY_OF_YEAR, (currentDOW * -1) + 1);

				calendar.add(Calendar.WEEK_OF_MONTH, 1);
				calendar.get(Calendar.WEEK_OF_MONTH);
			}
		}
	}
	
	private void checkPaymentWeek(Calendar calendar) {
		checkPaymentWeek(calendar, planHolder.getPaymentPlan());
	}

	private void checkPaymentDay(Calendar calendar, PaymentPlan plan) {
    	int dayOfWeek = calendar.get(Calendar.DAY_OF_WEEK) - 1;
    	
//    	log.info("Checking payment day. Payment plan code:{0}, Day of Week:{1}", plan.getCode(), dayOfWeek);

    	int additionTime = 0;
    	List<Boolean> paymentDays = plan.paymentDaysAsList();

    	boolean continueToSecondBlock = true;
		for (int i=dayOfWeek; i<paymentDays.size();i++) {
			if (!paymentDays.get(i)) {
				additionTime++;

			} else {
				continueToSecondBlock = false;
				break;
			}
		}
		if (continueToSecondBlock) {
			for (int i=0;i<dayOfWeek;i++) {
				if (!paymentDays.get(i)) {
					additionTime++;
	
				} else {
					break;
				}
	    	}
		}

    	if (additionTime != 0) {
//    		getLog().info("Eklenecek gün sayısı:{0}", additionTime);
    		calendar.add(Calendar.DAY_OF_MONTH, additionTime);
    	}
	}

    private void checkPaymentDay(Calendar calendar) {
    	checkPaymentDay(calendar, planHolder.getPaymentPlan());
    }

	private void setLineNumbers() {
		InvoicePaymentPlanItem item;
		for (int i=0;i<planHolder.getPaymentPlanItems().size();i++) {
			item = planHolder.getPaymentPlanItems().get(i);
			item.setLineNumber(i+1);
		}
	}
	
	private InvoicePaymentPlanItem findSameDateLine(Date aDate) {
		for (InvoicePaymentPlanItem item : planHolder.getPaymentPlanItems()) {
			if (item.getDate().compareTo(aDate) == 0) return item;
		}
		return null;
	}
	
	private Map<PaymentPlanDestType, ItemTotalModel> prepareItemTotalMap() {
		Map<PaymentPlanDestType, ItemTotalModel> result = new HashMap<PaymentPlanDestType, ItemTotalModel>();

		ItemTotalModel grandTotalModel = new ItemTotalModel(planHolder.getGrandTotal());
		result.put(PaymentPlanDestType.Total, grandTotalModel);

		ItemTotalModel totalTaxModel = new ItemTotalModel(planHolder.getTotalTax());
		result.put(PaymentPlanDestType.Tax, totalTaxModel);

		ItemTotalModel totalExpenseModel = new ItemTotalModel(planHolder.getTotalExpense());
		result.put(PaymentPlanDestType.Cost, totalExpenseModel);

		ItemTotalModel totalFeeModel = new ItemTotalModel(planHolder.getTotalFee());
		result.put(PaymentPlanDestType.Fee, totalFeeModel);
		
		ItemTotalModel taxExcludedTotalModel = new ItemTotalModel(planHolder.getTaxExcludedTotal());
		result.put(PaymentPlanDestType.TaxBase, taxExcludedTotalModel);
		
		return result;
	}

	public void setPlanHolder(PaymentPlanHolder planHolder) {
		this.planHolder = planHolder;
	}

	public Log getLog() {
		if (log == null) {
			log  = (Log)Component.getInstance("log",true);
		}
		return log;
	}

}

class ItemTotalModel {
	private BigDecimal totalAmount;
	private BigDecimal calculatedAmount;
	private BigDecimal remainingAmount;

	public ItemTotalModel(MoneySet moneySet) {
		super();
		this.totalAmount = moneySet.getValue();
		this.remainingAmount = BigDecimal.ZERO;
		this.calculatedAmount = BigDecimal.ZERO;
	}

	public BigDecimal getTotalAmount() {
		return totalAmount;
	}

	public void setTotalAmount(BigDecimal totalAmount) {
		this.totalAmount = totalAmount;
	}

	public BigDecimal getRemainingAmount() {
		return remainingAmount;
	}

	public void setRemainingAmount(BigDecimal remainingAmount) {
		this.remainingAmount = remainingAmount;
	}

	public BigDecimal getCalculatedAmount() {
		return calculatedAmount;
	}

	public void setCalculatedAmount(BigDecimal calculatedAmount) {
		this.calculatedAmount = calculatedAmount;
		
		this.remainingAmount = totalAmount.subtract(calculatedAmount);
	}
	
	
}