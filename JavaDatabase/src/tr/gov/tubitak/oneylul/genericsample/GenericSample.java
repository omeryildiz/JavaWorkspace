package tr.gov.tubitak.oneylul.genericsample;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import com.mysql.fabric.xmlrpc.base.Array;

public class GenericSample {
	public static void main(String[] args) {
		KeyValuPair<String, String> pair = new KeyValuPair<String, String>();
		pair.setKey("bir");
		pair.setValue("1");

		KeyValuPair<Integer, Integer> pair2 = new KeyValuPair<Integer, Integer>();
		pair2.setKey(2);
		pair2.setValue(3);
		System.out.println(pair);
		System.out.println(pair2);
		
		System.out.println(getValue(Arrays.asList(1,2,3), 2));
		
		//List<Number> þeklinde olduðunda hata verir fakat 
		//Aþaðýdaki gibi bir taným kullanýlabilir number'dan türeyen Integer aþaðýdaki gibi atanabilir.
		List<? extends Number> numberlist = new ArrayList<Integer>();
		//bunlara eleman ekleyemiyoruz
		for (Number number : numberlist) {
			System.out.println(number);
		}
		List<? super Integer> intList = new ArrayList<Integer>();
		intList.add(3);
		
	}

	static <T> T getValue(List<T> list, int index) {
		return list.get(index);
	}

}
