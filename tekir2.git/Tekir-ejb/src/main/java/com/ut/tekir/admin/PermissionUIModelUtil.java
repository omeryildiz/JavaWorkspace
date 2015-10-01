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

import com.ut.tekir.entities.RolePermission;
import com.ut.tekir.entities.Role;
import com.ut.tekir.permission.ActionConsts;
import com.ut.tekir.permission.PermissionRegistery;
import com.ut.tekir.permission.modal.PermissionDefinition;
import com.ut.tekir.permission.modal.PermissionGroup;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Hak sisteminin GUI'ye uyarlanması için bir ara model yardımcısı.
 *
 * PermissionRegistry'den aldığı PermissionGroup ve PermissionDefinition'larla RolePermission arasında geçiş sağlar.
 * @see PermissionRegistery
 * @see PermissionGroup
 * @see PermissionDefinition
 * @see RolePermission
 *
 * @author Hakan Uygun
 */
public class PermissionUIModelUtil {

    /**
     * Sistemden gelen RolePermission'ların bilgisine bakarak, yeni bir bir GUI modeli hazırlar.
     * @param pgs
     * @param rps
     */
    public static List<PermissionGroupUIModel> getUIModel( List<PermissionGroup> pgs, List<RolePermission> rps){
        List<PermissionGroupUIModel> groups = new ArrayList<PermissionGroupUIModel>();
        Map<String,PermissionUIModel> perms = new HashMap<String, PermissionUIModel>();

        //Önce tanımlardan bütün model oluşturuluyor...
        for( PermissionGroup pg : pgs ){
            PermissionGroupUIModel pgui = new PermissionGroupUIModel();
            pgui.setName(pg.getName());
            for( PermissionDefinition pd : pg.getDefinitions()){
                PermissionUIModel pui = new PermissionUIModel();
                pui.setDefinition(pd);
                pgui.getPermissions().add(pui);
                perms.put(pui.getDefinition().getTarget(), pui);
            }
            groups.add(pgui);
        }

        //Şimdide verilen RolePermission'lardan mevcut haklar setleniyor...
        for( RolePermission rp : rps){
            PermissionUIModel pui = perms.get(rp.getTarget());
            if( ActionConsts.SELECT_ACTION.equals(rp.getAction()) ){
                pui.setSelect(Boolean.TRUE);
            } else if( ActionConsts.INSERT_ACTION.equals(rp.getAction()) ){
                pui.setInsert(Boolean.TRUE);
            } else if( ActionConsts.UPDATE_ACTION.equals(rp.getAction()) ){
                pui.setUpdate(Boolean.TRUE);
            } else if( ActionConsts.DELETE_ACTION.equals(rp.getAction()) ){
                pui.setDelete(Boolean.TRUE);
            } else if( ActionConsts.EXEC_ACTION.equals(rp.getAction()) ){
                pui.setExec(Boolean.TRUE);
            } else {
                pui.setOtherAction( rp.getAction(), Boolean.TRUE);
            }
        }

        return groups;
    }

    /**
     * Verilen UiModel'e göre RolePermission'ları düzenler...
     * @param pgs
     * @param rps
     */
    public static void updateModelFromUI( List<PermissionGroupUIModel> pgs, Role role ){
        //TODO: Aslında hepsini silmek yerine güncellemenin yoluna bakmak lazım...
        for( PermissionGroupUIModel pg : pgs ){
            for( PermissionUIModel pd : pg.getPermissions()){
                for( String a : pd.getDefinition().getActions()){
                    if( pd.hasPermission(a)){
                    	if ( ! role.permissionsContains(pd.getDefinition().getTarget(), a) ) {
                    		RolePermission p = new RolePermission();
                    		p.setRole(role);
                    		p.setTarget(pd.getDefinition().getTarget());
                    		p.setAction(a);
                    		role.getPermissions().add(p);
                    	}
                    } else {
                    	role.removePermission(pd.getDefinition().getTarget(), a);
                    }
                }
            }
        }
    }
    
}
