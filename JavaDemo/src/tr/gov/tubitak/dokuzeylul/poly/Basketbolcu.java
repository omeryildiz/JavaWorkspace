package tr.gov.tubitak.dokuzeylul.poly;

public class Basketbolcu extends Sporcu {
	
	public void basketAt(int kaclik)
	{
		System.out.println(super.getName()+" "+ super.getSurname() + kaclik + "basket atti");
	}

}
