package tr.gov.tubitak.demo;

public class Calculate {
	public void Sum(int... candidate)
	{
		int result = 0;
		for(int i=0;i<candidate.length;i++)
			result = result + candidate[i];
		System.out.println(result);
	}
	public int mult(int... candidate)
	{
		int result = 1;
		for(int i=0;i<candidate.length;i++)
			result = result * candidate[i];
		
		return result;
	}

}
