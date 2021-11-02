#set( $className = ${classObject.getName()} )
#set( $lowercaseClassName = ${Utils.lowercaseFirstLetter( ${className} )} )
#set( $pk = "${lowercaseClassName}Id" )		
package ${aib.getRootPackageName(true)}.aggregate;

#set( $imports = [ "api", "entity", "enumerator", "exception", "valueobject"] )
#importStatements( $imports )

import java.util.*;

import org.axonframework.commandhandling.CommandHandler;
import org.axonframework.eventsourcing.EventSourcingHandler;
import org.axonframework.modelling.command.AggregateIdentifier;
import org.axonframework.spring.stereotype.Aggregate;

import static org.axonframework.modelling.command.AggregateLifecycle.apply;

import org.springframework.context.annotation.Profile;

@Profile("command")
@Aggregate(snapshotTriggerDefinition = " ${lowercaseClassName}AggregateSnapshotTriggerDefinition", cache = "${lowercaseClassName}Cache")
class ${className}Aggregate {
	
    @AggregateIdentifier
    private UUID ${pk};

#declareAggregateAttributes(  $classObject  )  

#declareAggregateAssociations( $classObject )  

	// -----------------------------------------
	// Axon requires an empty constructor
    // -----------------------------------------
    public ${className}Aggregate() {
    }
    
#set( $argName = "command" )
#set( $argsAsInput = "#determineArgsAsInput( ${classObject} ${argName} )" )    

    @CommandHandler
    ${className}Aggregate(Create${className}Command command) {
        apply(new Created${className}Event(${argsAsInput}));
    }


    @CommandHandler
    ${className}Aggregate(Update${className}Command command) {
        apply(new Updated${className}Event(${argsAsInput}));
    }

    
    @CommandHandler
    ${className}Aggregate(Delete${className}Command command) {
    	
        apply(new Deleted${className}Event(command.get${className}Id()));
    }

    @EventSourcingHandler
    void on(Created${className}Event event ) {	
#applyAggregateAttributes( $classObject, "event" )    
    }
    
    @EventSourcingHandler
    void on(Updated${className}Event event) {
    	this.${pk} = event.get${className}Id();
#applyAggregateAttributes( $classObject, "event" )    	
    }   
}
