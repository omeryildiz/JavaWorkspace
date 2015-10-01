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

import java.io.ByteArrayOutputStream;
import java.io.OutputStream;
import java.text.NumberFormat;
import java.util.List;
import java.util.Properties;

import javax.ejb.Stateless;
import javax.faces.context.FacesContext;
import javax.servlet.http.HttpServletResponse;

import org.jboss.seam.ScopeType;
import org.jboss.seam.annotations.AutoCreate;
import org.jboss.seam.annotations.In;
import org.jboss.seam.annotations.Logger;
import org.jboss.seam.annotations.Name;
import org.jboss.seam.annotations.Scope;
import org.jboss.seam.core.Events;
import org.jboss.seam.faces.FacesMessages;
import org.jboss.seam.log.Log;

import com.ut.tekir.entities.BarcodeTxn;
import com.ut.tekir.entities.MoneySet;
import com.ut.tekir.entities.Option;
import com.ut.tekir.entities.PriceItem;
import com.ut.tekir.entities.PriceItemDetail;
import com.ut.tekir.entities.Product;
import com.ut.tekir.framework.OptionManager;
import com.ut.tekir.tender.PriceProvider;


/**
 * @author sinan.yumak
 *
 */
@Stateless
@Name("barcodePrinterHome")
@Scope(ScopeType.EVENT)
@AutoCreate
public class BarcodePrinterHomeBean implements BarcodePrinterHome {

	private static final long serialVersionUID = 1L;
	@Logger
	Log log;
	@In
	FacesContext facesContext;
	@In
	FacesMessages facesMessages;
	@In
	OptionManager optionManager;
	@In
	PriceProvider priceProvider;
    @In
    Events events;

	private static final char CR = 0x0D;
	private static final char STX = 0x02;

	@Override
	public void sendToBarcodePrinter(List<BarcodeTxn> barcodeList) throws Exception {
		log.info("Sending to barcode printer.");
        HttpServletResponse response = (HttpServletResponse) facesContext.getExternalContext().getResponse();

        String printerPath = getPrinterPath();
        
    	Properties props = new Properties();
    	props.setProperty("printerPath", printerPath);

    	String barcodeOutput = prepareBarcodeOutputForOneLabel(barcodeList);
    	props.setProperty("barcodeOutput", barcodeOutput);
    	

    	ByteArrayOutputStream baos = new ByteArrayOutputStream();
    	props.store(baos, "Barcode Printer setting properties...");
    	byte[] fileContent = baos.toByteArray();

    	response.setContentType("application/prn");
    	response.setContentLength(fileContent.length);
    	response.setHeader("Content-disposition","attachment;filename=barcodePrint.brn");
    	OutputStream out = response.getOutputStream();
    	
    	out.write(fileContent);
    	
    	out.flush();
    	out.close();

    	facesContext.responseComplete();

    	
//		try {
//			FileWriter fw = new FileWriter("/home/uygun/Desktop/sonuc.txt");
//			fw.append(barcodeOutput);
//			fw.flush();
//			fw.close();
//		} catch (Exception e) {
//			log.error("hata = " + e.getMessage());
//		}

    	events.raiseTransactionSuccessEvent("refreshBrowser:com.ut.tekir.entities.BarcodeTxn");
	}

	private String prepareBarcodeOutputForOneLabel(List<BarcodeTxn> barcodeList) throws Exception {
		StringBuffer sb = new StringBuffer();
		
		String xCoordinateForName = "0015";
		String yCoordinateForName = "0025";

		String xCoordinateForBarcode = "0020";
		String yCoordinateForBarcode = "0003";

		String xCoordinateForCurrency = "0135";
		String yCoordinateForCurrency = "0010";

		BarcodeTxn txn1= null;
		for (int j=0;j<barcodeList.size();j++) {
			txn1 = barcodeList.get(j);
			
			if (txn1.getActive()) {

				Product product = txn1.getProduct();

				String name = getProductCaptionAsFixed(product);
				
				PriceItemDetail foundItemDetail = priceProvider.findSalePriceItemDetailForProduct(product);

				String price = "";
				String currency = "";
				MoneySet grossPrice = foundItemDetail.getGrossPrice();
				if (grossPrice != null) {
			        NumberFormat f = NumberFormat.getInstance();
			        f.setMaximumFractionDigits(2);
			        f.setMinimumFractionDigits(2);

					price = f.format(grossPrice.getValue());
					
					currency = grossPrice.getCurrency();
				}

				String barcode = "";
				if (product.getBarcode1() != null && product.getBarcode1().length()>0) {
					barcode = product.getBarcode1();
				}
				
				for (int i=0;i<txn1.getUnit();i++) {

					sb.append(STX).append("L").append(CR);
					sb.append("D11").append(CR);

					
					sb.append("1200000" + yCoordinateForName + xCoordinateForName + name + " " + price).append(CR);
					
					String barcodeType = "F";
					if (barcode.length() == 12) {
						barcodeType  = "B";
					}
					sb.append("1" + barcodeType +"22010" + yCoordinateForBarcode + xCoordinateForBarcode + barcode).append(CR);

					sb.append("1200000" + yCoordinateForCurrency + xCoordinateForCurrency + currency).append(CR);

					sb.append("E").append(CR);
					sb.append(STX).append("F");

					sb.append("~");
					
				}
			}
			
		}
		
		return sb.toString();
	}

	private String getProductCaptionAsFixed(Product aProduct) {
		String productName = aProduct.getLabelName();
		
		if ( productName.length() >16 )  {
			productName = productName.substring(0, 16);
		} else {
			productName = productName.concat( createSpace(16 - productName.length()) );
		}
		productName = productName.toUpperCase();
		
		productName = replaceUnicodeCharacters(productName);
		
		return productName;
	}
	
	private String createSpace(int count) {
		StringBuffer sb = new StringBuffer();
		for (int i=0; i<count;i++) {
			sb.append(" ");
		}
		return sb.toString();
	}
	
	private String prepareBarcodeOutput(List<BarcodeTxn> barcodeList) throws Exception {
		StringBuffer sb = new StringBuffer();
		
		sb.append(STX).append("L").append(CR);
		
		sb.append("D11").append(CR);

		String[] xCoordinateForName = new String[]{"0026","0106","0186","0266","0346"};
		String yCoordinateForName = "0010";

		String[] xCoordinateForPrice = new String[]{"0038","0118","0198","0278","0358"};
		String yCoordinateForPrice = "0010";

		String[] xCoordinateForBarcode = new String[]{"0080","0160","0240","0320","0400"};
		String yCoordinateForBarcode = "0020";
		
		int column = 0;
		
//    VOLKAN BEYIN YAZDIGI KOD..

//		int toplamEleman = 0;
//		int elemanSayisi = 0;
//		for (BarcodeTxn aa : barcodeList) {
//			if (aa.getActive()) {
//				toplamEleman = toplamEleman + aa.getUnit();
//				elemanSayisi++;
//			}
//		}

//		int l=1;
//		int u=1;
//		int e=1;
//		int urunsay = 0;
//		BarcodeTxn txn = null;
//		for (int s=1;s<=elemanSayisi;) {
//			for (;l<=toplamEleman;) {
//			
//				txn = barcodeList.get(s-1);
//				urunsay = txn.getUnit();
//				
//				for (;u<=urunsay;) {
//					
//					for (;e<=5;) {
//						
//						if (u>urunsay) {
//							break;
//						}
//						Product product = txn.getProduct();
//						String name = product.getCaption();
//
//						
//						PriceItemDetail foundItemDetail = getDefaultPriceItemForProduct(product);
//						
//						String price = "";
//						MoneySet grossPrice = foundItemDetail.getGrossPrice();
//						if (grossPrice != null) {
//							price = grossPrice.toString();
//						}
//
//						String barcode = "";
//						if (product.getBarcode1() != null && product.getBarcode1().length()>0) {
//							barcode = product.getBarcode1();
//						}
//
//						sb.append("4200000" + yCoordinateForName + xCoordinateForName[e-1] + name).append(CR);
//						
//						sb.append("4200000" + yCoordinateForPrice + xCoordinateForPrice[e-1] + price).append(CR);
//	
//						sb.append("4F22030" + yCoordinateForBarcode + xCoordinateForBarcode[e-1] + barcode).append(CR);
//
//						
//						
////						System.out.println("etiket  e=" + e + "l= "+ l + "u = "+ u);
//						
//						e++;
//						u++;
//						l++;
//						if (e>5) {
//							sb.append("E").append(CR);
//							sb.append(STX).append("F");
//
//							sb.append(STX).append("L").append(CR);
//							sb.append("D11").append(CR);
////							sb.append("E").append(CR);
////							System.out.println("E döngü sonu");
//							e = 1;
//						}
//					}
//					
//				}
//				s++;
//				u=1;
//			}
//			
//		}
//		sb.append("E").append(CR);
			
		
		int j = 0;

		for (BarcodeTxn txn1 : barcodeList) {
			if (txn1.getActive()) {
				Product product = txn1.getProduct();
				String name = product.getCaption();

				PriceItemDetail foundItemDetail = getDefaultPriceItemForProduct(product);

				String price = "";
				MoneySet grossPrice = foundItemDetail.getGrossPrice();
				if (grossPrice != null) {
					price = grossPrice.toString();
				}

				String barcode = "";
				if (product.getBarcode1() != null && product.getBarcode1().length()>0) {
					barcode = product.getBarcode1();
				}
				
				for (int i=0;i<txn1.getUnit();i++) {
					column = j % 5;

					if (j != 0 && column == 0 ) {
						sb.append("E").append(CR);
						sb.append(STX).append("F");

						sb.append(STX).append("L").append(CR);
						sb.append("D11").append(CR);
					}
					
					sb.append("4200000" + yCoordinateForName + xCoordinateForName[column] + name).append(CR);

					sb.append("4200000" + yCoordinateForPrice + xCoordinateForPrice[column] + price).append(CR);

					sb.append("4F22030" + yCoordinateForBarcode + xCoordinateForBarcode[column] + barcode).append(CR);


					j= j + 1;
				}
				
			}

		}
		sb.append("E").append(CR);

		return sb.toString();
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

	private String getPrinterPath() throws Exception {
		Option printerOption = optionManager.getOption("systemSettings.control.DefaultBarcodePrinter");
		
		String printerPath = printerOption.getValue();
		
		if (printerPath != null && printerPath.length() > 0) {
			return printerPath;
		} else {
			throw new Exception("Ön tanımlı yazıcı bulunamadı. Lütfen sistem ayarları" +
					"sayfasından yazıcınızı tanımlayınız.");
		}
	}

	private PriceItemDetail getDefaultPriceItemForProduct(Product aProduct) throws Exception {
		log.info("Getting default price item for product, product caption :{0}", aProduct.getCaption());
		PriceItemDetail foundItemDetail = null;
		PriceItem defaultItem = priceProvider.findDefaultPriceItem();
		if (defaultItem == null) {
			throw new Exception("Ön tanımlı fiyat listesini bulunamadı.");
		}
		
		foundItemDetail = defaultItem.findItemWithProduct(aProduct);
		
		if (foundItemDetail == null) {
			throw new Exception("'" + defaultItem.getCode() + 
		            "' Fiyat listesi içerisinde " +
		            "'"+ aProduct.getName() + 
		            "' ürünü ile ilgili fiyat bulunamadı!");
		}
		return foundItemDetail;
	}
	
}
