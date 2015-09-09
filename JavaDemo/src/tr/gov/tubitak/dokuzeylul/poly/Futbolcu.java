
package tr.gov.tubitak.dokuzeylul.poly;

import java.util.Random;

public class Futbolcu extends Sporcu {
	private String[] atisTipi = new String[]{"Gol","Taç","Penalti"};
	
	public Futbolcu(){
		new Sporcu();
	}
	
	public void at() {
		Random rand = new Random();
		System.out.println(super.getName()+" "+ super.getSurname()+" " +atisTipi[rand.nextInt(2)] + " atti");
	}
}
