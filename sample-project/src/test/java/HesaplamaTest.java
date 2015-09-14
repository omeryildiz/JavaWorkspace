import org.junit.Assert;
import org.junit.Test;



public class HesaplamaTest {
	
@Test
public void toplamaTest() {
	Hesaplama hesaplama = new Hesaplama();
	int sonuc = hesaplama.topla(2, 4);
	Assert.assertEquals(6,	 sonuc);

}
}
