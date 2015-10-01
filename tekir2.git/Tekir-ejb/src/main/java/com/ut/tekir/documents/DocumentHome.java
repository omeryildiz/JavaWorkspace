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

package com.ut.tekir.documents;


import com.ut.tekir.framework.IEntityBase;
import com.ut.tekir.entities.Contact;
import com.ut.tekir.entities.DocumentFile;
import com.ut.tekir.entities.DocumentFileType;

import javax.ejb.Local;
import org.richfaces.event.UploadEvent;

/**
 *
 * @author haky
 */
@Local
public interface DocumentHome<E> extends IEntityBase<E> {

    void init();

    DocumentFile getDocument();
    void setDocument(DocumentFile document);
    
    
    void upload();
    void closeDocument();
    
    byte[] getFileData();
    void setFileData(byte[] fileData);

    String getContentType();
    void setContentType(String contentType);

    Integer getFileSize();
    void setFileSize(Integer fileSize);
    
    String getFileName();
    void setFileName(String fileName);

    boolean isIsUploaded();
    void setIsUploaded(boolean isUploaded);

    DocumentFileType[] getDocType();
    void fileUploadListener( UploadEvent event );

    void editDocument(DocumentFile doc);
    
    void deleteDocument(Long id);
    
    void downloadDocument(Long id);
    
    Contact getContact();
	void setContact(Contact contact);
	void init(Contact contact);
}
