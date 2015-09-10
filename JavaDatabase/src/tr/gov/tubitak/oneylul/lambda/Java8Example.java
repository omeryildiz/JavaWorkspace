package tr.gov.tubitak.oneylul.lambda;

import java.util.Arrays;
import java.util.Collections;
import java.util.Date;
import java.util.List;
import java.util.function.Function;

public class Java8Example {
	public static void main(String[] args) {
		List<Integer> list = Arrays.asList(1, -10, 5, 8, 26, 78, -59);
		// printList(list, new TestClosureImpl());


		//java 8 öncesi
		printList(list, new TestClosure<Integer>() {
			public boolean test(Integer candidate) {
				// System.out.println(candidate);
				return candidate < 5;
			}
		});
		
		// java 8 sonrasý
		// printList(list,p -> p < 7);
		

		// eski hali
		// Collections.sort(list,new Comparator<Integer>() {
		//
		// @Override
		// public int compare(Integer o1, Integer o2) {
		// return o1 < o2 ? -1 : 1;
		// }
		// });

		// java8 ile gelen özellik
		Collections.sort(list, (arg0, arg1) -> arg0 < arg1 ? -1 : 1);
		
		////////////////////////////////////////////////////
		List<String> list1 = Arrays.asList("mehmet","kemal","ömer","sami");
		//java8 ile gelen foreach consumer özelliði
		list1.forEach(p -> System.out.println(p));
		Collections.sort(list1,Java8Example::compare);
		for (String string : list1) {
			System.out.println(string);
		}
		
		List<Asker> askerler = Arrays.asList(new Asker("birol"),new Asker("omer"),new Asker("kemal"));
		//askerler.forEach(p->p.name = "ezildi");
		askerler.forEach(p -> System.out.println(p.name));
		//stream bize liste üzerinde filtreleme deðiþiklik vs. iþlemleri yapmamýzý saðlýyor
		askerler.stream().filter(p->p.name.equals("omer")).forEach(p->System.out.println(p.name));
		askerler.stream().filter(p->p.name.equals("omer")).map(p->new Asker( p.name +" degistirildi")).forEach(p->System.out.println(p.name));
		TestInterface.getDate();
		
	}
	
	interface TestInterface<T> {
		String getName();
		
		static Date getDate() {
			return new Date();
		}
		
		default String getType() {
			return "";
		}
		
		default Integer getInteger() {
			return 1;
		}
	}
	
	static int compare(String s1, String s2) {
		return s1.compareTo(s2);
	}

	static <T> void printList(List<T> list, TestClosure<T> tester) {
		for (T obj : list) {
			if (tester.test(obj)) {
				System.out.println(obj);
			}
		}
	}

}

interface TestClosure<T> {
	boolean test(T candidate);
}

// 10'dan büyük durumlarda deðeri gönderen bir implementasyon yapýldý
class TestClosureImpl implements TestClosure<Integer> {

	@Override
	public boolean test(Integer candidate) {
		// TODO Auto-generated method stub
		return candidate > 10;
	}

}

class Asker {
	public String name;
	public Asker(String name) {
		// TODO Auto-generated constructor stub
		this.name = name;
	}
}