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

package com.ut.tekir.finance;

import javax.ejb.Stateful;

import org.jboss.seam.ScopeType;
import org.jboss.seam.annotations.In;
import org.jboss.seam.annotations.Name;
import org.jboss.seam.annotations.Out;
import org.jboss.seam.annotations.Scope;

import com.ut.tekir.entities.FinanceAction;
import com.ut.tekir.entities.Payment;
import com.ut.tekir.framework.SequenceType;

/**
 * 
 * @author haky , volkan
 */
@Stateful
@Name("collectionHome")
@Scope(value = ScopeType.CONVERSATION)
public class CollectionHomeBean extends PaymentHomeBeanBase<Payment> implements CollectionHome<Payment> {

	@Override
	@Out(required=false)
	public Payment getCollection() {
		return getEntity();
	}

	@Override
	@In(required=false)
	public void setCollection(Payment payment) {
		setEntity(payment);
	}

	@Override
	public void createNew() {
		super.createNew();
		entity.setAction(FinanceAction.Credit);
		entity.setSerial(sequenceManager.getNewSerialNumber(SequenceType.SERIAL_FUND_COLLECTION));
	}

}
