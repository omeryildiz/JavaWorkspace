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

package com.ut.tekir.general;

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

import com.ut.tekir.entities.Category;
import com.ut.tekir.entities.CategoryItem;
import com.ut.tekir.entities.WorkBunch;
import com.ut.tekir.framework.BaseConsts;
import com.ut.tekir.framework.EntityHome;
import com.ut.tekir.util.CategoryLoader;
import com.ut.tekir.util.CategoryPathUtil;

/**
 * @author sinan.yumak
 *
 */
@Stateful
@Name("followUpHome")
@Scope(value=ScopeType.CONVERSATION)
public class FollowUpHomeBean extends EntityHome<WorkBunch> implements FollowUpHome<WorkBunch> {

	@DataModel("followUpList")
    private List<CategoryItem> itemList;
    
	@Create
	@Begin(join=true,flushMode=FlushModeType.MANUAL)
	public void init() {
	}
/*
	@Factory("followUpList")
    public void initFollowUpList() {	//OK
		setItemList(CategoryLoader.instance().load(getEntityClass()));
    }
*/	
    @Override
	public void createNew() { //OK
//    	entity = new WorkBunch();
//        entity.setActive(true);
//        entity.setWeight(10);
    	WorkBunch fu = new WorkBunch();
    	fu.setActive(true);
    	setEntity(fu);
	}

	@Out(required=false)
    public WorkBunch getWorkBunch() {
        return getEntity();
    }

    @In(required=false)
    public void setFollowUp(WorkBunch workBunch) {
        setEntity( workBunch );
    }
    
	private void manualFlush() {
    	Conversation.instance().changeFlushMode(org.jboss.seam.annotations.FlushModeType.MANUAL);
    }
/*    
	private void setCategoryPaths() {
		log.info("Setting category paths...");
		
		CategoryPathUtil.instance().setCategoryPath(getEntity());

		for (Category cat : entity.getChildList()) {
			
			CategoryPathUtil.instance().setCategoryPath(cat);
			log.info("Category code #0, path #1", cat.getId(), cat.getPath());
		}
	}
*/
    @Override
	public String save() {	//OK
		manualFlush();
		
		String result = super.save();
//		setCategoryPaths();

		entityManager.merge(getEntity());
		entityManager.flush();
		
//		refreshList();
		return result;
	}

	@SuppressWarnings("unchecked")
	@Override
	public String delete() {

        if (entity == null) {
            return BaseConsts.FAIL;
        }

        try {
            entityManager.remove(entity);
            entityManager.flush();
        } catch (Exception e) {
            log.debug("Hata : #0", e);
            facesMessages.add("#{messages['general.message.record.DeleteFaild']}");
            return BaseConsts.FAIL;
        }


        List ls = getItemList();
        if (ls != null) {
            log.debug("Listeden çıkartılıyor : #0", ls);
            ls.remove(entity);
        }

        log.debug("Entity Removed : {0} ", entity);
        entity = null;
        facesMessages.add("#{messages['general.message.record.DeleteSuccess']}");

//        refreshList();

        return BaseConsts.SUCCESS;
		
	}
/*
	private void refreshList() {	//OK
		if (entity.getParent() != null) entityManager.refresh(entity.getParent());
		initFollowUpList();
	}
	
    public void createIdenticalCat() {	//OK
    	setEntity(new WorkBunch(getEntity().getParent()));
    }

    public void createNewLine() {	//OK
    	manualFlush();
    	log.info("Creating new line...");
    	getEntity().addChild();
    }
*/
    public void createSubCat() {	//OK

//    	manualFlush();
    	
    	/*
    	 * alt kategori, bir ust kategorinin carisine sahip olmali.
    	 */
//    	WorkBunch fu = new WorkBunch(getEntity());
//    	fu.setContact(getEntity().getContact());
//    	setEntity(fu);
    }

	public void deleteLine(int rowKey) {
    	manualFlush();

    	log.info("Deleting line.. rowKey: #0", rowKey);
//    	getEntity().getChildList().remove(rowKey);
    }
/*
	public String getCodePaths(WorkBunch fu) {
		return CategoryPathUtil.instance().generateCodePath(fu);
	}
*/
    public List<CategoryItem> getItemList() {
		return itemList;
	}

	public void setItemList(List<CategoryItem> itemList) {
		this.itemList = itemList;
	}
    
}
