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

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.ejb.Remove;
import javax.ejb.Stateful;
import javax.persistence.EntityManager;

import org.jboss.seam.ScopeType;
import org.jboss.seam.annotations.AutoCreate;
import org.jboss.seam.annotations.Destroy;
import org.jboss.seam.annotations.In;
import org.jboss.seam.annotations.Logger;
import org.jboss.seam.annotations.Name;
import org.jboss.seam.annotations.Scope;
import org.jboss.seam.faces.FacesMessages;
import org.jboss.seam.log.Log;

import com.ut.tekir.entities.Option;
import com.ut.tekir.entities.Sequence;

/**
 *
 * @author haky
 */
@Stateful
@Name("sequenceManager")
@Scope(value = ScopeType.APPLICATION)
@AutoCreate
public class SequenceManagerBean implements SequenceManager {

    @Logger
    protected Log log;
    @In
    protected EntityManager entityManager;
    @In
    protected FacesMessages facesMessages;
    @In
    private OptionManager optionManager;
    private Map<String, Sequence> sequenceMap = new HashMap<String, Sequence>();

    /** Creates a new instance of SequenceManagerBean */
    public SequenceManagerBean() {
    }

    public String getNewNumber(String serial, Integer len) {

        //TODO: burda gerçek numaranın bir şekilde veri tabanından alınması lazım. Veri tabanı sequence'i v.s. bişey kullnılması lazım.'
        Sequence seq = findSequence(serial);

        log.debug("Sequence #0 oldValue #1", seq.getSerial(), seq.getNumber());

        seq.setNumber(seq.getNumber() + 1);
        entityManager.merge(seq);
        entityManager.flush();

        log.debug("Sequence #0 newValue #1", seq.getSerial(), seq.getNumber());

        //TODO: numara digit sayısı dönecek digit sayısından büyük ise hata dönmesi lazım. Taşma var.
        String s = "000000000" + seq.getNumber();

        return s.substring(s.length() - len);
    }
    
    public String getNewTempNumber(String serial, Integer len) {

        //TODO: burda gerçek numaranın bir şekilde veri tabanından alınması lazım. Veri tabanı sequence'i v.s. bişey kullnılması lazım.'
        Sequence seq = findSequence(serial);

        log.debug("Sequence #0 oldValue #1", seq.getSerial(), seq.getNumber());

        Long num = seq.getNumber() + 1;
        //entityManager.merge(seq);
        //entityManager.flush();

        log.debug("Sequence #0 newValue #1", seq.getSerial(), num);

        //TODO: numara digit sayısı dönecek digit sayısından büyük ise hata dönmesi lazım. Taşma var.
        String s = "000000000" + num;

        return s.substring(s.length() - len);
    }

    @SuppressWarnings("unchecked")
	protected Sequence findSequence(String serial) {
        log.debug("Find Sequence : #0", serial);
        Sequence seq = null;

        //Önce eldeki Map kontrol ediliyor...
        if (!sequenceMap.containsKey(serial)) {
            //Veri tabanından isteniyor
            List ls = entityManager.createQuery("select c from Sequence c where serial = '" + serial + "' ").getResultList();
            if ( ls.size() > 0 ){
                seq = (Sequence) ls.get(0);
            } else {
            //Veri tabanında da yoksa yeni açılıyor...
                seq = new Sequence();
                seq.setSerial(serial);
                seq.setNumber(0l);
                entityManager.persist(seq);
                entityManager.flush();
            }
            
            sequenceMap.put(serial, seq);
        } else {
            seq = sequenceMap.get(serial);
        }
        
        return seq;
    }

    public Long getCurrenctNumber(String serial) {
        Sequence seq = findSequence(serial);

        return seq.getNumber();
    }

    public void setCurrenctNumber(String serial, Long number) {
        Sequence seq = findSequence(serial);
        seq.setNumber(number);
        entityManager.merge(seq);
        entityManager.flush();
    }

    /*
    public String getNewSerialNumber( String serial ){
    //TODO: Taşma kontrolü yapılması lazım. ona göre belkide seri kısmını da otomatik arttırmalı...
    return serial + "-" + getNewNumber( serial, 6 );
    }
     */
    public String getNewSerialNumber(String key) {
        //TODO: Burda ilgili optiondan değeri okuyacak.

        String serial = "AA";
        Option o = optionManager.getOption(key);
        if (o != null) {
            serial = o.getAsString();
        }

        return serial + "-" + getNewNumber(key + "." + serial, 6);
    }

    
    /**
     * Geçici olarak numara isteniyor. Eğer kayıt saklanmazsa kaybolmasın diye.
     * @param key
     * @return geçici olarak atanmış numara döner.
     */
    public String getTempSerialNumber( String key ){
        String serial = "AA";
        Option o = optionManager.getOption(key);
        if (o != null) {
            serial = o.getAsString();
        }

        return serial + "-" + getNewTempNumber(key + "." + serial, 6);
    }


    public String getBarcodeNumber( String key, Integer len) {

        //12 digit seri no uretecek
        String serial = "B1";
        // Integer len = 12;  //paritesiz basamak sayisi

        Option o = optionManager.getOption(key);
        if (o != null) {
            serial = o.getAsString();
        }

        serial = key + "." + serial;

        Sequence seq = findSequence(serial);

        log.debug("Sequence #0 oldValue #1", seq.getSerial(), seq.getNumber());

        seq.setNumber(seq.getNumber() + 1);
        entityManager.merge(seq);
        entityManager.flush();

        log.debug("Sequence #0 newValue #1", seq.getSerial(), seq.getNumber());

        //TODO: numara digit sayısı dönecek digit sayısından büyük ise hata dönmesi lazım. Taşma var.
        String s = "00000000000000" + seq.getNumber();

        s = s.substring(s.length() - (len-1));
        return s = '1'+ s;
    }

    @Remove
    @Destroy
    public void destroy() {
    }
}
