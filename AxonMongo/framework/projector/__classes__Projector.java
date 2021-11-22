#header()
#set( $className = ${classObject.getName()} )
#set( $lowercaseClassName = ${Utils.lowercaseFirstLetter( ${className} )} )
#set( $pk = "${className}Id" )		
package ${aib.getRootPackageName(true)}.handler;

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


#set( $imports = [ "api", "entity", "exception", repository ] )
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
@Component("${lowercaseClassName}-handler")
public class ${className}Projector {
		
	// core constructor
	public ${className}Projector(${className}Repository repository, QueryUpdateEmitter queryUpdateEmitter ) {
        this.repository = repository;
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
#determineArgsAsAssignment( ${classObject} "entity" "event" ${includeAssociations} )  
	    
    	// ------------------------------------------
    	// persist a new one
    	// ------------------------------------------ 
	    repository.save(entity);
        
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
#determineArgsAsAssignment( ${classObject} "entity" "event" ${includeAssociations} )  
 
    	// ------------------------------------------
    	// save with an existing instance
    	// ------------------------------------------ 
		repository.save(entity);

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
    	LOGGER.info("handling ${classObject.getCreateEventAlias()} - " + event );
    	
    	$className entity = entityManager.find(${className}.class, event.get${className}Id());
    	
    	// ------------------------------------------
    	// delete what is discovered via find on the embedded identifier
    	// ------------------------------------------
    	repository.delete( entity );

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
	    
	    @Autowired
	    ${childType}Repository chidRepo;
	    $className entity = repository.findById( event.get${className}Id());
	    $childType assignment = chidRepo.find(event.getAssignment().get${childType}Id());
	    
	    // ------------------------------------------
		// assign the $roleName
		// ------------------------------------------ 
	    entity.set${roleName}( assignment );

	    // ------------------------------------------
    	// save 
    	// ------------------------------------------ 
	    entityManager.save(entity);
        
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
    
    $className entity = repository.findById(event.get${className}Id());

    // ------------------------------------------
	// null out the ${roleName}
	// ------------------------------------------     
    entity.set${roleName}(null);

    // ------------------------------------------
	// save 
	// ------------------------------------------ 
    repository.save(entity);
    
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
	    
	    @Autowired
	    ${childType}Repository chidRepo;
	    $className entity = repository.findById(event.get${className}Id());
	    $childType child = childRepo.findById(event.getAddTo().get${childType}Id());
	    
	    entity.get${roleName}().add( child );

	    // ------------------------------------------
    	// save 
    	// ------------------------------------------ 
	    repository.save(entity);
        
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

    @Autowired
    ${childType}Repository chidRepo;
    $className entity = repository.findById(event.get${className}Id());
    $childType child = childRepo.find(${childType}.class, event.getRemoveFrom().get${childType}Id() );
    
    entity.get${roleName}().remove( child );

    // ------------------------------------------
	// save
	// ------------------------------------------ 
    repository.save(entity);
    
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
    	LOGGER.info("handling Find${className}Query" );
    	UUID id = query.getFilter().get${pk}();
    	return repository.findById(id);
    }
    
    /**
     * Method to retrieve a collection of all ${className}s
     *
     * @param	inputQuery	FindAll${className}Query 
     * @return 	List<${className}> 
     * @exception ProcessingException Thrown if any problems
     */
    @SuppressWarnings("unused")
    @QueryHandler
    public List<${className}> handle( FindAll${className}Query inputQuery ) throws ProcessingException {
    	LOGGER.info("handling FindAll${className}Query" );
    	
    	return repository.findAll();
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
#if ( $handler.getSingleValueReturnValue() == false)
		return repository.${method.getName()}( query.getFilter().get${Utils.capitalizeFirstLetter(${argName})}() );
#else##pageable
        Pageable pageable = new PageRequest(query.getOffset(), query.getLimit());
        return repository.${method.getName()}( query.getFilter().get${Utils.capitalizeFirstLetter(${argName})}(), pageable ).toList();
#end##if ( $handler.getSingleValueReturnValue() == false)
		$returnType result = null;
        }
        catch( Throwable exc ) {
        	LOGGER.log( Level.WARNING, "Failed to ${method.getName()} using " + query.getFilter(), exc );
        }
        
        return result;
	}
#end##if ( ${method.hasArguments()} )## should only be one argument
#end##foreach( $handler in $query.getHandlers() )
#end##foreach( $query in $aib.getQueriesToGenerate() )

	/**
	 * emit to subscription queries of type Find${className}, 
	 * but only if the id matches
	 * 
	 * @param		entity	${className}
	 */
	protected void emitFind${className}( ${className} entity ) {
	    queryUpdateEmitter.emit(Find${className}.class,
	                            query -> query.get${className}Id().equals(entity.get${className}Id()),
	                            entity);
	}
	
	/**
	 * unconditionally emit to subscription queries of type FindAll${className}
	 * 
	 * @param		entity	${className}
	 */
	protected void emitFindAll${className}( ${className} entity ) {
	    queryUpdateEmitter.emit(FindAll${className}.class,
	                            query -> true,
	                            entity);
	}


    //--------------------------------------------------
    // attributes
    // --------------------------------------------------
	@Autowired
    private final ${className}Repository repository;
	@Autowired
	private final QueryUpdateEmitter queryUpdateEmitter;
    private static final Logger LOGGER 	= Logger.getLogger(${className}Projector.class.getName());

}
