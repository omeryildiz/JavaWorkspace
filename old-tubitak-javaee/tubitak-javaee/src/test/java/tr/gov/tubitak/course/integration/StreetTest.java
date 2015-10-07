package tr.gov.tubitak.course.integration;

import javax.inject.Inject;
import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;

import org.jboss.arquillian.container.test.api.Deployment;
import org.jboss.arquillian.junit.Arquillian;
import org.jboss.shrinkwrap.api.Archive;
import org.jboss.shrinkwrap.api.ShrinkWrap;
import org.jboss.shrinkwrap.api.asset.EmptyAsset;
import org.jboss.shrinkwrap.api.spec.WebArchive;
import org.junit.Assert;
import org.junit.Test;
import org.junit.runner.RunWith;

import tr.gov.tubitak.course.dao.StreetDAO;
import tr.gov.tubitak.course.entity.Street;
import tr.gov.tubitak.course.service.StreetService;
import tr.gov.tubitak.course.util.GenericDAO;
import tr.gov.tubitak.course.web.StreetBean;



@RunWith(Arquillian.class)
public class StreetTest {

	@Deployment
    public static Archive<?> createTestArchive() {
        return ShrinkWrap.create(WebArchive.class, "tubitak-javaee.war")
                .addPackage(Street.class.getPackage())
                .addPackage(StreetService.class.getPackage())
                .addPackage(StreetDAO.class.getPackage())
                .addPackage(GenericDAO.class.getPackage())
                .addPackage(StreetBean.class.getPackage())
                .addAsResource("META-INF/test-persistence.xml", "META-INF/persistence.xml")
                .addAsWebInfResource(EmptyAsset.INSTANCE, "beans.xml")
                // Deploy our test datasource
                .addAsWebInfResource("test-ds.xml");
    }

	@Inject
	StreetService service;

	@Test
	public void saveTest() {
		Street street = new Street();
		street.setName("Gaziosmanpaşa");
		street.setNumber(1234);
		service.save(street);
		Assert.assertNotNull(street.getId());
		Assert.assertTrue(street.getId() > 0);
	}

	@PersistenceContext(unitName = "primary")
	EntityManager em;

	@Test
	public void updateTest() {
		Street street = new Street();
		street.setName("Istanbul");
		street.setNumber(12344);
		service.save(street);

		street.setNumber(2345);
		service.update(street);

		Street updateStreet = em.find(Street.class, street.getId());
		Assert.assertEquals(updateStreet.getNumber().intValue(), 18000000);
	}

	@Test
	public void removeTest() {
		Street street = new Street();
		street.setName("Kesan cad.");
		street.setNumber(2427);
		service.save(street);
		service.remove(street);

		Street removeStreet = em.find(Street.class, street.getId());
		Assert.assertNull(removeStreet);
	}

}
