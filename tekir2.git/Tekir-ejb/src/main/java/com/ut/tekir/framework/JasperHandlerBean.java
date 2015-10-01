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

import java.io.ByteArrayOutputStream;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.sql.Connection;
import java.sql.SQLException;
import java.util.Collection;
import java.util.Map;

import javax.faces.context.FacesContext;
import javax.naming.Context;
import javax.naming.NamingException;
import javax.servlet.ServletContext;
import javax.servlet.http.HttpServletResponse;
import javax.sql.DataSource;

import net.sf.jasperreports.engine.JRException;
import net.sf.jasperreports.engine.JRExporterParameter;
import net.sf.jasperreports.engine.JasperCompileManager;
import net.sf.jasperreports.engine.JasperFillManager;
import net.sf.jasperreports.engine.JasperPrint;
import net.sf.jasperreports.engine.JasperReport;
import net.sf.jasperreports.engine.JasperRunManager;
import net.sf.jasperreports.engine.data.JRBeanCollectionDataSource;
import net.sf.jasperreports.engine.export.JRXlsExporter;
import net.sf.jasperreports.engine.export.JRXlsExporterParameter;

import org.jboss.seam.ScopeType;
import org.jboss.seam.annotations.AutoCreate;
import org.jboss.seam.annotations.In;
import org.jboss.seam.annotations.Logger;
import org.jboss.seam.annotations.Name;
import org.jboss.seam.annotations.Scope;
import org.jboss.seam.faces.FacesMessages;
import org.jboss.seam.international.StatusMessage.Severity;
import org.jboss.seam.log.Log;

import com.ut.tekir.util.Utils;

/**
 * Jasper Report için genel bileşen...
 * @author haky
 */
@Name("jasperReport")
@Scope(ScopeType.EVENT)
@AutoCreate
public class JasperHandlerBean {

    //@Resource(name="java:/ucrmDatasource")
    DataSource dataSource;
    
    @In
    FacesMessages facesMessages;
    @In
    private FacesContext facesContext;
    @Logger
    private Log log;
    
    @In
    private SystemProperties systemProperties;

   
    

    /**
     * Verilen isimli jasper reportu verilen isimli pdf olarak istemciye gönderir.
     * Rapor parametreleri MAp içerisinde gönderilecektir.
     * @param name Jasper Dosya adı. /jasper/{name}.jasper olarak aranacaktır
     * @param fileName İstemciye gönderilecek olan pdf adı {fileName}.pdf olarak yollanacaktır.
     * @param params rapor parametreleri
     * @throws net.sf.jasperreports.engine.JRException
     */
    @SuppressWarnings("unchecked")
	public void reportToPDF(String name, String fileName, Map params) throws JRException {

        log.info("Jasper Rapor Exec : #0 #1", name, params);
        /**
         * tekir.properties den raporlar icin tanimlanan on eki alir.
         *
         */
        String repPrefix = systemProperties.getProperties().get("report.prefix") + "_";

        ServletContext servletContext = (ServletContext) facesContext.getExternalContext().getContext();

        //byte[] data = JasperRunManager.runReportToPdf( compileDir + report.getReport() + ".jasper", params);
        //log.info( servletContext.getRealPath("/jasper"));
        
        if( params != null ){
            params.put("SUBREPORT_DIR", servletContext.getRealPath("/jasper") + "/");
        }
        
        InputStream is = servletContext.getResourceAsStream("/jasper/" + name + ".jasper");

        if (is == null) {
            log.info("Dosya Bulunamadı");
            return;
        }

        try {
            Context ctx = new javax.naming.InitialContext();
            dataSource = (DataSource) ctx.lookup("java:/tekirDatasource");
        } catch (NamingException ex) {
            log.error("DataSource Not Found", ex);
        }

        Connection conn = null;
        byte[] data = null;
        
        log.info("Bağlantı : #0", dataSource);
        try {
            conn = dataSource.getConnection();

            log.info("Bağlantı : #0,  #1", dataSource, conn);

        
            data = JasperRunManager.runReportToPdf(is, params, conn);

            log.info("Report is ready");

        } catch (SQLException ex) {
            log.error("Connection Error : ", ex);
            facesMessages.add("Veri tabanı bağlantı hatası!");
            return;
        } finally {
        	if (conn != null) {
        		try {
        			conn.close();
				} catch (Exception e) {
					log.error("Bağlantı kapatılırken hata oluştu. Sebebi: {0}", e);
					facesMessages.add(Severity.ERROR, "Bağlantı kapatılırken hata oluştu. Sebebi: {0}", e);
				}
        	}
        }

        //FacesContext facesContext = FacesContext.getCurrentInstance();

        HttpServletResponse response = (HttpServletResponse) facesContext.getExternalContext().getResponse();
        response.setContentType("application/pdf");
        response.setContentLength(data.length);
        response.setHeader("Content-disposition",
                "attachment;filename=" +  repPrefix + fileName + ".pdf");

        try {

            OutputStream out = response.getOutputStream();

            out.write(data);

            out.flush();
            out.close();

            facesContext.responseComplete();
        } catch (IOException ex) {
            facesMessages.add("Error while downloading the file: " + fileName + ".pdf");
        }
    }

    /**
     * İstenen raporu öncelikle folder.templates altından derler. Daha sonra çıktısını gönderir.
     * @param aTemplate, derlenmek istenen şablon
     * @param aDocumentName, oluşacak pdf raporunun adı
     */
    public void compileAndRunReportToPdf(String aDocumentName, String aTemplate, Map params) throws Exception {
        log.info("Executing report: Document Name=#0, Template Name=#1, Parameters:#2", aDocumentName, aTemplate, params);

        InputStream is = getTemplate(aTemplate);
    	
        JasperReport jasperReport = compileReport(is);

        Connection conn = getConnection();

        byte[] data = JasperRunManager.runReportToPdf(jasperReport, params, conn);

        log.info("Report is ready");

    	if (conn != null) {
    		try {
    			conn.close();
			} catch (Exception e) {
				log.error("Bağlantı kapatılırken hata oluştu. Sebebi: {0}", e);
				facesMessages.add(Severity.ERROR, "Bağlantı kapatılırken hata oluştu. Sebebi: {0}", e);
			}
    	}
        
        servletResponse(data, aDocumentName);
    }

    private JasperReport compileReport(InputStream is) throws Exception {
        return JasperCompileManager.compileReport(is);
    }
    
    private Connection getConnection() throws Exception {
        try {
            Context ctx = new javax.naming.InitialContext();
            dataSource = (DataSource) ctx.lookup("java:/tekirDatasource");
        } catch (NamingException ex) {
            log.error("DataSource Not Found", ex);
        }

        Connection conn;
        log.info("Bağlantı : #0", dataSource);

        conn = dataSource.getConnection();

        log.info("Bağlantı : #0,  #1", dataSource, conn);

        return conn;
    }
    
    private void servletResponse(byte[] data, String aDocumentName){
    	HttpServletResponse response = (HttpServletResponse) facesContext.getExternalContext().getResponse();
        response.setContentType("application/pdf");
        response.setContentLength(data.length);
        response.setHeader("Content-disposition",
                "attachment;filename=" + aDocumentName + ".pdf");
    	
        try {

            OutputStream out = response.getOutputStream();

            out.write(data);

            out.flush();
            out.close();

            facesContext.responseComplete();
        } catch (IOException ex) {
            facesMessages.add("Error while downloading the file: " + aDocumentName + ".pdf");
        }

    }
    
    private InputStream getTemplate(String aTemplate) throws Exception {
    	return new FileInputStream( systemProperties.getProperties().get("folder.templates") + Utils.getFileSeperator() + aTemplate + ".jrxml");
    }

    /**
     * Verilen şablona verilen collectionı göndererek verilen isimli pdf'i üretir.
     * Faturalarda kullanılır.
     * 
     * @param name
     * @param template
     * @param params
     * @param collection
     * @throws net.sf.jasperreports.engine.JRException
     * @throws FileNotFoundException 
     */
    @SuppressWarnings("unchecked")
	public void printObjectToPDF(String name, String template, Map params, Collection collection) throws JRException, FileNotFoundException {

        log.info("Jasper Rapor Exec : #0 #1", name, params);
        log.info("Folder #0", systemProperties.getProperties().get("folder.templates"));
        /**
         * tekir.properties den raporlar icin tanimlanan on eki alir.
         *
         */
        String repPrefix = systemProperties.getProperties().get("report.prefix") + "_";
        
        //ServletContext servletContext = (ServletContext) facesContext.getExternalContext().getContext();
        //log.info("Folder #0", servletContext.getServerInfo() );
        //log.info("Folder #0", servletContext.getRealPath("/") );
        //InputStream is = servletContext.getResourceAsStream("/jasper/" + template + ".jrxml");
        
        InputStream is = new FileInputStream( systemProperties.getProperties().get("folder.templates") + Utils.getFileSeperator() + template + ".jrxml");
        	
        if (is == null) {
            log.info("Dosya Bulunamadı");
            return;
        }

        //byte[] data = JasperRunManager.runReportToPdf( "/home/haky/Apps/iReport-2.0.3/cities.jasper", null, session.connection() );
        JasperReport jasperReport = null;
        try {
            jasperReport = JasperCompileManager.compileReport(is);
        } catch (Throwable e) {
            log.info(e);
        }
        JRBeanCollectionDataSource ds = new JRBeanCollectionDataSource(collection);

        byte[] data = JasperRunManager.runReportToPdf(jasperReport, params, ds);

        log.info("Report is ready");

        //FacesContext facesContext = FacesContext.getCurrentInstance();
        HttpServletResponse response = (HttpServletResponse) facesContext.getExternalContext().getResponse();
        response.setContentType("application/pdf");
        response.setContentLength(data.length);
        response.setHeader("Content-disposition",
                "attachment;filename=" + repPrefix + name + ".pdf");

        try {

            OutputStream out = response.getOutputStream();

            out.write(data);

            out.flush();
            out.close();

            facesContext.responseComplete();
        } catch (IOException ex) {
            facesMessages.add("Error while downloading the file: " + name + ".pdf");
        }

    }
    
    @SuppressWarnings("unchecked")
	public void reportToXls(String name, String fileName, Map params) throws JRException {

        log.info("Jasper Rapor Exec : #0 #1 #2", name, fileName, params);
        /**
         * tekir.properties den raporlar icin tanimlanan on eki alir.
         *
         */
        String repPrefix = systemProperties.getProperties().get("report.prefix") + "_";
        
        
        ServletContext servletContext = (ServletContext) facesContext.getExternalContext().getContext();

        //byte[] data = JasperRunManager.runReportToPdf( compileDir + report.getReport() + ".jasper", params);
        //log.info( servletContext.getRealPath("/jasper"));
        
        if( params != null ){
            params.put("SUBREPORT_DIR", servletContext.getRealPath("/jasper") + "/");
        }
        
        InputStream is = servletContext.getResourceAsStream("/jasper/" + name + ".jasper");

        if (is == null) {
            log.info("Dosya Bulunamadı");
            return ;
        }

        try {
            Context ctx = new javax.naming.InitialContext();
            dataSource = (DataSource) ctx.lookup("java:/tekirDatasource");
        } catch (NamingException ex) {
            log.error("DataSource Not Found", ex);
        }

        Connection conn = null;
        ByteArrayOutputStream os = null;
        
        log.info("Bağlantı : #0", dataSource);
        try {
            conn = dataSource.getConnection();


            log.info("Bağlantı : #0,  #1", dataSource, conn);
            
            //byte[] data = JasperRunManager.runReportToPdf( "/home/haky/Apps/iReport-2.0.3/cities.jasper", null, session.connection() );
            //byte[] data = JasperRunManager.runReportToPdf(is, params, conn);
     
            os = new ByteArrayOutputStream();
            
            JasperPrint jasperPrint;

            
            jasperPrint = JasperFillManager.fillReport(is, params, conn);
            
            
            JRXlsExporter exporter =  new JRXlsExporter(); //getJasperExporter(outputFormat);
            if (exporter == null)
                throw new RuntimeException("jasper service dont know about the output-type.");

            
            
            exporter.setParameter(JRExporterParameter.JASPER_PRINT, jasperPrint);
            exporter.setParameter(JRExporterParameter.OUTPUT_STREAM, os );
            exporter.setParameter(JRXlsExporterParameter.IGNORE_PAGE_MARGINS, true );
            exporter.setParameter(JRXlsExporterParameter.IS_REMOVE_EMPTY_SPACE_BETWEEN_COLUMNS, true );
            exporter.setParameter(JRXlsExporterParameter.IS_REMOVE_EMPTY_SPACE_BETWEEN_ROWS, true );
            exporter.setParameter(JRXlsExporterParameter.IS_IGNORE_CELL_BORDER, false );
            exporter.setParameter(JRXlsExporterParameter.IS_WHITE_PAGE_BACKGROUND, false );
            //FIXME: JAsper sürümleri ile ilgili bir sorun var. Butaya detaylı bakmak lazım.
            //exporter.setParameter(JRXlsExporterParameter.IS_AUTO_DETECT_CELL_TYPE, true);
            
            exporter.exportReport();

        } catch (SQLException ex) {
            log.error("Connection Error : ", ex);
            facesMessages.add("Veri tabanı bağlantı hatası!");
            return ;
        } finally {
        	if (conn != null) {
        		try {
        			conn.close();
				} catch (Exception e) {
					log.error("Bağlantı kapatılırken hata oluştu. Sebebi: {0}", e);
					facesMessages.add(Severity.ERROR, "Bağlantı kapatılırken hata oluştu. Sebebi: {0}", e);
				}
        	}
        }

        HttpServletResponse response = (HttpServletResponse) facesContext.getExternalContext().getResponse();
        response.setContentType("application/vnd.ms-excel");
        response.setContentLength(os.toByteArray().length);
        response.setHeader("Content-disposition",
                "attachment;filename=" + repPrefix + fileName + ".xls");

        try {

            OutputStream out = response.getOutputStream();


        
            out.write(os.toByteArray());

            out.flush();
            out.close();

            facesContext.responseComplete();
        } catch (IOException ex) {
            facesMessages.add("Error while downloading the file: " + fileName + ".xls");
        }
     
    }
}
