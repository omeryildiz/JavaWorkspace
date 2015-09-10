package tr.gov.tubitak.demo;

import java.io.FileOutputStream;
import java.io.ObjectOutputStream;
import java.util.Scanner;

import tr.gov.tubitak.sekizeylul.CollectionFrameworkSamples;

public class StringTryOut {
	
	public static void withSBuilder()
	{
		StringBuilder str = new StringBuilder();
		long baslangic =System.currentTimeMillis();
		for (int i = 0; i < 10000; i++) {
			str.append("test ");
		}
		long bitis = System.currentTimeMillis();
		System.out.println("gecen sure = "+ (bitis-baslangic));
	}
	
	public static void withSbBuffer()
	{
		StringBuffer str = new StringBuffer();
		//mili saniye cinsinden o anki zaman
		//string buffersýz
		long baslangic =System.currentTimeMillis();
		for (int i = 0; i < 10000; i++) {
			str.append("test ");
		}
		long bitis = System.currentTimeMillis();
		System.out.println("gecen sure = "+ (bitis-baslangic));
	}
	
	public static void withoutSbBuffer()
	{
		//string birleþtirme
				String str = "";
				//mili saniye cinsinden o anki zaman
				//string buffersýz
				long baslangic =System.currentTimeMillis();
				for (int i = 0; i < 10000; i++) {
					str += "test ";
				}
				long bitis = System.currentTimeMillis();
				System.out.println("gecen sure = "+ (bitis-baslangic));
	}

	public static void main(String[] args) {
		String name = "Ömer YILDIZ";
		System.out.println(name.substring(0,name.lastIndexOf(' ')));
		System.out.println(name.toUpperCase());
		System.out.println(name.substring(2,5));
		System.out.println(name.charAt(7));
		
		
		withoutSbBuffer();
		CollectionFrameworkSamples.cizgiCek();
		withSbBuffer();
		CollectionFrameworkSamples.cizgiCek();
		withSBuilder();
		
		

	}
	


}
