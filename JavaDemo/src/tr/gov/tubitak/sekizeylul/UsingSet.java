package tr.gov.tubitak.sekizeylul;

import java.util.HashSet;
import java.util.Set;

public class UsingSet {

	public static void main(String[] args) {
		Set<Integer> mySet = new HashSet<>();
		for (int i = 10; i > 0; i--) {
			mySet.add(i);
		}
		//sette var olan bir de�eri ekliyoruz. sonu� olarak sadece bir adet 5 olacakt�r setin i�inde
		mySet.add(5);
		for(Integer i : mySet)
		{
			System.out.println(i);
		}

	}

}
