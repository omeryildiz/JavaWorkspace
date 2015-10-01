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

package com.ut.tekir.finance.print;

import java.util.ArrayList;
import java.util.List;

import com.ut.tekir.entities.Payment;
import com.ut.tekir.entities.PaymentItem;

/**
 *
 * @author nexus
 */

public class PaymentPrintModelBuilder {
    private PaymentPrintMasterModel master;
    private List<PaymentPrintLineModel> lines;

    public void begin( Payment payment ){
        master = new PaymentPrintMasterModel(payment);
        prepareLines();
    }

    private void prepareLines() {
		for (PaymentItem lm : master.getPayment().getItems()) {
			addLine(lm);
		}
    }
    
    public void addLine( PaymentItem line ){
        PaymentPrintLineModel lm = new PaymentPrintLineModel( master, line );
        getModel().add(lm);
    }

    public List<PaymentPrintLineModel> getModel(){
        if (lines == null) {
        	lines = new ArrayList<PaymentPrintLineModel>();
        }
    	return lines;
    }

}
