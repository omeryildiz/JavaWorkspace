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

package com.ut.tekir.framework;

import java.util.List;

import javax.ejb.Local;

import org.richfaces.event.UploadEvent;

import com.ut.tekir.entities.Contact;

/**
 * 
 * @author sinan.yumak
 *
 */
@Local
public interface ContactImport {

	void init();
	
	void fileUploadListener(UploadEvent event);

	String save();

	List<Contact> getContactList();

	void destroy();
}