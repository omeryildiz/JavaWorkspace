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

package com.ut.tekir.entities;

import java.io.Serializable;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.Table;

import org.hibernate.validator.NotNull;

/**
 * Entity class RolePermission
 * @author Hakan Uygun
 */
@Entity
@Table(name="ROLE_PERMISSION")
public class RolePermission implements Serializable{

    private static final long serialVersionUID = 1L;

    @Id
    @GeneratedValue(strategy = GenerationType.TABLE,generator="genericSeq")
    @Column(name="ID")
    private Long id;

    @ManyToOne
    @JoinColumn(name="ROLE_ID")
    private Role role;

    @Column(name="TARGET", nullable=false, length=40)
    @NotNull
    private String target;

    @Column(name="ACTION")
    @NotNull
    private String action;

    public String getAction() {
        return action;
    }

    public void setAction(String action) {
        this.action = action;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public Role getRole() {
        return role;
    }

    public void setRole(Role role) {
        this.role = role;
    }

    public String getTarget() {
        return target;
    }

    public void setTarget(String target) {
        this.target = target;
    }

    @Override
    public boolean equals(Object obj) {
        if (obj == null) {
            return false;
        }
        if (getClass() != obj.getClass()) {
            return false;
        }
        final RolePermission other = (RolePermission) obj;
        if (this.id != other.id && (this.id == null || !this.id.equals(other.id))) {
            return false;
        }
        return true;
    }

    @Override
    public int hashCode() {
        int hash = 5;
        hash = 79 * hash + (this.id != null ? this.id.hashCode() : 0);
        return hash;
    }

    @Override
    public String toString() {
        return "RolePermission{" + "id=" + id + '}';
    }


    //Aslında işlemi yapmak için limit ve başkasına uyarı için limit kavramları olabilir.
    //private BigDecimal actionLimit;
    //private BigDecimal notifyLimit;
    //private Boolean notify;

    //Kayıt seviyesinde kontrol için filter gerekir. Diyelim ki TOP_SECRET kodlu fişleri bu kullanıcı göremesin
    //private String filter;
    //Kullanıcının işlem yapa bilme scopu. Sadece bu bölgedeki kayıtları, tüm kayıtları gibi... Ama bu bilgi role'e de veriliyor olabilir. Bu role'ün scopu şu diye...
    //private String scope



}
