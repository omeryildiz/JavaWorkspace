package tr.gov.tubitak.oneylul.lambda;

import java.util.Arrays;
import java.util.List;

public class Java8Example {
	public static void main(String[] args) {
		List<Integer> list = Arrays.asList(1, -10, 5, 8, 26, 78, -59);
		//printList(list, new TestClosureImpl());
		printList(list, new TestClosure<Integer>(){
			public boolean test(Integer candidate) {
				//System.out.println(candidate);
				return candidate < 5;
			}
		});
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
//10'dan büyük durumlarda deðeri gönderen bir implementasyon yapýldý
class TestClosureImpl implements TestClosure<Integer>{

	@Override
	public boolean test(Integer candidate) {
		// TODO Auto-generated method stub
		return candidate > 10;
	}
	
}