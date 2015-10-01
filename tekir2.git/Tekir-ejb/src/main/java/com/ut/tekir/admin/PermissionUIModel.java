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

import com.ut.tekir.permission.ActionConsts;
import com.ut.tekir.permission.modal.PermissionDefinition;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Hakların GUI için uygun hale getirilmiş modeli
 * @author Hakan Uygun
 */
public class PermissionUIModel implements Serializable {

    private PermissionDefinition definition;
    private Boolean select = Boolean.FALSE;
    private Boolean insert = Boolean.FALSE;
    private Boolean update = Boolean.FALSE;
    private Boolean delete = Boolean.FALSE;
    private Boolean exec = Boolean.FALSE;
    private Boolean other = Boolean.FALSE;
    private Map<String, Boolean> otherActions = new HashMap<String, Boolean>();

    public PermissionDefinition getDefinition() {
        return definition;
    }

    public void setDefinition(PermissionDefinition definition) {
        this.definition = definition;
        buildOtherActions();
    }

    public Boolean getDelete() {
        return delete;
    }

    public void setDelete(Boolean delete) {
        this.delete = delete;
    }

    public Boolean getExec() {
        return exec;
    }

    public void setExec(Boolean exec) {
        this.exec = exec;
    }

    public Boolean getInsert() {
        return insert;
    }

    public void setInsert(Boolean insert) {
        this.insert = insert;
    }

    public Boolean getOther() {
        return other;
    }

    public void setOther(Boolean other) {
        this.other = other;
    }

    public Boolean getSelect() {
        return select;
    }

    public void setSelect(Boolean select) {
        this.select = select;
    }

    public Boolean getUpdate() {
        return update;
    }

    public void setUpdate(Boolean update) {
        this.update = update;
    }

    public Boolean hasPermission(String action) {
        if (ActionConsts.SELECT_ACTION.equals(action)) {
            return select;
        } else if (ActionConsts.INSERT_ACTION.equals(action)) {
            return insert;
        } else if (ActionConsts.UPDATE_ACTION.equals(action)) {
            return update;
        } else if (ActionConsts.DELETE_ACTION.equals(action)) {
            return delete;
        } else if (ActionConsts.EXEC_ACTION.equals(action)) {
            return exec;
        } else {
            if( otherActions.containsKey(action) ){
                return otherActions.get(action);
            }
            return false;
        }
    }

    /**
     * Standart hakların dışında kalanlar için bir map oluşturur.
     */
    private void buildOtherActions() {
        for (String s : definition.getActions()) {
            if (!ActionConsts.SELECT_ACTION.equals(s)
                    && !ActionConsts.INSERT_ACTION.equals(s)
                    && !ActionConsts.UPDATE_ACTION.equals(s)
                    && !ActionConsts.DELETE_ACTION.equals(s)
                    && !ActionConsts.EXEC_ACTION.equals(s)) {
                otherActions.put(s, Boolean.FALSE);
            }
        }
    }

    public Map<String, Boolean> getOtherActions() {
        return otherActions;
    }

    public void setOtherActions(Map<String, Boolean> otherActions) {
        this.otherActions = otherActions;
    }

    public void setOtherAction( String action, Boolean val){
        otherActions.put(action, val);
    }

    public Boolean getOtherAction( String action){
        return otherActions.containsKey(action) ? otherActions.get(action) : false;
    }

    public List<String> otherActionList(){
        return new ArrayList<String>(otherActions.keySet());
    }
}
