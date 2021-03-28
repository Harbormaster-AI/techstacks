#header()
package ${aib.getRootPackageName()};

import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.EnableAutoConfiguration;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.autoconfigure.mongo.MongoAutoConfiguration;
import org.springframework.boot.autoconfigure.data.mongo.MongoDataAutoConfiguration;
import org.springframework.boot.builder.SpringApplicationBuilder;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.support.ClassPathXmlApplicationContext;
import org.springframework.context.ApplicationContext;
import org.springframework.web.servlet.view.JstlView;
import org.springframework.web.servlet.view.UrlBasedViewResolver;

@Configuration
@ComponentScan("${aib.getRootPackageName()}")
@EnableAutoConfiguration(exclude = { MongoAutoConfiguration.class, MongoDataAutoConfiguration.class})
@SpringBootApplication
public class Application extends org.springframework.boot.web.servlet.support.SpringBootServletInitializer {

	@Override
	protected SpringApplicationBuilder configure(SpringApplicationBuilder application) {
		return application.sources(Application.class);
	}    
	
	public static void main(String[] args) 
    {
    	ApplicationContext context =
    	    	new ClassPathXmlApplicationContext(new String[] {"spring-bean.xml"});
#if ( $aib.getParamsByPrefix("hibernate").size() > 0  )
    	// jumpstart Hibernate since it can be slow to start-up
    	${aib.getRootPackageName()}.persistent.FrameworkPersistenceHelper.self();
#end    	
        SpringApplication.run(Application.class, args);
    }
    
}
