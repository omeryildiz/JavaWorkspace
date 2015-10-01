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

package com.ut.tekir.external;

import java.math.BigDecimal;
import java.net.URL;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;

import org.jboss.seam.ScopeType;
import org.jboss.seam.annotations.AutoCreate;
import org.jboss.seam.annotations.Name;
import org.jboss.seam.annotations.Scope;
import org.w3c.dom.CharacterData;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;

import com.ut.tekir.entities.CurrencyPair;
import com.ut.tekir.entities.CurrencyRate;
/**
 * TCMB xml file reader
 *
 * @author selman
 */
@Name("tcmbRateReader")
@Scope(value=ScopeType.EVENT)
@AutoCreate
public class TcmbRateReader {
	
	public ArrayList<CurrencyRate> getCurrencies(List<CurrencyPair> activeCPairs) {
		
        ArrayList<CurrencyRate> items = new ArrayList<CurrencyRate>();
		try {
			//DocumentBuilder nesnesini yaratiyoruz
			DocumentBuilder builder = DocumentBuilderFactory.newInstance()
					.newDocumentBuilder();
			//Xml belgesinin URL'si
			URL u = new URL("http://www.tcmb.gov.tr/kurlar/today.xml");

			//Belgeyi URL'den yukleyip, builder ile parse ediyoruz
			Document doc = builder.parse(u.openStream());

			//Belge icerisindeki Currency etiketli elemanlari aliyoruz
			NodeList nodes = doc.getElementsByTagName("Currency");
			//Aktif döviz çiftleri için,
            for (CurrencyPair cp : activeCPairs) {
                //currency node ları icinde dolasiyoruz
                for (int i = 0; i < nodes.getLength(); i++) {
                   Element element = (Element) nodes.item(i);
                   //bize lazım olan döviz kurlarını almak ve o kadar,
                   //currency rate nesnesi oluşturmak için karşılaştırıyoruz
                    if (cp.getHardCurrency().getCode().equals(element.getAttribute("CurrencyCode"))) {

                        CurrencyRate currencyRate = new CurrencyRate();

                        currencyRate.setCurrencyPair(cp);
                        currencyRate.setBid(new BigDecimal(getElementValue(element, "ForexBuying")));
                        currencyRate.setAsk(new BigDecimal(getElementValue(element, "ForexSelling")));
                        currencyRate.setBankNoteBuying(new BigDecimal(getElementValue(element, "BanknoteBuying")));
                        currencyRate.setBankNoteSelling(new BigDecimal(getElementValue(element, "BanknoteSelling")));
                        currencyRate.setDate(new Date());

                        items.add(currencyRate);
                    }
                }
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
		return items;
	}

	private String getCharacterDataFromElement(Element e) {
		try {
			Node child = e.getFirstChild();
			if (child instanceof CharacterData) {
				CharacterData cd = (CharacterData) child;
				return cd.getData();
			}
		} catch (Exception ex) {
                    ex.printStackTrace();
		}
		return "";
	}

	protected float getFloat(String value) {
		if (value != null && !value.equals("")) {
			return Float.parseFloat(value);
		}
		return 0;		
	}

	protected String getElementValue(Element parent, String label) {
		return getCharacterDataFromElement((Element) parent
				.getElementsByTagName(label).item(0));
	}
	
}
