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

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;

import org.jboss.seam.ScopeType;
import org.jboss.seam.annotations.In;
import org.jboss.seam.annotations.Name;
import org.jboss.seam.annotations.Scope;
import org.jboss.seam.core.Conversation;

import com.ut.tekir.entities.CurrencyPair;
import com.ut.tekir.entities.CurrencyRate;
import com.ut.tekir.entities.CurrencyRateType;
import com.ut.tekir.entities.DiscountOrExpense;
import com.ut.tekir.entities.DistributionStyle;
import com.ut.tekir.entities.ITender;
import com.ut.tekir.entities.ITenderDetail;
import com.ut.tekir.entities.Money;
import com.ut.tekir.entities.MoneySet;
import com.ut.tekir.entities.ProductType;
import com.ut.tekir.entities.Tax;
import com.ut.tekir.entities.TaxBase;
import com.ut.tekir.entities.TaxEmbeddable;
import com.ut.tekir.entities.TaxKind;
import com.ut.tekir.entities.TaxRate;
import com.ut.tekir.entities.TaxType;
import com.ut.tekir.entities.TenderBase;
import com.ut.tekir.entities.TenderCurrencyRate;
import com.ut.tekir.entities.TenderCurrencyRateBase;
import com.ut.tekir.entities.TenderCurrencySummary;
import com.ut.tekir.entities.TenderCurrencySummaryBase;
import com.ut.tekir.entities.TenderDetailBase;
import com.ut.tekir.entities.TenderTaxSummary;
import com.ut.tekir.entities.TenderTaxSummaryBase;
import com.ut.tekir.entities.inv.TekirInvoiceCurrencyRate;
import com.ut.tekir.entities.inv.TekirInvoiceCurrencySummary;
import com.ut.tekir.entities.inv.TekirInvoiceTaxSummary;
import com.ut.tekir.entities.ord.TekirOrderNoteCurrencyRate;
import com.ut.tekir.entities.ord.TekirOrderNoteCurrencySummary;
import com.ut.tekir.entities.ord.TekirOrderNoteTaxSummary;
import com.ut.tekir.entities.shp.TekirShipmentNoteCurrencyRate;
import com.ut.tekir.entities.shp.TekirShipmentNoteCurrencySummary;
import com.ut.tekir.entities.shp.TekirShipmentNoteTaxSummary;
import com.ut.tekir.framework.BaseConsts;
import com.ut.tekir.framework.CurrencyManager;
import com.ut.tekir.framework.EntityBase;
import com.ut.tekir.general.GeneralSuggestion;

/* Notlar:
 * Teklif - Sipariş - İrsaliye - Fatura hakkında: 

 * Tüm entityler master detay yapıda olacak.
	
 * Masterların TenderBase sınıfından türemeleri, detayların 
   TenderDetailBase den türemeleri gerekiyor. Bu iki sınıf 
   tüm entitylerin ortak alanlarını tutuyor.
	
 * TenderCalculationHomeBean sınıfının tüm home sınıfları 
   tarafından extend edilmeleri gerekiyor.
   Bu sınıf hesaplamaların yapıldığı ortak sınıf.
	
 * Hesaplamalarda ortak yanlardan yararlanabilmek için ITender 
   ve ITenderDetail interfaceleri kullanıldı.
	
 * Yeni entityler oluşturulduğunda gelecek masraf ve indirimler 
   için, örneğin sipariş indirim ve masrafları için siparişin 
   detayına iki tane alan eklenmeli ve getOrderDiscount ve 
   getOrderExpense metotları bu alanları döndürecek şekilde 
   düzenlenmelidir.
 */

//FIXME: bi tane hata yazmalı.
//FIXME: mesajları properties e çekmeli.
//FIXME: bu bileşenin yerel tutara çevrimlerle ilgili eksiklikleri var.

/**
 * 
 * Teklif, sipariş, irsaliye ve fatura hesaplamaları yapılırken 
 * kullanılacak home sınıfıdır.
 * @author sinan.yumak
 *
 */
@Name("tenderCalculationHome")
@Scope(ScopeType.SESSION)
public class TenderCalculationHomeBean<T> extends EntityBase<T> {
	@In
	GeneralSuggestion generalSuggestion;
	
	@In
	CurrencyManager currencyManager;
	
	private final BigDecimal HUNDRED = BigDecimal.valueOf(100);
	
	private List<String> errorList = new ArrayList<String>();
	
	/**
	 * Verilen teklif,sipariş,irsaliye veya faturanın tüm hesaplamalarını 
	 * yapar.
	 * @param tender
	 * @return result
	 */
	public Boolean calculateEverything(ITender tender) {
		manualFlush();
		fillCurrencyRateList(tender);

		for (TenderDetailBase item : tender.getProductItems()) {
			item.getBeforeTax().setValue(item.getTaxExcludedAmount().getValue());
			item.getBeforeTax().setCurrency(item.getTaxExcludedAmount().getCurrency());
			item.getBeforeTax().setLocalAmount(item.getTaxExcludedAmount().getLocalAmount());
			
			addLineDiscountAndExpenses(item);
			calculateTaxExcludedTotal(item);
			calculateEmbeddedTaxes(item);
			calculateTotal(item);
		}
		
		calculateDocumentFees();

		for (TenderDetailBase item : tender.getProductItems()) {
			calculateGrandTotalForItem(item);
		}
		calculateDocumentTotals(tender);

		//döküman toplamı hesaplandıktan sonra döküman indirim ve masraflarını ekle.
		calculateDocumentDiscountAndExpenses();

		for (TenderDetailBase item : tender.getProductItems()) {

			addDocumentDiscountToLine(item);
			
			calculateTaxExcludedTotal(item);
			calculateEmbeddedTaxes(item);
			calculateTotal(item);
		}

		for (TenderDetailBase item : tender.getProductItems()) {
			calculateGrandTotalForItem(item);
		}
		calculateDocumentTotals(tender);

		fillTaxSummaryList(tender);
		fillCurrencySummaryList(tender);
		
		hibernateEmbeddedObjProblemWorkAround();
		return false;
	}

	public BigDecimal checkTotalAmountChanges(TenderDetailBase item) {
		
		MoneySet totalAmountTransient = item.getTotalAmountTransient();
		MoneySet totalAmount = item.getTotalAmount();
		MoneySet expense = item.getExpense().getAsMoney();
		
		BigDecimal oldTaxExcludedAmount = item.getTaxExcludedAmount().getValue();

		BigDecimal difference = BigDecimal.ZERO;
		if (totalAmount.getValue().setScale(2, RoundingMode.HALF_UP)
				.compareTo(totalAmountTransient.getValue().setScale(2, RoundingMode.HALF_UP)) != 0) {
			
			BigDecimal totalAmountSubtractExpense = totalAmount.getValue().subtract(expense.getValue());
			item.getTaxExcludedAmount().setValue(totalAmountSubtractExpense);
			
			BigDecimal newTaxBase = calculateTaxes(item);


			//tax excluded eski değerine çekiyoruz.
			item.getTaxExcludedAmount().setValue(oldTaxExcludedAmount);
			

			difference = newTaxBase.subtract(item.getBeforeTax().getValue());

			BigDecimal quantityValue = BigDecimal.valueOf(item.getQuantity().getValue());
			difference = difference.divide(quantityValue, 10, RoundingMode.HALF_UP);
			
			log.info("Bulunan fark :{0}", difference);
		}
		return difference;
	}

	/**
	 * This method initializes embedded objects.
	 */
	private void hibernateEmbeddedObjProblemWorkAround() {
		for(TenderDetailBase item : getTender().getItems()) {
			item.getExpense();
			item.getDiscount();

			item.getTax1();
			item.getTax2();
			item.getTax3();
			item.getTax4();
			item.getTax5();
//			item.getShipmentDiscount();
//			item.getShipmentExpense();
//			item.getInvoiceDiscount();
//			item.getInvoiceExpense();
		}
	}

	public Boolean calculateEverythingForItem(TenderDetailBase item) {

		item.getBeforeTax().setValue(item.getTaxExcludedAmount().getValue());
		item.getBeforeTax().setCurrency(item.getTaxExcludedAmount().getCurrency());
		item.getBeforeTax().setLocalAmount(item.getTaxExcludedAmount().getLocalAmount());

//		calculateTaxExcludedUnitPriceAndAmount(item);
		addLineDiscountAndExpenses(item);
		calculateTaxExcludedTotal(item);
		calculateEmbeddedTaxes(item);
		calculateTotal(item);
		calculateGrandTotalForItem(item);
		return false;
	}

	//FIXME:Alış veya satış olmasına göre vergileri düzenle.
	/**
	 * Öncelikle verilen satırın vergi matrahını hesaplar.
	 * Daha sonra vergileri hesaplar. En son olarak ta
	 * satır üzerinde vergiler için açılmış alanlara gereken 
	 * atamaları yapar.
	 * 
	 * @param item
	 */
	public BigDecimal calculateTaxes(TenderDetailBase item) {
		log.info("Vergiler hesaplanıyor... \n\n");
		List<Tax> taxList = new ArrayList<Tax>();
		List<Boolean> taxIncludedList = new ArrayList<Boolean>();

		Tax otvTax = null;
		boolean otvTaxIncluded = true; 

		try {
			Tax tax = null;
			boolean taxIncluded = true; 

			for (int i = 1; i <= 5; i++) {
				tax = getTaxForItem(item, i);
				taxIncluded = getTaxIncludedForItem(item, i);
				
				if (tax != null) {
					//Bu aşamaya gelmeden önce sadece bir tane OTV olup olmadığı
					//kontrolü yapılmalı. Yoksa hesaplama hatalı olacaktır.
					if (tax.getType().equals(TaxType.OTV)) {
						otvTax = tax;
						otvTaxIncluded = taxIncluded;
					} else {
						taxList.add(tax);
						taxIncludedList.add(taxIncluded);
					}
				}
				
			}

		} catch (Exception e) {
			log.error("Hata: ", e);
			facesMessages.add(e.getMessage());
		}

		BigDecimal taxBase = BigDecimal.ZERO;//vergi matrahı
		BigDecimal totalMultiplier = BigDecimal.valueOf(100);
		BigDecimal otvTotalAmount = BigDecimal.ZERO;
		if (otvTax != null) {
			log.info("Bulunan OTV = #0, Included = #1", otvTax.getName(), otvTaxIncluded);

			TaxRate otvRate = generalSuggestion.findTaxRate(otvTax, getTender().getDate());
			if (otvRate == null) {
				log.error("#0 , ötv için uygun vergi oranı bulunamadı!", otvTax.getName());
				return BigDecimal.ZERO;
			}
			
			Money convertedOtvMoney = null;
			if (otvTaxIncluded) {
				//eğer ötv vergisi yüzde ise...
				if (otvRate.getKind().equals(TaxKind.Rate)) {
					totalMultiplier = totalMultiplier.add(otvRate.getRate());
					log.info("OTV multiplier = #0", otvRate.getRate());
				} else if (otvRate.getKind().equals(TaxKind.Amount)) {
					//otv tutarını, satırın döviz cinsine çevirmemiz gerekiyor.
					//item.getAmount() ->item.getTaxExcludedAmount();
					convertedOtvMoney = convertToCurrency(otvRate.getAmount(), item.getTaxExcludedAmount().getCurrency());
					
					otvTotalAmount = otvTotalAmount.add(convertedOtvMoney.getValue());
					log.info("OTV amount = #0", convertedOtvMoney);
				}
				
				TaxRate rate = null;
				Tax tax = null;
				Money convertedTaxMoney = null;
				boolean taxIncluded;
				for (int i=0;i<taxIncludedList.size();i++) {
					tax = taxList.get(i);
					taxIncluded = taxIncludedList.get(i);
					rate = generalSuggestion.findTaxRate(tax, getTender().getDate());
					if (rate == null) {
						log.error("#0 , için uygun vergi oranı bulunamadı!", tax.getName());
						return BigDecimal.ZERO;
					}
					
					if (taxIncluded) {
						//vergi oranı yüzde ise...
						if (rate.getKind().equals(TaxKind.Rate)) {
							log.info("Normal vergi = #0", rate.getRate());
							
							if (otvRate.getKind().equals(TaxKind.Rate)) {
								//otv oranı x vergi oranı
								BigDecimal multiplier = (otvRate.getRate().add(HUNDRED))
																		  .multiply(rate.getRate()).divide(HUNDRED);
								
								totalMultiplier = totalMultiplier.add(multiplier);
							} else {
								totalMultiplier = totalMultiplier.add(rate.getRate());
								otvTotalAmount = otvTotalAmount.add(convertedOtvMoney.getValue().multiply(rate.getRate()));
							}

						} else {
						//vergi oranı tutar ise...
							//item.getAmount() ->item.getTaxExcludedAmount();
							convertedTaxMoney = convertToCurrency(rate.getAmount(), item.getTaxExcludedAmount().getCurrency());
							
							otvTotalAmount = otvTotalAmount.add(convertedTaxMoney.getValue());
						}
					} else {
						//Vergi dahil değilse matrahı etkilemeyecektir...
					}
					
				}
				
				totalMultiplier = totalMultiplier.divide(HUNDRED, 10, RoundingMode.HALF_UP);
				
				//item.getAmount() ->item.getTaxExcludedAmount();
				taxBase = item.getTaxExcludedAmount().getValue()
								.subtract(otvTotalAmount)
								.divide(totalMultiplier, 2, RoundingMode.HALF_UP);

				log.info("Toplam yüzde çarpan = #0 ", totalMultiplier);
				log.info("Toplam tutar = #0 ", otvTotalAmount);
			} else {
				//otv vergisi dahil değilse...
				if (otvRate.getKind().equals(TaxKind.Rate)) {
					//tutar x otv yüzdesi
					//item.getAmount() ->item.getTaxExcludedAmount();
					taxBase = item.getTaxExcludedAmount().getValue().multiply(otvRate.getRate().add(HUNDRED));
				} else {
					//otv tutarını, satırın döviz cinsine çevirmemiz gerekiyor.
					//item.getAmount() ->item.getTaxExcludedAmount();
					convertedOtvMoney = convertToCurrency(otvRate.getAmount(), item.getTaxExcludedAmount().getCurrency());
										
					//item.getAmount() ->item.getTaxExcludedAmount();
					taxBase = item.getTaxExcludedAmount().getValue().add(convertedOtvMoney.getValue());
				}

			}
			
		} else {
			//otv yoksa...

			TaxRate rate = null;
			Tax tax = null;
			Money convertedTaxMoney = null;
			boolean taxIncluded;
			for (int i=0;i<taxIncludedList.size();i++) {
				tax = taxList.get(i);
				taxIncluded = taxIncludedList.get(i);
				rate = generalSuggestion.findTaxRate(tax, getTender().getDate());
				if (rate == null) {
					log.error("#0 , için uygun vergi oranı bulunamadı!", tax.getName());
					return BigDecimal.ZERO;
				}
				
				if (taxIncluded) {
					//vergi oranı yüzde ise...
					if (rate.getKind().equals(TaxKind.Rate)) {
						log.info("Normal vergi = #0", rate.getRate());
						
						totalMultiplier = totalMultiplier.add(rate.getRate());
					} else {
						//item.getAmount() ->item.getTaxExcludedAmount();
						convertedTaxMoney = convertToCurrency(rate.getAmount(), item.getTaxExcludedAmount().getCurrency());
						
						otvTotalAmount = otvTotalAmount.add(convertedTaxMoney.getValue());
					}
				} else {
					//Vergi dahil değilse matrahı etkilemeyecektir...
				}
				
			}
			totalMultiplier = totalMultiplier.divide(HUNDRED, 10, RoundingMode.HALF_UP);
			
			//item.getAmount() ->item.getTaxExcludedAmount();
			taxBase = (item.getTaxExcludedAmount().getValue()
						  .subtract(otvTotalAmount))
						  .divide(totalMultiplier, item.getUnitPriceScale(), RoundingMode.HALF_UP);

			log.info("Toplam yüzde çarpan = #0 ", totalMultiplier);
			log.info("Toplam tutar = #0 ", otvTotalAmount);
		}
		log.info("Verginin hesaplanacağı tutar(OTV haric) = #0", taxBase);
		
		//BeforeTax alanını dolduruyoruz.
//		item.getBeforeTax().setCurrency(item.getCurrencyOfItem());
//		item.getBeforeTax().setValue(taxBase);
		
		log.info("Before Tax: #0", item.getBeforeTax());
		return taxBase;
	}

	private Tax getTaxForItem(TenderDetailBase item, int index) {
		TaxEmbeddable te = null;
		Tax result = null;
		try {
//			if (item.getProductType().equals(ProductType.Expense) || 
//					item.getProductType().equals(ProductType.DocumentExpense)) {

//				te = (TaxEmbeddable)item.getClass().getMethod("getTax" + index).invoke(item);
//				
//				result = (Tax)te.getClass().getMethod("getTax").invoke(te);
//			} else {
//				result = (Tax)item.getProduct().getClass().getMethod("getSellTax" + index).invoke(item.getProduct());
//			}
			if (item.getProduct() != null) {
				result = (Tax)item.getProduct().getClass().getMethod("getSellTax" + index).invoke(item.getProduct());
			}
		} catch (SecurityException e) {
			log.error("Hata: {0}", e);
		} catch (NoSuchMethodException e) {
			log.error("Hata: {0}", e);
		} catch (IllegalArgumentException e) {
			log.error("Hata: {0}", e);
		} catch (IllegalAccessException e) {
			log.error("Hata: {0}", e);
		} catch (InvocationTargetException e) {
			log.error("Hata: {0}", e);
		} 
		return result;
	}
	
	private Boolean getTaxIncludedForItem(TenderDetailBase item, int index) {
		log.debug("Getting tax included for item :"+ index);
		TaxEmbeddable te = null;
		Boolean result = null;
		try {
			if (item.getProductType().equals(ProductType.Expense) || 
					item.getProductType().equals(ProductType.DocumentExpense)) {
				te = (TaxEmbeddable)item.getClass().getMethod("getTax" + index).invoke(item);
				
				result = (Boolean)te.getClass().getMethod("getTaxIncluded").invoke(te);
			} else {
				result = (Boolean)item.getProduct().getClass().getMethod("getTax" + index + "Included").invoke(item.getProduct());
			}
		} catch (SecurityException e) {
			log.error("Hata: {0}", e);
		} catch (NoSuchMethodException e) {
			log.error("Hata: {0}", e);
		} catch (IllegalArgumentException e) {
			log.error("Hata: {0}", e);
		} catch (IllegalAccessException e) {
			log.error("Hata: {0}", e);
		} catch (InvocationTargetException e) {
			log.error("Hata: {0}", e);
		} 
		return result;
	}
	
	private void addLineExpense(TenderDetailBase item) {
		calculateLineExpense(item);
		
		log.info("\tHesaplanan masraf satırı: Value =#0, Currency=#1, LocalAmount=#2", 
				 item.getExpense().getValue(), item.getExpense().getCurrency(), item.getExpense().getLocalAmount());
	}

	private void addLineFee(TenderDetailBase item) {
		BigDecimal calculatedFees = calculateLineFee(item);
		
		item.getFee().setValue(calculatedFees);
		item.getFee().setCurrency(getTender().getDocCurrency());
		
		BigDecimal convertedAmount = convertToLocale(item.getFee().getValue(), 
				item.getFee().getCurrency());
		
		convertedAmount = convertedAmount.setScale(2, RoundingMode.HALF_UP);
		item.getFee().setLocalAmount(convertedAmount);
		
		log.info("\tHesaplanan harç satırı: Value =#0, Currency=#1, LocalAmount=#2", 
				item.getFee().getValue(), item.getFee().getCurrency(), item.getFee().getLocalAmount());
	}
	
	private void addLineDiscount(TenderDetailBase item) {
		calculateLineDiscount(item);

		MoneySet beforeTax = item.getBeforeTax();
		beforeTax.setValue( beforeTax.getValue().subtract(item.getDiscount().getValue()) );
		
		setLocalAmountOf(item.getBeforeTax());
	}
	
	/**
	 * Adds line expense and discounts to row amount.
	 * @param item
	 */
	private void addLineDiscountAndExpenses(TenderDetailBase item) {
		log.info("İndirim ve masraflar hesaplanıyor...");
		log.info("\tİndirim ve masraflardan önce: Value =#0, Currency=#1", 
				 item.getBeforeTax().getValue(), item.getBeforeTax().getCurrency());

		addLineExpense(item);

		//FIXME: satır harcını düzenle.
		addLineFee(item);
		
		addLineDiscount(item);
		
		log.info("\tİndirimden sonra: Value =#0, Currency=#1", 
				 item.getBeforeTax().getValue(), item.getBeforeTax().getCurrency());
	}

	private void addDocumentDiscountToLine(TenderDetailBase item) {
		log.info("İndirim ve masraflar hesaplanıyor...");
		log.info("\tİndirim ve masraflardan önce: Value =#0, Currency=#1", 
				item.getBeforeTax().getValue(), item.getBeforeTax().getCurrency());

		
		MoneySet beforeTax = item.getBeforeTax();

		beforeTax.setValue(beforeTax.getValue().subtract(getTenderItemDiscount(item).getValue()));
		
		setLocalAmountOf(item.getBeforeTax());

		log.info("\tİndirimden sonra: Value =#0, Currency=#1", 
				item.getBeforeTax().getValue(), item.getBeforeTax().getCurrency());
	}

	/**
	 * Masraf satırının vergilerinin hesaplanmasını sağlar. 
	 * @param expenseItem
	 */
	private void prepareExpenseItemForTaxes(TenderDetailBase expenseItem) {
		expenseItem.setTaxExcludedAmount(expenseItem.getExpense().getAsMoney());
//		calculateTaxes(expenseItem);
		calculateTaxExcludedUnitPriceAndAmount(expenseItem);
		
		calculateTaxExcludedTotal(expenseItem);
		calculateEmbeddedTaxes(expenseItem);
		calculateTotalOfDiscountOrExpenseLine(expenseItem);
	}

	private void calculateLineDiscount(TenderDetailBase item) {
		DiscountOrExpense discount = item.getDiscount();
		discount = new DiscountOrExpense();
		
		item.setDiscount(discount);
		for(TenderDetailBase discountItem : ((ITenderDetail)item).getDiscountList()) {
			BigDecimal calculatedAmount = prepareDiscountOrExpenseAmount(item, discountItem.getDiscount());
			
			discount.setValue(discount.getValue().add(calculatedAmount));
			discount.setCurrency(getTender().getDocCurrency());

			//hesaplanan indirimi hem indirim satırındaki amount alanına, hem de 
			//indirim satırının parentinin toplamına atıyoruz.
			discountItem.getAmount().setValue(calculatedAmount);
			discountItem.getAmount().setCurrency(discount.getCurrency());
			setLocalAmountOf(discountItem.getAmount());
		}

		BigDecimal convertedAmount = convertToLocale(discount.getValue(), discount.getCurrency());
		convertedAmount = convertedAmount.setScale(2, RoundingMode.HALF_UP);
		discount.setLocalAmount(convertedAmount);
		item.setDiscount(discount);
	}

	private BigDecimal prepareFeeAmount(TenderDetailBase parentDetail, TenderDetailBase childDetail) {
		BigDecimal calculatedAmount = null;
		BigDecimal itemQuantity = BigDecimal.valueOf(parentDetail.getQuantity().getValue());

		DiscountOrExpense fee = childDetail.getFee();
		
		if (fee.getPercentage()) {
			BigDecimal multiplier = fee.getRate().divide(HUNDRED);
			
			calculatedAmount = parentDetail.getBeforeTax().getValue().multiply(multiplier);
		} else {
			MoneySet convertedMoney = new MoneySet(fee.getValue(), fee.getLocalAmount(), fee.getCurrency());
			
			calculatedAmount = convertToCurrency(convertedMoney, parentDetail.getBeforeTax().getCurrency()).getValue();

			//Birim adedince harç ekleniyor.
			BigDecimal quantityValue = BigDecimal.valueOf(parentDetail.getQuantity().getValue());
			calculatedAmount = calculatedAmount.multiply(quantityValue);
		}
		fee.setValue(calculatedAmount);
		fee.setCurrency(getTender().getDocCurrency());

		childDetail.getQuantity().moveFieldsOf(parentDetail.getQuantity());
		childDetail.getUnitPrice().moveFieldsOf(fee.getAsMoney());

		childDetail.getAmount().setValue(fee.getValue().multiply(itemQuantity));
		
		setLocalAmountOf(childDetail.getAmount());
		
		return childDetail.getAmount().getValue();
	}
	
	private BigDecimal prepareDiscountOrExpenseAmount(TenderDetailBase item, DiscountOrExpense doe) {
		BigDecimal calculatedAmount = null;
		if (doe.getPercentage()) {
			BigDecimal multiplier = doe.getRate().divide(HUNDRED);
			
			calculatedAmount = item.getBeforeTax().getValue().multiply(multiplier);
		
		} else {
			MoneySet convertedMoney = new MoneySet(doe.getValue(), doe.getLocalAmount(), doe.getCurrency());
			
			calculatedAmount = convertToCurrency(convertedMoney, item.getBeforeTax().getCurrency()).getValue();

			//Birim adedince indirim ekleniyor.
			BigDecimal quantityValue = BigDecimal.valueOf(item.getQuantity().getValue());
			calculatedAmount = calculatedAmount.multiply(quantityValue);
		}
		return calculatedAmount;
	}
	
	private void calculateLineExpense(TenderDetailBase item) {
		DiscountOrExpense expense = item.getExpense();
		expense = new DiscountOrExpense();

		for (TenderDetailBase expenseItem : ((ITenderDetail)item).getExpenseList()) {
			BigDecimal calculatedAmount = prepareDiscountOrExpenseAmount(item, expenseItem.getExpense());

			prepareExpenseItemForTaxes(expenseItem);

			expense.setValue(expense.getValue().add(calculatedAmount));
			expense.setCurrency( getTender().getDocCurrency() );
			
			//hesaplanan masrafı hem masraf satırındaki amount alanına, hem de 
			//masraf satırının parentinin toplamına atıyoruz.
			expenseItem.getAmount().setValue(calculatedAmount);
			expenseItem.getAmount().setCurrency(expense.getCurrency());
			setLocalAmountOf(expenseItem.getAmount());
			
		}
		BigDecimal convertedAmount = convertToLocale(expense.getValue(), expense.getCurrency());
		convertedAmount = convertedAmount.setScale(2, RoundingMode.HALF_UP);
		expense.setLocalAmount(convertedAmount);
		item.setExpense(expense);
	}

	private BigDecimal calculateLineFee(TenderDetailBase item) {
		BigDecimal result = BigDecimal.ZERO;
		
//		DiscountOrExpense fee1 = item.getFee();

		for (TenderDetailBase feeItem : ((ITenderDetail)item).getFeeList()) {
			prepareFeeAmount(item,feeItem);
			
			result = result.add(feeItem.getTotalAmount().getValue());
			
		}
		return result;
	}

	/**
	 * Calculates taxes for row and sets them to embedded tax 
	 * object's fields. And calculates total tax amount.
	 * @param item
	 */
	public void calculateEmbeddedTaxes(TenderDetailBase item) {
		BigDecimal taxTotalValue = BigDecimal.ZERO;
		BigDecimal taxTotalLocalAmount = BigDecimal.ZERO;

		try {
			Tax tax = null;
			boolean taxIncluded = true; 

			Method taxEmbeddableGetter = null;
			TaxEmbeddable taxEmbeddable = null;

			TaxRate rate = null;
			BigDecimal result = BigDecimal.ZERO;
			BigDecimal taxExcludedTotal = BigDecimal.ZERO;
			for (int i = 1; i <= 5; i++) {
				tax = (Tax)getTaxForItem(item, i);
				taxIncluded = (Boolean)getTaxIncludedForItem(item, i);

				taxEmbeddableGetter = item.getClass().getMethod("getTax" + i);
				taxEmbeddable = (TaxEmbeddable)taxEmbeddableGetter.invoke(item);
				
				if (tax != null) {
					rate = generalSuggestion.findTaxRate(tax, getTender().getDate());
					taxExcludedTotal = item.getTaxExcludedTotal().getValue();

					if (tax.getType().equals(TaxType.OTV)) {
						BigDecimal beforeTax = item.getBeforeTax().getValue();
						result = taxExcludedTotal.subtract(beforeTax);
						
						if (rate.getKind().equals(TaxKind.Rate)) {
							taxEmbeddable.setRate(rate.getRate());
						}
					} else {

						if (rate.getKind().equals(TaxKind.Rate)) {
							result = taxExcludedTotal.multiply(rate.getRate()).divide(HUNDRED, 2, RoundingMode.HALF_UP);
							
							taxEmbeddable.setRate(rate.getRate());
						} else {
							Money convertedAmount = convertToCurrency(rate.getAmount(), item.getAmount().getCurrency());
							
							result = convertedAmount.getValue();
						}

					}

					TaxKind withholdingKind = rate.getWithholdingKind();
					if (withholdingKind != null) {
						taxEmbeddable.setWithholdingKind(withholdingKind);
						
						if (withholdingKind.equals(TaxKind.Amount)) {
							
							taxEmbeddable.setWithholdingAmount(rate.getWithholdingAmount());
							
						} else if (withholdingKind.equals(TaxKind.Rate)) {
							taxEmbeddable.setWithholdingRate(rate.getWithholdingRate());
							
							BigDecimal multiplier = rate.getWithholdingRate().divide(HUNDRED, 2, RoundingMode.HALF_UP);
								
							taxEmbeddable.setWithholdingAmount(result.multiply(multiplier));
							
						} else if (withholdingKind.equals(TaxKind.Fraction)) {

							taxEmbeddable.setWithholdingRate(rate.getWithholdingRate());
							
							taxEmbeddable.setWithholdingAmount(result.divide(rate.getWithholdingRate(), 2, RoundingMode.HALF_UP));
						}

					}
					
					taxEmbeddable.setCurrency(item.getTaxExcludedTotal().getCurrency());
					taxEmbeddable.setTax(tax);
					taxEmbeddable.setValue(result);
					taxEmbeddable.setKind(rate.getKind());
					taxEmbeddable.setTaxIncluded(taxIncluded);
					taxEmbeddable.setType(tax.getType());

					BigDecimal convertedResult = convertToLocale(result, item.getTaxExcludedTotal().getCurrency());
					taxEmbeddable.setLocalAmount(convertedResult);
					
					log.info("Tax Embeddable: Currency #0, LocalAmount #1, " +
							 "Tax Kind #2, Value #3, TaxInc. #4,Type #5, " +
							 "Tax Withholding #6", 
							taxEmbeddable.getCurrency(), taxEmbeddable.getLocalAmount(),
							taxEmbeddable.getKind(), taxEmbeddable.getValue(),
							taxEmbeddable.getTaxIncluded(), taxEmbeddable.getType(),
							taxEmbeddable.getWithholdingAmount());
				}
				taxTotalLocalAmount = taxTotalLocalAmount.add(taxEmbeddable.getLocalAmount());
				taxTotalValue = taxTotalValue.add(taxEmbeddable.getValue());
			}

		} catch (Exception e) {
			log.error("Hata: ", e);
			facesMessages.add(e.getMessage());
		}
		
		//toplam vergi tutarını atıyoruz.
		item.getTaxTotalAmount().setCurrency(item.getAmount().getCurrency());
		item.getTaxTotalAmount().setLocalAmount(taxTotalLocalAmount);
		item.getTaxTotalAmount().setValue(taxTotalValue);
		
		log.info("Vergi toplamı:Value #0, Local #1", taxTotalValue, taxTotalLocalAmount);
	}

	/**
	 * Sums row's before tax, tax total, expenses and sets to
	 * total amount.
	 * @param item
	 */
	private void calculateTotal(TenderDetailBase item) {
		log.info("Calculating Total...");
		
		BigDecimal beforeTax = item.getBeforeTax().getValue();
		BigDecimal taxTotal = item.getTaxTotalAmount().getValue();
		
		item.getTotalAmount().setCurrency(item.getCurrencyOfItem());
		item.getTotalAmount().setValue(beforeTax.add(taxTotal));
		setLocalAmountOf(item.getTotalAmount());
		
		//satır toplamının değişip değişmediği bilgisini tutacağız.
		item.getTotalAmountTransient().setCurrency(item.getCurrencyOfItem());
		item.getTotalAmountTransient().setValue(beforeTax.add(taxTotal));
		
		log.info("Total Amount: #0", item.getTotalAmount());
	}

	/**
	 * Sets local amount of given money.
	 * @param money
	 */
	protected void setLocalAmountOf(MoneySet money) {
		//FIXME: scale i 2 yerine parametrik hale getirmeli.
		money.setValue( money.getValue().setScale(2, RoundingMode.HALF_UP) );
		BigDecimal convertedValue = convertToLocale(money.getValue(), money.getCurrency());
		
		convertedValue = convertedValue.setScale(2, RoundingMode.HALF_UP);
		money.setLocalAmount(convertedValue);
	}

	protected void setLocalAmountOf(DiscountOrExpense doe) {
		//FIXME: scale i 2 yerine parametrik hale getirmeli.
		doe.setValue( doe.getValue().setScale(2, RoundingMode.HALF_UP) );
		BigDecimal convertedValue = convertToLocale(doe.getValue(), doe.getCurrency());
		
		convertedValue = convertedValue.setScale(2, RoundingMode.HALF_UP);
		doe.setLocalAmount(convertedValue);
	}
	
	protected void setCurrencyValue(MoneySet money) {
		money.setValue( convertToCurrency(money.getLocalAmount(), BaseConsts.SYSTEM_CURRENCY_CODE, getTender().getDocCurrency()) );
	}
	
	/**
	 * Sums row's before tax, tax total, document discount, document expense
	 * and fee. If row has got withholding, it will be subtracted.
	 * @param item
	 */
	private void calculateGrandTotalForItem(TenderDetailBase item) {
		log.info("Calculating grand total of item...");

		BigDecimal totalAmount = item.getTotalAmount().getValue();
		BigDecimal fee = item.getFee().getValue();
		BigDecimal expense = item.getExpense().getValue();

		item.getGrandTotal().setCurrency(item.getCurrencyOfItem());
		item.getGrandTotal().setValue(totalAmount.add(fee)
												 .add(getTenderItemExpense(item).getValue())
												 .add(expense)
												 .subtract(findWithholdingTotalForItem(item)));

		setLocalAmountOf(item.getGrandTotal());
		
		log.info("Grand Total Of Item: #0", item.getGrandTotal());
	}

	/**
	 * Finds total withholding amount for item.
	 * @param item
	 * @return result
	 */
	private BigDecimal findWithholdingTotalForItem(TenderDetailBase item) {
		log.info("Calculating withholding amount...");
		BigDecimal result = BigDecimal.ZERO;
		
		for (TaxEmbeddable taxItem : item.getTaxList()) {
			if (taxItem != null && taxItem.getWithholdingAmount() != null) {
				result = result.add(taxItem.getWithholdingAmount());
			}
		}

		log.info("Total withholding amount of item: #0",result);
		return result;
	}

	/**
	 * Calculates tax excluded total of row. If row has
	 * OTV tax, it'll be added to total. 
	 * @param item
	 */
	private void calculateTaxExcludedTotal(TenderDetailBase item) {
		log.info("Calculating tax excluded total...");
		Tax otvTax = item.getOTVTax();
		BigDecimal excludedTotal = null;
		if (otvTax != null) {
			TaxRate otvRate = generalSuggestion.findTaxRate(otvTax, getTender().getDate());
			if (otvRate.getKind().equals(TaxKind.Rate)) {
				BigDecimal multiplier = (HUNDRED.add(otvRate.getRate())).divide(HUNDRED);
				
				excludedTotal = item.getBeforeTax().getValue().multiply(multiplier);
			} else {
				Money convertedTaxMoney = currencyManager.convert(otvRate.getAmount(),
						item.getTaxExcludedAmount().getCurrency(),
						getTender().getDate());
				
				excludedTotal = item.getBeforeTax().getValue().add(convertedTaxMoney.getValue());
			}
			
		} else {
			//ötv yoksa beforeTax alanı taxEcludedTotal e eşit olmalı.
			excludedTotal = item.getBeforeTax().getValue();
		}
		item.getTaxExcludedTotal().setCurrency(item.getCurrencyOfItem());
		item.getTaxExcludedTotal().setValue(excludedTotal);
		
		setLocalAmountOf(item.getTaxExcludedTotal());

		log.info("Calculated tax excluded total: value=#0, currency=#1", 
					item.getTaxExcludedTotal().getValue(), item.getTaxExcludedTotal().getCurrency());
	}

	@SuppressWarnings("unchecked")
	private void fillTaxSummaryList(ITender tender) {
		List<TenderTaxSummaryBase> taxSummaryList = (List<TenderTaxSummaryBase>)tender.getTaxSummaryList();
		taxSummaryList.clear();


		TenderTaxSummaryBase tts = null;

		Method taxGetter = null;
		TaxEmbeddable taxEmbeddable = null;
		try {
			for (TenderDetailBase item : tender.getProductItems()) {
				for (int i=1;i<=5;i++) {
					taxGetter = item.getClass().getMethod("getTax" + i);
					taxEmbeddable = (TaxEmbeddable)taxGetter.invoke(item);

					if (taxEmbeddable.getTax() != null) {
						tts = createSuitableTaxSummaryItem();
						
						tts.setKind(taxEmbeddable.getKind());
						tts.setType(taxEmbeddable.getType());

						
						MoneySet moneySet = new MoneySet();
						moneySet.setCurrency(taxEmbeddable.getCurrency());
						moneySet.setLocalAmount(taxEmbeddable.getLocalAmount());
						moneySet.setValue(taxEmbeddable.getValue());

						tts.setAmount(moneySet);

						MoneySet baseMoneySet = new MoneySet();
						baseMoneySet.setCurrency(item.getCurrencyOfItem());
                        if (tts.getType().equals(TaxType.OTV)) {
                            baseMoneySet.setLocalAmount(item.getBeforeTax().getLocalAmount());
                            baseMoneySet.setValue(item.getBeforeTax().getValue());
                        } else {
                            baseMoneySet.setLocalAmount(item.getTaxExcludedTotal().getLocalAmount());
                            baseMoneySet.setValue(item.getTaxExcludedTotal().getValue());
                        }
						tts.setBase(baseMoneySet);
						
						
						TaxRate foundRate = generalSuggestion.findTaxRate(taxEmbeddable.getTax(), getTender().getDate());

						if (taxEmbeddable.getKind().equals(TaxKind.Amount)) {
							
							MoneySet sourceAmount = new MoneySet();
							
							sourceAmount.setCurrency(foundRate.getAmount().getCurrency());
							sourceAmount.setLocalAmount(foundRate.getAmount().getLocalAmount());
							sourceAmount.setValue(foundRate.getAmount().getValue());
							tts.setSourceAmount(sourceAmount);
							
						} else {
							tts.setSourceRate(foundRate.getRate());
						}
						
						TaxBase foundReference = getReferenceToTaxBase(taxSummaryList, tts);

						if (foundReference == null) {
							taxSummaryList.add(tts);
						} else {
							MoneySet amount = foundReference.getAmount();
							amount.setValue(amount.getValue().add(tts.getAmount().getValue()));
							foundReference.setAmount(amount);
							
							MoneySet base = foundReference.getBase();
							base.setLocalAmount(base.getLocalAmount().add(item.getBeforeTax().getLocalAmount()));
							base.setValue(base.getValue().add(item.getBeforeTax().getValue()));
							foundReference.setBase(base);
						}
					}
				}
			}
			
			for (TenderTaxSummaryBase summaryItem : taxSummaryList) {
				setLocalAmountOf(summaryItem.getAmount());
			}
		} catch (Exception e) {
			log.info("Hata: ", e.getMessage());
		}
		log.info("Tax Summary Map: #0", taxSummaryList);
	}

	private TaxBase getReferenceToTaxBase(List<TenderTaxSummaryBase> summaryBaseList, TenderTaxSummaryBase aSummaryBase) {
		for (TenderTaxSummaryBase item : summaryBaseList) {
			if (item.getType().equals(aSummaryBase.getType())) {

				if (item.getKind().equals(aSummaryBase.getKind())) {
					if (item.getKind().equals(TaxKind.Amount)) {
						if (item.getSourceAmount().getValue().equals(aSummaryBase.getSourceAmount().getValue()) &&
								item.getAmount().getCurrency().equals(aSummaryBase.getAmount().getCurrency())) {
							return item;
						} 
					} else if (item.getKind().equals(TaxKind.Rate)) {
						if (item.getSourceRate().equals(aSummaryBase.getSourceRate()) &&
								item.getAmount().getCurrency().equals(aSummaryBase.getAmount().getCurrency())) {
							return item;
						} 
					}
				}
			}
		}
		return null;
	}
	
	private void fillCurrencySummaryList(ITender tender) {
		log.info("Filling Currency Summary List...");
		Map<String, MoneySet> currencySummaryMap = new HashMap<String, MoneySet>();
		
		MoneySet moneySet = null;
		MoneySet grandTotal = null;
		for (TenderDetailBase item : tender.getItems()) {
			grandTotal =  convertToCurrency(item.getGrandTotal(), item.getForeignUnitPrice().getCurrency());
			if (!currencySummaryMap.containsKey(grandTotal.getCurrency())) {
				moneySet = new MoneySet();
				moneySet.setCurrency(grandTotal.getCurrency());
				moneySet.setValue(grandTotal.getValue());
				
				currencySummaryMap.put(grandTotal.getCurrency(), moneySet);
			} else {
				moneySet = currencySummaryMap.get(grandTotal.getCurrency());
				moneySet.setValue(moneySet.getValue().add(grandTotal.getValue()));
			}
		}

		prepareCurrencySummaryList(tender, currencySummaryMap);
		
		log.info("Currency Summary Map: #0", currencySummaryMap);
	}

	@SuppressWarnings("unchecked")
	private void prepareCurrencySummaryList(ITender tender, Map<String, MoneySet> currencySummaryMap) {
		List<TenderCurrencySummaryBase> currencySummaryList = (List<TenderCurrencySummaryBase>)tender.getCurrencySummaryList();

		currencySummaryList.clear();

		TenderCurrencySummaryBase tcs = null;
		for(Entry<String, MoneySet> item : currencySummaryMap.entrySet()) {
			tcs = createSuitableCurrencySummaryItem();
			
			tcs.getAmount().setValue(item.getValue().getValue());
			tcs.getAmount().setCurrency(item.getKey());
			setLocalAmountOf(tcs.getAmount());
			
			currencySummaryList.add(tcs);
		}
		
	}
	
	/**
	 * Creates and returns suitable tax summary items.
	 * @return item
	 */
	private TenderTaxSummaryBase createSuitableTaxSummaryItem() {
		TenderTaxSummaryBase item = null;
		String entityName = entity.getClass().getSimpleName();
		if(entityName.equals("Tender")) {
			item = new TenderTaxSummary();
		} else if (entityName.equals("TekirOrderNote")){
			item = new TekirOrderNoteTaxSummary();
		} else if (entityName.equals("TekirShipmentNote")){
			item = new TekirShipmentNoteTaxSummary();
		} else if (entityName.equals("TekirInvoice")){
			item = new TekirInvoiceTaxSummary();
		}
		return item;
	}

	/**
	 * Creates and returns suitable currency summary items.
	 * @return item
	 */
	private TenderCurrencySummaryBase createSuitableCurrencySummaryItem() {
		TenderCurrencySummaryBase item = null;
		String entityName = entity.getClass().getSimpleName();
		if(entityName.equals("Tender")) {
			item = new TenderCurrencySummary();
		} else if (entityName.equals("TekirOrderNote")){
			item = new TekirOrderNoteCurrencySummary();
		} else if (entityName.equals("TekirShipmentNote")){
			item = new TekirShipmentNoteCurrencySummary();
		} else if (entityName.equals("TekirInvoice")){
			item = new TekirInvoiceCurrencySummary();
		}
		return item;
	}
	
	/**
	 * Creates and returns suitable currency rate items.
	 * @return item
	 */
	private TenderCurrencyRateBase createSuitableCurrencyRateItem() {
		TenderCurrencyRateBase item = null;
		String entityName = entity.getClass().getSimpleName();
		if(entityName.equals("Tender")) {
			item = new TenderCurrencyRate();
		} else if (entityName.equals("TekirOrderNote")){
			item = new TekirOrderNoteCurrencyRate();
		} else if (entityName.equals("TekirShipmentNote")){
			item = new TekirShipmentNoteCurrencyRate();
		} else if (entityName.equals("TekirInvoice")){
			item = new TekirInvoiceCurrencyRate();
		}
		return item;
	}

	public void fillCurrencyRateForItem(TenderDetailBase item) {
		String itemCurrency = item.getForeignUnitPrice().getCurrency();
		if (!itemCurrency.equals(BaseConsts.SYSTEM_CURRENCY_CODE) && !itemCurrency.equals(getTender().getDocCurrency()) && 
				!currencyRateListContains(itemCurrency)) {
			addToCurrencyRateList(itemCurrency);
		}
		addDocumentCurrencyToCurrencyRates();
	}

	@SuppressWarnings("unchecked")
	private void fillCurrencyRateList(ITender tender) {

		log.info("Filling Currency Rate List...");
		List<TenderCurrencyRateBase> currencyRateList = (List<TenderCurrencyRateBase>)tender.getCurrencyRateList();

		String itemCurrency = null;
		for (TenderDetailBase item : tender.getItems()) {
			itemCurrency = item.getForeignUnitPrice().getCurrency();
			
			if (!itemCurrency.equals(BaseConsts.SYSTEM_CURRENCY_CODE) && !itemCurrency.equals(getTender().getDocCurrency()) && 
					!currencyRateListContains(itemCurrency)) {
				addToCurrencyRateList(itemCurrency);
			}
		}
		//listeyi tersten kontrol ediyoruz.
		TenderCurrencyRateBase tcr = null;
		for (int i=0;i<currencyRateList.size();i++) {
			tcr = currencyRateList.get(i);
			
			if (!(itemListContains(tcr.getCurrencyPair().getHardCurrency().getCode()))) {
				currencyRateList.remove(i);
			}
		}

		addDocumentCurrencyToCurrencyRates();

		log.info("Currency Rate List: #0", currencyRateList);
	}
	
	private void addDocumentCurrencyToCurrencyRates() {
		if (!getTender().getDocCurrency().equals(BaseConsts.SYSTEM_CURRENCY_CODE) && 
			!currencyRateListContains(getTender().getDocCurrency())) {
			addToCurrencyRateList(getTender().getDocCurrency());
		}
	}

	@SuppressWarnings("unchecked")
	private void addToCurrencyRateList(String itemCurrency) {
		List<TenderCurrencyRateBase> rateList = (List<TenderCurrencyRateBase>)getTender().getCurrencyRateList();

		CurrencyPair cp = currencyManager.getCurrencyPair(itemCurrency, BaseConsts.SYSTEM_CURRENCY_CODE);
		CurrencyRate cr = currencyManager.getCurrencyRate(getTender().getDate(), cp);
		
		TenderCurrencyRateBase item = createSuitableCurrencyRateItem();
		item.setCurrencyPair(cp);

		if (cr != null) {
			item.setAsk( cr.getAsk().setScale(4, RoundingMode.HALF_UP));
			item.setBid( cr.getBid().setScale(4, RoundingMode.HALF_UP));
		}
		rateList.add(item);
	}
	
	/**
	 * Checks if given currency in entity's currency list or
	 * not. 
	 * @param currency
	 * @return result
	 */
	@SuppressWarnings("unchecked")
	private boolean currencyRateListContains(String currency) {
		for (TenderCurrencyRateBase tcr : (List<TenderCurrencyRateBase>)getTender().getCurrencyRateList()) {
			if(tcr.getCurrencyPair().getHardCurrency().getCode().equals(currency)) {
				return true;
			}
		}
		return false;
	}

	private BigDecimal convertToLocale(BigDecimal value, String currency) {
		BigDecimal currencyRate = getCurrencyRate(currency);
		if (!currency.equals(BaseConsts.SYSTEM_CURRENCY_CODE) && currencyRate != null) return value.multiply(currencyRate);
		return value;
	}

	/**
	 * Converts given locale to currency.
	 * @param value
	 * @param currency
	 * @return
	 */
	public MoneySet convertToCurrency(MoneySet money, String currency) {
		MoneySet result = new MoneySet(currency);
		
		if (!money.getCurrency().equals(currency)) {
			result.setValue( convertToCurrency(money.getLocalAmount(), money.getCurrency(), currency) );
		} else {
			result.setValue( money.getValue() );
		}
		log.info("Converted #0 => #1",money ,result);
		return result;
	}

    public BigDecimal convertToCurrency( BigDecimal value, String actualCcy, String destCcy){

    	if (!actualCcy.equals(destCcy)) {
    		BigDecimal currencyRate = getCurrencyRate(destCcy);
			if (currencyRate != null && currencyRate.compareTo(BigDecimal.ZERO) != 0) return value.divide(currencyRate, 2, RoundingMode.HALF_UP);
    	}
    	return value;
    }
    
	private BigDecimal findCurrencyRate(String currency, CurrencyRateType rateType) {
		BigDecimal result = null;
		
		result = getRateFromDocumentCurrencyRates(currency, rateType);

		if (result == null) {
			result = getRateFromTCMB(currency, rateType);
		}
		return result;
	}

	private BigDecimal getRateFromDocumentCurrencyRates(String currency, CurrencyRateType rateType) {
		for (TenderCurrencyRateBase tcr :getTender().getCurrencyRateList()) {
			if (tcr.getCurrencyPair().getHardCurrency().getCode().equals(currency)) {
				if (rateType.equals(CurrencyRateType.Ask)) {
					return tcr.getAsk().setScale(4, RoundingMode.HALF_UP);
				} else {
					return tcr.getBid().setScale(4, RoundingMode.HALF_UP);
				}
			}
		}
		return null;
	}

	private BigDecimal getRateFromTCMB(String currency, CurrencyRateType rateType) {
		CurrencyRate rate = currencyManager.getCurrencyRate(getTender().getDate(), currency, BaseConsts.SYSTEM_CURRENCY_CODE);
		if (rate != null) {
			if (rateType.equals(CurrencyRateType.Ask)) {
				return rate.getAsk().setScale(4, RoundingMode.HALF_UP);
			} else {
				return rate.getBid().setScale(4, RoundingMode.HALF_UP);
			}
		}
		return null;
	}
	
	private BigDecimal getCurrencyRate(String currency) {
		return findCurrencyRate(currency, getTender().getRateType());
	}
	
	/**
	 * Checks if given currency in entity item's currency list or
	 * not. 
	 * @param currency
	 * @return result
	 */
	@SuppressWarnings("unchecked")
	private boolean itemListContains(String currency) {
		for (TenderDetailBase item : (List<TenderDetailBase>)getTender().getItems()) {
			log.debug("currency of item: #0, currency #1",item.getForeignUnitPrice().getCurrency(), currency);
			if(item.getForeignUnitPrice().getCurrency().equals(currency) || 
					item.getTaxExcludedAmount().getCurrency().equals(currency)) return true;
		}
		return false;
	}

	private void calculateDocumentDiscountAndExpenses() {
		clearDocumentsDiscountsAndExpenses();

		calculateDocumentDiscounts();

		calculateDocumentExpenses();

	}

	private void calculateDocumentDiscounts() {
		log.info("Calculating Documents Discounts...");

		MoneySet discount = null;
		BigDecimal result= null;
		for(TenderDetailBase discountItem : getTender().getDocumentDiscountItems()) {
			for(TenderDetailBase tdb : getTender().getProductItems()) {
				result = prepareDocumentDiscountOrExpenseAmount(discountItem, tdb, 
																discountItem.getDiscountStyle(), 
																discountItem.getDiscount());
				
				discount = getTenderItemDiscount(tdb);
				discount.setCurrency(tdb.getCurrencyOfItem());
				discount.setLocalAmount(discount.getLocalAmount().add(result));
				
				BigDecimal convertedMoney = convertToCurrency(result, tdb.getCurrencyOfItem(), tdb.getAmount().getCurrency());
				discount.setValue(discount.getValue().add(convertedMoney));
				
				log.info("Calculated discount: Value =#0, Currency =#1", discount.getValue(), discount.getCurrency());
			}
		}
	}

	private void calculateDocumentExpenses() {
		log.info("Calculating Documents Expenses...");

		MoneySet expense = null;
		BigDecimal result= null;
		for(TenderDetailBase expenseItem : getTender().getDocumentExpenseItems()) {
			for(TenderDetailBase tdb : getTender().getProductItems()) {
				
				prepareExpenseItemForTaxes(expenseItem);
				
				result = prepareDocumentDiscountOrExpenseAmount(expenseItem, tdb, 
																expenseItem.getExpenseStyle(), 
																expenseItem.getExpense());
				
				expense = getTenderItemExpense(tdb);
				expense.setCurrency(tdb.getCurrencyOfItem());
				expense.setLocalAmount(expense.getLocalAmount().add(result));
				
				BigDecimal convertedMoney = convertToCurrency(result, tdb.getCurrencyOfItem(), tdb.getAmount().getCurrency());
				expense.setValue(expense.getValue().add(convertedMoney));
			}
			log.info("Calculated document expense: Value =#0, Currency =#1", expense.getValue(), expense.getCurrency());
		}
	}

	private void calculateDocumentFees() {
		log.info("Calculating Document Fees...");
		DiscountOrExpense fee = null;
		for (TenderDetailBase item : getTender().getDocumentFeeItems()) {
			fee = item.getFee();
			
			if (fee.getPercentage()) {
				BigDecimal multiplicand = fee.getRate().divide(HUNDRED);

				fee.setValue(getTender().getTotalAmount().getValue().multiply(multiplicand));
				fee.setLocalAmount(fee.getValue());
				log.info("Calculated document fee: Value =#0, Currency =#1", fee.getValue(), fee.getCurrency());
			} else {
				BigDecimal convertedAmount = convertToLocale(fee.getValue(), fee.getCurrency());
				fee.setLocalAmount(convertedAmount);
			}
			item.getAmount().moveFieldsOf(fee.getAsMoney());
		}
	}
	
	private void calculateTotalOfDiscountOrExpenseLine(TenderDetailBase item) {
		log.info("Calculating Total Of Discount Or Expense Line...");
		
		BigDecimal beforeTax = item.getBeforeTax().getValue();
		BigDecimal taxTotal = item.getTaxTotalAmount().getValue();
		
		item.getTotalAmount().setCurrency(item.getAmount().getCurrency());
		item.getTotalAmount().setValue(beforeTax.add(taxTotal));
		
		setLocalAmountOf(item.getTotalAmount());
		
		log.info("Total Amount: #0", item.getTotalAmount());
	}

	public void calculateTaxExcludedUnitPriceAndAmount(TenderDetailBase item) {
		BigDecimal taxBase = calculateTaxes(item);
		
		item.getBeforeTax().setCurrency(item.getCurrencyOfItem());
		item.getBeforeTax().setValue(taxBase);
		
		
		calculateTaxExcludedUnitPrice(item);
		calculateTaxExcludedAmount(item);
	}
	
	private void calculateTaxExcludedUnitPrice(TenderDetailBase item) {
		log.info("Calculating Tax Excluded Unit Price For Item...");
		MoneySet beforeTax = item.getBeforeTax();
		
		BigDecimal quantity = BigDecimal.valueOf(item.getQuantity().getValue()); 

		
		if (quantity.compareTo(BigDecimal.ZERO) > 0) {
			MoneySet taxExcludedUnitPrice = item.getTaxExcludedUnitPrice();
			taxExcludedUnitPrice.setValue(beforeTax.getValue().divide(quantity));
			taxExcludedUnitPrice.setCurrency(getTender().getDocCurrency());

			setLocalAmountOf(taxExcludedUnitPrice);

			log.info("Tax Excluded Unit Price For Item :{0}", taxExcludedUnitPrice.getValue());
		}
	}

	/**
	 * It is equal to before tax value, before discounts and expenses applied
	 * to the line.
	 * @param item
	 */
	private void calculateTaxExcludedAmount(TenderDetailBase item) {
		log.info("Calculating Tax Excluded Amount For Item...");
		
		MoneySet beforeTax = item.getBeforeTax();
		
		MoneySet taxExcludedAmount = item.getTaxExcludedAmount();
		
		taxExcludedAmount.setCurrency(beforeTax.getCurrency());
		taxExcludedAmount.setValue(beforeTax.getValue());

		setLocalAmountOf(taxExcludedAmount);
		
		log.info("Tax Excluded Amount For Item :{0}",taxExcludedAmount.getValue());
	}
	
	private void clearDocumentsDiscountsAndExpenses() {
		MoneySet discount = null;
		MoneySet expense = null;
		for (TenderDetailBase item : getTender().getItems()) {
			discount = getTenderItemDiscount(item);
			discount.clearMoney();

			expense = getTenderItemExpense(item);
			expense.clearMoney();
		}
	}

	private BigDecimal prepareDocumentDiscountOrExpenseAmount(TenderDetailBase mainItem, TenderDetailBase tdb, 
															  DistributionStyle discountStyle, DiscountOrExpense doe) {
		
		if (doe.getPercentage()) {
			BigDecimal multiplicand = doe.getRate().divide(HUNDRED);
			
			doe.setValue(getTender().getTotalBeforeTax().getValue().multiply(multiplicand));
			doe.setLocalAmount(doe.getValue());
		}
		
		BigDecimal calculationAmount = null;
		if (mainItem.getProductType().equals(ProductType.DocumentDiscount) ||
			mainItem.getProductType().equals(ProductType.ContactDiscount) || 
			mainItem.getProductType().equals(ProductType.DocumentExpense)) {
			calculationAmount = doe.getValue();
		} else {
			calculationAmount = mainItem.getTotalAmount().getValue();
		}
		
		BigDecimal result= null;
		BigDecimal divisor = null;
		BigDecimal multiplier = null;
		if (discountStyle.equals(DistributionStyle.AmountBased)) {
			multiplier = calculationAmount.divide(sumBeforeTaxes(), 10, RoundingMode.HALF_UP);

			result = tdb.getBeforeTax().getLocalAmount().multiply(multiplier);
			result = result.setScale(2, RoundingMode.HALF_UP);
		} else if (discountStyle.equals(DistributionStyle.Equally)) {
			divisor = BigDecimal.valueOf(getTender().getProductItems().size());
			
			result = calculationAmount.divide(divisor, 2, RoundingMode.HALF_UP);
		} else if (discountStyle.equals(DistributionStyle.UnitBased)) {
			divisor = sumQuantity();
			
			result = calculationAmount.divide(divisor, 2, RoundingMode.HALF_UP);
			
			BigDecimal quantityValue = BigDecimal.valueOf(tdb.getQuantity().getValue()); 
			result = result.multiply(quantityValue);
		}
		return result;
	}
	
	private MoneySet getTenderItemDiscount(TenderDetailBase tdb) {
		String entityName = entity.getClass().getSimpleName();
		if(entityName.equals("Tender")) {
			return  ((ITenderDetail)tdb).getTenderDiscount();
		} else if (entityName.equals("TekirOrderNote")){
			return ((ITenderDetail)tdb).getOrderDiscount();
		} else if (entityName.equals("TekirShipmentNote")){
			return ((ITenderDetail)tdb).getShipmentDiscount();
		} else if (entityName.equals("TekirInvoice")){
			return ((ITenderDetail)tdb).getInvoiceDiscount();
		}
		return null;
	}

	private MoneySet getTenderItemExpense(TenderDetailBase tdb) {
		String entityName = entity.getClass().getSimpleName();
		if(entityName.equals("Tender")) {
			return  ((ITenderDetail)tdb).getTenderExpense();
		} else if (entityName.equals("TekirOrderNote")){
			return ((ITenderDetail)tdb).getOrderExpense();
		} else if (entityName.equals("TekirShipmentNote")){
			return ((ITenderDetail)tdb).getShipmentExpense();
		} else if (entityName.equals("TekirInvoice")){
			return ((ITenderDetail)tdb).getInvoiceExpense();
		}
		return null;
	}

	private BigDecimal sumQuantity() {
		Double result = 0.0;
		for (TenderDetailBase tdb : getTender().getProductItems()) {
			result = result + tdb.getQuantity().getValue();
		}
		return BigDecimal.valueOf(result);
	}
	
	/**
	 * Calculates sum of before taxes.
	 * @return result
	 */
	private BigDecimal sumBeforeTaxes() {
		BigDecimal result = BigDecimal.ZERO;
		for (TenderDetailBase tdb : getTender().getProductItems()) {
			result = result.add(tdb.getBeforeTax().getLocalAmount());
		}
		return result;
	}
	
	//FIXME: satır indirim ve masraflar için düzenlemeler eksik.
	/**
	 * Sums up all taxes, fees, expenses, discounts and amounts. 
	 * @param tender
	 */
	private void calculateDocumentTotals(ITender tender) {
		log.info("Calculating Document Totals...\n");
		BigDecimal totalTaxExcludedAmount = BigDecimal.ZERO;
		BigDecimal beforeTaxTotal = BigDecimal.ZERO;
		BigDecimal taxTotal = BigDecimal.ZERO;
		BigDecimal taxExcludedTotal = BigDecimal.ZERO;
		BigDecimal feeTotal = BigDecimal.ZERO;
		BigDecimal expenseTotal = BigDecimal.ZERO;
		BigDecimal discountTotal = BigDecimal.ZERO;
		BigDecimal documentDiscountTotal = BigDecimal.ZERO;
		BigDecimal documentExpenseTotal = BigDecimal.ZERO;
		BigDecimal grandTotal = BigDecimal.ZERO;
		BigDecimal totalAmount = BigDecimal.ZERO;
		BigDecimal discountAdditionTotal = BigDecimal.ZERO;
		BigDecimal expenseAdditionTotal = BigDecimal.ZERO;

		
		for(TenderDetailBase td : tender.getItems()) {
			setLocalAmountOf(td.getTaxExcludedAmount());

			if ( td.isTypeOf(ProductType.Product, ProductType.Service) ) {
				discountTotal = discountTotal.add(td.getDiscount().getValue());
				
				expenseTotal =  expenseTotal.add(td.getExpense().getValue());

				totalTaxExcludedAmount = totalTaxExcludedAmount.add(td.getTaxExcludedAmount().getValue());

				taxTotal = taxTotal.add(td.getTaxTotalAmount().getValue());

				beforeTaxTotal = beforeTaxTotal.add(td.getBeforeTax().getValue());

				taxExcludedTotal = taxExcludedTotal.add(td.getTaxExcludedTotal().getValue());

				td.amountCalculation();
				setLocalAmountOf( td.getAmount() );
			}
			
			if (td.getFee() != null) {
				if (td.getProductType().equals(ProductType.DocumentFee)) {
					feeTotal = feeTotal.add(td.getFee().getValue());
				} else {
					feeTotal = feeTotal.add(td.getAmount().getValue());
				}
			}

			if (getTenderItemDiscount(td) != null) {
				documentDiscountTotal = documentDiscountTotal.add(getTenderItemDiscount(td).getValue());
			}

			if (getTenderItemExpense(td) != null) {
				documentExpenseTotal = documentExpenseTotal.add(getTenderItemExpense(td).getValue());
			}

			//katkı indirimlerinin genel toplama ve döküman toplamına etkisi.
			if (td.getProductType().equals(ProductType.DiscountAddition)) {
				discountAdditionTotal = discountAdditionTotal.add(td.getDiscount().getValue());
				
				grandTotal = grandTotal.subtract(td.getDiscount().getValue());
			}

			//katkı masraflarının genel toplama ve döküman toplamına etkisi.
			if (td.getProductType().equals(ProductType.ExpenseAddition)) {
				expenseAdditionTotal = expenseAdditionTotal.add(td.getExpense().getValue());
				
				grandTotal = grandTotal.add(td.getExpense().getValue());
			}

			totalAmount = totalAmount.add(td.getTotalAmount().getValue());

			grandTotal = grandTotal.add(td.getGrandTotal().getValue());
		}
		TenderBase tb = (TenderBase) tender;
		
		tb.getTotalTaxExcludedAmount().setValue(totalTaxExcludedAmount);
//		setCurrencyValue(tb.getTotalTaxExcludedAmount());
		setLocalAmountOf(tb.getTotalTaxExcludedAmount());
		
		tb.getTotalBeforeTax().setValue(beforeTaxTotal);
//		setCurrencyValue(tb.getTotalBeforeTax());
		setLocalAmountOf(tb.getTotalBeforeTax());
		
		tb.getTaxExcludedTotal().setValue(taxExcludedTotal);
//		setCurrencyValue(tb.getTaxExcludedTotal());
		setLocalAmountOf(tb.getTaxExcludedTotal());
		
		tb.getTotalExpense().setValue(expenseTotal);
//		setCurrencyValue(tb.getTotalExpense());
		setLocalAmountOf(tb.getTotalExpense());

		tb.getTotalDiscount().setValue(discountTotal);
//		setCurrencyValue(tb.getTotalDiscount());
		setLocalAmountOf(tb.getTotalDiscount());

		tb.getTotalDocumentDiscount().setValue(documentDiscountTotal);
//		setCurrencyValue(tb.getTotalDocumentDiscount());
		setLocalAmountOf(tb.getTotalDocumentDiscount());

		tb.getTotalDocumentExpense().setValue(documentExpenseTotal);
//		setCurrencyValue(tb.getTotalDocumentExpense());
		setLocalAmountOf(tb.getTotalDocumentExpense());

		tb.getTotalDiscountAddition().setValue(discountAdditionTotal);
//		setCurrencyValue(tb.getTotalDiscountAddition());
		setLocalAmountOf(tb.getTotalDiscountAddition());
		
		tb.getTotalExpenseAddition().setValue(expenseAdditionTotal);
//		setCurrencyValue(tb.getTotalExpenseAddition());
		setLocalAmountOf(tb.getTotalExpenseAddition());

		tb.getTotalFee().setValue(feeTotal);
		setLocalAmountOf(tb.getTotalFee());

		tb.getTotalTax().setValue(taxTotal);
		setLocalAmountOf(tb.getTotalTax());
		
		tb.getTotalAmount().setValue(totalAmount);
		setLocalAmountOf(tb.getTotalAmount());

		tb.getGrandTotal().setValue(grandTotal);
		setLocalAmountOf(tb.getGrandTotal());

		log.info("\tTax Excluded Total:#0\n", tb.getTaxExcludedTotal().getLocalAmount() );
		log.info("\tTotal Tax:#0\n", 		  tb.getTotalTax().getLocalAmount() );
		log.info("\tTotal Expense:#0\n", 	  tb.getTotalExpense().getLocalAmount() );

		log.info("\tTotal Discount:#0\n", tb.getTotalDiscount().getLocalAmount() );
		log.info("\tTotal Fee:#0\n", 	  tb.getTotalFee().getLocalAmount() );
		log.info("\tTotal Amount:#0\n",   tb.getTotalAmount().getLocalAmount() );

		log.info("\tGrand Total:#0\n", 		 tb.getGrandTotal().getLocalAmount() );
		log.info("\tDiscount Addition:#0\n", tb.getTotalDiscountAddition().getLocalAmount() );
		log.info("\tExpense Addition:#0\n",  tb.getTotalExpenseAddition().getLocalAmount() );

		log.info("\tTotal Document Expense:#0\n",  tb.getTotalDocumentExpense().getLocalAmount() );
		log.info("\tTotal Document Discount:#0\n", tb.getTotalDocumentDiscount().getLocalAmount() );
	}

	public void calculateUnitPrice(int rowKey) {
		TenderDetailBase item = getTender().getItems().get(rowKey);

		fillCurrencyRateForItem(item);
		setLocalAmountOf(item.getForeignUnitPrice());
		
		MoneySet teUnitPrice = new MoneySet();
		teUnitPrice.setLocalAmount(item.getForeignUnitPrice().getLocalAmount());
		teUnitPrice.setCurrency(getTender().getDocCurrency());
		setCurrencyValue(teUnitPrice);
		
		item.setTaxExcludedUnitPrice( teUnitPrice );
		log.debug("calculated unit price value: #0, localAmount: #1", item.getTaxExcludedUnitPrice().getValue(), item.getTaxExcludedUnitPrice().getLocalAmount());
	}

	public void setTaxExcludedAmount(int rowKey) {
		//FIXME:daha anlamlı bi isimlendirme olmalı.
		TenderDetailBase item = getTender().getItems().get(rowKey);

		item.getTaxExcludedAmount().setCurrency(getTender().getDocCurrency());
		item.getTaxExcludedAmount().setValue(item.getTaxExcludedUnitPrice().getValue().multiply(item.getQuantity().asBigDecimal()));
		setLocalAmountOf(item.getTaxExcludedAmount());
	}
	
	public void calculateForeignUnitPrice(int rowKey) {
		TenderDetailBase item = getTender().getItems().get(rowKey);

		fillCurrencyRateForItem(item);

		setLocalAmountOf(item.getTaxExcludedUnitPrice());

		MoneySet fup = new MoneySet(item.getForeignUnitPrice().getCurrency());
		fup.setLocalAmount(item.getTaxExcludedUnitPrice().getLocalAmount());
		fup.setValue( convertToCurrency(fup.getLocalAmount(), BaseConsts.SYSTEM_CURRENCY_CODE, fup.getCurrency()));
		
		item.setForeignUnitPrice(fup);
		log.info("calculated foreign unit price value: #0, localAmount: #1", item.getForeignUnitPrice().getValue(), item.getForeignUnitPrice().getLocalAmount());
	}

    public void manualFlush() {
        Conversation.instance().changeFlushMode(org.jboss.seam.annotations.FlushModeType.MANUAL);
    }

    public ITender getTender() {
		return (ITender)entity;
	}

	public List<String> getErrorList() {
		return errorList;
	}

}
