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

import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import javax.ejb.Remove;
import javax.persistence.EntityManager;

import org.jboss.seam.annotations.Create;
import org.jboss.seam.annotations.Destroy;
import org.jboss.seam.annotations.In;
import org.jboss.seam.annotations.Logger;
import org.jboss.seam.core.Conversation;
import org.jboss.seam.core.Events;
import org.jboss.seam.faces.FacesMessages;
import org.jboss.seam.log.Log;
import org.jboss.seam.security.Identity;

import com.ut.tekir.entities.ControlType;
import com.ut.tekir.entities.Option;
import com.ut.tekir.framework.BaseConsts;
import com.ut.tekir.framework.OptionManager;
import com.ut.tekir.framework.SequenceManager;
import com.ut.tekir.framework.SequenceType;

/**
 * Uygulama seçeneklerinin edit işlemlerini yapar.
 * @author haky
 */
//@Stateful()
//@Name("optionEditor")
//@Scope(value = ScopeType.CONVERSATION)
public class OptionEditorBean implements OptionEditor {
	@Logger
	protected Log log;
    @In
    Identity identity;
    @In
    private Events events;
    @In
    protected EntityManager entityManager;
    @In
    protected FacesMessages facesMessages;
    @In
    protected OptionManager optionManager;
    @In
    protected SequenceManager sequenceManager;
    
    private String userName;
    private List<Option> optionList;
    private Map<String, Option> optionMap;
    private Map<String, Long> sequenceMap;
    
    private byte[] logo;
    private String contentType;
    private String fileName;
    private Integer size;

    private Map<String, Option> userOptionMap = new HashMap<String, Option>();

    @Create
    public void initOptionEditor() {
        initOptionList(identity.getUsername());

        //FIXME: Yeni option yapısına göre burası düzeltilecek.
        //setOptionMap(optionManager.getSystemOption());

        /*
         * TODO:Burdaki yapı düzeltilmeli...
        for ( Option op : optionList ){
        getOptionMap().put( op.getDefinition().getKey(), op );
        }
         */

        setSequenceMap(new HashMap<String, Long>());

        getSequenceMap().put(SequenceType.REFERENCE_FUND_COLLECTION, 0l);
        getSequenceMap().put(SequenceType.REFERENCE_FUND_DEBITCREDIT, 0l);
        getSequenceMap().put(SequenceType.REFERENCE_FUND_PAYMENT, 0l);
        getSequenceMap().put(SequenceType.REFERENCE_FUND_TRANSFER, 0l);
        getSequenceMap().put(SequenceType.REFERENCE_INVOICE_PURCHASE, 0l);
        getSequenceMap().put(SequenceType.REFERENCE_INVOICE_SALE, 0l);
        getSequenceMap().put(SequenceType.REFERENCE_SHIPMENT_PURCHASE, 0l);
        getSequenceMap().put(SequenceType.REFERENCE_SHIPMENT_SALE, 0l);
        getSequenceMap().put(SequenceType.REFERENCE_ORDER_PURCHASE, 0l);
        getSequenceMap().put(SequenceType.REFERENCE_ORDER_SALE, 0l);
        getSequenceMap().put(SequenceType.REFERENCE_SHIPMENT_TRANSFER, 0l);
        getSequenceMap().put(SequenceType.REFERENCE_PRODUCT_VIREMENT, 0l);
        getSequenceMap().put(SequenceType.SERIAL_FUND_COLLECTION, 0l);
        getSequenceMap().put(SequenceType.SERIAL_FUND_DEBITCREDIT, 0l);
        getSequenceMap().put(SequenceType.SERIAL_FUND_PAYMENT, 0l);
        getSequenceMap().put(SequenceType.SERIAL_FUND_TRANSFER, 0l);
        getSequenceMap().put(SequenceType.SERIAL_INVOICE_PURCHASE, 0l);
        getSequenceMap().put(SequenceType.SERIAL_INVOICE_SALE, 0l);
        getSequenceMap().put(SequenceType.SERIAL_SHIPMENT_PURCHASE, 0l);
        getSequenceMap().put(SequenceType.SERIAL_SHIPMENT_SALE, 0l);
        getSequenceMap().put(SequenceType.SERIAL_ORDER_PURCHASE, 0l);
        getSequenceMap().put(SequenceType.SERIAL_ORDER_SALE, 0l);
        getSequenceMap().put(SequenceType.SERIAL_SHIPMENT_TRANSFER, 0l);
        getSequenceMap().put(SequenceType.SERIAL_PRODUCT_VIREMENT, 0l);
        getSequenceMap().put(SequenceType.SERIAL_DEBITCREDITNOTE_COLLECTION, 0l);
        getSequenceMap().put(SequenceType.SERIAL_DEBITCREDITNOTE_PAYMENT, 0l);
        getSequenceMap().put(SequenceType.SERIAL_BANKTO_ACCOUNT_TRANSFER, 0l);
        getSequenceMap().put(SequenceType.SERIAL_ACCOUNT_TO_BANK_TRANSFER, 0l);
        getSequenceMap().put(SequenceType.SERIAL_BANKTO_BANK_TRANSFER, 0l);
        getSequenceMap().put(SequenceType.SERIAL_BANKTO_CONTACT_TRANSFER, 0l);
        getSequenceMap().put(SequenceType.SERIAL_CONTACT_TO_BANK_TRANSFER, 0l);
        getSequenceMap().put(SequenceType.SERIAL_FINANCE_DEPOSITACCOUNT, 0l);
        getSequenceMap().put(SequenceType.SERIAL_BOND_SALE, 0l);
        getSequenceMap().put(SequenceType.SERIAL_BOND_PURCHASE, 0l);
        getSequenceMap().put(SequenceType.SERIAL_PORTFOLIOTO_PORTFOLIO, 0l);
        getSequenceMap().put(SequenceType.SERIAL_CHEQUE_TO_BANK_PAYROLL, 0l);
        getSequenceMap().put(SequenceType.SERIAL_CHEQUE_TO_CONTACT_PAYROLL, 0l);
        getSequenceMap().put(SequenceType.SERIAL_PROMISSORY_TO_BANK_PAYROLL, 0l);
        getSequenceMap().put(SequenceType.SERIAL_PROMISSORY_TO_CONTACT_PAYROLL, 0l);
        getSequenceMap().put(SequenceType.SERIAL_CARDS_CONTACT, 0l);
        getSequenceMap().put(SequenceType.SERIAL_CARDS_STOCK, 0l);
        getSequenceMap().put(SequenceType.SERIAL_CARDS_SERVICE, 0l);
        getSequenceMap().put(SequenceType.SERIAL_CHEQUE_FROM_CONTACT_PAYROLL, 0l);
        getSequenceMap().put(SequenceType.SERIAL_CHEQUE_TO_BANK_ASSURANCE_PAYROLL, 0l);
        getSequenceMap().put(SequenceType.SERIAL_CHEQUE_COLLECTED_AT_BANK_PAYROLL, 0l);
        getSequenceMap().put(SequenceType.SERIAL_CHEQUE_TO_ACCOUNT_COLLECTION_PAYROLL, 0l);
        getSequenceMap().put(SequenceType.SERIAL_CHEQUE_TO_ACCOUNT_PAYMENT_PAYROLL, 0l);
        getSequenceMap().put(SequenceType.SERIAL_PROMISSORY_FROM_CONTACT_PAYROLL, 0l);
        getSequenceMap().put(SequenceType.SERIAL_PROMISSORY_TO_BANK_ASSURANCE_PAYROLL, 0l);
        getSequenceMap().put(SequenceType.SERIAL_PROMISSORY_COLLECTED_AT_BANK_PAYROLL, 0l);
        getSequenceMap().put(SequenceType.SERIAL_PROMISSORY_TO_ACCOUNT_COLLECTION_PAYROLL, 0l);
        getSequenceMap().put(SequenceType.SERIAL_PROMISSORY_TO_ACCOUNT_PAYMENT_PAYROLL, 0l);
        getSequenceMap().put(SequenceType.SERIAL_EXPENSE_NOTE, 0l);

    }

    @SuppressWarnings("unchecked")
	public void initOptionList(String userName) {
        optionList = (List<Option>) entityManager.createQuery("select c from Option c where user = :sysUser or user = :username ").setParameter("sysUser", "SYSTEM").setParameter("username", userName).getResultList();
    }

    public String save() {
        Conversation.instance().changeFlushMode(org.jboss.seam.annotations.FlushModeType.MANUAL);
    	for (Option opt : userOptionMap.values()) {
    		entityManager.merge(opt);
    	}

    	for (Option op : optionMap.values()) {
            entityManager.merge(op);
        }
        entityManager.flush();


        Iterator<String> it = sequenceMap.keySet().iterator();
        while (it.hasNext()) {
            String key = it.next();
            Long o = sequenceMap.get(key);
            if (o > 0) {
                sequenceManager.setCurrenctNumber(key + "." + optionMap.get(key).getValue(), o);
            }
        }

        events.raiseTransactionSuccessEvent("refreshOptions");

        return BaseConsts.SUCCESS;
    }

    public String close() {
        return BaseConsts.SUCCESS;
    }

    /**
     * Karşı taraftan imaj uploadunu yapar...
     * @return success fail
     */
    public String upload() {
        log.debug("dosya türü : #0, dosya adı : #1, dosya size : #2", contentType, fileName, size);
        log.debug("Yüklenen Dosya  : #0", getLogo());

        
        return BaseConsts.SUCCESS;
        
    }

    @SuppressWarnings("unchecked")
    public void prepareUserSpecificOptions() {
        List<Option> userOptionList = (List<Option>) entityManager.createQuery("select c from Option c where " +
        													  "user = :username ")
        													  .setParameter("username", userName)
        													  .getResultList();

        userOptionMap.clear();
        for (Option opt : userOptionList) {
            //FIXME: Yeni option yapısına göre burası düzeltilecek.
            //userOptionMap.put(opt.getDefinition().getKey(), opt);
        }
    }
    
    public void loadCompnayInfo() {
    /*
    Option op = optionMap.get("company.Name");
    companyInfo.setCompanyName( op.getAsString());
    op = optionMap.get("company.TaxOffice");
    companyInfo.setTaxOffice( op.getAsString());
    op = optionMap.get("company.TaxNumber");
    companyInfo.setNu( op.getAsString());
     */
    }

    @Remove
    @Destroy
    public void destroy() {
    }

    public List<Option> getOptionList() {
        return optionList;
    }

    public void setOptionList(List<Option> optionList) {
        this.optionList = optionList;
    }

    public String getUserName() {
        return userName;
    }

    public void setUserName(String userName) {
        this.userName = userName;
    }

    public Map<String, Option> getOptionMap() {
        return optionMap;
    }

    public void setOptionMap(Map<String, Option> optionMap) {
        this.optionMap = optionMap;
    }

    public Map<String, Long> getSequenceMap() {
        return sequenceMap;
    }

    public void setSequenceMap(Map<String, Long> sequenceMap) {
        this.sequenceMap = sequenceMap;
    }

    public String getContentType() {
        return contentType;
    }

    public void setContentType(String contentType) {
        this.contentType = contentType;
    }

    public String getFileName() {
        return fileName;
    }

    public void setFileName(String fileName) {
        this.fileName = fileName;
    }

    public Integer getSize() {
        return size;
    }

    public void setSize(Integer size) {
        this.size = size;
    }

    public byte[] getLogo() {
        return logo;
    }

    public void setLogo(byte[] logo) {
        this.logo = logo;
    }

	public Boolean getCurrencyAlertOption() {
		Boolean result = Boolean.TRUE;
		Option option = null;
		if (optionMap != null) {

			if (optionMap.get("currencyAlertPopup.isVisible") != null) {
				option = optionMap.get("currencyAlertPopup.isVisible");

				result = option.getAsBoolean();
			}
			
		}
		return result;
	}

	public void setCurrencyAlertOption(Boolean currencyAlertOption) {
		Option option = null;
		if (optionMap != null) {

			if (optionMap.get("currencyAlertPopup.isVisible") != null) {
				option = optionMap.get("currencyAlertPopup.isVisible");

				option.setValue(currencyAlertOption.toString());
			}
			
		}
		
	}

	public Boolean getOrganizationSchemeOption() {
		Boolean result = Boolean.TRUE;
		Option option = null;
		if (optionMap != null) {

			if (optionMap.get("organizationSchemeOption.UseScheme") != null) {
				option = optionMap.get("organizationSchemeOption.UseScheme");

				result = option.getAsBoolean();
			}
			
		}
		return result;
	}

	public void setOrganizationSchemeOption(Boolean organizationSchemeOption) {
		Option option = null;
		if (optionMap != null) {

			if (optionMap.get("organizationSchemeOption.UseScheme") != null) {
				option = optionMap.get("organizationSchemeOption.UseScheme");

				option.setValue(organizationSchemeOption.toString());
			}

		}

	}

	public ControlType getStockLevelControl() {
		ControlType result = ControlType.NoControl;
		Option option = null;
		if (optionMap != null) {

			if (optionMap.get("systemSettings.control.StockLevel") != null) {
				option = optionMap.get("systemSettings.control.StockLevel");

				result = option.getAsEnum(ControlType.class);
			}
			
		}
		return result;
	}

	public void setStockLevelControl(ControlType type) {
		Option option = null;
		if (optionMap != null) {
			if (optionMap.get("systemSettings.control.StockLevel") != null) {
				option = optionMap.get("systemSettings.control.StockLevel");

				option.setValue(type.toString());
			}
		}
	
	}

	public ControlType getAccountLevelControl() {
		ControlType result = ControlType.NoControl;
		Option option = null;
		if (optionMap != null) {

			if (optionMap.get("systemSettings.control.AccountLevel") != null) {
				option = optionMap.get("systemSettings.control.AccountLevel");

				result = option.getAsEnum(ControlType.class);
			}
			
		}
		return result;
	}

	public void setAccountLevelControl(ControlType type) {
		Option option = null;
		if (optionMap != null) {
			if (optionMap.get("systemSettings.control.AccountLevel") != null) {
				option = optionMap.get("systemSettings.control.AccountLevel");

				option.setValue(type.toString());
			}
		}
	
	}
	
	public ControlType getContactRiskLimitControl() {
		ControlType result = ControlType.NoControl;
		Option option = null;
		if (optionMap != null) {
			
			if (optionMap.get("systemSettings.control.ContactRiskLimit") != null) {
				option = optionMap.get("systemSettings.control.ContactRiskLimit");
				
				result = option.getAsEnum(ControlType.class);
			}
			
		}
		return result;
	}
	
	public void setContactRiskLimitControl(ControlType type) {
		Option option = null;
		if (optionMap != null) {
			if (optionMap.get("systemSettings.control.ContactRiskLimit") != null) {
				option = optionMap.get("systemSettings.control.ContactRiskLimit");
				
				option.setValue(type.toString());
			}
		}
		
	}
	
	public ControlType getContactCreditLimitControl() {
		ControlType result = ControlType.NoControl;
		Option option = null;
		if (optionMap != null) {
			
			if (optionMap.get("systemSettings.control.ContactCreditLimit") != null) {
				option = optionMap.get("systemSettings.control.ContactCreditLimit");
				
				result = option.getAsEnum(ControlType.class);
			}
			
		}
		return result;
	}
	
	public void setContactCreditLimitControl(ControlType type) {
		Option option = null;
		if (optionMap != null) {
			if (optionMap.get("systemSettings.control.ContactCreditLimit") != null) {
				option = optionMap.get("systemSettings.control.ContactCreditLimit");
				
				option.setValue(type.toString());
			}
		}
		
	}
	
	public ControlType getStockShelfLife() {
		ControlType result = ControlType.NoControl;
		Option option = null;
		if (optionMap != null) {
			
			if (optionMap.get("systemSettings.control.StockShelfLife") != null) {
				option = optionMap.get("systemSettings.control.StockShelfLife");
				
				result = option.getAsEnum(ControlType.class);
			}
			
		}
		return result;
	}
	
	public void setStockShelfLife(ControlType type) {
		Option option = null;
		if (optionMap != null) {
			if (optionMap.get("systemSettings.control.StockShelfLife") != null) {
				option = optionMap.get("systemSettings.control.StockShelfLife");
				
				option.setValue(type.toString());
			}
		}
		
	} 

	public ControlType getPriceChangeControl() {
		ControlType result = ControlType.NoControl;
		Option option = null;
		if (optionMap != null) {
			
			if (optionMap.get("systemSettings.control.PriceChangeControl") != null) {
				option = optionMap.get("systemSettings.control.PriceChangeControl");
				
				result = option.getAsEnum(ControlType.class);
			}
			
		}
		return result;
	}
	
	public void setPriceChangeControl(ControlType type) {
		Option option = null;
		if (optionMap != null) {
			if (optionMap.get("systemSettings.control.PriceChangeControl") != null) {
				option = optionMap.get("systemSettings.control.PriceChangeControl");
				
				option.setValue(type.toString());
			}
		}
		
	}

	public Map<String, Option> getUserOptionMap() {
		return userOptionMap;
	}

	public void setUserOptionMap(Map<String, Option> userOptionMap) {
		this.userOptionMap = userOptionMap;
	}
	
}	
