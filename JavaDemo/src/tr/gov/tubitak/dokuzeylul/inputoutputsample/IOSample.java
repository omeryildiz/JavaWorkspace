package tr.gov.tubitak.dokuzeylul.inputoutputsample;

import java.io.BufferedInputStream;
import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.FileReader;
import java.io.InputStream;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.io.OutputStream;
import java.util.Scanner;;

public class IOSample {
	
	
	static File file = new File("D:\\egitim\\test.txt");
	static File dizin = new File("D:\\egitim\\dosyaolustur");
	public static void main(String[] args) throws Exception {
		dosyaYaz();
		dosyaOku();
	}
	
	static void dosyaYaz() throws Exception{
		
		
		//fileoutputstream ikinci parametre olarak boolean bir deðer alýr bu deðer append olup olmayacaðýný belirler
		//Eðer true olursa yazma iþlemi üzerine yazmaz altýna ekler.
		OutputStream out = new FileOutputStream(file);
		for (long i = 0; i < 10000000000000l; i++) {
			out.write( Long.toString(i).getBytes());
		}
		
		
		//stream mutlaka close edilmeldir.
		out.close();
		
	}
	
	static void dosyaOku() throws Exception{
		long baslangic =System.currentTimeMillis();
		InputStream in = new FileInputStream(file);
		int data;
		while((data = in.read()) != -1)
		{
			System.out.print((char) data);
		}
		in.close();
		long bitis = System.currentTimeMillis();
		System.out.println("gecen sure = "+ (bitis-baslangic));
	}
	static void dosyaOkuWithBuffer() throws Exception{
		long baslangic =System.currentTimeMillis();
		InputStream in = new BufferedInputStream(new FileInputStream(file));
		int data;
		while((data = in.read()) != -1)
		{
			System.out.print((char) data);
		}
		in.close();
		long bitis = System.currentTimeMillis();
		System.out.println("gecen sure = "+ (bitis-baslangic));
	}
	static void dosyaOkuWithScanner() throws Exception{
		long baslangic =System.currentTimeMillis();
		Scanner sc = new Scanner(new FileInputStream(file));
		while(sc.hasNextLine())
		{
			String line = sc.nextLine();
			System.out.println(line);
			
		}
		long bitis = System.currentTimeMillis();
		System.out.println("gecen sure = "+ (bitis-baslangic));
	}
	
	static void dosyaOlustur() throws Exception {
	
		file.createNewFile();
	}
	
	static void dosyaSil() {
		file.delete();
	}
	
	static void dizinOlustur() {
		//dizin.mkdirs dediðimizde dizine giden yoldaki tüm klasörleri yaratýr
		dizin.mkdir();
	}
	
	static void dosyaOkuReader() throws Exception {
		BufferedReader reader = new BufferedReader(new FileReader(file));
		String line;
		while((line = reader.readLine()) != null)
		{
			System.out.println(line);
			
		}
		reader.close();
	}
	
	static void writeStudent() throws Exception {
		Student student = new Student();
		//name
		//age doldur
		ObjectOutputStream out = new ObjectOutputStream(new FileOutputStream(file));
		out.writeObject(student);
		out.close();
	}
	
	static void readStudent() throws Exception {
		ObjectInputStream in = new ObjectInputStream(new FileInputStream(file));
		Student student = (Student) in.readObject();
		in.close();
	}
	

}
