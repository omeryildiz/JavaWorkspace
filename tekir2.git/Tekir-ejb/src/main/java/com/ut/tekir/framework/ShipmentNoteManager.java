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

import javax.persistence.EntityManager;


/**
*
* @author yigit
*/
public interface ShipmentNoteManager {

	void initManager();
	void destroy();

	List<ShipNoteModel> buildShipmentNotes();
	List<TransShipNoteModel> buildTransportShipmentNotes();
	
	List<ShipNoteModel> getShipmentNote();
	List<TransShipNoteModel> getTransShipmentNote();
	
	List<ShipNoteModel> getShipmentNotes();
	void setShipmentNotes(List<ShipNoteModel> shipmentNotes);
	void refreshShipmentNotes();
	
	
	List<TransShipNoteModel> getTransShipNotes();
	void setTransShipNotes(List<TransShipNoteModel> transShipNotes);
	void refreshTransShipNotes();
	
	List<TransShipNoteModel> getNotLoadedTransNote();
	List<TransShipNoteModel> getNotLoadedTransNotes();
	void setNotLoadedTransNotes(List<TransShipNoteModel> notLoadedTransNotes);
	void refreshNotLoadedTransNotes();
	
	EntityManager getEm();
	void setEm(EntityManager em);
}
