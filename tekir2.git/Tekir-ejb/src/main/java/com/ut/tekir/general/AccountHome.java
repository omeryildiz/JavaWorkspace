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

package com.ut.tekir.general;

import java.util.List;

import com.ut.tekir.entities.Account;
import com.ut.tekir.framework.IEntityHome;

/**
 *
 * @author haky
 */
public interface AccountHome<E> extends IEntityHome<E> {
    Account getAccount();
    void setAccount(Account account);
    
    List<Account> getEntityList();
    void setEntityList(List<Account> entityList);
    
    void initAccountList();
    boolean getOrganizationSchemeOption();
}
