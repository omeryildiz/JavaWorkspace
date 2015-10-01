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

import java.util.Properties;

import org.jboss.seam.ScopeType;
import org.jboss.seam.annotations.Create;
import org.jboss.seam.annotations.Name;
import org.jboss.seam.annotations.Scope;
import org.jboss.seam.annotations.Startup;

/**
 * @author dumlupinar
 *
 */
@Name("applicationStartup")
@Scope(ScopeType.APPLICATION)
@Startup
public class ApplicationStartup {
	
	@Create
    public void init(){
		//TODO: Buradaki proxy ayarları component.xml dosyasından alınacak
//    	Properties sysProps = System.getProperties();
//    	sysProps.put("proxySet",  "true");
//        sysProps.put("proxyHost", "192.168.0.22");
//        sysProps.put("proxyPort", "3128");
    }
    
}