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

import java.math.BigDecimal;

/**
 *
 * @author haky
 */
public abstract class NumberToText {
	int[] intArray = new int[30];
    int intArrayLenght;
    int[] decimalArray = new int[2];
    
    public void makeArray(double money ) {
//    	BigDecimal b = new BigDecimal(money); /* bitwise işlem yaptığından tip dönüşümünde sorun yaşanabiliyor. */
    	BigDecimal b = BigDecimal.valueOf(money);

        b = b.setScale( 2, BigDecimal.ROUND_HALF_UP);

        double intPart = b.doubleValue();
        
        int i = 0;

        while (intPart >= 1) {
            intArray[i] = (int) (intPart % 10);
            intPart -= (intPart % 10);
            intPart /= 10;
            i++;
        }
        intArrayLenght = i;

        if (i % 3 == 2) {
            intArrayLenght = i + 1;
        }
        if (i % 3 == 1) {
            intArrayLenght = i + 2;
        }
        if (i % 3 == 0) {
            intArrayLenght = i;
        }

        //intPart = Math.round( (money - (long) money) * 100 );
        intPart = Math.round( (b.doubleValue() - (long) b.doubleValue()) * 100 );

        for (i = 0; i < 2; i++) {
            decimalArray[2 - i - 1] = (int) intPart % 10;
            intPart -= intPart % 10;
            intPart /= 10;
        }

    }

    /**
     * Verilen değeri yazı ile yazar.
     * @param number
     * @return
     */
    public abstract String convert(double number, String ccy, String dcy);
    
    public abstract void stringMerge(int digit, int intArray[], int intArrayLenght, StringBuffer moneyStr);
    
    public abstract String getNumberString(Integer i);
    
}
