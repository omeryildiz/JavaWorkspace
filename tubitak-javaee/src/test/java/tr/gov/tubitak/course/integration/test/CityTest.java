package tr.gov.tubitak.course.integration.test;

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

import tr.gov.tubitak.course.dao.CityDAO;
import tr.gov.tubitak.course.entity.City;
import tr.gov.tubitak.course.service.CityService;
import tr.gov.tubitak.course.util.GenericDAO;
import tr.gov.tubitak.course.web.CityBean;

@RunWith(Arquillian.class)
public class CityTest {

	@Deployment
    public static Archive<?> createTestArchive() {
        return ShrinkWrap.create(WebArchive.class, "tubitak-javaee.war")
                .addPackage(City.class.getPackage())
                .addPackage(CityService.class.getPackage())
                .addPackage(CityDAO.class.getPackage())
                .addPackage(GenericDAO.class.getPackage())
                .addPackage(CityBean.class.getPackage())
                .addAsResource("META-INF/test-persistence.xml", "META-INF/persistence.xml")
                .addAsWebInfResource(EmptyAsset.INSTANCE, "beans.xml")
                // Deploy our test datasource
                .addAsWebInfResource("test-ds.xml");
    }
	
	
	@Inject CityService cityService;
	
	@Test
	public void saveTest(){
		City city = new City();
		city.setName("Istanbul");
		city.setPopulation(16000000);
		cityService.save(city);
		Assert.assertNotNull(city.getId());
		Assert.assertTrue(city.getId() > 0);
	}
	
	@PersistenceContext(unitName="primary") EntityManager em;
	
	@Test
	public void updateTest(){
		City city = new City();
		city.setName("Istanbul");
		city.setPopulation(16000000);
		cityService.save(city);
		
		city.setPopulation(18000000);
		cityService.update(city);
		
		
		City updateCity = em.find(City.class, city.getId());
		Assert.assertEquals(updateCity.getPopulation().intValue(), 18000000);
	}
	
	@Test
	public void removeTest(){
		City city = new City();
		city.setName("Istanbul");
		city.setPopulation(16000000);
		cityService.save(city);
		
		
		cityService.remove(city);
		
		
		City updateCity = em.find(City.class, city.getId());
		Assert.assertNull(updateCity);
	}
	

    	
}









