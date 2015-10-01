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

import com.ut.tekir.entities.DocumentFile;
import java.util.ArrayList;

import org.jboss.seam.ScopeType;
import org.jboss.seam.annotations.Name;
import org.jboss.seam.annotations.Scope;
import org.richfaces.event.UploadEvent;
import org.richfaces.model.UploadItem;

/**
 * @author Ilya Shaikovsky
 *
 */
@Name("fileUploadBean")
@Scope(ScopeType.CONVERSATION)
public class FileUploadBean{

	private ArrayList<DocumentFile> files = new ArrayList<DocumentFile>();
	private int uploadsAvailable = 5;
	private boolean autoUpload = false;
	private boolean useFlash = false;
	public int getSize() {
		if (getFiles().size()>0){
			return getFiles().size();
		}else
		{
			return 0;
		}
	}

	public FileUploadBean() {
	}

	public synchronized void listener(UploadEvent event) throws Exception{
	    UploadItem item = event.getUploadItem();
	    DocumentFile file = new DocumentFile();
	    file.setName(item.getFileName());
        file.setTitle(item.getFileName());
	    files.add(file);
	    uploadsAvailable--;
	}

	public String clearUploadData() {
		files.clear();
		setUploadsAvailable(5);
		return null;
	}

	public long getTimeStamp(){
		return System.currentTimeMillis();
	}

	public ArrayList<DocumentFile> getFiles() {
		return files;
	}

	public void setFiles(ArrayList<DocumentFile> files) {
		this.files = files;
	}

	public int getUploadsAvailable() {
		return uploadsAvailable;
	}

	public void setUploadsAvailable(int uploadsAvailable) {
		this.uploadsAvailable = uploadsAvailable;
	}

	public boolean isAutoUpload() {
		return autoUpload;
	}

	public void setAutoUpload(boolean autoUpload) {
		this.autoUpload = autoUpload;
	}

	public boolean isUseFlash() {
		return useFlash;
	}

	public void setUseFlash(boolean useFlash) {
		this.useFlash = useFlash;
	}

}
