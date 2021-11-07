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
@Component("${lowercaseClassName}-handler")
@ProcessingGroup("${lowercaseClassName}-processing-group")
public class ${className}EventHandler {
	
	// core constructor
	${className}EventHandler(EntityManager entityManager) {
        this.entityManager = entityManager;
    }	
	
#set( $argName = "event" )
#set( $argsAsInput = "#determineArgsAsInput( ${classObject} ${argName} )" )
    /*
     * @param	event Create${className}Event
     */
    @EventHandler
    public void handle( Created${className}Event event) {
        entityManager.persist(new ${className}(${argsAsInput}));
    }

    /*
     * @param	event Update${className}Event
     */
    @EventHandler
    public ${className} handle( Updated${className}Event event) {
    	entityManager.merge(new ${className}(${argsAsInput}));
    }
    
    /*
     * @param	event Delete${className}Event
     */
    @EventHandler
    public void on( Deleted${className}Event event) {
    	${className} entity = new ${className}();
   
    	// ------------------------------------------
    	// apply the ${className}Id
    	// ------------------------------------------
    	entity.set${className}Id( event.get${className}Id() );
    	
    	// ------------------------------------------
    	// add to an Iterable and delete
    	// ------------------------------------------
    	entityManager.remove( entity );
    }    
    
    // attributes
	private final ${className}Repository ${lowercaseClassName}EntityRepository;
    private static final Logger LOGGER 			= Logger.getLogger(${className}EventHandler.class.getName());

}
