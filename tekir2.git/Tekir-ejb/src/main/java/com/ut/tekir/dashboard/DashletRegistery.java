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

import java.util.HashMap;
import java.util.Map;
import org.jboss.seam.ScopeType;
import org.jboss.seam.annotations.Logger;
import org.jboss.seam.annotations.Name;
import org.jboss.seam.annotations.Scope;
import org.jboss.seam.annotations.Startup;
import org.jboss.seam.log.Log;
import com.ut.tekir.annotations.Dashlet;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Set;
import org.jboss.seam.annotations.Create;
import org.jboss.seam.annotations.In;
/**
 * Sistemde tanımlı olan dashletlerin listesini ve meta Datasını saklar.
 * @author Hakan Uygun
 */
@Name("dashletRegistery")
@Scope(ScopeType.APPLICATION)
@Startup
public class DashletRegistery {

    @Logger
    Log log;

    Map<String, Dashlet> metaData = new HashMap<String, Dashlet>();
    Map<String, List<DashletCapability>> capabilities = new HashMap<String, List<DashletCapability>>();

    @In(value = "#{deploymentStrategy.annotatedClasses['com.ut.tekir.annotations.Dashlet']}", required = false)
    private Set<Class<Object>> deployedClasses;

    //@In(value = "#{hotDeploymentStrategy.annotatedClasses['com.ut.tekir.annotations.Dashlet']}", required = false)
    private Set<Class<Object>> hotdeployedClasses;


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
        log.info("Deployed Dashlets : #0 ", metaData.keySet() );
    }

    public void handleClass(Class clazz) {

        Dashlet a = (Dashlet) clazz.getAnnotation(Dashlet.class);

        if (a != null) {

            Name s = (Name) clazz.getAnnotation(Name.class);
            if( s != null ){
                //MetaData'yı sakladık. Böylece her lazım olduğunda ulaşabileceğiz.

                metaData.put(s.value(), a);
                //Performans için capabilityleri de ayrı bi rmap'e aldık.
                capabilities.put(s.value(), Arrays.asList(a.capabilities()));
            } else {
                log.error("A Dashlet must be a Seam Component. Dashlet : " + clazz.getName());
            }

        }

    }

    /**
     * Geriye sistemde kayıtlı dashlet listesini döndürür.
     * @return
     */
    public List<String> getDashletList(){
        return new ArrayList(metaData.keySet());
    }

    /**
     * Geriye ismi verilen dashletin yetenek listesini döndürür
     * @param name
     * @return
     */
    public DashletCapability[] getCapabilities(String name ){
        //TODO: Null kontrolü yapılmalı
        return metaData.get(name).capabilities();
    }

    /**
     * ismi verilen dashletin yeteneği olup olmadığını döndürür.
     * @param name
     * @param capability
     * @return
     */
    public boolean hasCapability( String name, DashletCapability capability ){
        if( capabilities.get(name) == null ) return false;
        log.debug("Dashlet:#0 Capability:#1, Result : #2, All : #3", name, capability, capabilities.get(name).contains(capability), Arrays.asList(metaData.get(name).capabilities()) );

        return capabilities.get(name).contains(capability);
    }
}
