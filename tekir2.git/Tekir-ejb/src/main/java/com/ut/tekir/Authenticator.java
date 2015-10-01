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

package com.ut.tekir;

import com.ut.tekir.entities.User;
import com.ut.tekir.entities.UserRole;
import com.ut.tekir.framework.Hash;
import java.security.NoSuchAlgorithmException;
import org.jboss.seam.annotations.In;
import org.jboss.seam.annotations.Logger;
import org.jboss.seam.annotations.Name;
import org.jboss.seam.log.Log;
import org.jboss.seam.security.Identity;
import javax.persistence.EntityManager;
import javax.persistence.NoResultException;
import org.jboss.seam.ScopeType;
import org.jboss.seam.annotations.Out;
import org.jboss.seam.faces.FacesMessages;




@Name("authenticator")
public class Authenticator{
    @Logger Log log;
    
    @In Identity identity;
    
    @In
    protected EntityManager entityManager;
    
    private Hash hash = new Hash();
    
    @Out(required=false,scope=ScopeType.SESSION)
    User activeUser;
    
    public boolean authenticate() throws NoSuchAlgorithmException {
        log.info("authenticating #0", identity.getUsername());
        FacesMessages.instance().clear();
        try {
            activeUser = (User) entityManager.createQuery(
                    "from User where userName = :username and pass = :password and active = :active")
                    .setParameter("username", identity.getUsername())
                    .setParameter("password", hash.md5(identity.getPassword()))
                    .setParameter("active", true)
                    .getSingleResult();
            if (activeUser.getRoles() != null) {
                for (UserRole mr : activeUser.getRoles()){
                    identity.addRole(mr.getRole().getName());
                }
            }
            
            return true;
        } catch (NoResultException ex) {
            FacesMessages.instance().add("#{messages['general.message.login.InvalidUser']}");
            return false;
        }
        
    }
}
