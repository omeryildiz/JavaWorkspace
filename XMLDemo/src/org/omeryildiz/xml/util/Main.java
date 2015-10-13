package org.omeryildiz.xml.util;

import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.InputStream;
import java.nio.file.FileAlreadyExistsException;

import javax.xml.bind.JAXBContext;
import javax.xml.bind.JAXBException;
import javax.xml.bind.Marshaller;
import javax.xml.bind.Unmarshaller;

import org.omeryildiz.xml.sample.AddressType;
import org.omeryildiz.xml.sample.ObjectFactory;
import org.omeryildiz.xml.sample.PersonType;

public class Main {
	public static void main(String[] args) throws JAXBException, FileNotFoundException {
		JAXBContext context = JAXBContext.newInstance(PersonType.class);
		
		System.out.println("XML --> JAVA");
		Unmarshaller u = context.createUnmarshaller();
		InputStream is  = new FileInputStream("D:/egitim/JavaWorkspace/XMLDemo/resource/Person.xml");
		PersonType p = (PersonType) u.unmarshal(is);
		
		
		p.setName("Omer yildiz");
		
		AddressType a = new ObjectFactory().createAddressType();
		a.setNumber(123);
		a.setStreet("gebze caddesi");
		p.getAddress().add(a);
		
		System.out.println(p.getName());
		System.out.println(p.getAddress());
		
		Marshaller m = context.createMarshaller();
		m.marshal(p, System.out);
		
		System.out.println("formatted xml");
		m.setProperty(Marshaller.JAXB_FORMATTED_OUTPUT, true);
		m.marshal(p, System.out);
		
		

	}

}
