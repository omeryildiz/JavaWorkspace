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

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.Calendar;
import java.util.Date;

import javax.ejb.Stateful;
import javax.faces.context.FacesContext;
import javax.servlet.http.HttpServletResponse;

import org.jboss.seam.ScopeType;
import org.jboss.seam.annotations.AutoCreate;
import org.jboss.seam.annotations.Begin;
import org.jboss.seam.annotations.Create;
import org.jboss.seam.annotations.In;
import org.jboss.seam.annotations.Name;
import org.jboss.seam.annotations.Out;
import org.jboss.seam.annotations.Scope;
import org.jboss.seam.core.Conversation;
import org.richfaces.event.UploadEvent;
import org.richfaces.model.UploadItem;

import com.ut.tekir.entities.Contact;
import com.ut.tekir.entities.DocumentFile;
import com.ut.tekir.entities.DocumentFileType;
import com.ut.tekir.entities.OwnerType;
import com.ut.tekir.entities.User;
import com.ut.tekir.framework.BaseConsts;
import com.ut.tekir.framework.CalendarManager;
import com.ut.tekir.framework.EntityBase;
import com.ut.tekir.framework.SequenceManager;
import com.ut.tekir.framework.SystemProperties;

//FIXME: popupta tamama basıldığında her halükarda birşeyler ekliyor!
//Düzeltilmeli.

/**
 *
 * @author sozmen
 */
@Stateful
@Name("documentHome")
@Scope(value = ScopeType.CONVERSATION)
@AutoCreate
public class DocumentHomeBean extends EntityBase<DocumentFile> implements DocumentHome<DocumentFile> {

    private byte[] fileData;
    private String contentType;
    private Integer fileSize;
    private String fileName;
    @In
    private FacesContext facesContext;
    @In
    private CalendarManager calendarManager;
    @In
    private SequenceManager sequenceManager ;
    @In
    SystemProperties systemProperties;
    
    private DocumentFile document;
    private Contact contact;//döküman eklenecek cari.
    
	private boolean isUploaded=false;

	@In
    private User activeUser;
    
    @Create
    @Begin(join=true,flushMode = org.jboss.seam.annotations.FlushModeType.MANUAL)
    public void init() {
        log.debug("DocumentHome Init");
    }
        
    public void init(Contact contact) {
    	this.contact = contact;
    	newContactDocument();
    }
    
    private void newContactDocument() {
    	createNew();
    	document.setOwnerType(OwnerType.Contact);
    }
    
    @Override
    public void createNew() {
        log.debug("Yeni Document");
        document = new DocumentFile();
        document.setUpdateDate(calendarManager.getCurrentDate());
        document.setActive(true);
        document.setUser(activeUser.getUserName());
    //entity.setType( ContactType.Both );
    }

    @Out(required = false)
    public DocumentFile getDocument() {
        return document;
    }
    
    @In(required = false)
    public void setDocument(DocumentFile document) {
        this.document = document;
    }

    public void upload() {

        log.info("Dosya Yüklemesi OK", fileName);
        
        document.setName(fileName);
        document.setFormat(contentType);
        document.setFileSize(fileSize);
        document.setUser(activeUser.getUserName());
        if(fileData == null){
            log.info("Dosya bos");
        }

        isUploaded=true;
    }

    public void closeDocument() {
        setDocument(null);
    }

    @Override
    public String save() {

        super.save();
        return BaseConsts.SUCCESS;
    }
    public void editDocument(DocumentFile doc) {
        this.document = doc;
    }

    public void fileUploadListener( UploadEvent event ){
        try {
            manualFlush();
            UploadItem item = event.getUploadItem();

            log.info("Data #0, File #1, TempFile #2", item.getData(), item.getFile(), item.isTempFile());

            if( item != null ){
                fileData = item.getData();
                document.setFileSize(item.getFileSize());
                document.setFormat(item.getContentType());
                document.setName(item.getFileName());
                document.setUpdateDate(new Date());
                document.setUser(activeUser.getUserName());

                
                //fileName = sequenceManager.getNewNumber("DOC", 6) + ".trio";
                
                document.setUrl(sequenceManager.getNewNumber("DOC", 6) + ".tkr");

                log.info("DOC NEWFILENAME #0", document.getUrl());
                log.info("DOC ID = #0", document.getId());
                
                storeDocument(document.getUrl(), item.getFile());
                log.info("#DOC stored document with url;" + document.getUrl());

                entityManager.merge(document);
                entityManager.flush();
                
                addToContactDocuments();
                log.info("#DOC stored document with url;" + document.getUrl());
            }
        } catch (Exception e) {
            log.info(e);
        }
    }
    
    private void addToContactDocuments() {
    	getContact().getDocumentFiles().add(getDocument());
    	getDocument().setContact(getContact());
    }
    
    public void manualFlush() {
		Conversation.instance().changeFlushMode(org.jboss.seam.annotations.FlushModeType.MANUAL);
	}


    public void storeDocument(String fileName, File file) {


        FileOutputStream os = null;
        FileInputStream is = null;

        try {
            log.info("File Store Path #" + fileName);
            //TODO: Gerçek dosya adı numaratörden alınacak
            //os = new FileOutputStream(servletContext.getRealPath("/") + "/files/" + fileName);
            os = new FileOutputStream(getStorePath() + "/" + fileName);
            log.info("File store full path #" + os);
            is = new FileInputStream( file );

            byte[] buf = new byte[1000];

            int i = is.read(buf);
            while( i > 0 ){
                os.write(buf, 0, i);
                i = is.read(buf);
            }

            log.info("filestored");

        } catch (FileNotFoundException ex) {
            log.error("FileStore not found", ex);
        } catch (IOException ex) {
            log.error("File cannot stored", ex);
        } finally {
            try {
                os.close();
            } catch (IOException ex) {
                log.error("File cannot stored", ex);
            }
        }
    }
    
    /**
     * Verilen id'li dosyayı HTTP üzerinden istemciye gönderir.
     *
     * @param id
     */
    public void downloadDocument(Long id) {
    	log.info("Document id: #0", id);
        InputStream is = null;

        try {
            if (id == null) {
                log.info("File #0 not found", id);
                facesMessages.add("Error while downloading the file: " + id);
                return;
            }

            document = entityManager.find(DocumentFile.class, id);

            if (document == null) {
                log.info("File #0 not found", id);
                facesMessages.add("Error while downloading the file: " + id);
                return;
            }
            is = new FileInputStream(getStorePath() + "/" + document.getUrl());
            if (is == null) {
                log.info("File #0 not found", document.getUrl());
                facesMessages.add("Error while downloading the file: " + document.getName());
                return;
            }

            HttpServletResponse response = (HttpServletResponse) facesContext.getExternalContext().getResponse();
            response.setContentType(document.getFormat());
            response.setContentLength(document.getFileSize());
            response.setHeader("Content-disposition", "attachment;filename=" + document.getName().replace(" ", ""));
            try {
                OutputStream out = response.getOutputStream();
                byte[] buffer = new byte[1024];
                //int len;
                while (is.read(buffer) != -1) {
                    out.write(buffer);
                }
                out.flush();
                out.close();
                facesContext.responseComplete();
            } catch (IOException ex) {
                facesMessages.add("Error while downloading the file: " + document.getName());
            }

        } catch (FileNotFoundException ex) {
            log.error("Document not found", ex);
        }
    }

    /**
     * Verilen id'li documanı siler.
     *
     * @param id
     */
    public void deleteDocument(Long id) {

        if (id == null) {
            log.info("File #0 not found", id);
            facesMessages.add("Error while downloading the file: " + id);
            return;
        }

        document = entityManager.find(DocumentFile.class, id);

        if (document == null) {
            log.info("File #0 not found", id);
            facesMessages.add("Error while downloading the file: " + id);
            return;
        }


        File f = new File(getStorePath() + "/" + document.getUrl());
        f.delete();

        entityManager.remove(document);
        entityManager.flush();
    }


     /**
     * Store'un fiziksel yolu.
     *
     * TODO: ayar Dosyası
     *
     * @return
     */
    public String getStorePath() {
        Calendar c = Calendar.getInstance();
        c.setTime(document.getUpdateDate());
        Integer y = c.get(Calendar.YEAR);
        Integer m = c.get(Calendar.MONTH) + 1;
        String s = systemProperties.getProperties().get("file.folder") + "/" + y.toString() + "/" + m.toString();

        log.info ("getstorepath s = " + s);
        File f = new File(s);
        f.mkdirs();

        return s;
    }

    public DocumentFileType[] getDocType(){
        return DocumentFileType.values();
    }

    public byte[] getFileData() {
        return fileData;
    }

    public void setFileData(byte[] fileData) {
        this.fileData = fileData;
    }

    public String getContentType() {
        return contentType;
    }

    public void setContentType(String contentType) {
        this.contentType = contentType;
    }

    public Integer getFileSize() {
        return fileSize;
    }

    public void setFileSize(Integer fileSize) {
        this.fileSize = fileSize;
    }

    public String getFileName() {
        return fileName;
    }

    public void setFileName(String fileName) {
        this.fileName = fileName;
    }

    public boolean isIsUploaded() {
        return isUploaded;
    }

    public void setIsUploaded(boolean isUploaded) {
        this.isUploaded = isUploaded;
    }

    public Contact getContact() {
		return contact;
	}

	public void setContact(Contact contact) {
		this.contact = contact;
	}

}
