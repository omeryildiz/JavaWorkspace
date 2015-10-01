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

import com.ut.tekir.entities.RolePermission;
import com.ut.tekir.entities.User;
import com.ut.tekir.entities.UserRole;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import javax.persistence.EntityManager;
import org.jboss.seam.Component;
import org.jboss.seam.ScopeType;
import org.jboss.seam.annotations.AutoCreate;
import org.jboss.seam.annotations.Install;
import org.jboss.seam.annotations.Name;
import org.jboss.seam.annotations.Observer;
import org.jboss.seam.annotations.Scope;
import org.jboss.seam.annotations.intercept.BypassInterceptors;
import org.jboss.seam.log.Log;
import org.jboss.seam.log.Logging;
import org.jboss.seam.security.Identity;

/**
 * 
 * @author Hakan Uygun
 */
@Name("org.jboss.seam.security.identity")
@Scope(ScopeType.SESSION)
@BypassInterceptors
@Install(precedence=Install.APPLICATION)
@AutoCreate
public class TekirIdentity extends Identity{
	private static final long serialVersionUID = 1L;

	private static final Log log = Logging.getLog(TekirIdentity.class);

    private User activeUser;

    private Map<String, RolePermission> permissionMap = new HashMap<String, RolePermission>();

    @Override
    public boolean hasPermission(Object target, String action) {
        return hasPermission( target.toString(), action, null, null);
    }

    @Override
    public boolean hasPermission(String name, String action, Object... args) {

        activeUser = (User)Component.getInstance("activeUser");
        //currentAccessLevel = (Integer)Component.getInstance("currentAccessLevel");

        log.debug("permission check #0, #1, #2", name, action, activeUser );

        //Önce build-in roller kontrol edilecek.
        //TODO: Build-in roller net bir şekilde tanımlanmalı...
        if( hasRole("su") || hasRole("SU") || hasRole("admin") || hasRole("ADMIN") ){
            return true;
        }

        if( permissionMap == null ){
            return false;
        }

        if( permissionMap.isEmpty() ){
            return false;
        }

        //TODO: daha detaylı hak kontrolü için args üzerindeki değerler kontrol edilmeli...
        String key = name + ":" + action;
        return permissionMap.containsKey(key);
        //return false;
    }

    @Override
    public boolean addRole(String role) {
        populatePermissionMap(role);
        return super.addRole(role);
    }

    @Observer("refreshPermissionMap")
    public void refreshPermissionMap() {
    	log.debug("Refreshing permission map");
    	permissionMap.clear();
    	for (UserRole role : activeUser.getRoles()) {
    		populatePermissionMap(role.getRole().getName());
    	}
    }

    @SuppressWarnings("unchecked")
    private void populatePermissionMap(String role) {

        List<RolePermission> ls = getEntityManager().createQuery("select c from RolePermission c where c.role.name = :role")
                .setParameter("role", role)
                .getResultList();

        for( RolePermission rp : ls ){
            String key = rp.getTarget() + ":" + rp.getAction();
            //TODO: ileride daha kapsamlı ( filter, limit v.s. ) geldiğinde en geniş hak setlenecek.
            permissionMap.put(key, rp);
        }
    }

    private EntityManager getEntityManager(){
        return (EntityManager) Component.getInstance("entityManager");
    }
}
