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
import org.jboss.seam.annotations.In;
import org.jboss.seam.annotations.Name;
import org.jboss.seam.annotations.Out;
import org.jboss.seam.annotations.Scope;
import org.jboss.seam.core.Conversation;

import com.ut.tekir.entities.Organization;
import com.ut.tekir.entities.OrganizationLevel;
import com.ut.tekir.framework.EntityBase;

/**
 *
 * @author sinan.yumak
 */
@Stateful
@Name("organizationHome")
@Scope(value=ScopeType.CONVERSATION)
public class OrganizationHomeBean extends EntityBase<Organization> implements OrganizationHome<Organization> {

	private Boolean showOrganizationCombobox = Boolean.TRUE;

	private OrganizationLevel level = null;
	private Organization parentOrganization = null;
	
	@Create 
	@Begin(join=true,flushMode = org.jboss.seam.annotations.FlushModeType.MANUAL)
	public void init() {
	}

	@Out(required=false)
    public Organization getOrganization() {
        return getEntity();
    }

    @In(required=false)
    public void setOrganization(Organization organization) {
        setEntity(organization);
    }
    
    @Override
    public void createNew(){
        entity = new Organization();
    }
    
    @Override
	public String save() {
    	entity.setLevel(getLevel());
    	
		return super.save();
	}
    
	@Override
	public void setId(Long id) {
        if (entity != null) {
            return;
        } 
        
		super.setId(id);

		setLevel(entity.getLevel());
	}

	public Organization createNewChildOrganization() {
    	manualFlush();

    	Organization parentOrganization = findOrganizationWithId(getEntity().getId());
		
    	Organization childOrganization = null;
    	if (parentOrganization != null) {
    		
    		childOrganization = new Organization();
    		
    		childOrganization.setParentOrganization(parentOrganization);

    		parentOrganization.setHasChild(Boolean.TRUE);

    		entity = childOrganization;
    		
    		setLevel(findOrganizationLevelWithLevel(parentOrganization.getLevel().getLevel() + 1));
    		
    		entity.setLevel(getLevel());
    	}
    	
    	return childOrganization;
    }
    
    private Organization findOrganizationWithId(Long id) {
    	Organization region = null;
    	try {
    		region = (Organization)entityManager.createQuery("select r from Organization r where r.id =:id")
									      .setParameter("id", id)
									      .getSingleResult();
    		
		} catch (Exception e) {
			log.error("Hata :", e);
		}
		return region;
    }

    /**
     * Returns one level upper organization elements by
     * comparing levels.
     */
    @SuppressWarnings("unchecked")
    public List<Organization> findParentList() { 
    	List<Organization> parentList = null;
    	if (getLevel() != null) {
    		try {
    			parentList = entityManager.createQuery("select o from Organization o where " +
    										           "o.level.level = :level")
    										           .setParameter("level", getLevel().getLevel() - 1)
    										           .getResultList();
				
			} catch (Exception e) {
				log.error("Hata :", e);
			}
    	}
    	return parentList;
    }

    private OrganizationLevel findOrganizationLevelWithLevel(Integer lvl) {
    	OrganizationLevel level = null;
    	try {
    		level = (OrganizationLevel)entityManager.createQuery("select o from OrganizationLevel o where " +
										      "o.level =:level")
										      .setParameter("level", lvl)
										      .getSingleResult();
		} catch (Exception e) {
			log.error("Hata :", e);
		}
		return level;
    }
    
    /**
     * Returns the top level of the organization levels.
     * @return topLevel
     */
    @SuppressWarnings("unchecked")
    private OrganizationLevel findTopOrganizationLevel() {
    	OrganizationLevel topLevel = null;
    	try {
    		topLevel = (OrganizationLevel)entityManager.createQuery("select o from OrganizationLevel o where " +
										      "o.level =:level")
										      .setParameter("level", 1)
										      .getSingleResult();
		} catch (Exception e) {
			log.error("Hata :", e);
		}
		return topLevel;
    }
    
    public void manualFlush() {
        Conversation.instance().changeFlushMode(org.jboss.seam.annotations.FlushModeType.MANUAL);
    }

	public Boolean getShowOrganizationCombobox() {
		if (entity != null && entity.getLevel() != null && 
				entity.getLevel().getLevel().compareTo(1) == 0) {
			
			showOrganizationCombobox = Boolean.FALSE;
		}
		System.out.println("deger =" + showOrganizationCombobox);
		return showOrganizationCombobox;
	}

	public void setShowOrganizationCombobox(Boolean showOrganizationCombobox) {
		this.showOrganizationCombobox = showOrganizationCombobox;
	}

	public OrganizationLevel getLevel() {
		return level;
	}

	public void setLevel(OrganizationLevel level) {
		this.level = level;
	}

	public Organization getParentOrganization() {
		return parentOrganization;
	}

	public void setParentOrganization(Organization parentOrganization) {
		this.parentOrganization = parentOrganization;
	}
}
