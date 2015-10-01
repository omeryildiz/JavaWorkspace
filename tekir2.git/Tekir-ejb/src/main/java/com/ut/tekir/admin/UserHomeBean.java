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

import com.ut.tekir.framework.Hash;
import com.ut.tekir.entities.Role;
import com.ut.tekir.entities.User;
import com.ut.tekir.entities.UserRole;
import com.ut.tekir.framework.BaseConsts;
import com.ut.tekir.framework.EntityHome;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import javax.ejb.Stateful;
import org.jboss.seam.ScopeType;
import org.jboss.seam.annotations.Create;
import org.jboss.seam.annotations.Factory;
import org.jboss.seam.annotations.In;
import org.jboss.seam.annotations.Name;
import org.jboss.seam.annotations.Out;
import org.jboss.seam.annotations.Scope;
import org.jboss.seam.annotations.datamodel.DataModel;

/**
 *
 * @author haky
 */
@Stateful
@Name("userHome")
@Scope(value = ScopeType.CONVERSATION)
public class UserHomeBean extends EntityHome<User> implements UserHome<User> {

    private List<Role> roleList;
    private Map<Role, Boolean> roleMap;
    private String pass1;
    private String pass2;
    
    Hash hash = new Hash();

	@Create
	@SuppressWarnings("unchecked")
    public void initUserHome() {

        setRoleMap(new HashMap<Role, Boolean>());

        roleList = entityManager.createQuery("select c from Role c").getResultList();

    }

	@DataModel("userList")
    private List<User> entityList;

    @Factory("userList")
    @SuppressWarnings("unchecked")
    public void initUserList() {
        log.debug("User Listesi hazırlanıyor...");

        setEntityList(getEntityManager().createQuery("select c from User c") //.setMaxResults(100)
                //.setHint("org.hibernate.cacheable", true)
                .getResultList());
    }

    @Out(required = false)
    public User getUser() {
        return getEntity();
    }

    @In(required = false)
    public void setUser(User user) {
        setEntity(user);
    }

    @Override
    public void createNew() {
        log.debug("Yeni Kullanıcı");
        entity = new User();
        entity.setActive(true);
    }

    @Override
    public List<User> getEntityList() {
        return entityList;
    }

    public void setEntityList(List<User> entityList) {
        this.entityList = entityList;
    }

    public Map<Role, Boolean> getRoleMap() {
        return roleMap;
    }

    public void setRoleMap(Map<Role, Boolean> roleMap) {
        this.roleMap = roleMap;
    }

    public List<Role> getRoleList() {
        return roleList;
    }

    public void setRoleList(List<Role> roleList) {
        this.roleList = roleList;
    }

    @Override
    public void edit(User e) {
        super.edit(e);
        log.debug("Role Map'ini mıncırıyoz...");
        setRoleMap(new HashMap<Role, Boolean>());
        for (UserRole r : e.getRoles()) {
            roleMap.put(r.getRole(), true);
        }
    }

    @Override
    public String save() {


        //TODO: Hata mesajları dil dosyasına çekilecek.
        try {
            if ( ( entity.getPass() == null || entity.getPass().length() == 0 ) && ( pass1 == null || pass1.length() == 0 ) ) {

                facesMessages.add("Parola girmelisiniz!");
                throw new RuntimeException("Hata var! Parola girmelisiniz!");

            }

            if (pass1 != null && pass1.length() > 0) {
                if (!pass1.equals(pass2)) {
                    //"#{messages['general.message.record.SaveSuccess']}"
                    facesMessages.add("Parolalar uyuşmuyor. Lütfen tekrar deneyiniz!");
                    throw new RuntimeException("Hatalar Var! Parolalar uyuşmuyor, lütfen tekrar deneyiniz! Kayıt gerçekleşmedi!");
                } else {
                    
                    entity.setPass(hash.md5(pass1));
                }
            }
            if (entity.getId() == null) {

                for (User u : getEntityList()) {
                	if (u.getUserName().equalsIgnoreCase(entity.getUserName())) {
                    	facesMessages.add("Bu kullanıcı adı zaten kullanımda, kayıt gerçekleşmedi.");
                        throw new RuntimeException("Hatalar Var! Bu kullanıcı adı zaten kullanımda, kayıt gerçekleşmedi.");
                	}
                }
                
            }
            
	        entity.getRoles().clear();
	
	        for (Role r : roleList) {
	            if (roleMap.containsKey(r)) {
	                if (roleMap.get(r) == true) {
	                    UserRole ur = new UserRole();
	
	                    ur.setUser(entity);
	                    ur.setRole(r);
	                    entity.getRoles().add(ur);
	                }
	            }
	        }
        
        } catch (Exception e) {
            log.error("Hata :", e);
            return BaseConsts.FAIL;
        }

        return super.save();
    }

    public String getPass1() {
        return pass1;
    }

    public void setPass1(String pass1) {
        this.pass1 = pass1;
    }

    public String getPass2() {
        return pass2;
    }

    public void setPass2(String pass2) {
        this.pass2 = pass2;
    }
}
