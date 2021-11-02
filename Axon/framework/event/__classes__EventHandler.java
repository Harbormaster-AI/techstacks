#header()
#set( $className = ${classObject.getName()} )
#set( $lowercaseClassName = ${Utils.lowercaseFirstLetter( ${className} )} )
#set( $pk = "${className}Id" )		
package ${aib.getRootPackageName(true)}.event;

import org.axonframework.config.ProcessingGroup;
import org.axonframework.eventhandling.EventHandler;
import org.springframework.stereotype.Component;

import java.util.*;
import java.util.logging.Level;
import java.util.logging.Logger;
import java.util.stream.Collectors;

#set( $imports = [ "api", "entity", "repository" ] )
#importStatements( $imports )

/**
 * Event handler for ${className} as outlined for the CQRS pattern.  All reads related to ${className} are invoked here
 * and dispersed as an event to be handled elsewhere.
 * 
 * @author ${aib.getAuthor()}
 *
 */
@Component
@ProcessingGroup("${lowercaseClassName}")
public class ${className}EventHandler {
		
	
	// core constructor
	${className}EventHandler(${className}EntityRepository ${lowercaseClassName}EntityRepository) {
        this.${lowercaseClassName}EntityRepository = ${lowercaseClassName}EntityRepository;
    }	
#set( $argName = "event" )
#set( $argsAsInput = "#determineArgsAsInput( ${classObject} ${argName} )" )

    /*
     * @param	event Create${className}Event
     */
    @EventHandler
    public void on( Created${className}Event event) {
        ${lowercaseClassName}EntityRepository.save(new ${className}Entity(${argsAsInput}));
    }

    /*
     * @param	event Update${className}Event
     */
    @EventHandler
    public void on( Updated${className}Event event) {
        ${lowercaseClassName}EntityRepository.save(new ${className}Entity(${argsAsInput}));
    }
    
    /*
     * @param	event Delete${className}Event
     */
    @EventHandler
    public void on( Deleted${className}Event event) {
    	${className}Entity entity = new ${className}Entity();
   
    	// ------------------------------------------
    	// apply the ${className}Id
    	// ------------------------------------------
    	entity.set${className}Id( event.get${className}Id() );
    	
    	// ------------------------------------------
    	// add to an Iterable and delete
    	// ------------------------------------------
    	List<${className}Entity> deleteList = new ArrayList<>();
    	deleteList.add( entity );
        ${lowercaseClassName}EntityRepository.deleteAllInBatch( deleteList );
    }    
    
    // attributes
	private final ${className}EntityRepository ${lowercaseClassName}EntityRepository;

}
