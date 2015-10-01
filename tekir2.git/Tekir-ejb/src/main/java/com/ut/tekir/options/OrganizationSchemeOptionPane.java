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

package com.ut.tekir.options;

import java.util.ArrayList;
import java.util.List;

import javax.persistence.EntityManager;

import org.jboss.seam.ScopeType;
import org.jboss.seam.annotations.AutoCreate;
import org.jboss.seam.annotations.Create;
import org.jboss.seam.annotations.In;
import org.jboss.seam.annotations.Logger;
import org.jboss.seam.annotations.Name;
import org.jboss.seam.annotations.Scope;
import org.jboss.seam.annotations.Transactional;
import org.jboss.seam.core.Events;
import org.jboss.seam.faces.FacesMessages;
import org.jboss.seam.log.Log;

import com.ut.tekir.annotations.OptionPane;
import com.ut.tekir.entities.Option;
import com.ut.tekir.entities.OrganizationLevel;
import com.ut.tekir.framework.OptionManager;
import com.ut.tekir.util.Utils;

/**
 *
 *  Organization Scheme Option editor
 * 
 * @author sinan.yumak
 */
@Name("organizationSchemeOptionPane")
@OptionPane(OptionPaneType.System)
@Scope(ScopeType.CONVERSATION)
@AutoCreate
public class OrganizationSchemeOptionPane extends AbstractOptionPane {
    @Logger
    Log log;
    @In(create=true)
    OptionManager optionManager;
    @In
    EntityManager entityManager;
    @In
    FacesMessages facesMessages;
    @In
    Events events;

    private List<OrganizationLevel> levelList = new ArrayList<OrganizationLevel>();

    private List<Option> optionList = new ArrayList<Option>();

    @Create
    public void init() {
    	prepareLevelList();
    }
    
    @SuppressWarnings("unchecked")
	private void prepareLevelList() {
    	try {
			levelList = entityManager.createQuery("select ol from OrganizationLevel ol " +
												  "order by ol.level asc")
												  .getResultList();
		} catch (Exception e) {
			log.error("Hata :#0", e);
		}
	}

    public Option getOption(OrganizationSchemeOptionKey key) {
    	return optionManager.getOption( key );
    }
    
    public List<Option> getOrganizationOptionList() {
    	if (optionList.size() == 0) {
    		for (OrganizationSchemeOptionKey key : OrganizationSchemeOptionKey.values()) {
    			if (key.name().endsWith("_LEVEL")) {
    				optionList.add( optionManager.getOption(key) );
    			}
    		}
    	}
    	return optionList;
    }
    
    /**
	 * When scheme checkbox is selected, checks if default organization has been 
	 * added to list or not. If not, creates a default(Merkez) organization level.
	 */
	public void checkDefaultOrganization() {
		if (levelList.size() == 0) {
			OrganizationLevel level = new OrganizationLevel();
			level.setName("Merkez");
			level.setLevel(1);
			levelList.add(level);
		}
	}

	public void addNewOrganizationLevel() {
//		manualFlush();
		OrganizationLevel level =  new OrganizationLevel();

		levelList.add(level);
		
		updateLevels();
	}

	private void updateLevels() {
//		manualFlush();
		for (int i = 1; i < levelList.size() + 1; i++) {
			levelList.get(i - 1).setLevel(i);
		}
	}

	public void deleteOrganizationLevel(int rowKey) {
//		manualFlush();

		OrganizationLevel level = levelList.get(rowKey);
		
		entityManager.remove(level);
		
		levelList.remove(rowKey);

		updateLevels();
	}

    @Transactional
    public void save(){
    	try {
			for (OrganizationLevel level : levelList) {
				if (level.getId() == null) {
					entityManager.persist(level);
				} else {
					entityManager.merge(level);
				}
			}
			for (OrganizationSchemeOptionKey key : OrganizationSchemeOptionKey.values()) {
				optionManager.updateOption(key);
			}
			entityManager.flush();
			facesMessages.add("#{messages['organizationSchemaOptionPane.message.SchemeSavedSuccesfully']}");
		} catch (Exception e) {
			facesMessages.add(Utils.getExceptionMessage(e));
			log.error("Organizasyon seviyeleri kaydedilirken hata meydana geldi. Sebebi #0", e);
		}
    }

	public List<OrganizationLevel> getLevelList() {
		return levelList;
	}

	public void setLevelList(List<OrganizationLevel> levelList) {
		this.levelList = levelList;
	}

    public String getName() {
        return "organizationSchemeOptionPane";
    }

    public void cancel() {
        //TODO: editten önceki hale dönmeli...
    }

}
