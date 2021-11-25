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
	
@SpringBootApplication
@ComponentScan(basePackages = "${packageName}.*")
#if( $aib.getParam( "axon-framework.entity-store-type") == "mongodb" )
@EnableMongoRepositories(basePackages = "${packageName}.repository")
#else
@EnableJpaRepositories(basePackages = "${packageName}.repository")
#end##if( $aib.getParam( "axon-framework.entity-store-type") == "mongodb" )
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
}