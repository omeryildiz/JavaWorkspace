package tr.gov.tubitak.oneylul.reflection;

import java.lang.reflect.Constructor;
import java.lang.reflect.Field;
import java.lang.reflect.Method;

public class ReflectionSample {
	public static void main(String[] args) throws Exception {

		Class clazz = Class.forName("tr.gov.tubitak.oneylul.Ogrenci");

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
		// baþka bir paketteki bir sýnýfýn private alanlarýnýn listelenmesi

		// Field idField = clazz.getDeclaredField("id");
		Field[] idField = clazz.getDeclaredFields();
		for (Field field : idField) {
			System.out.println(field);
		}
		// idField.setAccessible(true);
		// System.out.println(idField);

		Constructor constructor = clazz.getConstructor();
		Object obj = constructor.newInstance();
		Method setIdMethod = clazz.getMethod("setName", String.class);
		setIdMethod.invoke(obj, "omer");
		System.out.println(obj);
		
		Method getAdMethod = clazz.getMethod("getName");
	 	System.out.println(getAdMethod.invoke(obj));
	 	System.out.println(obj);
		

	}

}
