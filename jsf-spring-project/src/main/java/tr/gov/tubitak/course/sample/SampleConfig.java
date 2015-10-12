package tr.gov.tubitak.course.sample;

import javax.inject.Inject;

import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Import;
import org.springframework.context.annotation.ImportResource;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Component;

import tr.gov.tubitak.course.service.SampleService;


@Configuration
@ImportResource("classpath:application-context.xml")
public class SampleConfig {
	
	@Qualifier("ogreniKayit")
	@Bean(name="sampleService")
//	@Scope("prototype")
	public SampleService sampleService(){
		
		return new SampleService();
		
	}
}
