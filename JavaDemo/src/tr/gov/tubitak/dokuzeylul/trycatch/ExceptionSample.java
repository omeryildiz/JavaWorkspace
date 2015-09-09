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
			System.out.println("hata olu�tu");
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
		} catch (BolumHatas�Exception e) {
			
			System.out.println(e.getMessage());
		}

	}
	
	//try catch kullan�m �rne�i
	static String birinciMetod()
	{
		DateFormat df = new SimpleDateFormat("yyyy-MM-dd");
		String[] diziString = new String[]{"Birinci","�kinci","���nc�","D�rd�nc�"};
		
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
	//exception'� metod �zerinden yollad���m�z �rnek
	static void ikinciMetod() throws ParseException {
		DateFormat df = new SimpleDateFormat("yyyy-MM-dd");
		
		Date date = df.parse("1988-10-27");
		System.out.println(date);
		
	}
	//iki exception g�nderebildi�im �rnek:
	static void ucuncuMetod() throws ParseException, NullPointerException {
		Date date = null;
		DateFormat df = new SimpleDateFormat("yyyy-MM-dd");
		if (1 == 2) {
			date = df.parse("1988-10-27");
		}
		
		System.out.println(date);
		
	}
	//kendi exception�m�z� yad���m�z metod
	static void printDiziElemani(int[] dizi, int i) throws Exception
	{
		if(i < 0 || i > dizi.length -1)
		{
			throw new Exception("G�nderilen index 0'dan k���k veya dizi boyutundan b�y�k");
		}
		System.out.println(dizi[i]);
	}
	
	static void printYeniDiziElemani(int[] dizi, int i) throws DiziLimitException
	{
		if(i < 0 || i > dizi.length -1)
		{
			throw new DiziLimitException("G�nderilen index 0'dan k���k veya dizi boyutundan b�y�k");
		}
		System.out.println(dizi[i]);
	}
	
	static void bol (int a , int b) throws BolumHatas�Exception{
		int c;
		if(a == 0)
		{
			throw new BolumHatas�Exception("ilk de�er asla 0 olamaz ak�ll� ol!");
		}
		c = a/b;
		System.out.println(c+" d�zg�n �ekilde b�l�nemedi");
	}

}
