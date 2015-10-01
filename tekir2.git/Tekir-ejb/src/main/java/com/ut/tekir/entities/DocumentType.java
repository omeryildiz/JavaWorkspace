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
 * Tekir Document Type List
 * Şimdilik sadece modeller işaret ediliyor ama aslında GUI viewlarını işaret etmeli.
 * Yani DebitCreditNote DebitNote, CreditNote şeklinde, ShipmentNote Sale Shipment, Purchase Shipment şeklinde ayrışmalı...
 * @author haky
 */
public enum DocumentType {
    Unknown,
    DebitCreditVirement, //1
    FundTransfer, //2
    PurchaseInvoice, //3
    SaleInvoice, //4
    PurchaseShipmentInvoice, //5 İrsaliyeli Alış Faturası
    SaleShipmentInvoice, //6
    Payment, //7
    Collection, //8
    PurchaseShipmentNote, //9
    SaleShipmentNote, //10
    ProductTransfer, //11
    ExpenseNote, //12
    ProductVirement, //13
    DebitCreditNote, //14
    BankToBankTransfer, //15
    BankToContactTransfer, //16
    BankToAccountTransfer, //17
    PorfolioToPortfolioTransfer, //18
    DepositAccount, //19
    BondSale, //20
    BondPurchase, //21
    Repo, //22
    
    //FIXME: burada gereksiz tipler var bunları silmeli. Örneğin,
    //ChequeBankPayment,ChequeBankCollection,
    //PromissoryBankPayment,PromissoryBankCollection gibi.
    
    ChequeStatusChanging,	//23			Durum değişikliği
    ChequePayment,			//24			Kasa/Elden Tahsil
    ChequeCollection,		//25			Kasa/Elden ödeme
    ChequeBankPayment,		//26			Banka Ödeme
    ChequeBankCollection,	//27			Banka Tahsil
    ChequeBankReturn,		//28			Bankadan Geri Alma
    ChequePaymentPayroll,	//29			Ödeme Bordrosu
    ChequeCollectionPayroll,//30			Tahsilat Bordrosu
    ChequeUnpaid,			//31			Karşılıksız
    ChequeDoubtful,			//32			Şüpheli
    ChequeClosed,			//33			Kapandı
    
    PromissoryStatusChanging,	//34			Durum değişikliği
    PromissoryPayment,			//35			Kasa/Elden Tahsil
    PromissoryCollection,		//36			Kasa/Elden ödeme
    PromissoryBankPayment,		//37			Banka Ödeme
    PromissoryBankCollection,	//38			Banka Tahsil
    PromissoryBankReturn,		//39			Bankadan Geri Alma
    PromissoryPaymentPayroll,	//40			Ödeme Bordrosu
    PromissoryCollectionPayroll,//41			Tahsilat Bordrosu
    PromissoryUnpaid,			//42			Karşılıksız
    PromissoryDoubtful,			//43			Şüpheli
    PromissoryClosed,			//44			Kapandı
    InvestmentFundPurchase, //45
    InvestmentFundSale, //46

    //UYARI: transport ile ilgili olanlar kullanılmamakta. Ancak sırada durmaları gerekiyor.
    TransportInvoice,			//47			Nakliye faturası
    TransportShipmentInvoice, //48
    
    ChequeTransfer,				//49			Çek Devir
    PromissoryTransfer,			//50			Senet Devir

    SpendingNote, //51
    FixedAssetPurchaseInvoice, //52
    FixedAssetSaleInvoice, //53
    
    ChequeAccountPaymentPayroll,	// 54			Çek kasa ödeme bordrosu
    ChequeAccountCollectionPayroll,	// 55			Çek kasa tahsilat bordrosu
    ChequeCollectedAtBankPayroll,	// 56			Çek banka tahsil edildi bordrosu

    PromissoryAccountPaymentPayroll,	// 57			Senet kasa ödeme bordrosu
    PromissoryAccountCollectionPayroll,	// 58			Senet kasa tahsilat bordrosu
    PromissoryCollectedAtBankPayroll,	// 59			Senet banka tahsil edildi bordrosu

    ChequeToBankAssurancePayroll,	// 60			Çek banka teminata gönderme bordrosu
    ChequeFromContactPayroll,	// 61			Çek giriş bordrosu
    
    PromissoryToBankAssurancePayroll,	// 62			Senet banka teminata gönderme bordrosu
    PromissoryFromContactPayroll,	// 63			Senet giriş bordrosu
    
    DebitCreditNotePayment,    //64			Borç Dekont Fişi
    DebitCreditNoteCollection,  //65			Alacak Dekont Fişi

    ContactToBankTransfer, //66
    AccountToBankTransfer, //67
    ForeignExchange,			//68			Döviz Transferi Fişi
    // Yeni tip eklendiğinde ilgili raporlardaki EVRAK alanına da eklenmeli.
    RetailSaleShipmentInvoice, //69    perakende irsaliyeli satış faturası
    SaleOrder, //70 satış siparişi
    PurchaseOrder, //71 alış siparişi
    ChequeBankPaymentPayroll, // 72 Çek Banka ödeme bordrosu
    PromissoryBankPaymentPayroll, // 73 Senet Banka ödeme bordrosu
    AccountCreditNote, // 74 Kasa çıkış fişi
    RetailSaleReturnInvoice, // 75 Perakende satış iade fişi
    ReturnExpenseNote, //76 İade gider fişi
    CountNote, //77 Sayım fişi
    SaleProformaInvoice, //78 proforma satış faturası
    PaymentItem, //79 tediye satırı
    CollectionItem; // 80 tahsilat satırı
    
	public static DocumentType fromString(int anOrdinal) {
		for (DocumentType type : values()) {
			if ( type.ordinal() == anOrdinal ) {
				return type;
			}
		}
		return DocumentType.AccountToBankTransfer;
	}
	
	public static DocumentType fromString(String documentName) {
		for (DocumentType type : values()) {
			if ( type.name().equals(documentName)) {
				return type;
			}
		}
		return null;
	}

	public String camelCaseName() {
		StringBuilder sb = new StringBuilder(name());
		sb.replace(0, 1, name().substring(0, 1).toLowerCase());
		return sb.toString();
	}

}
