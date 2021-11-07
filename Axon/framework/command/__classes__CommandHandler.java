#set( $className = ${classObject.getName()} )
#set( $lowercaseClassName = ${Utils.lowercaseFirstLetter( ${className} )} )
package ${aib.getRootPackageName(true)}.command;

import java.util.*;

import org.axonframework.modelling.command.Repository;

import org.axonframework.commandhandling.CommandHandler;
import org.springframework.stereotype.Component;

#set( $imports = [ "aggregate", "api", "exception", "repository", "validator" ] )
#importStatements( $imports )

/**
 * Command handler for ${className} as outlined for the CQRS pattern.  All creates, updates, deletes and other writes 
 * related to ${className} are handled here as external events, and delegated to an associated Aggregate.
 * 
 * @author ${aib.getAuthor()}
 *
 */
@Component
class ${className}CommandHandler {
#set( $argName = "command" )
#set( $argsAsInput = "#determineArgsAsInput( ${classObject} ${argName} )" )    

    public ${className}CommandHandler( Repository<${className}Aggregate> repository ) {
    	super();
    	this.aggregateRepository = repository;	
    }
    
    @CommandHandler
    public void handle(Create${className}Command command) throws Exception {
    	
    	// --------------------------------------
    	// validate the create command
    	// --------------------------------------    	
		${className}Validator.getInstance().validate( command );
		
    	// --------------------------------------
    	// delegate to the aggregate
    	// -------------------------------------- 
    	aggregateRepository.newInstance(() -> new ${className}Aggregate(command));
    }


    @CommandHandler
    public void handle(Update${className}Command command) throws Exception {
    	// --------------------------------------
    	// validate the update command
    	// --------------------------------------    	
		${className}Validator.getInstance().validate( command );

    	// --------------------------------------
    	// delegate to the aggregate
    	// --------------------------------------
    	aggregateRepository.newInstance(() -> new ${className}Aggregate(command));
    }

    
    @CommandHandler
    public void handle(Delete${className}Command command) throws Exception {
    	// --------------------------------------
    	// validate the delete command
    	// --------------------------------------    	
		${className}Validator.getInstance().validate( command );    
		
    	// --------------------------------------
    	// delegate to the aggregate
    	// --------------------------------------
    	aggregateRepository.newInstance(() -> new ${className}Aggregate(command));
    }
    
    // attributes
    private final Repository<${className}Aggregate> aggregateRepository;
}