#header()
#set( $packageName = $aib.getRootPackageName() )
package ${packageName};

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.scheduling.annotation.EnableScheduling;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.ApplicationContext;
#if( $aib.getParam( "axon-framework.entity-store-type") == "mongodb" )
import org.springframework.data.mongodb.repository.config.EnableMongoRepositories;
#else
import org.springframework.data.jpa.repository.config.EnableJpaRepositories;	
#end##if( $aib.getParam( "axon-framework.entity-store-type") == "mongodb" )
#if ( $aib.getParam("axon-framework.enableDiscoveryClient") == true )

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cloud.client.discovery.EnableDiscoveryClient;
import org.springframework.cloud.client.ServiceInstance;
import org.springframework.cloud.client.discovery.DiscoveryClient;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
#end##if ( $aib.getParam("axon-framework.enableDiscoveryClient") == true )
@SpringBootApplication
@ComponentScan(basePackages = "${packageName}.*")
#if( $aib.getParam( "axon-framework.entity-store-type") == "mongodb" )
@EnableMongoRepositories(basePackages = "${packageName}.repository")
#else
@EnableJpaRepositories(basePackages = "${packageName}.repository")
#end##if( $aib.getParam( "axon-framework.entity-store-type") == "mongodb" )
#if ( $aib.getParam("axon-framework.enableDiscoveryClient") == true )
@EnableDiscoveryClient 
#end##if ( $aib.getParam("axon-framework.enableDiscoveryClient") == true )
public class Application {

    public static void main(String[] args) {
    	ApplicationContext context = SpringApplication.run(Application.class, args);
    	
		System.out.println( "=================================" );
		System.out.println( "Checking in ApplicationContext for discovered handler components:\n" );
#foreach( $class in $aib.getClassesToGenerate() )
#set( $className = ${class.getName()} )
#set( $lowercaseClassName = ${Utils.lowercaseFirstLetter( ${className} )} )
        System.out.println( "- Contains ${lowercaseClassName}-handler = " + context.containsBeanDefinition("${lowercaseClassName}-handler"));
#end##foreach( $class in $aib.getClassesToGenerate() )
        System.out.println( "=================================" );
    }
    
#if ( $aib.getParam("axon-framework.enableDiscoveryClient") == true )  
	@RestController
	class ServiceInstanceRestController {

		@Autowired
		private DiscoveryClient discoveryClient;	
		
		@RequestMapping("/service-instances/{applicationName}")
		public List<ServiceInstance> serviceInstancesByApplicationName(
			@PathVariable String applicationName) {
			return this.discoveryClient.getInstances(applicationName);
		}
	}
#end##if ( $aib.getParam("axon-framework.enableDiscoveryClient") == true )
}