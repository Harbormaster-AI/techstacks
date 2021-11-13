#header()
#set( $className = ${classObject.getName()} )
#set( $lowercaseClassName = ${Utils.lowercaseFirstLetter( ${className} )} )
#set( $pk = "${className}Id" )		
package ${aib.getRootPackageName(true)}.handler;

import java.util.*;
import java.util.logging.Level;
import java.util.logging.Logger;
import java.util.stream.Collectors;

import javax.persistence.EntityManager;
import javax.persistence.Query;
import javax.persistence.TypedQuery;

import org.axonframework.config.ProcessingGroup;
import org.axonframework.eventhandling.EventHandler;
import org.axonframework.queryhandling.QueryHandler;
import org.axonframework.queryhandling.QueryUpdateEmitter;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;


#set( $imports = [ "api", "entity", "exception" ] )
#importStatements( $imports )

/**
 * Handler for ${className} as outlined for the CQRS pattern.  All event handling and query handling related to ${className} are invoked here
 * and dispersed as an event to be handled elsewhere.
 * 
 * Commands are handled by ${className}Aggregate
 * 
 * @author ${aib.getAuthor()}
 *
 */
@ProcessingGroup("${lowercaseClassName}")
@Component("${lowercaseClassName}-handler")
public class ${className}Handler {
		
	// core constructor
	public ${className}Handler(EntityManager entityManager, QueryUpdateEmitter queryUpdateEmitter ) {
        this.entityManager = entityManager;
        this.queryUpdateEmitter = queryUpdateEmitter;
    }	

	/*
     * @param	event Create${className}Event
     */
    @EventHandler( payloadType=Created${className}Event.class )
    public void handle( Created${className}Event event) {
	    LOGGER.info("handling Created${className}Event - " + event );
	    
	    $className entity = new ${className}();
#set( $includeAssociations = false )
#determineArgsAsAssignment( ${classObject} "entity" "event" ${includeAssociations} )        
    	// ------------------------------------------
    	// persist a new one
    	// ------------------------------------------ 
	    entityManager.persist(entity);
        
        // ------------------------------------------
    	// emit to subscribers that find all
    	// ------------------------------------------    	
        emitFindAll${className}( entity );
    }

    /*
     * @param	event Update${className}Event
     */
    @EventHandler( payloadType=Updated${className}Event.class )
    public void handle( Updated${className}Event event) {
    	LOGGER.info("handling Updated${className}Event - " + event );
    	
    	$className entity = new ${className}();

#set( $includeAssociations = true )    	
#determineArgsAsAssignment( ${classObject} "entity" "event" ${includeAssociations} )        
 
    	// ------------------------------------------
    	// merge with an existing instance
    	// ------------------------------------------ 
    	entityManager.merge(entity);

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
     * @param	event Delete${className}Event
     */
    @EventHandler( payloadType=Deleted${className}Event.class )
    public void handle( Deleted${className}Event event) {
    	LOGGER.info("handling Deleted${className}Event - " + event );
    	
    	$className entity = entityManager.find(${className}.class, event.get${className}Id());
    	
    	// ------------------------------------------
    	// delete what is discovered via find on the embedded identifier
    	// ------------------------------------------
    	entityManager.remove( entity );

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
     * @param	event Assigned${roleName}To${className}Event
     */
    @EventHandler( payloadType=Assigned${roleName}To${className}Event.class)
    public void handle( Assigned${roleName}To${className}Event event) {
	    LOGGER.info("handling Assigned${roleName}To${className}Event - " + event );
	    
	    $className entity = entityManager.find(${className}.class, event.get${className}Id());
	    $childType assignment = entityManager.find(${childType}.class, event.getAssignment().get${childType}Id());
	    entity.set${roleName}( assignment );

	    // ------------------------------------------
    	// persist 
    	// ------------------------------------------ 
	    entityManager.persist(entity);
        
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
 * @param	event UnAssigned${roleName}FromEvent
 */
@EventHandler( payloadType=UnAssigned${roleName}From${className}Event.class)
public void handle( UnAssigned${roleName}From${className}Event event) {
    LOGGER.info("handling UnAssigned${roleName}From${className}Event - " + event );
    
    $className entity = entityManager.find(${className}.class, event.get${className}Id());
    
    entity.set${roleName}(null);

    // ------------------------------------------
	// persist 
	// ------------------------------------------ 
    entityManager.persist(entity);
    
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
     * @param	event Added${roleName}To${className}Event
     */
    @EventHandler( payloadType=Added${roleName}To${className}Event.class)
    public void handle( Added${roleName}To${className}Event event) {
	    LOGGER.info("handling Added${roleName}To${className}Event - " + event );
	    
	    $className entity = entityManager.find(${className}.class, event.get${className}Id());
	    $childType child = entityManager.find(${childType}.class, event.getAddTo().get${childType}Id());
	    
	    entity.get${roleName}().add( child );

	    // ------------------------------------------
    	// persist 
    	// ------------------------------------------ 
	    entityManager.persist(entity);
        
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
 * @param	event RemovedFrom${roleName}Event
 */
@EventHandler( payloadType=Removed${roleName}From${className}Event.class)
public void handle( Removed${roleName}From${className}Event event) {
    LOGGER.info("handling Removed${roleName}From${className}Event - " + event );
    
    $className entity = entityManager.find(${className}.class, event.get${className}Id());
    $childType child = entityManager.find(${childType}.class, event.getRemoveFrom().get${childType}Id() );
    
    entity.get${roleName}().remove( child );

    // ------------------------------------------
	// persist 
	// ------------------------------------------ 
    entityManager.persist(entity);
    
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
    	return entityManager.find(${className}.class, id);
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
    	
    	Query query = entityManager.createQuery("SELECT e FROM ${className} e");
        return (List<${className}>) query.getResultList();
    }

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
		$returnType result = null;
        try {  
            TypedQuery<${className}> jpaQuery = entityManager.createNamedQuery("${className}.${method.getName()}", ${className}.class);
            jpaQuery.setParameter("${argName}", query.getFilter().get${Utils.capitalizeFirstLetter(${argName})}());
#if ( $handler.getSingleValueReturnValue() == false)
            jpaQuery.setFirstResult(query.getOffset());
            jpaQuery.setMaxResults(query.getLimit());           
            result = jpaQuery.getResultList();
#else            
            result = jpaQuery.getSingleResult();
#end##if ( $handler.getSingleValueReturnValue() == false)
        }
        catch( Throwable exc ) {
        	LOGGER.log( Level.WARNING, "Failed to ${method.getName()} using " + query.getFilter(), exc );
        }
        
        return result;
	}
#end##if ( ${method.hasArguments()} )	## should only be one argument
#end##foreach( $handler in $query.getHandlers() )
#end##foreach( $query in $aib.getQueriesToGenerate() )


    //--------------------------------------------------
    // attributes
    // --------------------------------------------------
	@Autowired
    private final EntityManager entityManager;
	@Autowired
	private final QueryUpdateEmitter queryUpdateEmitter;
    private static final Logger LOGGER 	= Logger.getLogger(${className}Handler.class.getName());

}
