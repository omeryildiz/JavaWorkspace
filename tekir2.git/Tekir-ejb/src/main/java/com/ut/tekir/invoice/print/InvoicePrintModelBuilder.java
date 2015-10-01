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

package com.ut.tekir.invoice.print;

import com.ut.tekir.entities.Invoice;
import com.ut.tekir.invoice.InvoiceItemModel;

import java.util.ArrayList;
import java.util.List;

/**
 *
 * @author haky
 */
public class InvoicePrintModelBuilder {

    private InvoicePrintMasterModel master;
    
    private List<InvoicePrintLineModel> lines;
    
    public void begin( Invoice invoice ){
        master = new InvoicePrintMasterModel(invoice);
        lines = new ArrayList<InvoicePrintLineModel>();
    }

    public void addLine( InvoiceItemModel line ){
        
        InvoicePrintLineModel lm = new InvoicePrintLineModel( master, line );
        lines.add(lm);
    }
    
    public List<InvoicePrintLineModel> getModel(){
        return lines;
    }
    
    /*
    public static Collection createTestData(){
    	
    	PrintModelBuilder builder = new PrintModelBuilder();
    	
    	
    	
    	Contact contact = new Contact();
    	
    	contact.setCode("C0001");
    	contact.setName("Ahmet Mehmet");
    	contact.setCompany("Uygun Teknoloji");
    	
    	Address adr = new Address();
    	adr.setCity("İstanbul");
    	adr.setProvince("Beşiktaş");
    	adr.setZip("34310");
    	adr.setStreet("Merkez Mh. 3. sk no: 14");
    	contact.setAddress1(adr);
    	
    	contact.setTaxOffice("Beşiktaş");
    	contact.setTaxNumber("12345678901");
    	
    	Invoice inv = new Invoice();
    	inv.setContact(contact);
    	inv.setBeforeTax(new Money( new BigDecimal( 10d ), BaseConsts.SYSTEM_CURRENCY_CODE));
    	inv.setTotalTax(new Money( new BigDecimal( 5d ), BaseConsts.SYSTEM_CURRENCY_CODE));
    	inv.setInvoiceTotal(new Money( new BigDecimal( 15d ), BaseConsts.SYSTEM_CURRENCY_CODE"));
 
    	builder.begin(inv);
    	
    	InvoiceItemModel it = new InvoiceItemModel();
    	it.setAmount(new MoneySet( new BigDecimal( 10 ),  new BigDecimal( 10 ), BaseConsts.SYSTEM_CURRENCY_CODE));
    	it.setInfo("satır hakkında bişiler bişiler");
    	
    	Service service = new Service();
    	service.setCode("S001");
    	service.setName("Yazılım Hizmeti");
    	service.setTaxIncluded(true);
    	service.setUnit("Adet");
    	
    	it.setService(service);
    	it.setQuantity(new Quantity( 1, "Adet"));
    	
    	builder.addLine(it);
    	
    	return builder.getModel();
    }
    */
}
