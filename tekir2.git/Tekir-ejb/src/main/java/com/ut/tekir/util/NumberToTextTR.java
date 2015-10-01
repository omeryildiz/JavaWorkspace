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
public class NumberToTextTR extends NumberToText {

	@Override
	public String convert(double number, String ccy, String dcy) {

        makeArray(number);

        int digit = intArrayLenght;

        StringBuffer moneyStr = new StringBuffer();

        if (18 >= digit && digit > 15) {
            stringMerge(digit - 1, intArray, intArrayLenght, moneyStr);
            if (intArray[digit - 1] != 0 || intArray[digit - 2] != 0 || intArray[digit - 3] != 0) {
                moneyStr.append("Katrilyon");
            }
            digit -= 3;
        }
        if (15 >= digit && digit > 12) {
            stringMerge(digit - 1, intArray, intArrayLenght, moneyStr);
            if (intArray[digit - 1] != 0 || intArray[digit - 2] != 0 || intArray[digit - 3] != 0) {
                moneyStr.append("Trilyon");
            }
            digit -= 3;
        }
        if (12 >= digit && digit > 9) {
            stringMerge(digit - 1, intArray, intArrayLenght, moneyStr);
            if (intArray[digit - 1] != 0 || intArray[digit - 2] != 0 || intArray[digit - 3] != 0) {
                moneyStr.append("Milyar");
            }
            digit -= 3;
        }
        if (9 >= digit && digit > 6) {
            stringMerge(digit - 1, intArray, intArrayLenght, moneyStr);
            if (intArray[digit - 1] != 0 || intArray[digit - 2] != 0 || intArray[digit - 3] != 0 ) {
                moneyStr.append("Milyon");
            }
            digit -= 3;
        }

        if (6 >= digit && digit > 3) {            
            if ( intArray[digit - 1] == 0 && intArray[digit - 2] == 0 && intArray[digit - 3] == 1 ) { 
                // Ozel durum duzeltmesi BirBin yerine Bin yazmali. --volkan
                moneyStr.append("Bin"); 
            } else {
                stringMerge(digit - 1, intArray, intArrayLenght, moneyStr);
                if (intArray[digit - 1] != 0 || intArray[digit - 2] != 0 || intArray[digit - 3] != 0 && intArray[digit - 3] != 1) {
                    moneyStr.append("Bin");
                }
            }                       
            digit -= 3;
        }

        if (3 >= digit) {
            if (digit == 0) {
                moneyStr.append("Sıfır");
            } else {
                stringMerge(digit - 1, intArray, intArrayLenght, moneyStr);
            }
        }

        //if (intArrayLenght != 0) {
        moneyStr.append( " " + ccy + " " );
        //}

        if (decimalArray[0] != 0 || decimalArray[1] != 0) {
            moneyStr.append(getNumberString(decimalArray[0] * 10));
            moneyStr.append(getNumberString(decimalArray[1]));
            moneyStr.append( " " + dcy + " " );
        }

        return moneyStr.toString();
	}

	@Override
	public String getNumberString(Integer i) {
        switch (i) {
            case 0:
                return "";
            case 1:
                return "Bir";
            case 2:
                return "İki";
            case 3:
                return "Üç";
            case 4:
                return "Dört";
            case 5:
                return "Beş";
            case 6:
                return "Altı";
            case 7:
                return "Yedi";
            case 8:
                return "Sekiz";
            case 9:
                return "Dokuz";
            case 10:
                return "On";
            case 20:
                return "Yirmi";
            case 30:
                return "Otuz";
            case 40:
                return "Kırk";
            case 50:
                return "Elli";
            case 60:
                return "Altmış";
            case 70:
                return "Yetmiş";
            case 80:
                return "Seksen";
            case 90:
                return "Doksan";

            default:
                return "";
        }
	}
	
	/* FIXME: move to test class
	 *  
	public static void main(String[] args) {
		NumberToTextTR hede = new NumberToTextTR();
		String sonuc = hede.convert(213.01, "TL", "KR");
		System.out.println(sonuc);
	}
	*/
	
	@Override
	public void stringMerge(int digit, int[] intArray, int intArrayLenght, StringBuffer moneyStr) {
        if (intArray[digit] != 0) {
            if (intArray[digit] != 1) {
                moneyStr.append(getNumberString(intArray[digit]));
            }
            moneyStr.append("Yüz");
        }
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
