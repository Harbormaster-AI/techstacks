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
    @EventHandler( payloadType=Created${className}Event.class)
    public void handle( Created${className}Event event) {
	    LOGGER.info("handling Created${className}Event - " + event );
	    
	    $className entity = new ${className}();
#determineArgsAsAssignment( ${classObject} "entity" "event" )        
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
    @EventHandler
    public void handle( Updated${className}Event event) {
    	LOGGER.info("handling Updated${className}Event - " + event );
    	
    	$className entity = new ${className}();
#determineArgsAsAssignment( ${classObject} "entity" "event" )        
 
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
    @EventHandler
    public void handle( Deleted${className}Event event) {
    	LOGGER.info("handling Deleted${className}Event - " + event );
    	${className} entity = new ${className}();
   
    	// ------------------------------------------
    	// apply only the ${className}Id
    	// ------------------------------------------
    	entity.set${className}Id( event.get${className}Id() );
    	
    	// ------------------------------------------
    	// add to an Iterable and delete
    	// ------------------------------------------
    	entityManager.remove( entity );

    	// ------------------------------------------
    	// emit to subscribers that find all
    	// ------------------------------------------    	
        emitFindAll${className}( entity );

    }    

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
    
#*    SECTION COMMENTED OUT
#foreach( $handler in $query.getHandlers() )
#set( $method = $handler.getMethodObject() )
#if ( ${method.hasArguments()} )	## should only be one argument
#set( $argType = ${method.getArguments().getArgs().get(0).getType()} )
#set( $argName = ${method.getArguments().getArgs().get(0).getName()} )
#set( $returnType = ${method.getReturnType()} )
    /**
     * query method to ${method.getName()}
     * @param 		$argType $argName
     * @return		$returnType
     */     
	@SuppressWarnings("unused")
	@QueryHandler
	public $returnType ${method.getName()}( $argType $argName ) {
		$returnType ${lowercaseClassName} = null;
        try {  
            TypedQuery<CardSummary> jpaQuery = entityManager.createNamedQuery("${className}.fetchOne", ${className}.class);
            jpaQuery.setParameter("${lowercaseClassName}Id", query.getFilter().get${className}Id());
            jpaQuery.setFirstResult(query.getOffset());
            jpaQuery.setMaxResults(query.getLimit());
            return jpaQuery.getResultList();
        }
        catch( Throwable exc ) {
        	LOGGER.info(  "Failed to load ${returnType} - " + exc );
        }
        return {lowercaseClassName};
	}
#end##if ( ${method.hasArguments()} )	## should only be one argument
#end##foreach( $handler in $query.getHandlers() )
*#

    //--------------------------------------------------
    // attributes
    // --------------------------------------------------
	@Autowired
    private final EntityManager entityManager;
	@Autowired
	private final QueryUpdateEmitter queryUpdateEmitter;
    private static final Logger LOGGER 	= Logger.getLogger(${className}Handler.class.getName());

}
