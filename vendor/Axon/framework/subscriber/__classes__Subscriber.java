#header()
#set( $className = ${classObject.getName()} )
#set( $lowercaseClassName = ${Utils.lowercaseFirstLetter( ${className} )} )
package ${aib.getRootPackageName(true)}.subscriber;

import java.util.*;
import java.util.logging.Level;
import java.util.logging.Logger;

import org.axonframework.extensions.reactor.queryhandling.gateway.ReactorQueryGateway;
import org.axonframework.messaging.responsetypes.ResponseTypes;

import org.springframework.context.ApplicationContext;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.messaging.handler.annotation.DestinationVariable;
import org.springframework.stereotype.Component;

import reactor.core.publisher.Flux;

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
		reactorQueryGateway = applicationContext.getBean(ReactorQueryGateway.class);
	}
	
    public Flux<${className}> ${lowercaseClassName}Subscribe() {
        return reactorQueryGateway
                .subscriptionQuery(new FindAll${className}(), ResponseTypes.instanceOf(${className}.class));
    }

    public Flux<${className}> ${lowercaseClassName}Subscribe(@DestinationVariable UUID ${lowercaseClassName}Id) {
        return reactorQueryGateway
                .subscriptionQuery(new Find${className}(${lowercaseClassName}Id), ResponseTypes.instanceOf(${className}.class));
    }
 
#set( $includeComposites = false )
#foreach( $singleAssociation in $classObject.getSingleAssociations( ${includeComposites} ) )
#set( $roleName = $singleAssociation.getRoleName() )
	public Flux<${className}> ${roleName}Subscribe(@DestinationVariable UUID ${lowercaseClassName}Id) {
	    return reactorQueryGateway
	            .subscriptionQuery(new Find${roleName}For${className}(${lowercaseClassName}Id),ResponseTypes.instanceOf( ${className}.class));
	}
#end##foreach( $singleAssociation in $classObject.getSingleAssociations( ${includeComposites} ) )
#foreach( $multiAssociation in $class.getMultipleAssociations() )
#set( $roleName = $multiAssociation.getRoleName() )
	public Flux<${className}> ${roleName}Subscribe(@DestinationVariable UUID ${lowercaseClassName}Id) {
		return reactorQueryGateway
				.subscriptionQuery(new Find${roleName}For${className}(${lowercaseClassName}Id), ResponseTypes.instanceOf(${className}.class));
	}
#end##foreach( $multiAssociation in $class.getMultipleAssociations() )
    
    // -------------------------------------------------
    // attributes
    // -------------------------------------------------
	@Autowired
    private final ReactorQueryGateway reactorQueryGateway;
}

