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
 * Çek/Senet durumları
 * 
 * @author dumlupinar
 * 
 * Çeklerin durumuna göre yapabileceği hareketler, senetlerde de durum benzerdir:
 * 
 * Müşteri Çekleri için
 *  	Portfoy 		-> Ciro, KasaTahsilat, BankaTahsilatta, BankaTeminat, Karsiliksiz
 *		Ciro 			-> Kapandi, Portfoy, Karsiliksiz
 * 		Cikis 			-> Olamaz
 * 		BankaOdeme 		-> Olamaz
 * 		KasaOdeme 		-> Olamaz
 * 		KasaTahsilat 	-> Kapandi
 * 		BankaTahsilatta -> BankaTahsilEdildi, Portfoy, Karsiliksiz
 * 		BankaTahsilEdildi-> Kapandi
 * 		BankaTeminat  	-> Portfoy, Karsiliksiz
 * 		Karsiliksiz 	-> KasaTahsilat, Portfoy, Supheli
 * 		Supheli 		-> Kapandi
 * 		Kapandi			-> Hareket göremez
 * 		Takipte			-> KasaTahsilat, Portfoy, Supheli
 * 
 * Firma Çekleri için
 *  	Portfoy 		-> BankaTeminat, Cikis, Kapandi
 *		Ciro 			-> Olamaz
 * 		Cikis 			-> Portfoy, BankaOdeme, KasaOdeme
 * 		BankaOdeme 		-> Kapandi
 * 		KasaOdeme 		-> Kapandi
 * 		KasaTahsilat 	-> Olamaz
 * 		BankaTahsilatta -> Olamaz
 * 		BankaTahsilEdildi-> Olamaz
 * 		BankaTeminat 	-> Portfoy
 * 		Karsiliksiz 	-> Olamaz, olmamalı
 * 		Supheli 		-> Olamaz, olmamalı :)
 * 		Kapandi			-> Hareket göremez
 * 
 */
public enum ChequeStatus {
	Portfoy,
	Ciro,
	Cikis,
	BankaOdeme,
	KasaOdeme,
	BankaTahsilatta,
	BankaTahsilEdildi,
	KasaTahsilat,
	BankaTeminat,
	Karsiliksiz,
	Supheli,
	Kapandi,
	Takipte;
}
