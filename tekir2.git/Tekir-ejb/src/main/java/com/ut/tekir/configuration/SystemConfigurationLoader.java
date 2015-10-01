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

package com.ut.tekir.configuration;

import java.io.Serializable;

import org.jboss.seam.ScopeType;
import org.jboss.seam.annotations.Create;
import org.jboss.seam.annotations.In;
import org.jboss.seam.annotations.Logger;
import org.jboss.seam.annotations.Name;
import org.jboss.seam.annotations.Scope;
import org.jboss.seam.annotations.Startup;
import org.jboss.seam.log.Log;

import com.ut.tekir.entities.Option;
import com.ut.tekir.framework.OptionManager;
import com.ut.tekir.options.OptionKey;
import com.ut.tekir.options.SystemOptionKey;

/**
 * @author sinan.yumak
 *
 */
@Name("systemConfigurationLoader")
@Scope(ScopeType.APPLICATION)
@Startup
public class SystemConfigurationLoader implements Serializable {
	private static final long serialVersionUID = 1L;

	@Logger
    private Log log;
	@In(create=true)
	OptionManager optionManager;
	
	@Create
	public void init() {
		initConfiguration();
	}

	private void initConfiguration() {
		log.debug("Initializing configuration...");
		SystemConfiguration.setCountryCode( getOption(SystemOptionKey.COUNTRY_CODE).getAsString() );
		SystemConfiguration.setCountryName( getOption(SystemOptionKey.COUNTRY_NAME).getAsString() );
		SystemConfiguration.setCountryShortName( getOption(SystemOptionKey.COUNTRY_SHORTNAME).getAsString() );
		SystemConfiguration.setCurrencyCode( getOption(SystemOptionKey.CURRENCY_CODE).getAsString() );
		SystemConfiguration.setCurrencydecCode( getOption(SystemOptionKey.CURRENCYDEC_CODE).getAsString() );
		SystemConfiguration.setLocale( getOption(SystemOptionKey.LOCALE).getAsString() );
	}

	private Option getOption(OptionKey key) {
		return optionManager.getOption(key, true);
	}
	
}
