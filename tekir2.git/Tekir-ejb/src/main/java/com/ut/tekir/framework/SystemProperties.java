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

package com.ut.tekir.framework;

import java.io.IOException;
import java.io.InputStream;
import java.util.Properties;

import org.jboss.seam.Component;
import org.jboss.seam.ScopeType;
import org.jboss.seam.annotations.AutoCreate;
import org.jboss.seam.annotations.Create;
import org.jboss.seam.annotations.Logger;
import org.jboss.seam.annotations.Name;
import org.jboss.seam.annotations.Scope;
import org.jboss.seam.log.Log;

/**
 *
 * @author haky
 */
@Name("systemProperties")
@Scope(ScopeType.APPLICATION)
@AutoCreate
public class SystemProperties {

    @Logger
    private Log log;
    private Properties properties = new Properties();

    //private Map<String, String> properties = new HashMap<String, String>();
    @Create
    public void init() {

        Properties prop = new Properties();

        try {
            InputStream is = this.getClass().getResourceAsStream("/utapp.properties");
            if (is != null) {
                prop.load(is);
                properties.putAll(prop);
            } else {
                log.error("Application Properties not found");
            }
        } catch (IOException ex) {
            log.error("systemProperties not found", ex);
        }

        try {

            InputStream is = this.getClass().getResourceAsStream("/utapp.properties");
            if (is != null) {
                prop.clear();
                prop.load(this.getClass().getResourceAsStream("/" + properties.getProperty("setup.properties")));
                properties.putAll(prop);
            } else {
                log.error("Setup Properties not found : #0", properties.getProperty("setup.properties"));
            }
        } catch (IOException ex) {
            log.error("systemProperties not found", ex);
        }

        log.info("System Properties : #0", properties);
    }

    public String getProperty( String key ){
        return properties.getProperty(key);
    }

    public String getProperty( String key, String defaultValue ){
        return properties.getProperty(key,defaultValue);
    }

    public Properties getProperties() {
        return properties;
    }

    public void setProperties(Properties properties) {
        this.properties = properties;
    }
    
    public static SystemProperties instance() {
    	return (SystemProperties) Component.getInstance("systemProperties");
    }
}
