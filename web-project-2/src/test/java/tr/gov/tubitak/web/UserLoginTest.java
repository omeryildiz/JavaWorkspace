package tr.gov.tubitak.web;

import org.junit.Assert;
import org.junit.Test;

public class UserLoginTest {

	UserLoginBean userLoginBean = new UserLoginBean();
	
	@Test
	public void loginSuccessTest(){
		userLoginBean.setUsername("admin");
		userLoginBean.setPassword("123456");
		Assert.assertTrue(userLoginBean.login());
	}
	
	@Test
	public void loginUnsuccessTest(){
		userLoginBean.setUsername("admin");
		userLoginBean.setPassword("122");
		Assert.assertFalse(userLoginBean.login());
	}
}
