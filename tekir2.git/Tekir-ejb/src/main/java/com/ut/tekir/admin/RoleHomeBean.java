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

package com.ut.tekir.admin;

import java.util.List;

import javax.ejb.Stateful;

import org.jboss.seam.ScopeType;
import org.jboss.seam.annotations.Begin;
import org.jboss.seam.annotations.Create;
import org.jboss.seam.annotations.Factory;
import org.jboss.seam.annotations.FlushModeType;
import org.jboss.seam.annotations.In;
import org.jboss.seam.annotations.Name;
import org.jboss.seam.annotations.Out;
import org.jboss.seam.annotations.Scope;
import org.jboss.seam.annotations.datamodel.DataModel;
import org.jboss.seam.core.Conversation;
import org.jboss.seam.core.Events;

import com.ut.tekir.entities.Role;
import com.ut.tekir.framework.EntityHome;
import com.ut.tekir.permission.PermissionRegistery;
import com.ut.tekir.permission.modal.PermissionGroup;

/**
 * Role Tanımlama Control sınıfı
 * @author Hakan Uygun
 */
@Stateful
@Name("roleHome")
@Scope(value = ScopeType.CONVERSATION)
public class RoleHomeBean extends EntityHome<Role> implements RoleHome<Role>{

    @In
    PermissionRegistery permissionRegistery;
    @In
    private Events events;

    private List<PermissionGroupUIModel> uiModel = null;

    @Create
    @Begin(join=true, flushMode=FlushModeType.MANUAL)
    public void initRoleHome() {

    }

    @DataModel("roleList")
    private List<Role> entityList;

    @Factory("roleList")
    @SuppressWarnings("unchecked")
    public void initRoleList() {
        log.debug("User Listesi hazırlanıyor...");

        setEntityList(getEntityManager().createQuery("select c from Role c") //.setMaxResults(100)
                //.setHint("org.hibernate.cacheable", true)
                .getResultList());
    }

    @Override
    public List<Role> getEntityList() {
        return entityList;
    }

    public void setEntityList(List<Role> entityList) {
        this.entityList = entityList;
    }

    @Out(required = false)
    public Role getRole() {
        return getEntity();
    }

    @In(required = false)
    public void setRole(Role role) {
        
        setEntity(role);
    }

    @Override
    public void createNew() {
        log.debug("New Role");
        entity = new Role();
        //entity.setActive(true);
        uiModel = null;
    }

    @Override
    public void edit(Role e) {
        if( entity == null || !entity.equals(e)){
            uiModel=null;
        }
        super.edit(e);
    }

    @Override
    public String save() {
    	manualFlush();
    	
    	PermissionUIModelUtil.updateModelFromUI(uiModel, entity);
        String result = super.save();

        refreshPermissionMapEvent();
        return result;
    }

    private void refreshPermissionMapEvent() {
    	log.debug("event: Refresh Permission Map");
        events.raiseTransactionSuccessEvent("refreshPermissionMap");
    }

    public List<PermissionGroup> getPermissionGroups(){
        return permissionRegistery.getPermissionGroups();
    }

    public List<PermissionGroupUIModel> getUiModel() {
        if( uiModel == null ){
            uiModel = PermissionUIModelUtil.getUIModel(getPermissionGroups(), entity.getPermissions());
            log.info("uiModel build : #0", uiModel);
        }
        return uiModel;
    }

    public void setUiModel(List<PermissionGroupUIModel> uiModel) {
        this.uiModel = uiModel;
    }

    public void manualFlush() {
        Conversation.instance().changeFlushMode(org.jboss.seam.annotations.FlushModeType.MANUAL);
    }

}
