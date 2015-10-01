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


import com.ut.tekir.framework.BrowserBase;
import com.ut.tekir.entities.DocumentFile;
import javax.ejb.Stateful;
import org.hibernate.criterion.DetachedCriteria;
import org.hibernate.criterion.Order;
import org.hibernate.criterion.Restrictions;
import org.jboss.seam.ScopeType;
import org.jboss.seam.annotations.Name;
import org.jboss.seam.annotations.Observer;
import org.jboss.seam.annotations.Scope;

/**
 *
 * @author haky
 */
@Stateful
@Name("documentBrowse")
@Scope(value=ScopeType.SESSION)
public class DocumentBrowseBean extends BrowserBase<DocumentFile, DocumentFilterModel> implements DocumentBrowse<DocumentFile, DocumentFilterModel>{

    @Override
    public DocumentFilterModel newFilterModel() {
        DocumentFilterModel model = new DocumentFilterModel();
        //TODO: Default filtre değerleri konacak.
        model.setStatus(DocumentFilterModel.Status.Active);

        return model;
    }
        
    @Override
    public DetachedCriteria buildCriteria(){
        DetachedCriteria crit = DetachedCriteria.forClass( DocumentFile.class );
        
        if ( filterModel.getFirstDate() != null ){
            
            crit.add( Restrictions.ge( "updateDate", filterModel.getFirstDate()));
        }
        
        if ( filterModel.getSecondDate() != null ){
            
            crit.add( Restrictions.le( "updateDate", filterModel.getSecondDate()));
        }
                
        if( filterModel.getName() != null && filterModel.getName().length() > 0 ){
            crit.add( Restrictions.like("name", filterModel.getName() + "%" ));
        }
        
        if ( filterModel.getStatus() != DocumentFilterModel.Status.All ){
            
            if( filterModel.getStatus() == DocumentFilterModel.Status.Active ){
                crit.add( Restrictions.eq("active",  true ));
            } else {
                crit.add( Restrictions.eq("active",  false ));
            }
        }
        
        if ( filterModel.getDocType() == DocumentFilterModel.DocType.Genel ){
            crit.add( Restrictions.eq("docType", DocumentFilterModel.DocType.Genel));    
        }
        
        if ( filterModel.getDocType() == DocumentFilterModel.DocType.Genelge ){
            crit.add( Restrictions.eq("docType", DocumentFilterModel.DocType.Genelge));    
        }
        
        if ( filterModel.getDocType() == DocumentFilterModel.DocType.Haber ){
            crit.add( Restrictions.eq("docType", DocumentFilterModel.DocType.Haber));    
        }
        
        if ( filterModel.getDocType() == DocumentFilterModel.DocType.Sozlesme ){
            crit.add( Restrictions.eq("docType", DocumentFilterModel.DocType.Sozlesme));    
        }
        
        if ( filterModel.getDocType() == DocumentFilterModel.DocType.Teblig ){
            crit.add( Restrictions.eq("docType", DocumentFilterModel.DocType.Teblig));    
        }

        if ( filterModel.getDocType() == DocumentFilterModel.DocType.Basvuru ){
            crit.add( Restrictions.eq("docType", DocumentFilterModel.DocType.Basvuru) );
        } else if ( filterModel.getDocType() != DocumentFilterModel.DocType.Hepsi )  {
            crit.add( Restrictions.ne("docType", DocumentFilterModel.DocType.Basvuru) );
        }
                
        crit.addOrder(Order.desc("updateDate"));
        
        return crit;
    }
    
    @Observer("refreshBrowser:com.ut.tekir.entities.DocumentFile")
    public void refreshResults(){
        log.debug("Uyarı geldi resultSet tazeleniyor");
        if ( getEntityList() == null || getEntityList().isEmpty() ) return;
        search();
    }

}
