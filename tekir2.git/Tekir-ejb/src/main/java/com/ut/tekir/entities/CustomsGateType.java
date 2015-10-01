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

package com.ut.tekir.entities;

/**
 * Sınır kapısı türü
 * 
 * @author yigit
 */

public enum CustomsGateType {

	Unknown, 	// Bilinmeyan ve Diğer
	Land, 	// Kara Sınır Kapısı
	Air, 	// Hava Sınır Kapısı
	Sea, 	// Deniz Sınır Kapısı
	FreeZone, 	// Serbest Bölge
	Railway, 	//Demiryolu Sınır Kapısı
	PipeLine,	 // Boru Hattı
	Internal 	//İç gümrük İdaresi
}
