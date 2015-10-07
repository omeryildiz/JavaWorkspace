package tr.gov.tubitak.course.unittest;

import org.junit.Test;

import tr.gov.tubitak.course.entity.Street;
import tr.gov.tubitak.course.service.StreetService;

public class StreetServiceTest {
	
	StreetService service = new StreetService();
	@Test
	public void saveTest() {
		Street street = new Street();
		street.setName("Gaziosmanpaşa");
		street.setNumber(1234);
		service.save(street);
	}

}
