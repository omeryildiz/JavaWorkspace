package tr.gov.tubitak.protobuf;

import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.util.Scanner;

import tr.gov.tubitak.protobuf.CatProto.Cat;

public class UseOfCat {

	public static void main(String[] args) {
		Scanner keyboard = new Scanner(System.in);
		
		Cat kedi = CatProto.Cat.newBuilder().setName("tekir").setAge(5).build();
		System.out.println(kedi.getName()+ " " + kedi.getAge());
		try {
			//serileþtirilmiþ veriyi dosyaya yaz
			
			FileOutputStream output = new FileOutputStream("D:\\egitim\\kedi.ser");
			kedi.writeTo(output);
			output.close();
			
			System.out.println("Okuma iþlemi baþlasýn mý?");
			String something = keyboard.nextLine();
			System.out.println(something);
			
			
			//veriyi dosyadan oku
			Cat catFromFile = Cat.parseFrom(new FileInputStream("D:\\egitim\\kedi.ser"));
			System.out.println(catFromFile);
			
			
		} catch (Exception e) {
			e.printStackTrace();
		}

	}

}
