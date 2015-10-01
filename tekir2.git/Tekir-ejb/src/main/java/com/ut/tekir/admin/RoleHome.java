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

import com.ut.tekir.entities.Role;
import com.ut.tekir.framework.IEntityHome;
import com.ut.tekir.permission.modal.PermissionGroup;
import java.util.List;
import javax.ejb.Local;

/**
 * Role Tanımlama EJB interface'i
 * @author Hakan Uygun
 */
@Local
public interface RoleHome<E> extends IEntityHome<E>{
    public void initRoleHome();
    public void initRoleList();

    public Role getRole();
    public void setRole(Role role);

    public List<PermissionGroup> getPermissionGroups();
    public List<PermissionGroupUIModel> getUiModel();
    public void setUiModel(List<PermissionGroupUIModel> uiModel);
}
