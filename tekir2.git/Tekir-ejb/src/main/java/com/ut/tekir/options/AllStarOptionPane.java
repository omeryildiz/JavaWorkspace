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

package com.ut.tekir.options;

import com.ut.tekir.entities.Option;
import com.ut.tekir.annotations.OptionPane;
import java.util.List;
import javax.persistence.EntityManager;
import org.jboss.seam.annotations.Create;
import org.jboss.seam.annotations.In;
import org.jboss.seam.annotations.Logger;
import org.jboss.seam.annotations.Name;
import org.jboss.seam.annotations.Observer;
import org.jboss.seam.core.Events;
import org.jboss.seam.log.Log;

/**
 * Bu sınıf sistem içindeki tüm seçenekleri yetkili kullanıcının karşısına açar.
 * Bu işlemi yaparken de OptionManager'ı bypass eder.
 *
 * @author haky
 */
@Name("allStarOptionPane")
@OptionPane(OptionPaneType.System)
public class AllStarOptionPane extends AbstractOptionPane{

    @Logger
    protected Log log;
    @In
    protected EntityManager entityManager;

    @In
    protected Events events;

    private List<Option> options = null;

    public String getName() {
        return "allstar";
    }

    @Create
    public void init() {
        log.info("All Star Option Pane Init");
    }

    @Observer("refreshOptions")
    public void clearOptions() {
        options = null;
    }

    private void loadOptions(){

        setOptions((List<Option>) entityManager.createQuery("select c from Option c").getResultList());
        /* sanıyorum burda böyle bişey yapmamak lazım...
        for (Option op : ls) {
            options.put( op.getUser() + "." + op.getKey(), op);
        }
         */
    }

    public void save() {
        events.raiseTransactionSuccessEvent("refreshOptions");
    }

    public void cancel() {
        //TODO
    }

    /**
     * @return the options
     */
    public List<Option> getOptions() {
        if( options == null ){
            loadOptions();
        }
        return options;
    }

    /**
     * @param options the options to set
     */
    public void setOptions(List<Option> options) {
        this.options = options;
    }

}
