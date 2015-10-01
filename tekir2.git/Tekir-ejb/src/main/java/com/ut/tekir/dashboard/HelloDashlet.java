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

package com.ut.tekir.dashboard;

import com.ut.tekir.annotations.Dashlet;
import java.io.Serializable;
import org.jboss.seam.ScopeType;
import org.jboss.seam.annotations.AutoCreate;
import org.jboss.seam.annotations.Name;
import org.jboss.seam.annotations.Scope;
import org.jboss.seam.annotations.remoting.WebRemote;

/**
 * Daslet Testi için Hello World!
 * @author Hakan Uygun
 */
@Name("helloDashlet")
@Scope(ScopeType.SESSION)
@AutoCreate
@Dashlet(capabilities={DashletCapability.canHide, DashletCapability.canEdit, DashletCapability.hasIcon})
public class HelloDashlet implements Serializable{

    String msg = "Merhaba Dünya!";
    
    @WebRemote
    public void changeMessage(String message){
        msg = message;
    }

    public String greetingMessage(){
        return msg;
    }
    
}
