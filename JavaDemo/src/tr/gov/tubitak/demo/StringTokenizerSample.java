package tr.gov.tubitak.demo;

import java.util.StringTokenizer;

public class StringTokenizerSample {

	public static void main(String[] args) {
		//tüm boþluklarý atar sýrayla yazar
		StringTokenizer st = new StringTokenizer("ahmet mehmet ali");
		while(st.hasMoreTokens())
		{
			System.out.println(st.nextToken());
		}
		
		StringTokenizer newSt = new StringTokenizer("ahmet mehmet deli samet maret","e");
		while(newSt.hasMoreTokens())
		{
			System.out.println(newSt.nextToken());
		}

	}

}
