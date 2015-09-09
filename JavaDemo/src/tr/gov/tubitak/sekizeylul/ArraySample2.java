package tr.gov.tubitak.sekizeylul;

public class ArraySample2 {
	public static String getName(int id,String[][] arrayString){
		String result;
		result = arrayString[id-1][0];
		return result;
	}
	public static void main(String[] args) {
		String[][] ogrenciVeNotlari= new String[][]{{"ömer yýldýz","a"},{"ahmet yildiz","a"},{"esma yildiz","c"},{"mehmet güngör","d"}};
		String name = getName(2,ogrenciVeNotlari);
		System.out.println(name);
	}
	

}
