#header()
#set( $className = ${classObject.getName()} )
#set( $lowercaseClassName = ${Utils.lowercaseFirstLetter( ${className} )} )
#set( $pk = "${className}Id" )		
package ${aib.getRootPackageName(true)}.query;

import org.axonframework.config.ProcessingGroup;
import org.axonframework.eventhandling.EventHandler;
import org.axonframework.queryhandling.QueryHandler;

import org.springframework.context.ApplicationContext;
import org.springframework.stereotype.Component;

import java.util.*;
import java.util.logging.Level;
import java.util.logging.Logger;
import java.util.stream.Collectors;

#set( $imports = [ "api", "entity", "exception", "repository" ] )
#importStatements( $imports )

/**
 * Query handler for ${className} as outlined for the CQRS pattern.  All reads related to ${className} are invoked here
 * and dispersed as an event to be handled elsewhere.
 * 
 * @author ${aib.getAuthor()}
 *
 */
@Component
@ProcessingGroup("${lowercaseClassName}")
public class ${className}QueryHandler {
	
	// default constructor
	protected ${className}QueryHandler() {
	}
	
	
	public ${className}QueryHandler(${className}EntityRepository ${lowercaseClassName}EntityRepository) {
        this.${lowercaseClassName}EntityRepository = ${lowercaseClassName}EntityRepository;
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
    public ${className}Entity handle( Find${className}Query query ) 
    throws ProcessingException, IllegalArgumentException {
    	String id = query.getFilter().get${pk}().toString();
    	return ${lowercaseClassName}EntityRepository.findById(id).orElseThrow(() -> new ProcessingException("Failed to find ${className} using Id " + id));
    }
    
    /**
     * Method to retrieve a collection of all ${className}s
     *
     * @return 	List<${className}> 
     * @exception ProcessingException Thrown if any problems
     */
    @SuppressWarnings("unused")
    @QueryHandler
    public List<${className}Entity> handle( FindAll${className}Query query ) 
    throws ProcessingException {
    	return ${lowercaseClassName}EntityRepository.findAll().stream().collect(Collectors.toList());
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

    // attributes
	private final ${className}EntityRepository ${lowercaseClassName}EntityRepository;

}
