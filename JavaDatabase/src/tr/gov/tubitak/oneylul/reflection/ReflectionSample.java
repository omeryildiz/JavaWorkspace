package tr.gov.tubitak.oneylul.reflection;

import java.lang.reflect.Field;
import java.lang.reflect.Method;




public class ReflectionSample {
	public static void main(String[] args) {
		Class clazz = null;
		try {
			clazz = Class.forName("tr.gov.tubitak.oneylul.Ogrenci");
		} catch (ClassNotFoundException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		System.out.println(clazz.getSimpleName());
		System.out.println(clazz.getName());
		Method[] methods = clazz.getMethods();
		for (Method method : methods) {
			System.out.println(method.getName());
		}
		Field[] fields = clazz.getFields();
		for (Field field2 : fields) {
			System.out.println(field2.getName());
		}
		//ba�ka bir paketteki bir s�n�f�n private alanlar�n�n listelenmesi
		try {
			//Field idField = clazz.getDeclaredField("id");
			Field[] idField = clazz.getDeclaredFields();
			for (Field field : idField) {
				System.out.println(field);
			}
			//idField.setAccessible(true);
			//System.out.println(idField);
		} catch (Exception e) {
			e.printStackTrace();
		}
		
	}
	
}
