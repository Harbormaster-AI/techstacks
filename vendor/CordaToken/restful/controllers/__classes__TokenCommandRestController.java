#set( $callArgs = "#classArgsAsFunctionCall( $classObject ) " )
#set( $var = "${lowercaseClassName}${identifierFieldName}" )
#set( $className = ${classObject.getName()} )
#set( $lowercaseClassName = ${Utils.lowercaseFirstLetter( ${className} )} )
#set( $tokenSystemName = ${aib.getParam( "corda.token-system-name" ).toLowerCase()} )
#set( $identifierFieldName = "Id" )
#header()
package ${aib.getRootPackageName(true)}.#getRestControllerPackageName().command;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.*;
import java.util.concurrent.*;
import java.util.logging.Level;
import java.util.logging.Logger;

#set( $imports = [ "api", "exception", "projector", "delegate", "entity" ] )
#importStatements( $imports )

import ${aib.getRootPackageName(true)}.${tokenSystemName}market.flows.Create${className}Token;
import ${aib.getRootPackageName(true)}.${tokenSystemName}market.flows.TransferPartToken;
import ${aib.getRootPackageName(true)}.${tokenSystemName}market.flows.TotalPart;

import ${aib.getRootPackageName(true)}.helper.corda.CordaHelper;
import ${aib.getRootPackageName(true)}.helper.corda.api.PartyEnum;

import ${aib.getRootPackageName(true)}.#getRestControllerPackageName().BaseCordaSpringRestController;

import net.corda.core.identity.Party;

/** 
 * Implements Struts action processing for business entity ${className}.
 *
 * @author $aib.getAuthor()
 */
@CrossOrigin
@RestController
@RequestMapping("/${className}Token")
public class ${className}TokenCommandRestController extends BaseCordaSpringRestController {
	
    /**
     * Handles create a ${className} token.  
     * @param		UUID	${lowercaseClassName}Id
     * @return		CordaFuture
     */
	@PostMapping("/createToken")
    public net.corda.core.concurrent.CordaFuture createToken( @RequestParam(required=true) UUID ${lowercaseClassName}Id ) {

		// ------------------------------------------------
		//  the identifier must be associated with an existing $className
		// ------------------------------------------------
		$className $lowercaseClassName = get${className}( ${lowercaseClassName}Id );

		if ( $lowercaseClassName == null ) {
			LOGGER.log( Level.WARNING, "A ${className} with Id " + ${lowercaseClassName}Id + " does not exist in the persistent store" );
			return null;
		}
		
		LOGGER.log( Level.INFO, "Successfully found a ${className} in the entity store for UUID {0}", ${lowercaseClassName}Id );

		net.corda.core.messaging.CordaRPCOps proxy = proxy(PartyEnum.Notary);
		net.corda.core.concurrent.CordaFuture future = null;
		
		if ( proxy != null ) {
			LOGGER.info( "Located a Corda Notary" );
			LOGGER.log( Level.INFO, "Starting a flow for Create${className}Token for UUID {0}", ${lowercaseClassName} );
			future = proxy.startFlowDynamic(Create${className}Token.class, 
									${callArgs})
									.getReturnValue();
		}
		else {
			LOGGER.warning( "Failed to acquire an RPC proxy to a Corda Notary" );
		}
	
		return future;
    }

 
    /**
     * Handles deleting a ${className} entity as a token
     * @param		${lowercaseClassName}Id	UUID 
     * @return		net.corda.core.concurrent.CordaFuture 
     */
    @DeleteMapping("/destroyToken")    
    public net.corda.core.concurrent.CordaFuture destroyToken( @RequestBody(required=true) UUID ${lowercaseClassName}Id, PartyEnum partyEnum) {                

    	// -------------------------------------------------------
    	// validate the existenence of ${var} in the entity store
    	// -------------------------------------------------------
    	// TO DO
    	
		net.corda.core.messaging.CordaRPCOps proxy = proxy( partyEnum );
		net.corda.core.concurrent.CordaFuture future = null;
    	Party party = null;
    	boolean exactMatch = true;
		
		if ( proxy != null ) {
			party = party( partyEnum );

			if( party != null ) {
				LOGGER.log( Level.INFO, "Located a Corda Party for {0}", partyEnum.toString());
				LOGGER.info( "Starting a flow for TotalPart" );

				future = proxy.startFlowDynamic(TotalPart.class, 
								"${className}",
								${lowercaseClassName}Id).getReturnValue();
			}
			else {
				LOGGER.log( Level.WARNING, "Failed to locate a Corda Party for node {0}", partyEnum.toString() );
			}
		}
		else {
			LOGGER.log( Level.WARNING, "Failed to acquire an RPC Proxy to node {0}", partyEnum.toString() );
		}
	
		return future;

	}        
    
    /**
     * Handles transfering a token to a given Party
     * @param		${lowercaseClassName}Id	UUID 
     * @param		partyEnum					PartyEnum
     * @return		net.corda.core.concurrent.CordaFuture 
     */
    @PostMapping("/transferToken")    
    public net.corda.core.concurrent.CordaFuture transferToken( @RequestBody(required=true) UUID ${lowercaseClassName}Id, @RequestBody(required=true) PartyEnum partyEnum) {                

    	// -------------------------------------------------------
    	// validate the existenence of ${var} in the entity store
    	// -------------------------------------------------------
    	// TO DO

		net.corda.core.messaging.CordaRPCOps proxy = proxy( partyEnum );
		net.corda.core.concurrent.CordaFuture future = null;
    	Party party = null;
    	boolean exactMatch = true;
		
		if ( proxy != null ) {
			party = party( partyEnum );

			if( party != null ) {
	    		
				LOGGER.log( Level.INFO, "Located a Corda Party for {0}", partyEnum.toString());
				LOGGER.info( "Starting a flow for TransferPartToken" );

				future = proxy.startFlowDynamic(TransferPartToken.class, 
								"${className}", 
								${lowercaseClassName}Id, 
								party).getReturnValue();
			}
			else {
				LOGGER.log( Level.WARNING, "Failed to locate a Corda Party for node {0}", partyEnum.toString() );
			}
		}
		else {
			LOGGER.log( Level.WARNING, "Failed to acquire an RPC Proxy to node {0}", partyEnum.toString() );
		}
	
		return future;
    }
	
    
    /**
     * Helper method to return a $className entity from the entity store
     * 
     * @param	${lowercaseClassName} UUID
     * @return	$className
     */
    protected $className get${className}( UUID ${lowercaseClassName}Id ) {

		// ------------------------------------------------
		//  the identifier must be associated with an existing $className
		// ------------------------------------------------
		$className $lowercaseClassName = null;

    	try {  
    		$lowercaseClassName = ${className}BusinessDelegate.get${className}Instance().get${className}( new ${className}FetchOneSummary( ${lowercaseClassName}Id ) );   
        }
        catch( Throwable exc ) {
            LOGGER.log( Level.WARNING, "failed to load ${className} using Id " + ${lowercaseClassName}Id );
            return null;
        }
    	
    	return $lowercaseClassName;

    }
  
//************************************************************************    
// Attributes
//************************************************************************
    private static final Logger LOGGER = Logger.getLogger(${className}TokenCommandRestController.class.getName());
    
}
