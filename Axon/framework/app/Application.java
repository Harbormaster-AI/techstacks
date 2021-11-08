#header()
#set( $packageName = $aib.getRootPackageName() )

package ${packageName};

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.scheduling.annotation.EnableScheduling;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.ApplicationContext;

@SpringBootApplication
@ComponentScan(basePackages = "${packageName}.*")
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