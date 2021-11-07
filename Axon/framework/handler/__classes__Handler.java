#header()
#set( $className = ${classObject.getName()} )
#set( $lowercaseClassName = ${Utils.lowercaseFirstLetter( ${className} )} )
#set( $pk = "${className}Id" )		
package ${aib.getRootPackageName(true)}.handler;

import org.axonframework.config.ProcessingGroup;
import org.axonframework.eventhandling.EventHandler;
import org.axonframework.queryhandling.QueryHandler;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.*;
import java.util.logging.Level;
import java.util.logging.Logger;
import java.util.stream.Collectors;

import javax.persistence.EntityManager;
import javax.persistence.Query;

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
	${className}Handler(EntityManager entityManager) {
        this.entityManager = entityManager;
    }	
	
#set( $argName = "event" )
#set( $argsAsInput = "#determineArgsAsInput( ${classObject} ${argName} )" )
    /*
     * @param	event Create${className}Event
     */
    @EventHandler
    public void handle( Created${className}Event event) {
	LOGGER.info("handling Created${className}Event - " + event );
        entityManager.persist(new ${className}(${argsAsInput}));
    }

    /*
     * @param	event Update${className}Event
     */
    @EventHandler
    public void handle( Updated${className}Event event) {
    	LOGGER.info("handling Updated${className}Event - " + event );
    	entityManager.merge(new ${className}(${argsAsInput}));
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
    private static final Logger LOGGER 	= Logger.getLogger(${className}Handler.class.getName());

}
