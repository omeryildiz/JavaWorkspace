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

package com.ut.tekir.permission;

import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import org.dom4j.Attribute;
import org.dom4j.Document;
import org.dom4j.DocumentException;
import org.dom4j.Element;
import org.dom4j.io.SAXReader;
import org.jboss.seam.ScopeType;
import org.jboss.seam.annotations.Create;
import org.jboss.seam.annotations.In;
import org.jboss.seam.annotations.Logger;
import org.jboss.seam.annotations.Name;
import org.jboss.seam.annotations.Scope;
import org.jboss.seam.annotations.Startup;
import org.jboss.seam.deployment.FileDescriptor;
import org.jboss.seam.log.Log;
import com.ut.tekir.permission.modal.PermissionGroup;
import com.ut.tekir.permission.modal.PermissionDefinition;
import java.util.HashMap;
/**
 * PermissionRegistery scans .perm.xml files
 * @author Hakan Uygun
 */
@Name("permissionRegistery")
@Scope(ScopeType.APPLICATION)
@Startup
public class PermissionRegistery {

    @Logger
    Log log;
    @In(value = "#{deploymentStrategy.deploymentHandlers['permDeploymentHandler']}", required = false)
    private PermissionDeploymentHandler myDeploymentHandler;
    //@In(value = "#{hotDeploymentStrategy.deploymentHandlers['permDeploymentHandler']}", required = false)
    private PermissionDeploymentHandler myHotDeploymentHandler;

    private Map<String, PermissionGroup> permMap = new HashMap<String, PermissionGroup>();

    @Create
    public void create() {
        log.info("DeploymentHandlers #0 #1", myDeploymentHandler, myHotDeploymentHandler);
        if (myDeploymentHandler != null) {
            for (FileDescriptor fd : myDeploymentHandler.getResources()) {
                handlePermXml(fd);
            }
        }
        if (myHotDeploymentHandler != null) {
            for (FileDescriptor fd : myHotDeploymentHandler.getResources()) {
                handlePermXml(fd);
            }
        }
    }

    public void handlePermXml(FileDescriptor fd) {

        InputStream is = null;
        try {
            log.info("Register file : #0 #1", fd.getName(), fd.getUrl());
            is = fd.getUrl().openStream();

            SAXReader reader = new SAXReader();
            Document doc = reader.read(is);
            Element root = doc.getRootElement();
            List<Element> elements = root.elements("permissionGroup");
            for (Element e : elements) {
                registerPermissionGroup(e);
            }

        } catch (DocumentException ex) {
            log.error("perm cannot read", ex);
        } catch (IOException ex) {
            log.error("perm cannot read", ex);
        } finally {
            try {
                is.close();
            } catch (IOException ex) {
                log.error("perm cannot read", ex);
            }
        }

    }

    /**
     * Verilen elementteki grupları ve altındaki permissionları toplar
     * @param e
     */
    private void registerPermissionGroup(Element e) {
        String gn = e.attributeValue("name");
        List<Element> elements = e.elements("permission");
        for (Element a : elements) {
            registerPermission(a, gn);
        }
    }

    /**
     * Verilen elementten ( permission ) actionları toplar ve register eder.
     * @param e
     * @param gn
     */
    private void registerPermission(Element e, String gn) {
        Attribute target = e.attribute("target");
        String t = target.getText();
        List<String> al = new ArrayList<String>();

        populateEntityActions(e, al);
        populateActions(e, al);
        populateExculedActions(e, al);

        //Tüm data toplandı şimdi map'e ekleniyor...
        PermissionGroup pg = permMap.get(gn);
        if( pg == null ){
            pg = new PermissionGroup();
            pg.setName(gn);
            permMap.put(gn, pg);
        }

        PermissionDefinition pd = new PermissionDefinition();
        pd.setTarget(t);
        pd.getActions().addAll(al);
        pg.getDefinitions().add(pd);

        log.info("Registered Permission = #0.#1#2", gn, t, al);
    }

    /**
     * Verilen elementteki ( permission ) entityAction elementi varmı bakar varsa hakları listeye ekler
     * @param e
     * @param als
     */
    private void populateEntityActions(Element e, List<String> als) {
        Element element = e.element("entityActions");
        if (element != null) {
            als.add(ActionConsts.SELECT_ACTION);
            als.add(ActionConsts.INSERT_ACTION);
            als.add(ActionConsts.UPDATE_ACTION);
            als.add(ActionConsts.DELETE_ACTION);
        }

    }

    /**
     * verilen elementteki ( permission ) action elementelerini listeye ekler
     * @param e
     * @param als
     */
    private void populateActions(Element e, List<String> als) {
        List<Element> elements = e.elements("action");
        for (Element a : elements) {
            als.add(a.attributeValue("name"));
        }
    }

    /**
     * verilen elementteki ( permission ) exclude elemetlerini listeden çıkartır
     * @param e
     * @param als
     */
    private void populateExculedActions(Element e, List<String> als) {
        List<Element> elements = e.elements("exclude");
        for (Element a : elements) {
            als.remove(a.attributeValue("name"));
        }
    }

    public Map<String, PermissionGroup> getPermMap() {
        return permMap;
    }

    public void setPermMap(Map<String, PermissionGroup> permMap) {
        this.permMap = permMap;
    }


    /**
     * Geriye sistemde tanımlı olan hakları grup listesi halinde döndürür.
     * @return
     */
    public List<PermissionGroup> getPermissionGroups(){
        return new ArrayList<PermissionGroup>(permMap.values());
    }
}
