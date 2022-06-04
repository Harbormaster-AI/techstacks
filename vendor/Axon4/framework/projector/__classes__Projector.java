#header()
#set( $className = ${classObject.getName()} )
#set( $lowercaseClassName = ${Utils.lowercaseFirstLetter( ${className} )} )
#set( $pk = "${className}Id" )		
package ${aib.getRootPackageName(true)}.projector;

import java.util.*;
import java.util.logging.Level;
import java.util.logging.Logger;
import java.util.stream.Collectors;

import org.axonframework.config.ProcessingGroup;
import org.axonframework.eventhandling.EventHandler;
import org.axonframework.queryhandling.QueryHandler;
import org.axonframework.queryhandling.QueryUpdateEmitter;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Component;

#set( $imports = [ "api", "entity", "exception", "repository" ] )
#importStatements( $imports )

/**
 * Projector for ${className} as outlined for the CQRS pattern.  All event handling and query handling related to ${className} are invoked here
 * and dispersed as an event to be handled elsewhere.
 * 
 * Commands are handled by ${className}Aggregate
 * 
 * @author ${aib.getAuthor()}
 *
 */
@ProcessingGroup("${lowercaseClassName}")
@Component("${lowercaseClassName}-projector")
public class ${className}Projector extends ${className}EntityProjector {
		
	// core constructor
	public ${className}Projector(${className}Repository repository, QueryUpdateEmitter queryUpdateEmitter ) {
        super(repository);
        this.queryUpdateEmitter = queryUpdateEmitter;
    }	

	/*
     * @param	event ${classObject.getCreateEventAlias()}
     */
    @EventHandler( payloadType=${classObject.getCreateEventAlias()}.class )
    public void handle( ${classObject.getCreateEventAlias()} event) {
	    LOGGER.info("handling ${classObject.getCreateEventAlias()} - " + event );
	    $className entity = new ${className}();
#set( $includeAssociations = false )
#set( $includeId = true )
#determineArgsAsAssignment( ${classObject} "entity" "event" ${includeAssociations} ${includeId} )  
	    
    	// ------------------------------------------
    	// persist a new one
    	// ------------------------------------------ 
	    create(entity);
        
        // ------------------------------------------
    	// emit to subscribers that find all
    	// ------------------------------------------    	
        emitFindAll${className}( entity );
    }

    /*
     * @param	event ${classObject.getUpdateEventAlias()}
     */
    @EventHandler( payloadType=${classObject.getUpdateEventAlias()}.class )
    public void handle( ${classObject.getUpdateEventAlias()} event) {
    	LOGGER.info("handling ${classObject.getUpdateEventAlias()} - " + event );
    	
	    $className entity = new ${className}();
#set( $includeAssociations = true )
#set( $includeId = true )
#determineArgsAsAssignment( ${classObject} "entity" "event" ${includeAssociations} ${includeId} )  
 
    	// ------------------------------------------
    	// save with an existing instance
    	// ------------------------------------------ 
		update(entity);

    	// ------------------------------------------
    	// emit to subscribers that find one
    	// ------------------------------------------    	
        emitFind${className}( entity );

    	// ------------------------------------------
    	// emit to subscribers that find all
    	// ------------------------------------------    	
        emitFindAll${className}( entity );        
    }
    
    /*
     * @param	event ${classObject.getDeleteEventAlias()}
     */
    @EventHandler( payloadType=${classObject.getDeleteEventAlias()}.class )
    public void handle( ${classObject.getDeleteEventAlias()} event) {
    	LOGGER.info("handling ${classObject.getDeleteEventAlias()} - " + event );
    	
    	// ------------------------------------------
    	// delete delegation
    	// ------------------------------------------
    	$className entity = delete( event.get${className}Id() );

    	// ------------------------------------------
    	// emit to subscribers that find all
    	// ------------------------------------------    	
        emitFindAll${className}( entity );

    }    

#set( $includeComposites = false )
#foreach( $singleAssociation in $classObject.getSingleAssociations( ${includeComposites} ) )
#set( $roleName = $Utils.capitalizeFirstLetter( $singleAssociation.getRoleName() ) )
#set( $lowercaseRoleName = $Utils.lowercaseFirstLetter( $roleName ) )
#set( $childType = $singleAssociation.getType() )
    /*
     * @param	event ${singleAssociation.getAssignToEventAlias()}
     */
    @EventHandler( payloadType=${singleAssociation.getAssignToEventAlias()}.class)
    public void handle( ${singleAssociation.getAssignToEventAlias()} event) {
	    LOGGER.info("handling ${singleAssociation.getAssignToEventAlias()} - " + event );

	    // ------------------------------------------
	    // delegate to assignTo
	    // ------------------------------------------
	    $className entity = assign${roleName}( event.get${className}Id(), event.getAssignment() );

	    // ------------------------------------------
    	// emit to subscribers that find one
    	// ------------------------------------------    	
        emitFind${className}( entity );

    	// ------------------------------------------
    	// emit to subscribers that find all
    	// ------------------------------------------    	
        emitFindAll${className}( entity );
    }
    

	/*
	 * @param	event ${singleAssociation.getUnAssignFromEventAlias()}
	 */
	@EventHandler( payloadType=${singleAssociation.getUnAssignFromEventAlias()}.class)
	public void handle( ${singleAssociation.getUnAssignFromEventAlias()} event) {
	    LOGGER.info("handling ${singleAssociation.getUnAssignFromEventAlias()} - " + event );

	    // ------------------------------------------
	    // delegate to unAssignFrom
	    // ------------------------------------------
	    $className entity = unAssign${roleName}( event.get${className}Id() );

		// ------------------------------------------
		// emit to subscribers that find one
		// ------------------------------------------    	
	    emitFind${className}( entity );
	
		// ------------------------------------------
		// emit to subscribers that find all
		// ------------------------------------------    	
	    emitFindAll${className}( entity );
	}

#end##foreach( $singleAssociation in $classObject.getSingleAssociations( ${includeComposites} ) )

#set( $includeComposites = false )
#foreach( $multiAssociation in $classObject.getMultipleAssociations() )
#set( $roleName = $Utils.capitalizeFirstLetter( $multiAssociation.getRoleName() ) )
#set( $lowercaseRoleName = $Utils.lowercaseFirstLetter( $roleName ) )    
#set( $childType = $multiAssociation.getType() )
    /*
     * @param	event ${multiAssociation.getAddToEventAlias()}
     */
    @EventHandler( payloadType=${multiAssociation.getAddToEventAlias()}.class)
    public void handle( ${multiAssociation.getAddToEventAlias()} event) {
	    LOGGER.info("handling ${multiAssociation.getAddToEventAlias()} - " + event );
	    
	    // ------------------------------------------
    	// delegate to addTo 
    	// ------------------------------------------ 
	    $className entity = addTo${roleName}(event.get${className}Id(), event.getAddTo() );
        
    	// ------------------------------------------
    	// emit to subscribers that find one
    	// ------------------------------------------    	
        emitFind${className}( entity );

    	// ------------------------------------------
    	// emit to subscribers that find all
    	// ------------------------------------------    	
        emitFindAll${className}( entity );
    }
    

/*
 * @param	event ${multiAssociation.getRemoveFromEventAlias()}
 */
@EventHandler( payloadType=${multiAssociation.getRemoveFromEventAlias()}.class)
public void handle( ${multiAssociation.getRemoveFromEventAlias()} event) {
    LOGGER.info("handling ${multiAssociation.getRemoveFromEventAlias()} - " + event );

    $className entity = removeFrom${roleName}(event.get${className}Id(), event.getRemoveFrom() );
    
	// ------------------------------------------
	// emit to subscribers that find one
	// ------------------------------------------    	
    emitFind${className}( entity );

	// ------------------------------------------
	// emit to subscribers that find all
	// ------------------------------------------    	
    emitFindAll${className}( entity );
}

#end##foreach( $multiAssociation in $classObject.getMultipleAssociations() )


    /**
     * Method to retrieve the ${className} via an ${className}PrimaryKey.
     * @param 	id Long
     * @return 	${className}
     * @exception ProcessingException - Thrown if processing any related problems
     * @exception IllegalArgumentException 
     */
    @SuppressWarnings("unused")
    @QueryHandler
    public ${className} handle( Find${className}Query query ) 
    throws ProcessingException, IllegalArgumentException {
    	return find( query.getFilter().get${className}Id() );
    }
    
    /**
     * Method to retrieve a collection of all ${className}s
     *
     * @param	query	FindAll${className}Query 
     * @return 	List<${className}> 
     * @exception ProcessingException Thrown if any problems
     */
    @SuppressWarnings("unused")
    @QueryHandler
    public List<${className}> handle( FindAll${className}Query query ) throws ProcessingException {
    	return findAll( query );
    }


	/**
	 * emit to subscription queries of type Find${className}, 
	 * but only if the id matches
	 * 
	 * @param		entity	${className}
	 */
	protected void emitFind${className}( ${className} entity ) {
		LOGGER.info("handling emitFind${className}" );
		
	    queryUpdateEmitter.emit(Find${className}Query.class,
	                            query -> query.getFilter().get${className}Id().equals(entity.get${className}Id()),
	                            entity);
	}
	
	/**
	 * unconditionally emit to subscription queries of type FindAll${className}
	 * 
	 * @param		entity	${className}
	 */
	protected void emitFindAll${className}( ${className} entity ) {
		LOGGER.info("handling emitFindAll${className}" );
		
	    queryUpdateEmitter.emit(FindAll${className}Query.class,
	                            query -> true,
	                            entity);
	}

#foreach( $query in $aib.getQueriesToGenerate(${className}) )
#foreach( $handler in $query.getHandlers() )
#set( $method = $handler.getMethodObject() )
#set( $queryName = $Utils.capitalizeFirstLetter( $handler.getName() ) )
#if ( ${method.hasArguments()} )##should only be one argument
#set( $argType = ${method.getArguments().getArgs().get(0).getType()} )
#set( $argName = ${method.getArguments().getArgs().get(0).getName()} )
#set( $returnType = ${method.getArguments().getReturnType()} )
    /**
     * query method to ${method.getName()}
     * @param 		$argType $argName
     * @return		$returnType
     */     
	@SuppressWarnings("unused")
	@QueryHandler
	public $returnType ${method.getName()}( ${queryName}Query query ) {
		LOGGER.info("handling ${method.getName()}" );
		$returnType result = null;
		
		try {
#if ( $handler.getSingleValueReturnValue() == true )
		    result = repository.${method.getName()}( query.getFilter().get${Utils.capitalizeFirstLetter(${argName})}() );
#else##pageable
            Pageable pageable = PageRequest.of(query.getOffset(), query.getLimit());
            result = repository.${method.getName()}( query.getFilter().get${Utils.capitalizeFirstLetter(${argName})}(), pageable );
            LOGGER.log( Level.INFO, "${method.getName()} found {0} " + result.toString() );
#end##if ( $handler.getSingleValueReturnValue() == false)

        }
        catch( Throwable exc ) {
        	LOGGER.log( Level.WARNING, "Failed to ${method.getName()} using " + query.getFilter(), exc );
        }
        
        return result;
	}
#end##if ( ${method.hasArguments()} )## should only be one argument
#end##foreach( $handler in $query.getHandlers() )
#end##foreach( $query in $aib.getQueriesToGenerate() )

	//--------------------------------------------------
    // attributes
    // --------------------------------------------------
	@Autowired
	private final QueryUpdateEmitter queryUpdateEmitter;
    private static final Logger LOGGER 	= Logger.getLogger(${className}Projector.class.getName());

}
