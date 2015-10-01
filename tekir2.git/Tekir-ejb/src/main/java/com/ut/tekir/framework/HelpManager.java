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

import org.jboss.seam.ScopeType;
import org.jboss.seam.annotations.Logger;
import org.jboss.seam.annotations.Name;
import org.jboss.seam.annotations.Scope;
import org.jboss.seam.international.LocaleSelector;
import org.jboss.seam.log.Log;
import org.jboss.seam.util.Resources;

/**
 *
 * @author haky
 */
@Name( "helpManager")
@Scope(ScopeType.APPLICATION)
public class HelpManager {

    @Logger
    Log log;
    
    private String topic = "/";
    //FIXME: calisan sistemin dil kodu alinacak...
    private String localeCode = "tr_TR" ; //LocaleSelector.instance().getLocaleString();
    
    public String getHelpPath(){
        
        topic = topic.startsWith("/") ? topic : "/" + topic;
        log.info("Nereden Help istendi : #0", topic );
        
        //String topicPath = "/help/" + LocaleSelector.instance().getLocaleString() + topic;
        String topicPath = "/help/" + localeCode + topic;
        
        log.debug("Help yolu : #0", topicPath );
        
        
        if( Resources.getResourceAsStream(topicPath, null ) == null ){
            topicPath = "/help/" + localeCode + "/index.xhtml";
        }
        
        
        return topicPath;
    }
    
    public String getHelpImagePath(){
        return "/help/" + localeCode + "/img/";
    }
    
    public String topicPath( String s ){
        topic = s;
        return "/help/help.xhtml";
    }

    
    public String helpIndex(){
        return topicPath( "index.xhtml");
    }
    
    
    public String getTopic() {
        return topic;
    }

    public void setTopic(String topic) {
        this.topic = topic;
    }
    
}
