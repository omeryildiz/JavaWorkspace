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

package com.ut.tekir.util;

import java.util.ArrayList;
import java.util.List;

import javax.persistence.EntityManager;

import org.jboss.seam.Component;
import org.jboss.seam.log.Log;
import org.jboss.seam.log.Logging;

import com.ut.tekir.entities.Category;
import com.ut.tekir.entities.CategoryItem;

/**
 * @author sinan.yumak
 *
 */
public class CategoryLoader {
	private Log log = Logging.getLog(CategoryLoader.class);
	
	private EntityManager entityManager;
	private List<CategoryItem> results;
	
	public static CategoryLoader instance() {
		return new CategoryLoader();
	}
	
	public <T extends Category> List<CategoryItem> load(Class<T> clazz) {
		iterateCats( getParents(clazz) );
		return getResults();
	}		

	@SuppressWarnings("unchecked")
	private <T extends Category> List<Category> getParents(Class<T> clazz) {
		//parentleri olmayan kayıtlar parent kayıtlar.
		try {
			return (List<Category>)getEntityManager().createQuery("select c from "+getEntityName(clazz)+" c where "+
																  "c.parent.id is null")
																  .getResultList();
		} catch (Exception e) {
			log.error("Error while fetching parents. Reason #0", e);
		}
		return new ArrayList<Category>();
	}
	
	private void iterateCats(List<Category> parents) {
		for (Category ch : parents) {
			iterateCat(ch);
		}
	}

	private void iterateCat(Category cat) {
		add(cat);
		if (cat.getChildList() != null) {
			for (Object ch : cat.getChildList()) {
				iterateCat((Category)ch);
			}
		}
	}
	
	private <T extends Category> String getEntityName(Class<T> entityClass) {
		return entityClass.getSimpleName();
	}
	
	private EntityManager getEntityManager() {
		if (entityManager == null || !entityManager.isOpen()) {
			entityManager = (EntityManager)Component.getInstance("entityManager",true);
		}
		return entityManager;
	}

	private void add(Category cat) {
		getResults().add(new CategoryItem(cat, getResults().size(), parentIndex(cat.getParent())));
	}
	
	private int parentIndex(Category cat) {
		for(int i=0;i<getResults().size();i++) {
			if (getResults().get(i).equals(cat)) return i;
		}
		return -1;
	}

	private List<CategoryItem> getResults() {
		if (results == null) {
			results = new ArrayList<CategoryItem>();
		}
		return results;
	}

}

