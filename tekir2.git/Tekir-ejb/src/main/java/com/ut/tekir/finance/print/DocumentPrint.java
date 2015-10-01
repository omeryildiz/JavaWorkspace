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

package com.ut.tekir.finance.print;

import java.io.Serializable;
import java.util.Map;

import javax.persistence.EntityManager;

import org.jboss.seam.Component;
import org.jboss.seam.log.Log;
import org.jboss.seam.log.Logging;

import com.ut.tekir.framework.JasperHandlerBean;
import com.ut.tekir.framework.OptionManager;
import com.ut.tekir.framework.SystemProperties;

/**
 * @author sinan.yumak
 *
 */
public abstract class DocumentPrint implements Serializable {
	private static final long serialVersionUID = 1L;

	private Log log;
	private JasperHandlerBean jasperReport;
    private OptionManager optionManager;
    private SystemProperties systemProperties;
    private EntityManager entityManager;
    
	public JasperHandlerBean getJasperReport() {
		if (jasperReport == null) {
			jasperReport = (JasperHandlerBean)Component.getInstance("jasperReport");
		}
		return jasperReport;
	}

	public OptionManager getOptionManager() {
		if (optionManager == null) {
			optionManager = (OptionManager)Component.getInstance("optionManager");
		}
		return optionManager;
	}

	public SystemProperties getSystemProperties() {
		if (systemProperties == null) {
			systemProperties = (SystemProperties)Component.getInstance("systemProperties");
		}
		return systemProperties;
	}

	public EntityManager getEntityManager() {
		if (entityManager == null) {
			entityManager = (EntityManager)Component.getInstance("entityManager");
		}
		return entityManager;
	}

	public Log getLog() {
		if (log == null) {
			log = Logging.getLog(DocumentPrint.class);
		}
		return log;
	}

	public void print() throws Exception {}
	public void print(String templateName) throws Exception{};
	public abstract Map<Object, Object> getParams();
}
