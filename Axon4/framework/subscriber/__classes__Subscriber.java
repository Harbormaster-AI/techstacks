#header()
#set( $className = ${classObject.getName()} )
#set( $lowercaseClassName = ${Utils.lowercaseFirstLetter( ${className} )} )
package ${aib.getRootPackageName(true)}.subscriber;

import java.util.*;
import java.util.logging.Level;
import java.util.logging.Logger;

import org.axonframework.queryhandling.QueryGateway;
import org.axonframework.queryhandling.SubscriptionQueryResult;
import org.axonframework.messaging.responsetypes.ResponseTypes;

import org.springframework.context.ApplicationContext;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.messaging.handler.annotation.DestinationVariable;
import org.springframework.stereotype.Component;


#set( $imports = [ "api", "entity", "exception" ] )
#importStatements( $imports )

/**
 * Subscriber for ${className} related events.  .
 * 
 * @author ${aib.getAuthor()}
 *
 */
@Component("${lowercaseClassName}-subscriber")
public class ${className}Subscriber extends BaseSubscriber {

	public ${className}Subscriber() {
		queryGateway = applicationContext.getBean(QueryGateway.class);
	}
	
    public SubscriptionQueryResult<List<${className}>, ${className}> ${lowercaseClassName}Subscribe() {
        return queryGateway
                .subscriptionQuery(new FindAll${className}Query(), 
                		ResponseTypes.multipleInstancesOf(${className}.class),
                		ResponseTypes.instanceOf(${className}.class));
    }

    public SubscriptionQueryResult<${className}, ${className}> ${lowercaseClassName}Subscribe(@DestinationVariable UUID ${lowercaseClassName}Id) {
        return queryGateway
                .subscriptionQuery(new Find${className}Query(new Load${className}Filter(${lowercaseClassName}Id)), 
                		ResponseTypes.instanceOf(${className}.class),
                		ResponseTypes.instanceOf(${className}.class));
    }

#foreach( $query in $aib.getQueriesToGenerate(${className}) )
#foreach( $handler in $query.getHandlers() )
#set( $method = $handler.getMethodObject() )
#if ( ${method.hasArguments()} )
#set( $queryName = $Utils.capitalizeFirstLetter( $handler.getName() ) )
#set( $argType = ${method.getArguments().getArgs().get(0).getType()} )
#set( $argName = ${method.getArguments().getArgs().get(0).getName()} )
#if ( $handler.getSingleValueReturnValue() == true )
#set( $returnType = "SubscriptionQueryResult<${className}, ${className}>" )
#else
#set( $returnType = "SubscriptionQueryResult<List<${className}>, ${className}>" )	
#end##if ( $handler.getSingleValueReturnValue() == true )
	public ${returnType} ${method.getName()}Subscribe( ${queryName}Query query ) {
#if ( $handler.getSingleValueReturnValue() == true )
		return queryGateway.subscriptionQuery(query, 
			ResponseTypes.instanceOf(${className}.class), 
			ResponseTypes.instanceOf(${className}.class) );
#else
	    return queryGateway.subscriptionQuery(query, 
			ResponseTypes.multipleInstancesOf(${className}.class), 
			ResponseTypes.instanceOf(${className}.class) );
#end##if ( $handler.getSingleValueReturnValue() == true )
    }
	
#end##if ( ${method.hasArguments()} )
#end##foreach( $handler in $query.getHandlers() )
#end##foreach( $query in $aib.getQueriesToGenerate($className) )    

#* WE DO NOT HAVE QUERY HANDLERS FOR THESE

#set( $includeComposites = false )
#foreach( $singleAssociation in $classObject.getSingleAssociations( ${includeComposites} ) )
#set( $roleName = $singleAssociation.getRoleName() )
	public Flux<${className}> ${roleName}Subscribe(@DestinationVariable UUID ${lowercaseClassName}Id) {
	    return queryGateway
	            .subscriptionQuery(new Find${roleName}For${className}(${lowercaseClassName}Id),ResponseTypes.instanceOf( ${className}.class));
	}
#end##foreach( $singleAssociation in $classObject.getSingleAssociations( ${includeComposites} ) )
#foreach( $multiAssociation in $class.getMultipleAssociations() )
#set( $roleName = $multiAssociation.getRoleName() )
	public Flux<${className}> ${roleName}Subscribe(@DestinationVariable UUID ${lowercaseClassName}Id) {
		return queryGateway
				.subscriptionQuery(new Find${roleName}For${className}(${lowercaseClassName}Id), ResponseTypes.instanceOf(${className}.class));
	}
#end##foreach( $multiAssociation in $class.getMultipleAssociations() )

*#

    // -------------------------------------------------
    // attributes
    // -------------------------------------------------
	@Autowired
    private final QueryGateway queryGateway;
}

