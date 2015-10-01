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

import com.ut.tekir.entities.ControlType;
import com.ut.tekir.entities.Option;
import java.util.List;
import java.util.Map;
import javax.ejb.Local;

/**
 *
 * @author haky
 */
@Local
public interface OptionEditor {

    void initOptionEditor();
    void initOptionList(String userName);
    String save();
    String close();
    void destroy();

    List<Option> getOptionList();
    void setOptionList(List<Option> optionList);

    Map<String, Option> getOptionMap();
    void setOptionMap(Map<String, Option> optionMap);

    Map<String, Long> getSequenceMap();
    void setSequenceMap(Map<String, Long> sequenceMap);
    
    String getUserName();
    void setUserName(String userName);
    
    String upload();
    
    void setFileName(String fileName);
    String getFileName();
    
    Integer getSize();
    void setSize(Integer size);
    
    byte[] getLogo();
    void setLogo(byte[] logo);
    
    String getContentType();
    void setContentType(String contentType);
    
	Boolean getCurrencyAlertOption();
	
	void setCurrencyAlertOption(Boolean currencyAlertOption);
	
	Boolean getOrganizationSchemeOption();
	
	void setOrganizationSchemeOption(Boolean organizationSchemeOption);

	ControlType getStockLevelControl();
	
	void setStockLevelControl(ControlType type);	
	
	ControlType getAccountLevelControl();
	
	void setAccountLevelControl(ControlType type);
	
	ControlType getContactRiskLimitControl();
	
	void setContactRiskLimitControl(ControlType type);
	
	ControlType getContactCreditLimitControl();
	
	void setContactCreditLimitControl(ControlType type);
	
	ControlType getStockShelfLife();
	
	void setStockShelfLife(ControlType type);
	
	ControlType getPriceChangeControl();
	
	void setPriceChangeControl(ControlType type);
	
	Map<String, Option> getUserOptionMap();
	
	void setUserOptionMap(Map<String, Option> userOptionMap);

	void prepareUserSpecificOptions();
}
