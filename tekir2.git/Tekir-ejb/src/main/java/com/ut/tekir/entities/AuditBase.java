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

import java.util.Date;
import javax.persistence.Column;
import javax.persistence.MappedSuperclass;
import javax.persistence.Temporal;
import javax.persistence.TemporalType;
import org.hibernate.validator.Length;

/**
 * Base class for auditable entities.
 * 
 * @author haky
 */
@MappedSuperclass
public class AuditBase {

    @Column(name="CREATE_DATE")
    @Temporal(value=TemporalType.TIMESTAMP)
    private Date createDate;
    
    @Column(name="UPDATE_DATE")
    @Temporal(value=TemporalType.TIMESTAMP)
    private Date updateDate;
    
    @Column(name="CREATE_USER", length=10)
    @Length(max=10)
    private String createUser;
    
    @Column(name="UPDATE_USER", length=10)
    @Length(max=10)
    private String updateUser;

    public Date getCreateDate() {
        return createDate;
    }

    public void setCreateDate(Date createDate) {
        this.createDate = createDate;
    }

    public Date getUpdateDate() {
        return updateDate;
    }

    public void setUpdateDate(Date updateDate) {
        this.updateDate = updateDate;
    }

    public String getCreateUser() {
        return createUser;
    }

    public void setCreateUser(String createUser) {
        this.createUser = createUser;
    }

    public String getUpdateUser() {
        return updateUser;
    }

    public void setUpdateUser(String updateUser) {
        this.updateUser = updateUser;
    }
    
}
