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
    
	// ----------------------------------------------
	// intrinsic command handlers
	// ----------------------------------------------

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

	// ----------------------------------------------
	// association command handlers
	// ----------------------------------------------

    // single association commands
#set( $includeComposites = false )
#foreach( $singleAssociation in $classObject.getSingleAssociations( ${includeComposites} ) )
#set( $roleName = $singleAssociation.getRoleName() )
#set( $childType = $singleAssociation.getType() )
    @CommandHandler
    public void handle(Assign${roleName}To${className}Command command) throws Exception {
    	LOGGER.info( "Applying event Assign${roleName}To${className}Event" );
        apply(new Assigned${roleName}To${className}Event(command.get${className}Id(), command.getAssignment()));
    }

    @CommandHandler
    public void handle(UnAssign${roleName}From${className}Command command) throws Exception {
    	LOGGER.info( "Applying event UnAssign${roleName}From${className}Event" );
        apply(new UnAssigned${roleName}From${className}Event(command.get${className}Id()));
    }
#end##foreach( $singleAssociation in $classObject.getSingleAssociations( ${includeComposites} ) )

    // multiple association commands
#set( $includeComposites = false )
#foreach( $multiAssociation in $classObject.getMultipleAssociations() )
#set( $roleName = $multiAssociation.getRoleName() )
#set( $childType = $multiAssociation.getType() )
    @CommandHandler
    public void handle(Add${roleName}To${className}Command command) throws Exception {
    	LOGGER.info( "Applying event Add${roleName}To${className}Event" );
        apply(new Added${roleName}To${className}Event(command.get${className}Id(), command.getAddTo()));
    }

    @CommandHandler
    public void handle(Remove${roleName}From${className}Command command) throws Exception {
    	LOGGER.info( "Applying event Remove${roleName}From${className}Event" );
        apply(new Removed${roleName}From${className}Event(command.get${className}Id(), command.getRemoveFrom()));
    }
#end##foreach( $multiAssociation in $classObject.getMultipleAssociations() )

	// ----------------------------------------------
	// intrinsic event source handlers
	// ----------------------------------------------
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

	// ----------------------------------------------
	// association event source handlers
	// ----------------------------------------------
#set( $includeComposites = false )
#foreach( $singleAssociation in $classObject.getSingleAssociations( ${includeComposites} ) )
#set( $roleName = $Utils.capitalizeFirstLetter( $singleAssociation.getRoleName() ) )
#set( $lowercaseRoleName = $Utils.lowercaseFirstLetter( $roleName ) )

	// single associations
    @EventSourcingHandler
    void on(Assigned${roleName}To${className}Event event ) {	
    	LOGGER.info( "Event sourcing Assigned${roleName}To${className}Event" );
    	this.${lowercaseRoleName} = event.getAssignment();
    }

	@EventSourcingHandler
	void on(UnAssigned${roleName}From${className}Event event ) {	
		LOGGER.info( "UnAssigned${roleName}From${className}Event" );
		this.${lowercaseRoleName} = null;
	}
#end##foreach( $singleAssociation in $classObject.getSingleAssociations( ${includeComposites} ) )

#set( $includeComposites = false )
#foreach( $multipleAssociation in $classObject.getMultipleAssociations() )
#set( $roleName = $Utils.capitalizeFirstLetter( $multipleAssociation.getRoleName() ) )
#set( $lowercaseRoleName = $Utils.lowercaseFirstLetter( $roleName ) )
	// multiple associations
    @EventSourcingHandler
    void on(Added${roleName}To${className}Event event ) {
    	LOGGER.info( "Event sourcing Added${roleName}To${className}Event" );
    	this.${lowercaseRoleName}.add( event.getAddTo() );
    }

	@EventSourcingHandler
	void on(Removed${roleName}From${className}Event event ) {	
		LOGGER.info( "Event sourcing Removed${roleName}From${className}Event" );
		this.${lowercaseRoleName}.remove( event.getRemoveFrom() );
	}
	
#end##foreach( $multipleAssociation in $classObject.getMultipleAssociations ) )

    // ------------------------------------------
    // attributes
    // ------------------------------------------
	
    @AggregateIdentifier
    private UUID ${pk};
    
#declareAggregateAttributes(  $classObject  )  
#declareAggregateAssociations( $classObject )

    private static final Logger LOGGER 	= Logger.getLogger(${className}Aggregate.class.getName());
}
