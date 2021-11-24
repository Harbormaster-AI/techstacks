#header()
#set( $packageName = $aib.getRootPackageName() )
#set( $mongoEntityStoreInUse = false )
#if( $aib.getParam( "axon-framework.using-mongodb-as-entity-store") == "true" )
#set( $mongoEntityStoreInUse = true )
#end
package ${packageName};

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.scheduling.annotation.EnableScheduling;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.ApplicationContext;
#if ( $mongoEntityStoreInUse == true )
import org.springframework.data.mongodb.repository.config.EnableMongoRepositories;
#end##if ( $mongoEntityStoreInUse == true )
@SpringBootApplication
@ComponentScan(basePackages = "${packageName}.*")
#if ( $mongoEntityStoreInUse == true )
@EnableMongoRepositories(basePackages = "${packageName}.repository")
#end##if ( $mongoEntityStoreInUse == true )
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