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

package com.ut.tekir.permission.modal;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

/**
 * PemissionGrouplarını tutar.
 * @author Hakan Uygun
 */
public class PermissionGroup implements Serializable{

    private String name;
    private List<PermissionDefinition> definitions = new ArrayList<PermissionDefinition>();

    public List<PermissionDefinition> getDefinitions() {
        return definitions;
    }

    public void setDefinitions(List<PermissionDefinition> definitions) {
        this.definitions = definitions;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    @Override
    public boolean equals(Object obj) {
        if (obj == null) {
            return false;
        }
        if (getClass() != obj.getClass()) {
            return false;
        }
        final PermissionGroup other = (PermissionGroup) obj;
        if ((this.name == null) ? (other.name != null) : !this.name.equals(other.name)) {
            return false;
        }
        return true;
    }

    @Override
    public int hashCode() {
        int hash = 7;
        hash = 41 * hash + (this.name != null ? this.name.hashCode() : 0);
        return hash;
    }


}
