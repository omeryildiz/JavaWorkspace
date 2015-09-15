import org.junit.Assert;
import org.junit.Test;

import tr.gov.tubitak.web.UserLoginBean;

public class UserLoginTest {
	
	UserLoginBean userLoginBean = new UserLoginBean();
	@Test
	public void loginSuccessTest() {
		userLoginBean.setUsername("Admin");
		userLoginBean.setPassword("123456");
		Assert.assertTrue(userLoginBean.login());
	}
	public void loginUnSuccessTest() {
		userLoginBean.setUsername("Admein");
		userLoginBean.setPassword("1234e56");
		Assert.assertFalse(userLoginBean.login());
	}
	
}
