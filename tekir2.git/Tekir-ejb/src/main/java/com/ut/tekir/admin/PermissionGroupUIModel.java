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

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

/**
 * Hak gruplarının GUI için uygun hale gerilmiş hali
 * @author Hakan Uygun
 */
public class PermissionGroupUIModel implements Serializable{

    private String name;
    private List<PermissionUIModel> permissions = new ArrayList<PermissionUIModel>();

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public List<PermissionUIModel> getPermissions() {
        return permissions;
    }

    public void setPermissions(List<PermissionUIModel> permissions) {
        this.permissions = permissions;
    }


}
