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

import com.ut.tekir.annotations.OptionPane;
import java.util.ArrayList;
import java.util.List;
import java.util.Set;
import org.jboss.seam.Component;
import org.jboss.seam.ScopeType;
import org.jboss.seam.annotations.Create;
import org.jboss.seam.annotations.In;
import org.jboss.seam.annotations.Logger;
import org.jboss.seam.annotations.Name;
import org.jboss.seam.annotations.Scope;
import org.jboss.seam.annotations.Startup;
import org.jboss.seam.log.Log;

/**
 * OptionPane Manager
 * @author haky
 */
@Name("optionPaneManager")
@Scope(ScopeType.APPLICATION)
@Startup
public class OptionPaneManager {

    @Logger
    Log log;
    private List<String> systemOptionPaneNames = new ArrayList<String>();
    private List<String> userOptionPaneNames = new ArrayList<String>();
    @In(value = "#{deploymentStrategy.annotatedClasses['com.ut.tekir.annotations.OptionPane']}", required = false)
    private Set<Class<Object>> optionPaneClasses;

    @Create
    public void init() {

        if (optionPaneClasses != null) {
            for (Class clazz : optionPaneClasses) {
                handleClass(clazz);
            }
        }
        //for (Class clazz: hotFooClasses) {
        //    handleClass(clazz);
        //}
        log.info("Deployed System OptionPanes : #0 ", systemOptionPaneNames);
        log.info("Deployed User OptionPanes : #0 ", userOptionPaneNames);
    }

    public void handleClass(Class clazz) {

        Name a = (Name) clazz.getAnnotation(Name.class);

        if (a != null) {
            //Bu aynı zamanda bir seam componenti

            OptionPane o = (OptionPane) clazz.getAnnotation(OptionPane.class);

            if (o.value() == OptionPaneType.User) {
                userOptionPaneNames.add(a.value());
            } else {
                systemOptionPaneNames.add(a.value());
            }


        } else {
            //sadece bir OptionPane sınıfı

            log.error("A OptionPane must be a Seam Component. OptionPane : " + clazz.getName());
        }

    }

    /**
     * @return System Option Pane
     */
    public List<OptionEditorPane> getSystemOptionPanes() {

        List<OptionEditorPane> panes = new ArrayList<OptionEditorPane>();

        for (String a : systemOptionPaneNames) {
            OptionEditorPane d = (OptionEditorPane) Component.getInstance(a);
            if (d != null) {
                panes.add(d);
            }
        }
        return panes;
    }

    /**
     * @return User Option Panes
     */
    public List<OptionEditorPane> getUserOptionPanes() {

        List<OptionEditorPane> panes = new ArrayList<OptionEditorPane>();

        for (String a : userOptionPaneNames) {
            OptionEditorPane d = (OptionEditorPane) Component.getInstance(a);
            if (d != null) {
                panes.add(d);
            }
        }
        return panes;
    }
}
