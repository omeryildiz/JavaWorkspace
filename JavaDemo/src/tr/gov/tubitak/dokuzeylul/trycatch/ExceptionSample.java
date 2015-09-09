package tr.gov.tubitak.dokuzeylul.trycatch;

import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;



public class ExceptionSample {
	public static void main(String[] args) {
		birinciMetod();
		try {
			ikinciMetod();
		} catch (ParseException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
			System.out.println("hata oluþtu");
		}
		try {
			printDiziElemani(new int[]{1,2,3,4}, 5);
		} catch (Exception e) {
			System.out.println(e.getMessage());
		}
		
		try {
			printYeniDiziElemani(new int[]{1,2,3,4}, 5);
		} catch (Exception e) {
			System.out.println(e.getMessage());
		}
		
		try {
			bol(0, 5);
		} catch (BolumHatasýException e) {
			
			System.out.println(e.getMessage());
		}

	}
	
	//try catch kullaným örneði
	static String birinciMetod()
	{
		DateFormat df = new SimpleDateFormat("yyyy-MM-dd");
		String[] diziString = new String[]{"Birinci","Ýkinci","Üçüncü","Dördüncü"};
		
		try {
			System.out.println(diziString[5]);
		} catch (ArrayIndexOutOfBoundsException e) {
			e.printStackTrace();
		}

		Date date;
		try {
			date = df.parse("2005-10-12");
			System.out.println(date);
		} catch (ParseException e) {
			System.out.println(e.getMessage());
			e.printStackTrace();
		} finally {
			System.out.println("Final blogu neden var?");
		}
		return null;
		
	}
	//exception'ý metod üzerinden yolladýðýmýz örnek
	static void ikinciMetod() throws ParseException {
		DateFormat df = new SimpleDateFormat("yyyy-MM-dd");
		
		Date date = df.parse("1988-10-27");
		System.out.println(date);
		
	}
	//iki exception gönderebildiðim örnek:
	static void ucuncuMetod() throws ParseException, NullPointerException {
		Date date = null;
		DateFormat df = new SimpleDateFormat("yyyy-MM-dd");
		if (1 == 2) {
			date = df.parse("1988-10-27");
		}
		
		System.out.println(date);
		
	}
	//kendi exceptionýmýzý yadýðýmýz metod
	static void printDiziElemani(int[] dizi, int i) throws Exception
	{
		if(i < 0 || i > dizi.length -1)
		{
			throw new Exception("Gönderilen index 0'dan küçük veya dizi boyutundan büyük");
		}
		System.out.println(dizi[i]);
	}
	
	static void printYeniDiziElemani(int[] dizi, int i) throws DiziLimitException
	{
		if(i < 0 || i > dizi.length -1)
		{
			throw new DiziLimitException("Gönderilen index 0'dan küçük veya dizi boyutundan büyük");
		}
		System.out.println(dizi[i]);
	}
	
	static void bol (int a , int b) throws BolumHatasýException{
		int c;
		if(a == 0)
		{
			throw new BolumHatasýException("ilk deðer asla 0 olamaz akýllý ol!");
		}
		c = a/b;
		System.out.println(c+" düzgün þekilde bülünemedi");
	}

}
