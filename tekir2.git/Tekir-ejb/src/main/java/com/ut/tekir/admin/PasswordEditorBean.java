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

import com.ut.tekir.framework.Hash;
import com.ut.tekir.entities.User;
import com.ut.tekir.framework.BaseConsts;
import java.security.NoSuchAlgorithmException;
import javax.ejb.Remove;
import javax.ejb.Stateful;
import javax.persistence.EntityManager;
import org.jboss.seam.ScopeType;
import org.jboss.seam.annotations.Destroy;
import org.jboss.seam.annotations.In;
import org.jboss.seam.annotations.Logger;
import org.jboss.seam.annotations.Name;
import org.jboss.seam.annotations.Out;
import org.jboss.seam.annotations.Scope;
import org.jboss.seam.faces.FacesMessages;
import org.jboss.seam.log.Log;


/**
 * Kullanıcının parolasını değiştirebilmesi için gereken form işlemlerini sağlar.
 * @author haky
 */
@Stateful()
@Name( "passwordEditor")
@Scope(value=ScopeType.CONVERSATION)
public class PasswordEditorBean implements PasswordEditor {

    @Logger
    protected Log log;
    
    @In
    protected EntityManager entityManager;
    
    @In
    protected FacesMessages facesMessages;

    @In @Out
    User activeUser;
    
    private String oldPassword;
    private String newPassword;
    private String retypePassword;

    Hash hash = new Hash();
    
    public String save() {
        
        if( newPassword == null || newPassword.length() == 0 ){
            facesMessages.add("Parola boş olamaz.");
            return BaseConsts.FAIL;
        }
        
        if( !newPassword.equals( retypePassword )){
            facesMessages.add("Parola tekrar girilen ile uyuşmuyor. Lütfen tekrar deneyiniz.");
            return BaseConsts.FAIL;
        }
        try {
            if (!activeUser.getPass().equals(hash.md5(oldPassword))) {
                facesMessages.add("Eski şifreniz yanlış. Lütfen tekrar deneyiniz.");
                return BaseConsts.FAIL;
            }        
            activeUser.setPass(hash.md5(newPassword));
        
        } catch (NoSuchAlgorithmException ex) {
            facesMessages.add("Eski şifreniz yanlış. Lütfen tekrar deneyiniz.");
            return BaseConsts.FAIL;
        }
        
        entityManager.merge(activeUser);
        entityManager.flush();
        facesMessages.add("Şifreniz başarıyla değiştirildi.");
        return BaseConsts.SUCCESS;
    }
    
    public String getOldPassword() {
        return oldPassword;
    }

    public void setOldPassword(String oldPassword) {
        this.oldPassword = oldPassword;
    }

    public String getNewPassword() {
        return newPassword;
    }

    public void setNewPassword(String newPassword) {
        this.newPassword = newPassword;
    }

    public String getRetypePassword() {
        return retypePassword;
    }

    public void setRetypePassword(String retypePassword) {
        this.retypePassword = retypePassword;
    }
    
    @Remove @Destroy
    public void destroy() {
    }
}
