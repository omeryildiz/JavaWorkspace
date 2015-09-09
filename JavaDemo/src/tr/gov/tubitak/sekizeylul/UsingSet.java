package tr.gov.tubitak.sekizeylul;

import java.util.HashSet;
import java.util.Set;

public class UsingSet {

	public static void main(String[] args) {
		Set<Integer> mySet = new HashSet<>();
		for (int i = 10; i > 0; i--) {
			mySet.add(i);
		}
		//sette var olan bir deðeri ekliyoruz. sonuç olarak sadece bir adet 5 olacaktýr setin içinde
		mySet.add(5);
		for(Integer i : mySet)
		{
			System.out.println(i);
		}

	}

}
