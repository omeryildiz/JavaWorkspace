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

package com.ut.tekir.docmatch;

import java.io.Serializable;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.Map.Entry;

import org.jboss.seam.ScopeType;
import org.jboss.seam.annotations.Create;
import org.jboss.seam.annotations.In;
import org.jboss.seam.annotations.Logger;
import org.jboss.seam.annotations.Name;
import org.jboss.seam.annotations.Scope;
import org.jboss.seam.annotations.Startup;
import org.jboss.seam.log.Log;

import com.ut.tekir.docmatch.annotation.MatchProvider;
import com.ut.tekir.entities.DocumentType;

/**
 * @author sinan.yumak
 *
 */
@Name("matchProviderRegistry")
@Scope(ScopeType.APPLICATION)
@Startup
public class MatchProviderRegistry implements Serializable{
	private static final long serialVersionUID = 1L;

	@Logger
    private Log log;

    @In(value = "#{deploymentStrategy.annotatedClasses['com.ut.tekir.docmatch.annotation.MatchProvider']}", required = false)
    private Set<Class<Object>> deployedClasses;

    //@In(value = "#{hotDeploymentStrategy.annotatedClasses['com.ut.tekir.docmatch.annotation.MatchProvider']}", required = false)
    private Set<Class<Object>> hotdeployedClasses;

    private static Map<Class, List<DocumentType>> providers;

	private static Map<Class, List<DocumentType>> getProviders() {
		if (providers == null) {
			providers = new HashMap<Class, List<DocumentType>>();
		}
		return providers;
	}
    
    @Create
    public void init() {
        log.info( "Scanned classes : #0, #1", deployedClasses, hotdeployedClasses );
        if (deployedClasses != null) {
            for (Class clazz : deployedClasses) {
                handleClass(clazz);
            }
        }

        if( hotdeployedClasses != null ){
            for (Class clazz: hotdeployedClasses) {
                handleClass(clazz);
            }
        }
        log.info("Deployed Match Providers : #0 ", getProviders().keySet() );
    }

    public void handleClass(Class clazz) {
        MatchProvider a = (MatchProvider) clazz.getAnnotation(MatchProvider.class);

        if (a != null) {
        	getProviders().put(clazz, Arrays.asList(a.types()));
        }
    }

    /**
     * Verilen döküman tipine uygun provider dan bir instance döndürür.
     * 
     * @param docType
     */
    public static DocumentMatchProvider getProvider(DocumentType docType) {
    	try {
    		for (Entry<Class, List<DocumentType>> entry : getProviders().entrySet()) {
    			if (entry.getValue().contains(docType)) return (DocumentMatchProvider)entry.getKey().newInstance();
    		}
		} catch (Exception e) {
		}
		return null;
    }
    
}
