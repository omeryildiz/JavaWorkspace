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

package com.ut.tekir.general;

import javax.ejb.Stateful;

import org.jboss.seam.ScopeType;
import org.jboss.seam.annotations.In;
import org.jboss.seam.annotations.Name;
import org.jboss.seam.annotations.Out;
import org.jboss.seam.annotations.Scope;
import org.jboss.seam.faces.Redirect;

import com.ut.tekir.entities.QuickLaunch;
import com.ut.tekir.framework.EntityBase;

/**
 *
 * @author yigit
 */
@Stateful
@Name("quickLaunchHome")
@Scope(value=ScopeType.SESSION)
public class QuickLaunchHomeBean extends EntityBase<QuickLaunch> implements QuickLaunchHome<QuickLaunch>{

    private String key;
    
    @Out(required = false)
    public QuickLaunch getQuickLaunch(){
        return getEntity();
    }

    @In(required = false)
    public void setQuickLaunch(QuickLaunch quickLaunch){
        setEntity(quickLaunch);
    }

    /**
     *
     * @param key verilen anahtara göre entity i döndürür.
     * @return
     */
    public QuickLaunch getByKey(String key){
        try {
            return (QuickLaunch)getEntityManager().createQuery("select c from QuickLaunch c where c.launchKey = :key")
                                    .setParameter("key", key)
                                    .getSingleResult();
        } catch (Exception e) {
            return null;
        }
    }

    public void redirectPage() {
		Redirect redirect = Redirect.instance();

        if ( getKey() != null && getKey().length() != 0 ){
            String gidilecekSayfa = getByKey(getKey()).getLink();
            String viewId ;
            String[] parametreler;
            int soruIsaretiIndex;

            soruIsaretiIndex = gidilecekSayfa.indexOf("?");
            if (soruIsaretiIndex != -1) {
                viewId = gidilecekSayfa.substring(0, soruIsaretiIndex);

                parametreler = gidilecekSayfa.substring(soruIsaretiIndex + 1, gidilecekSayfa.length()).split("&");

                for (String s: parametreler) {
                    String[] params = s.split("=");
                    redirect.setParameter(params[0], params[1]);
                }
            } else {
                viewId = gidilecekSayfa;
            }
            redirect.setViewId(viewId);
            redirect.execute();
		}
    }

    /**
     * @return the key
     */
    public String getKey() {
        return key;
    }

    /**
     * @param key the key to set
     */
    public void setKey(String key) {
        this.key = key;
        redirectPage();
    }
}
