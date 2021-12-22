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
#if ( $aib.getParam("axon-framework.use-snapshot").equalsIgnoreCase("true") )
@Aggregate(snapshotTriggerDefinition = "${lowercaseClassName}AggregateSnapshotTriggerDefinition")
#else
@Aggregate
#end##if ( $aib.getParam("axon-framework.use-snapshot").equalsIgnoreCase("true") )
public class ${className}Aggregate {  

	// -----------------------------------------
	// Axon requires an empty constructor
    // -----------------------------------------
    public ${className}Aggregate() {
    }

	// ----------------------------------------------
	// intrinsic command handlers
	// ----------------------------------------------
#set( $includeAssociations = false )
#set( $varName = "command" )
#set( $includeId = true )
#set( $args = "#determineArgsAsInput( $classObject $varName $includeAssociations $includeId )" )
    @CommandHandler
    public ${className}Aggregate(${classObject.getCreateCommandAlias()} command) throws Exception {
    	LOGGER.info( "Handling command ${classObject.getCreateCommandAlias()}" );
    	${classObject.getCreateEventAlias()} event = new ${classObject.getCreateEventAlias()}(${args});
    	
        apply(event);
    }

#set( $includeAssociations = true )
#set( $varName = "command" )
#set( $includeId = true )
#set( $args = "#determineArgsAsInput( $classObject $varName $includeAssociations $includeId )" )
    @CommandHandler
    public void handle(${classObject.getUpdateCommandAlias()} command) throws Exception {
    	LOGGER.info( "handling command ${classObject.getUpdateCommandAlias()}" );
    	${classObject.getUpdateEventAlias()} event = new ${classObject.getUpdateEventAlias()}(${args});        
    	
        apply(event);
    }

    @CommandHandler
    public void handle(${classObject.getDeleteCommandAlias()} command) throws Exception {
    	LOGGER.info( "Handling command ${classObject.getDeleteCommandAlias()}" );
        apply(new ${classObject.getDeleteEventAlias()}(command.get${className}Id()));
    }

	// ----------------------------------------------
	// association command handlers
	// ----------------------------------------------

    // single association commands
#set( $includeComposites = false )
#foreach( $singleAssociation in $classObject.getSingleAssociations( ${includeComposites} ) )
#set( $roleName = $singleAssociation.getRoleName() )
#set( $childType = $singleAssociation.getType() )
#set( $lowercaseRoleName = $Utils.lowercaseFirstLetter( $roleName ) )
#set( $alias = ${singleAssociation.getAssignToCommandAlias()})
    @CommandHandler
    public void handle(${alias} command) throws Exception {
    	LOGGER.info( "Handling command ${alias}" );
    	
    	if (  $lowercaseRoleName != null && ${lowercaseRoleName}.get${childType}Id() == command.getAssignment().get${childType}Id() )
    		throw new ProcessingException( "${roleName} already assigned with id " + command.getAssignment().get${childType}Id() );  
    		
        apply(new ${singleAssociation.getAssignToEventAlias()}(command.get${className}Id(), command.getAssignment()));
    }

#set( $alias = ${singleAssociation.getUnAssignFromCommandAlias()})
    @CommandHandler
    public void handle(${alias} command) throws Exception {
    	LOGGER.info( "Handlign command ${alias}" );

    	if (  $lowercaseRoleName == null )
    		throw new ProcessingException( "${roleName} already has nothing assigned." );  

    	apply(new ${singleAssociation.getUnAssignFromEventAlias()}(command.get${className}Id()));
    }
#end##foreach( $singleAssociation in $classObject.getSingleAssociations( ${includeComposites} ) )

    // multiple association commands
#set( $includeComposites = false )
#foreach( $multiAssociation in $classObject.getMultipleAssociations() )
#set( $roleName = $multiAssociation.getRoleName() )
#set( $childType = $multiAssociation.getType() )
#set( $lowercaseRoleName = $Utils.lowercaseFirstLetter( $roleName ) )
#set( $alias = ${multiAssociation.getAddToCommandAlias()})
    @CommandHandler
    public void handle(${alias} command) throws Exception {
    	LOGGER.info( "Handling command ${alias}" );
    	
    	if ( ${lowercaseRoleName}.contains( command.getAddTo() ) )
    		throw new ProcessingException( "${roleName} already contains an entity with id " + command.getAddTo().get${childType}Id() );

    	apply(new ${multiAssociation.getAddToEventAlias()}(command.get${className}Id(), command.getAddTo()));
    }

#set( $alias = ${multiAssociation.getRemoveFromCommandAlias()})
    @CommandHandler
    public void handle(${alias} command) throws Exception {
    	LOGGER.info( "Handling command ${alias}" );
    	
    	if ( !${lowercaseRoleName}.contains( command.getRemoveFrom() ) )
    		throw new ProcessingException( "${roleName} does not contain an entity with id " + command.getRemoveFrom().get${childType}Id() );

        apply(new ${multiAssociation.getRemoveFromEventAlias()}(command.get${className}Id(), command.getRemoveFrom()));
    }
#end##foreach( $multiAssociation in $classObject.getMultipleAssociations() )

	// ----------------------------------------------
	// intrinsic event source handlers
	// ----------------------------------------------
    @EventSourcingHandler
    void on(${classObject.getCreateEventAlias()} event) {	
    	LOGGER.info( "Event sourcing ${classObject.getCreateEventAlias()}" );
    	this.${pk} = event.get${className}Id();
#set( $includeAssociations = false )    	
#applyAggregateAttributes( $classObject, "event", $includeAssociations )    
    }
    
    @EventSourcingHandler
    void on(${classObject.getUpdateEventAlias()} event) {
    	LOGGER.info( "Event sourcing classObject.getUpdateEventAlias()}" );
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
#set( $alias = ${singleAssociation.getAssignToEventAlias()} )
	// single associations
    @EventSourcingHandler
    void on(${alias} event ) {	
    	LOGGER.info( "Event sourcing ${alias}" );
    	this.${lowercaseRoleName} = event.getAssignment();
    }

#set( $alias = $singleAssociation.getUnAssignFromEventAlias() )
	@EventSourcingHandler
	void on(${alias} event ) {	
		LOGGER.info( "Event sourcing ${alias}" );
		this.${lowercaseRoleName} = null;
	}
#end##foreach( $singleAssociation in $classObject.getSingleAssociations( ${includeComposites} ) )

#set( $includeComposites = false )
#foreach( $multipleAssociation in $classObject.getMultipleAssociations() )
#set( $roleName = $Utils.capitalizeFirstLetter( $multipleAssociation.getRoleName() ) )
#set( $lowercaseRoleName = $Utils.lowercaseFirstLetter( $roleName ) )
#set( $alias = ${multipleAssociation.getAddToEventAlias()} )
	// multiple associations
    @EventSourcingHandler
    void on(${alias} event ) {
    	LOGGER.info( "Event sourcing ${alias}" );
    	this.${lowercaseRoleName}.add( event.getAddTo() );
    }

#set( $alias = $multipleAssociation.getRemoveFromEventAlias() )
	@EventSourcingHandler
	void on(${alias} event ) {	
		LOGGER.info( "Event sourcing ${alias}" );
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
