package tr.gov.tubitak.oneylul.lambda;

import java.util.Arrays;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;

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