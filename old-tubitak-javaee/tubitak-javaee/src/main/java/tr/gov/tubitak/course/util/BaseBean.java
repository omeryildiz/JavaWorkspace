package tr.gov.tubitak.course.util;

import java.lang.reflect.ParameterizedType;
import java.lang.reflect.Type;
 
public class BaseBean<E> {
       
       
        public Class<E> getClassInstance(){
                Type genericSuperclass = getClass().getGenericSuperclass();
                if(genericSuperclass instanceof ParameterizedType){
                        Type type = ((ParameterizedType) genericSuperclass).getActualTypeArguments()[0];
                        if(type instanceof Class<?>)
                                return (Class<E>) type;
                        return null;
                }
                return null;
        }
       
       
       
}