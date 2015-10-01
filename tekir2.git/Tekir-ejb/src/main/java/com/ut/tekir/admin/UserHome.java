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

package com.ut.tekir.admin;

import com.ut.tekir.entities.Role;
import com.ut.tekir.entities.User;
import com.ut.tekir.framework.IEntityHome;
import java.util.List;
import java.util.Map;
import javax.ejb.Local;

/**
 *
 * @author haky
 */
@Local
public interface UserHome<E> extends IEntityHome<E> {

    User getUser();
    void setUser(User User);
    
    String getPass1();
    void setPass1(String pass1);
    
    String getPass2();
    void setPass2(String pass2);
    
    void initUserHome();
    void initUserList();

    Map<Role, Boolean> getRoleMap();
    void setRoleMap(Map<Role, Boolean> roleMap);
    
    List<Role> getRoleList();
    void setRoleList(List<Role> roleList) ;

}
