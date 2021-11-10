#set( $className = ${classObject.getName()} )
#set( $lowercaseClassName = ${Utils.lowercaseFirstLetter( ${className} )} )
#set( $pk = "${lowercaseClassName}Id" )		
package ${aib.getRootPackageName(true)}.aggregate;

#set( $imports = [ "api", "entity", "exception" ] )
#importStatements( $imports )

import java.util.*;
import java.util.logging.Level;
import java.util.logging.Logger;

import org.axonframework.commandhandling.CommandHandler;
import org.axonframework.modelling.command.AggregateMember;
import org.axonframework.eventsourcing.EventSourcingHandler;
import org.axonframework.modelling.command.AggregateIdentifier;
import static org.axonframework.modelling.command.AggregateLifecycle.apply;
import org.axonframework.spring.stereotype.Aggregate;
import org.springframework.context.annotation.Profile;

/**
 * Aggregate handler for ${className} as outlined for the CQRS pattern, all write responsibilities 
 * related to ${className} are handled here.
 * 
 * @author ${aib.getAuthor()}
 * 
 */
@Aggregate(snapshotTriggerDefinition = "${lowercaseClassName}AggregateSnapshotTriggerDefinition")
public class ${className}Aggregate {  

	// -----------------------------------------
	// Axon requires an empty constructor
    // -----------------------------------------
    public ${className}Aggregate() {
    }
    

    @CommandHandler
    public ${className}Aggregate(Create${className}Command command) throws Exception {
    	LOGGER.info( "Applying event Created${className}Event" );
    	Created${className}Event event = new Created${className}Event();

#set( $includeAssociations = false )
#determineArgsAsAssignment( ${classObject}  "event" "command" ${includeAssociations} )        
    	
        apply(event);
    }

#set( $argsAsInput = "#determineArgsAsInput( ${classObject} ${argName} ${includeAssociations} )" )    
    @CommandHandler
    public void handle(Update${className}Command command) throws Exception {
    	LOGGER.info( "Applying event Updated${className}Event" );
    	Updated${className}Event event = new Updated${className}Event();

#set( $includeAssociations = true )
#determineArgsAsAssignment( ${classObject}  "event" "command" ${includeAssociations} )        
    	
        apply(event);
    }

    @CommandHandler
    public void handle(Delete${className}Command command) throws Exception {
    	LOGGER.info( "Applying event Deleted${className}Event" );
        apply(new Deleted${className}Event(command.get${className}Id()));
    }

    @EventSourcingHandler
    void on(Created${className}Event event ) {	
    	LOGGER.info( "Event sourcing Created${className}Event" );
    	this.${pk} = event.get${className}Id();
#set( $includeAssociations = false )    	
#applyAggregateAttributes( $classObject, "event", $includeAssociations )    
    }
    
    @EventSourcingHandler
    void on(Updated${className}Event event) {
    	LOGGER.info( "Event sourcing Updated${className}Event" );
#set( $includeAssociations = true )    	    	
#applyAggregateAttributes( $classObject, "event", $includeAssociations )    	
    }   
    
    // ------------------------------------------
    // attributes
    // ------------------------------------------
	
    @AggregateIdentifier
    private UUID ${pk};
    
#declareAggregateAttributes(  $classObject  )  
#declareAggregateAssociations( $classObject )

    private static final Logger LOGGER 	= Logger.getLogger(${className}Aggregate.class.getName());
}
