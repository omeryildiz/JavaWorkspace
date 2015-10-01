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

import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

import javax.ejb.Remove;
import javax.ejb.Stateful;
import javax.persistence.EntityManager;

import org.jboss.seam.ScopeType;
import org.jboss.seam.annotations.AutoCreate;
import org.jboss.seam.annotations.Create;
import org.jboss.seam.annotations.Destroy;
import org.jboss.seam.annotations.In;
import org.jboss.seam.annotations.Logger;
import org.jboss.seam.annotations.Name;
import org.jboss.seam.annotations.Observer;
import org.jboss.seam.annotations.Scope;
import org.jboss.seam.log.Log;
import org.jboss.seam.security.Identity;

import com.ut.tekir.entities.Option;
import com.ut.tekir.options.OptionKey;

/**
 * Uygulama içerisindeki temel seçenek sistemini yönetir.
 *
 * Tanımlar konvansiyoneldir. Yani özel olarak bir tabloya bir yerlere yazılmazlar.
 * Type safe olması için iyi dokümante edilmeliler.
 *
 * çeşitli alanlarda sadece değer tutucu değil key-pair veri tabanı olarak da kullanılır.
 *
 * key yapısı property dosyalarındaki gibi dir.
 * fazladan kullanıcı bilgisi bulunur. genel davranış, bir kullanıcı için aranılan değer bulunanamışsa system için bakmak şeylindedir.
 *
 *
 * @author haky
 */
@Stateful()
@Name("optionManager")
@Scope(value = ScopeType.APPLICATION)
@AutoCreate
public class OptionManagerBean implements OptionManager {

    @Logger
    protected Log log;
    @In
    protected EntityManager entityManager;
    @In(required=false)
    Identity identity;
    private Map<String, Option> options;

    @Create
    public void initManager() {
        options = new ConcurrentHashMap<String, Option>();
        loadOptions();

    }

    @Observer("refreshOptions")
    public void loadOptions() {
        log.debug("Optionlar Yükleniyor");

        List<Option> ls = entityManager.createQuery("select c from Option c").getResultList();
        for (Option op : ls) {
            options.put(op.getUser() + "." + op.getKey(), op);
        }

    }

    public Option getOption(String key) {

        Option o = null;

        if (identity != null && options.containsKey(identity.getCredentials().getUsername() + "." + key)) {
            o = options.get(identity.getCredentials().getUsername() + "." + key);
        } else if (options.containsKey("SYSTEM." + key)) {
            o = options.get("SYSTEM." + key);
        } else {
            log.info("Aranılan option bulunamadı : #0", key);
        }

        return o;
    }

    public Option getOption(OptionKey key) {
    	return getOption( key.getValue() );
    }

    /**
     * Bakar ve option yok ise bir tane oluşturur.
     * Kulanıcı için değil sistem için oluşturur.
     * @param key
     * @param defval
     * @return
     */
    public Option getOption(OptionKey key, boolean create) {
    	return create ? getOption( key.getValue(), key.getDefaultValue() ) : getOption( key );
    }
    
    /**
     * Bakar ve option yok ise bir tane oluşturur.
     * Kulanıcı için değil sistem için oluşturur.
     *
     * @param key
     * @param defval
     * @return
     */
    public Option getOption(String key, String defval) {
        return getOption(key, defval, false);
    }

    /**
     * Bakar ve option yok ise bir tane oluşturur.
     * Verilen kullanıcı için
     *
     * @param key
     * @param defval
     * @return
     */
    public Option getOption(String key, String defval, String user) {
        Option o = getOption(key);
        if (o == null) {
            o = crateOption(key, defval, user);
        }
        return o;
    }

    /**
     * Bakar ve option yok ise bir tane oluşturur.
     * kullanıcı için mi sistem için i oluşturacağı parametre olarak veriliyor.
     *
     * @param key
     * @param defval
     * @return
     */
    public Option getOption(String key, String defval, boolean foruser) {
        Option o = getOption(key);

        if (o == null) {

            String user = "SYSTEM";

            if (foruser) {
                user = identity.getCredentials().getUsername();
            }

            o = crateOption( key, defval, user );
        }
        return o;
    }

    private Option crateOption(String key, String val, String user) {
        Option o = new Option();
        o.setUser(user);
        o.setKey(key);
        o.setValue(val);
        entityManager.persist(o);
        entityManager.flush();
        options.put(user + "." + key, o);
        return o;
    }

    /**
     * Verilen optionı günceller.
     * @param o
     */
    public void updateOption(Option o) {
        //TODO: Buarada aslında Map üzerinde bulunana da bakmak iyi olabilir. ayrıca id kontrolü yapmakda lazım gelebilir.
        //API ihtiyacına göre düzenlenmeli
        //Kullanıcı için edit hakkı vermek lazım.
        entityManager.merge(o);
    }
    
    public void updateOption(String optionKey) {
    	entityManager.merge( getOption(optionKey) );
    }

    public void updateOption(OptionKey key) {
    	entityManager.merge( getOption(key) );
    }

    /**
     * Verilen listedeki tüm optionları günceller.
     * @param ls
     */
    public void updateOptions(List<Option> ls ){
        for( Option o : ls){
            updateOption(o);
        }
    }

    @Remove
    @Destroy
    public void destroy() {
    }
}
