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

/**
 * @author volkan
 *
 */
public class NumberToTextEN extends NumberToText {

	@Override
	public String convert(double number, String ccy, String dcy) {

        makeArray(number);

        int digit = intArrayLenght;

        StringBuffer moneyStr = new StringBuffer();

        //"21> Quintillion ", 24>"Sextillion "
        if (18 >= digit && digit > 15) {
            stringMerge(digit - 1, intArray, intArrayLenght, moneyStr);
            if (intArray[digit - 1] != 0 || intArray[digit - 2] != 0 || intArray[digit - 3] != 0) {
                moneyStr.append("Quadrillion"); //katrilyon
            }
            digit -= 3;
        }
        if (15 >= digit && digit > 12) {
            stringMerge(digit - 1, intArray, intArrayLenght, moneyStr);
            if (intArray[digit - 1] != 0 || intArray[digit - 2] != 0 || intArray[digit - 3] != 0) {
                moneyStr.append("Trillion"); //trilyon
            }
            digit -= 3;
        }
        if (12 >= digit && digit > 9) {
            stringMerge(digit - 1, intArray, intArrayLenght, moneyStr);
            if (intArray[digit - 1] != 0 || intArray[digit - 2] != 0 || intArray[digit - 3] != 0) {
                moneyStr.append("Billion"); //milyar
            }
            digit -= 3;
        }
        if (9 >= digit && digit > 6) {
            if ( intArray[digit - 1] == 0 && intArray[digit - 2] == 0 && intArray[digit - 3] == 1 ) { 
                // Ozel durum duzeltmesi BirBin yerine Bin yazmali. --volkan
                moneyStr.append("OneMillion"); //milyon 
            } else {
                stringMerge(digit - 1, intArray, intArrayLenght, moneyStr);
                if (intArray[digit - 1] != 0 || intArray[digit - 2] != 0 || intArray[digit - 3] != 0 && intArray[digit - 3] != 1) {
                    moneyStr.append("Millions"); //milyon
                }
            }        
            digit -= 3;
        }

        if (6 >= digit && digit > 3) {            
            if ( intArray[digit - 1] == 0 && intArray[digit - 2] == 0 && intArray[digit - 3] == 1 ) { 
                // Ozel durum duzeltmesi BirBin yerine Bin yazmali. --volkan
                moneyStr.append("OneThousand"); //bin 
            } else {
                stringMerge(digit - 1, intArray, intArrayLenght, moneyStr);
                if (intArray[digit - 1] != 0 || intArray[digit - 2] != 0 || intArray[digit - 3] != 0 && intArray[digit - 3] != 1) {
                    moneyStr.append("Thousand"); //bin
                }
            }                       
            digit -= 3;
        }

        if (3 >= digit) {
            if (digit == 0) {
                moneyStr.append("Zero");
            } else {
                stringMerge(digit - 1, intArray, intArrayLenght, moneyStr);
            }
        }

        //if (intArrayLenght != 0) {
        moneyStr.append( " " + ccy + " " );
        //}

        if (decimalArray[0] != 0 || decimalArray[1] != 0) {
        	Integer kurus = (decimalArray[0] * 10) + decimalArray[1];
        	// 20 den kucuk ingilizce sayilarin formati farkli 
        	// ( teneight degil, eighteen istiyoruz :) ) 
       		if (kurus < 20 ){
        		moneyStr.append(getNumberString(kurus));
        	} else {
	            moneyStr.append(getNumberString(decimalArray[0] * 10));
	            moneyStr.append(getNumberString(decimalArray[1]));
        	}
       		moneyStr.append( " " + dcy + " " );
        }

        return moneyStr.toString();
	}

	@Override
	public void stringMerge(int digit, int[] intArray, int intArrayLenght, StringBuffer moneyStr) {
        if (intArray[digit] != 0) {
            moneyStr.append(getNumberString(intArray[digit]));
            moneyStr.append("Hundred");
        }
        
        Integer para = (intArray[digit - 1] * 10) + intArray[digit - 2];
        
        if (para < 20){
        	moneyStr.append(getNumberString(para));
        } else {
        	if (intArray[digit - 1] != 0) {
        		moneyStr.append(getNumberString(intArray[digit - 1] * 10));
        	}
        	if (intArray[digit - 2] != 0) {
        		if (intArray[digit - 2] != 1 || (digit + 6) != intArrayLenght) 
        		{
        			moneyStr.append(getNumberString(intArray[digit - 2]));
        		}
        	}
        }
		
	}

	/* FIXME: move to test class
	 * 
	public static void main(String[] args) {
		NumberToTextEN hede = new NumberToTextEN();
		String sonuc = hede.convert(123456789.01, "USD", "CN");
		System.out.println(sonuc);
		
	} 
	*/
	
	@Override
	public String getNumberString(Integer i) {
	       switch (i) {
           case 0:
               return "";
           case 1:
               return "One";
           case 2:
               return "Two";
           case 3:
               return "Three";
           case 4:
               return "Four";
           case 5:
               return "Five";
           case 6:
               return "Six";
           case 7:
               return "Seven";
           case 8:
               return "Eight";
           case 9:
               return "Nine";
           case 10:
               return "Ten";
           case 11:
               return "Eleven";
           case 12:
               return "Twelve";
           case 13:
               return "Thirteen";
           case 14:
               return "Fourteen";
           case 15:
               return "Fifteen";
           case 16:
               return "Sixteen";
           case 17:
               return "Seventeen";
           case 18:
               return "Eighteen";
           case 19:
               return "Nineteen";
           case 20:
               return "Twenty";
           case 30:
               return "Thirty";
           case 40:
               return "Fourty";
           case 50:
               return "Fifty";
           case 60:
               return "Sixty";
           case 70:
               return "Seventy";
           case 80:
               return "Eighty";
           case 90:
               return "Ninety";

           default:
               return "";
       }
	}
}
