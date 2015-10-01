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

package com.ut.tekir.util;


import com.ut.tekir.framework.SequenceManager;
import com.ut.tekir.framework.SequenceType;
import org.jboss.seam.annotations.AutoCreate;
import org.jboss.seam.annotations.In;
import org.jboss.seam.annotations.Name;

/**
 *
 * @author volkan
 */
@Name ("generateBarcode")
@AutoCreate
public class GenerateBarcode {

    @In(create = true)
    SequenceManager sequenceManager;

    String barcode;
    Integer sumEven;
    Integer sumOdd;
    Integer product;
    Integer sumBase;
    Integer highBase;
    Integer checkDigit;

    public String create(String barcodeType) {

        if ( barcodeType == "EAN13") {

           //SequenceManager sm = (SequenceManager) Component.getInstance("sequenceManager", true);
           barcode = sequenceManager.getBarcodeNumber(SequenceType.SERIAL_STOCK_BARCODE, 12);

           if ( barcode.length() == 12 ) {

            //step1 cift haneleri topla
            sumEven = Integer.valueOf(barcode.substring(1,2))
                    + Integer.valueOf(barcode.substring(3,4))
                    + Integer.valueOf(barcode.substring(5,6))
                    + Integer.valueOf(barcode.substring(7,8))
                    + Integer.valueOf(barcode.substring(9,10))
                    + Integer.valueOf(barcode.substring(11,12));

            //step2 cift hane toplamini 3 ile carp
            product = sumEven * 3;

            //step3 tek haneleri topla
            sumOdd = Integer.valueOf(barcode.substring(0,1))
                    + Integer.valueOf(barcode.substring(2,3))
                    + Integer.valueOf(barcode.substring(4,5))
                    + Integer.valueOf(barcode.substring(6,7))
                    + Integer.valueOf(barcode.substring(8,9))
                    + Integer.valueOf(barcode.substring(10,11));

            //step4 ciftten uretilen ile tek haneleri topla
            sumBase = product + sumOdd;

            //step5 toplama en yakin 10 un kati buyuk sayiyi bul
            highBase = ( ( sumBase / 10 ) + 1 ) * 10;

            //step6 10 un katindan sayiyi cikar
            checkDigit =  highBase - sumBase ;
            checkDigit = (checkDigit % 10);

            //step7 ean13 formatina cevir
            barcode = barcode + checkDigit.toString();
            barcode = barcode.substring(0,13);


           } else {
                //FIXME:  12 den kucuk sayi uretildi hata mesaji verilecek
               barcode = "0000000000000";
           }
        
        } else {
            barcode = "0";
        }

        return barcode;
    }

}
