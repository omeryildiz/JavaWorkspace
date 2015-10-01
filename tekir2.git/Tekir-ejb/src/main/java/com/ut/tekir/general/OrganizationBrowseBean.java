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

import java.util.ArrayList;
import java.util.List;

import javax.ejb.Stateful;

import org.hibernate.criterion.DetachedCriteria;
import org.hibernate.criterion.MatchMode;
import org.hibernate.criterion.Projections;
import org.hibernate.criterion.Restrictions;
import org.hibernate.transform.Transformers;
import org.jboss.seam.ScopeType;
import org.jboss.seam.annotations.In;
import org.jboss.seam.annotations.Name;
import org.jboss.seam.annotations.Observer;
import org.jboss.seam.annotations.Scope;

import com.ut.tekir.entities.Organization;
import com.ut.tekir.framework.BrowserBase;

/**
 *
 * @author sinan.yumak
 */
@Stateful
@Name("organizationBrowse")
@Scope(ScopeType.SESSION)
public class OrganizationBrowseBean extends BrowserBase<Organization, OrganizationFilterModel> implements OrganizationBrowse<Organization, OrganizationFilterModel> {
	
	@In
	GeneralSuggestion generalSuggestion;
	
	private Organization mainNode;
	private List<Organization> resultList = new ArrayList<Organization>();
	private int i = 0;
	
	@Override
	public OrganizationFilterModel newFilterModel() {
		OrganizationFilterModel filterModel = new OrganizationFilterModel();
		return filterModel;
	}

	@Override
	public DetachedCriteria buildCriteria() {
        DetachedCriteria crit = DetachedCriteria.forClass(Organization.class);

        crit.setProjection(Projections.projectionList()
                .add(Projections.property("id"), "id")
                .add(Projections.property("code"), "code")
                .add(Projections.property("name"), "name")
                .add(Projections.property("info"), "info"));

        crit.setResultTransformer(Transformers.aliasToBean(Organization.class));

        if (filterModel.getCode() != null && filterModel.getCode().length() > 0) {
            crit.add(Restrictions.ilike("code", filterModel.getCode(), MatchMode.START));
        }

        if (filterModel.getName() != null && filterModel.getName().length() > 0) {
        	crit.add(Restrictions.ilike("name", filterModel.getName(), MatchMode.START));
        }

        if (filterModel.getLevel() != null) {
        	crit.add(Restrictions.eq("level", filterModel.getLevel()));
        }

		return crit;
	}
	
    @Override
	public void search() {
    	if (getFilterModel().getLevel() == null && getFilterModel().getShowHierarchy()) {
    		getFilterModel().setLevel(generalSuggestion.findOrganizationLevel(1));
    	}
    	
    	super.search();

		if (getFilterModel().getShowHierarchy()) {
			i = 0;
			mainNode = new Organization();
			resultList = new ArrayList<Organization>();
			for(Organization org : entityList) {
	
				mainNode.getChildOrganizationList().add(org);
	
			}
			prepareResultsRecursively(mainNode, 0);
			
			//clear the list.
			getEntityList().clear();
			
			//remove main node.
			resultList.remove(0);
			
			//insert new values.
			getEntityList().addAll(resultList);
		}

	}
 
    @SuppressWarnings("unchecked")
    private List<Organization> findChildList(Organization organization) {
    	List<Organization> resultList = new ArrayList<Organization>();
    	try {
    		//lazy initialization problem...
    		if (organization.getId() != null ) {
    			
				resultList = entityManager.createQuery("select o from Organization o where " +
										  "o.parentOrganization.id = :pid")
										  .setParameter("pid",	organization.getId())
										  .getResultList();

    		} else {
    			resultList = organization.getChildOrganizationList();
    		}
    	} catch (Exception e) {

		}
    	return resultList;
    }
    
    private void prepareResultsRecursively(Organization mainOrganization, int index) {

    	for (Organization org : findChildList(mainOrganization)) {
		
    		if (findChildList(mainOrganization).size() != 0) {
    			
    			prepareResultsRecursively(org, i);
    		}
	
    	}
    	resultList.add(index,mainOrganization);
    	i++;
    }
    
	@Observer("refreshBrowser:com.ut.tekir.entities.Organization")
    public void refreshResults() {
        log.debug("Uyarı geldi resultSet tazeleniyor");
        if (getEntityList() == null || getEntityList().isEmpty()) {
            return;
        }
        search();
    }
	
}
