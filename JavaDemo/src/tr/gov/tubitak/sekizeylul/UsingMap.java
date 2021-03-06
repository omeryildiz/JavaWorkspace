package tr.gov.tubitak.sekizeylul;

import java.util.Collection;
import java.util.HashMap;
import java.util.Map;
import java.util.Set;

public class UsingMap {

	public static void main(String[] args) {
		//map �zerinde key taraf�n� set olarak tutmu�tur. ��nk� key benzersiz olmal�d�r.
		//map'in values k�sm� collection d�ner.
		Map<String, String> myMap = new HashMap<>();
		//hasmap k�sm�na linkedhashmap dersek liste s�ral� gelir.
		//new LinkedHashMap<>();
		myMap.put("342", "�mer");
		myMap.put("343", "sami");
		myMap.put("344", "mehmet");
		myMap.put("345", "hediye");
		
		Set<String> keySet = myMap.keySet();
		Collection<String> values = myMap.values();
		
		for(String key : keySet)
		{
			System.out.println(key);
		}
		CollectionFrameworkSamples.cizgiCek();
		for(String value : values)
		{
			System.out.println(value);
		}
		CollectionFrameworkSamples.cizgiCek();
		
		//1. dola�ma y�ntemi
		for (String key : keySet) {
			System.out.println(key+""+myMap.get(key));
		}
		CollectionFrameworkSamples.cizgiCek();
		//mapin 2. dola�ma y�ntemi
		for (Map.Entry<String, String> entry : myMap.entrySet()) {
			System.out.println(entry.getKey()+" = "+entry.getValue());
		}
		
		
		
	}

}
