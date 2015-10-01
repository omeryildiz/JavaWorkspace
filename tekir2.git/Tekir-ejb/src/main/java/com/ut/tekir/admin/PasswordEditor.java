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


/**
 *
 * @author haky
 */
public interface PasswordEditor {

    void destroy();

    String getNewPassword();

    String getOldPassword();

    String getRetypePassword();

    String save();

    void setNewPassword(String newPassword);

    void setOldPassword(String oldPassword);

    void setRetypePassword(String retypePassword);

}
