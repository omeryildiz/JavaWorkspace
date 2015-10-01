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

import java.io.ByteArrayOutputStream;
import java.io.FileWriter;
import java.io.IOException;
import java.io.OutputStream;
import java.math.BigDecimal;
import java.math.RoundingMode;
import java.text.NumberFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.Properties;
import java.util.ResourceBundle;

import javax.ejb.Stateless;
import javax.faces.context.FacesContext;
import javax.servlet.http.HttpServletResponse;

import org.jboss.seam.Component;
import org.jboss.seam.ScopeType;
import org.jboss.seam.annotations.AutoCreate;
import org.jboss.seam.annotations.In;
import org.jboss.seam.annotations.Logger;
import org.jboss.seam.annotations.Name;
import org.jboss.seam.annotations.Scope;
import org.jboss.seam.core.SeamResourceBundle;
import org.jboss.seam.faces.FacesMessages;
import org.jboss.seam.log.Log;

import com.ut.tekir.entities.Address;
import com.ut.tekir.entities.Contact;
import com.ut.tekir.entities.ContactAddress;
import com.ut.tekir.entities.MoneySet;
import com.ut.tekir.entities.Option;
import com.ut.tekir.entities.PaymentTable;
import com.ut.tekir.entities.PaymentType;
import com.ut.tekir.entities.Product;
import com.ut.tekir.entities.ProductType;
import com.ut.tekir.entities.Quantity;
import com.ut.tekir.entities.TaxEmbeddable;
import com.ut.tekir.entities.TaxKind;
import com.ut.tekir.entities.TenderDetailBase;
import com.ut.tekir.entities.TenderTaxSummaryBase;
import com.ut.tekir.entities.User;
import com.ut.tekir.entities.inv.TekirInvoice;
import com.ut.tekir.entities.inv.TekirInvoiceDetail;
import com.ut.tekir.entities.inv.TekirInvoiceTaxSummary;
import com.ut.tekir.framework.BaseConsts;
import com.ut.tekir.framework.OptionManager;
import com.ut.tekir.util.NumberToText;
import com.ut.tekir.util.NumberToTextTR;


/**
 * @author sinan.yumak
 *
 */
@Stateless
@Name("posPrinterHome")
@Scope(ScopeType.EVENT)
@AutoCreate
public class PosPrinterHomeBean implements PosPrinterHome {

	@Logger
	Log log;
	@In
	FacesContext facesContext;
	@In
	FacesMessages facesMessages;
	@In
	OptionManager optionManager;

	private static final long serialVersionUID = 1L;
	private static final String SPACE = " ";

	private static final char LINE_FEED = 0x0A;
	private static final char DOUBLE_SIZE = 0x0E;
	private static final char NORMAL_SIZE = 0x14;
	private static final char[] LEFT_JUSTIFICATION = new char[]{0x1B, 0x1D, 0x61, 0x30};
	private static final char[] CENTER_JUSTIFICATION = new char[]{0x1B, 0x1D, 0x61, 0x31};
	private static final char[] RIGHT_JUSTIFICATION = new char[]{0x1B, 0x1D, 0x61, 0x50};
	private static final char[] FIVE_NINE = new char[]{0x1B,0x50};
	private static final char[] SEVEN_NINE = new char[]{0x1B,0x4D};

	@Override
	public String getPosOutput(TekirInvoice aDoc) {
		return preparePosOutput(aDoc);
	}

	@Override
	public void sendToPosPrinter(TekirInvoice aDoc) {
		log.info("Sending to pos printer. Invoice code :{0}",aDoc.getCode());
        HttpServletResponse response = (HttpServletResponse) facesContext.getExternalContext().getResponse();

        try {
        	Properties properties = preparePrintPropertiesFile(aDoc);

        	ByteArrayOutputStream baos = new ByteArrayOutputStream();
        	properties.store(baos, "Pos Printer setting properties...");
        	byte[] fileContent = baos.toByteArray();


        	response.setContentType("application/prn");
        	response.setContentLength(fileContent.length);
        	response.setHeader("Content-disposition","attachment;filename=print.prn");
            OutputStream out = response.getOutputStream();

            out.write(fileContent);

            out.flush();
            out.close();

            facesContext.responseComplete();
        } catch (Exception e) {
        	facesMessages.add("Yazdırma dosyası hazırlanırken hata meydana geldi. Sebebi:\n{0}", e.getMessage());
        	log.error("Pos yaziciya gönderirken hata oluştu! Sebebi şudur: #0",e);
		}
		
        try {
			FileWriter fw = new FileWriter("/home/uygun/Desktop/sonuc.txt");
			fw.append(preparePosOutput(aDoc));
			fw.flush();
			fw.close();
		} catch (Exception e) {
			log.error("hata = " + e.getMessage());
		}

	}

    private Properties preparePrintPropertiesFile(TekirInvoice aDoc) throws IOException, Exception {
    	Properties props = new Properties();

    	String printerPath = getPrinterPath();
    	
    	props.setProperty("printerPath", printerPath);
    	
    	String posOutput = preparePosOutput(aDoc);

    	props.setProperty("posOutput", posOutput);

    	return props;
    }

	private String preparePosOutput(TekirInvoice invoice) {
		StringBuilder sb = new StringBuilder();
		
		sb.append(NORMAL_SIZE);
		
		prepareHeader(sb, invoice);
		prepareBody(sb, invoice);
		prepareFooter(sb, invoice);

		sb.append(LINE_FEED);

		return sb.toString();
	}

	private void prepareFooter(StringBuilder sb, TekirInvoice invoice) {
		sb.append(LEFT_JUSTIFICATION);

		String format = "   %1$-20s %2$15s";

		sb.append(LINE_FEED);

		
        NumberFormat f = NumberFormat.getInstance(new Locale("tr","TR"));
        f.setMaximumFractionDigits(2);
        f.setMinimumFractionDigits(2);

        
        if (invoice.getTotalDiscount().getLocalAmount().compareTo(BigDecimal.ZERO) != 0 || 
        		invoice.getTotalDocumentDiscount().getLocalAmount().compareTo(BigDecimal.ZERO) != 0) {

    		sb.append(String.format(format, "ARA TOPLAM","*" + f.format(invoice.getTotalTaxExcludedAmount().getLocalAmount())));
    		sb.append(LINE_FEED);
        }

//		log.info("ARA TOPLAM", sb.toString());

/*        
// satir indirimi
        if (invoice.getTotalDiscount().getLocalAmount().compareTo(BigDecimal.ZERO) != 0) {
            sb.append(String.format(format, "INDIRIM","*" + f.format(invoice.getTotalDiscount().getLocalAmount())));
            sb.append(LINE_FEED);
        }

//		log.info("1", sb.toString());
// belge indirimi
        if (invoice.getTotalDocumentDiscount().getLocalAmount().compareTo(BigDecimal.ZERO) != 0) {
            sb.append(String.format(format, "INDIRIM","*" + f.format(invoice.getTotalDocumentDiscount().getLocalAmount())));
            sb.append(LINE_FEED);
        }
        
//		log.info("2", sb.toString());
*/

// 		toplam indirim satir + belge
        if (invoice.getTotalDiscount().getLocalAmount().compareTo(BigDecimal.ZERO) != 0 ||
        	invoice.getTotalDocumentDiscount().getLocalAmount().compareTo(BigDecimal.ZERO) != 0 ) {
            sb.append(String.format(format, " INDIRIM","*" + f.format(invoice.getTotalDiscount().getLocalAmount().add(invoice.getTotalDocumentDiscount().getLocalAmount()))));           
            sb.append(LINE_FEED);
        }
        
        sb.append(String.format(format, "ARA TOPLAM","*" + f.format(invoice.getTaxExcludedTotal().getLocalAmount())));
		sb.append(LINE_FEED);
        
//		log.info("ARA 2", sb.toString());
		
		for (TekirInvoiceTaxSummary ts :invoice.getTaxSummaryList()) {

			sb.append(String.format(format, " " +taxString(ts) + " (" + f.format(ts.getBase().getLocalAmount()) +")" , "*" + f.format(ts.getAmount().getValue())));
			sb.append(LINE_FEED);

//            log.info("taxlar", sb.toString());
			
		}

		sb.append("   ------------------------------------")
		  .append(LINE_FEED);
		
		if (invoice.getTotalDiscountAddition().getLocalAmount().compareTo(BigDecimal.ZERO) != 0 ||
				invoice.getTotalExpenseAddition().getLocalAmount().compareTo(BigDecimal.ZERO) != 0 ) {
			
			sb.append(String.format(format, "ARA TOPLAM","*" + f.format(invoice.getTotalAmount().getLocalAmount())));
			sb.append(LINE_FEED);
		}

		
		format = "   %1$-23s %2$12s";

		Map<Product, BigDecimal> additions = getAdditionsAsMap(invoice.getDiscountAdditionItems());

		String productName = "";
		BigDecimal additionAmount = BigDecimal.ZERO;
		for (Product prod : additions.keySet()) {
			productName = prod.getName();
			additionAmount = additions.get(prod);
			additionAmount = additionAmount.setScale(2, RoundingMode.HALF_UP);
			
			if (productName.length() > 22) {
				productName = productName.substring(0, 22);
			}
			productName = replaceUnicodeCharacters(productName);
			
			sb.append(String.format(format, " " + productName,"*" + f.format(additionAmount)));
			sb.append(LINE_FEED);
		}

		additions = getAdditionsAsMap(invoice.getExpenseAdditionItems());
		for (Product prod : additions.keySet()) {
			productName = prod.getName();
            additionAmount = additions.get(prod);
			additionAmount = additionAmount.setScale(2, RoundingMode.HALF_UP);

			if (productName.length() > 22) {
				productName = productName.substring(0, 22);
			}
			productName = replaceUnicodeCharacters(productName);
			
			sb.append(String.format(format, " " + productName,"*" + f.format(additionAmount)));
			sb.append(LINE_FEED);
		}
		
		if (invoice.getTotalDiscountAddition().getLocalAmount().compareTo(BigDecimal.ZERO) != 0 ||
				invoice.getTotalExpenseAddition().getLocalAmount().compareTo(BigDecimal.ZERO) != 0 ) {
		
				sb.append("   ------------------------------------")
					.append(LINE_FEED);
		}
		
		sb.append(String.format(format, "TOPLAM","*" + f.format(invoice.getGrandTotal().getLocalAmount())));
		sb.append(LINE_FEED);

		
		NumberToText ntt = new NumberToTextTR();
		String totalAsText = ntt.convert(invoice.getGrandTotal().getLocalAmount().doubleValue(), 
										 BaseConsts.SYSTEM_CURRENCY_CODE, 
										 BaseConsts.SYSTEM_CURRENCYDEC_CODE);

		totalAsText = replaceUnicodeCharacters(totalAsText);

//        log.info("toplam Stringi", sb.toString());


		sb.append(LINE_FEED);
		sb.append("Yalniz ")
		  .append(totalAsText);
		
		
		sb.append(LINE_FEED);
	
		format = "* %1$-27s %2$12s";
		
		PaymentTable pt =  invoice.getPaymentTable();
		
		if (pt != null) {
		
			for (PaymentTableGroupModel model : pt.getAsGroupped().keySet()) {

				if (model.getType().equals(PaymentType.Cash)) {
					sb.append(String.format(format, "Nakit:", "*" + f.format(pt.getAsGroupped().get(model))));
				} else {
					sb.append(String.format(format, "Kart: " + maskCreditCardNumber(model.getCreditCardNumber()), 
													"*" + f.format(pt.getAsGroupped().get(model))));

					if (model.getBank() != null) {
						sb.append(LINE_FEED);
						String bankName = replaceUnicodeCharacters(model.getBank().getName());
						sb.append(bankName);
					}
				}
				sb.append(LINE_FEED);
				
			}
			
		}
		
		if (invoice.getInfo1() != null && invoice.getInfo1().length() >0 ) {
			sb.append(LINE_FEED);
			sb.append(invoice.getInfo1());
		}

		if (invoice.getInfo2() != null && invoice.getInfo2().length() >0 ) {
			sb.append(LINE_FEED);
			sb.append(invoice.getInfo2());
		}
		
		User clerk = invoice.getClerk();
        if (clerk != null) {
            String clerkName = replaceUnicodeCharacters(clerk.getFullName());

            sb.append(LINE_FEED);
            sb.append("Satıcı : ")
              .append(clerkName);
              
        }

        User activeUser = (User)Component.getInstance("activeUser");
        if (activeUser != null) {
        	String activeUserName = replaceUnicodeCharacters(activeUser.getFullName());

        	sb.append(LINE_FEED);
            sb.append("Kasiyer: ")
              .append(activeUserName);
        	
        }
        
	}

    private Map<Product, BigDecimal> getAdditionsAsMap(List<? extends TenderDetailBase> anAdditionList) {
		Map<Product, BigDecimal> result = new HashMap<Product, BigDecimal>();
		Product product = null;
		for (TenderDetailBase item : anAdditionList) {
			product = item.getProduct();
			if (product != null) {

				if (!result.containsKey(product)) {
                    if (item.getProductType().equals(ProductType.DiscountAddition)) {
						result.put(product, item.getDiscount().getLocalAmount());
					} else {
						result.put(product, item.getExpense().getLocalAmount());
                    }
				} else {
					BigDecimal foundAmount = result.get(product);
					if (item.getProductType().equals(ProductType.DiscountAddition)) {
						foundAmount = foundAmount.add(item.getDiscount().getLocalAmount());
					} else {
						foundAmount = foundAmount.add(item.getExpense().getLocalAmount());
					}
					result.put(product, foundAmount);
				}
			}
		}
		return result;
	}

	private String taxString(TenderTaxSummaryBase aSummaryBase) {
		ResourceBundle rb = SeamResourceBundle.getBundle();
		
		StringBuffer result = new StringBuffer();
		
		result.append(rb.getString("general.label." + aSummaryBase.getType().name()));
		
		if (aSummaryBase.getKind().equals(TaxKind.Amount)) {
			result.append(aSummaryBase.getSourceAmount());
		} else {
			result.append(" %")
				  .append(aSummaryBase.getSourceRate());
		}
		return result.toString();
	}
	
	private String maskCreditCardNumber(String aCardNumber) {
		StringBuffer sb = new StringBuffer(aCardNumber);
		
		if (aCardNumber.length() == 16) {
			for (int i=4;i<aCardNumber.length()-4;i++) {
				sb.setCharAt(i, '*');
			}
		}
		return sb.toString();
	}
	
	private void prepareBody(StringBuilder sb, TekirInvoice invoice) {
		StringBuilder productNameAndCode = new StringBuilder();

		String barcode = null;
		TaxEmbeddable te = null;
		MoneySet beforeTax = null;
		MoneySet taxExcludedTotal = null;
		MoneySet amount = null;
		MoneySet taxExcludedUnitPrice = null;
		Quantity quantity = null;

		sb.append(LEFT_JUSTIFICATION);

		for (TekirInvoiceDetail item : invoice.getProductItems()) {
			productNameAndCode = new StringBuilder();
			productNameAndCode.append( item.getProduct().getCode() )
							  .append( "-" )
							  .append( item.getProduct().getName() );

			te = item.getTax1();
			beforeTax = item.getBeforeTax();
			taxExcludedTotal = item.getTaxExcludedAmount();
			amount = item.getAmount();
			taxExcludedUnitPrice = item.getTaxExcludedUnitPrice();
			quantity = item.getQuantity();
			barcode = item.getProduct().getBarcode1();


			if (productNameAndCode.length() > 32 ) {
				productNameAndCode.setLength(32);
			}
			//ProductCode-ProductName
			sb.append(LEFT_JUSTIFICATION)
			  .append( replaceUnicodeCharacters(productNameAndCode.toString()) )
			  .append(LINE_FEED);
            

			NumberFormat f = NumberFormat.getInstance(new Locale("tr","TR"));
            f.setMaximumFractionDigits(2);
            f.setMinimumFractionDigits(2);
            
			//(1 Ad x 510)  %18  *510
            StringBuilder quantityWithUnitPrice = new StringBuilder();
            
            quantityWithUnitPrice.append("(")
								 .append(quantity.toStringInNarrowFormat())
								 .append(" x ")
								 .append(f.format(taxExcludedUnitPrice.getValue()))
								 .append(")");
			
			
            String format = "%1$-25s %2$-3s %3$12s";
			
			String rate = "";
			if (te.getRate() != null) {
				rate = "%" + te.getRate().intValue();
			}
			if (rate.length() == 5) {
				rate = rate + " ";
			}
			sb.append(String.format(format, quantityWithUnitPrice, rate, "*" + f.format(taxExcludedTotal.getLocalAmount())));
			sb.append(LINE_FEED);
		}
	}

	private void prepareHeader(StringBuilder sb,TekirInvoice invoice) {
		sb.append(LINE_FEED);
		sb.append(LINE_FEED);
		sb.append(LINE_FEED);
		sb.append(LINE_FEED);
		sb.append(LINE_FEED);

//		sb.append("==========================================").append(LINE_FEED);

        Contact invoiceContact = invoice.getContact();

        String contactName = "";

		if (invoiceContact.getPerson()) {
            contactName = invoice.getContact().getName();
            if (invoice.getContact().getMidname() != null ) {
                contactName = contactName + ' ' + invoice.getContact().getMidname();
            }
            contactName = contactName + ' ' + invoice.getContact().getSurname();
            contactName = replaceUnicodeCharacters(contactName);
        } else {
            contactName = invoice.getContact().getCompany();
            contactName = replaceUnicodeCharacters(contactName);
            }

		sb.append(" " + contactName).append(LINE_FEED);

		if (invoiceContact != null) {

			ContactAddress ca = invoiceContact.getDefaultAddress();
			if (ca != null) {

				Address address = ca.getAddress();
				if (address != null) {
					String street = address.getStreet();
					street = replaceUnicodeCharacters(street);
					sb.append(" " + street).append(LINE_FEED);


					String zip = address.getZip();
					sb.append(" " + zip).append(LINE_FEED);
				}

				String provinceAndCityString = ca.getProvince().getName() + "/" + ca.getCity().getName();
				provinceAndCityString = replaceUnicodeCharacters(provinceAndCityString);

				sb.append(" " + provinceAndCityString).append(LINE_FEED);
			}

            String taxNumber ="";
            String taxOffice ="";

            if (invoiceContact.getPerson()) {
                if (invoiceContact.getSsn() != null) {
                    taxNumber = replaceUnicodeCharacters(invoiceContact.getSsn());
                }
			    sb.append(" TC.NO:").append(taxNumber).append(LINE_FEED);
            } else {
                if (invoiceContact.getTaxNumber() != null) {
                    taxNumber = replaceUnicodeCharacters(invoiceContact.getTaxNumber());
                }
                if (invoiceContact.getTaxOffice() != null) {
                    taxOffice = replaceUnicodeCharacters(invoiceContact.getTaxOffice());
                }
                sb.append(" V.D.:").append(taxOffice).append(LINE_FEED);
			    sb.append(" V.NO:").append(taxNumber);
            }

		}

//		sb.append("==========================================").append(LINE_FEED);
		sb.append(LINE_FEED);

		String format = "%1$-3s    %2$-3s     %3$-3s";
		SimpleDateFormat dateFormatter = new SimpleDateFormat ("dd/MM/yyyy");
		SimpleDateFormat timeFormatter = new SimpleDateFormat ("HH:mm:ss");

		if (invoice.getDate() != null) {
			sb.append(String.format(format, dateFormatter.format(invoice.getShippingDate()),
                                            timeFormatter.format(invoice.getTime()),
                                            dateFormatter.format(invoice.getDate())))
			  .append(LINE_FEED);
		}
        sb.append(LINE_FEED);
	}

	private List<String> splitSentenceToTokens(String aSentence,int tokenSize) {
		int sentenceLength = aSentence.length();

		List<String> tokenList = new ArrayList<String>();

		String token = null;
		int pos;
		for (pos=0;(pos<sentenceLength) && (pos + tokenSize <sentenceLength) ;pos = pos + tokenSize) {
			token = aSentence.substring(pos, pos + tokenSize);
			tokenList.add(token);
		}
		if (pos<sentenceLength) {
			token = aSentence.substring(pos,sentenceLength);
			tokenList.add(token);
		}
		return tokenList;
	}

	private String createSpace(int i) {
		StringBuilder sb = new StringBuilder();
		for (int j=0;j<i;j++) {
			sb.append(SPACE);
		}
		return sb.toString();
	}

	private String getCompanyName() {
		String companyName = optionManager.getOption("company.Name").getAsString();
		return replaceUnicodeCharacters(companyName);
	}

	private String getCompanyTitle() {
		String companyTitle = optionManager.getOption("company.Title").getAsString();
		return replaceUnicodeCharacters(companyTitle);
	}

	private String getPrinterPath() throws Exception {
		Option printerOption = optionManager.getOption("systemSettings.control.DefaultPosPrinter");
		
		String printerPath = printerOption.getValue();
		
		if (printerPath != null && printerPath.length() > 0) {
			return printerPath;
		} else {
			throw new Exception("Ön tanımlı yazıcı bulunamadı. Lütfen sistem ayarları" +
					"sayfasından yazıcınızı tanımlayınız.");
		}
	}

	/**
	 * Verilen stringdeki unicode karakterleri değiştirir.
	 * @param aString
	 */
	public String replaceUnicodeCharacters(String aString) {
		aString = aString.replace("ğ", "g");
		aString = aString.replace("Ğ", "G");
		aString = aString.replace("ü", "u");
		aString = aString.replace("Ü", "U");
		aString = aString.replace("İ", "I");
		aString = aString.replace("ı", "i");
		aString = aString.replace("Ç", "C");
		aString = aString.replace("ç", "c");
		aString = aString.replace("ö", "o");
		aString = aString.replace("Ö", "O");
		aString = aString.replace("Ş", "S");
		aString = aString.replace("ş", "s");
		return aString;
	}

}
