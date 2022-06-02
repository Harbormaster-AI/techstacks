#header()
#set( $className = ${classObject.getName()} )
#set( $lowercaseClassName = ${Utils.lowercaseFirstLetter( ${className} )} )
#set( $tokenSystemName = ${aib.getParam( "corda.token-system-name" ).toLowerCase()} )
#set( $identifierFieldName = $display.uncapitalize( $aib.getParam( "corda.identifier-field-name" ) ) )
#set( $tokenSystemName = ${aib.getParam( "corda.token-system-name" ).toLowerCase()} )
package ${aib.getRootPackageName(true)}.#getRestControllerPackageName().command;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.*;
import java.util.concurrent.*;
import java.util.logging.Level;
import java.util.logging.Logger;

#set( $imports = [ "api", "exception", "handler" ] )
#importStatements( $imports )

import ${aib.getRootPackageName(true)}.${tokenSystemName}market.flows.Create${className}Token;
import ${aib.getRootPackageName(true)}.${tokenSystemName}market.flows.TransferPartToken;
import ${aib.getRootPackageName(true)}.${tokenSystemName}market.flows.TotalPart;

import ${aib.getRootPackageName(true)}.helper.corda.CordaHelper;
import ${aib.getRootPackageName(true)}.helper.corda.api.HolderEnum;

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
	
	
#set( $var = "${lowercaseClassName}${display.capitalize( $identifierFieldName )}" )
    /**
     * Handles create a ${className} token.  
     * @param		${className}	${lowercaseClassName}
     * @return		CordaFuture
     */
	@PostMapping("/createToken")
    public net.corda.core.concurrent.CordaFuture createToken( @RequestParam(required=true) String $var ) {

		net.corda.core.messaging.CordaRPCOps proxy = proxy(HolderEnum.Notary);
		net.corda.core.concurrent.CordaFuture future = null;
		
		if ( proxy != null ) {
			LOGGER.info( "Located a Corda Notary" );
			LOGGER.info( "Starting a flow for Create${className}Token" );
			future = proxy.startFlowDynamic(Create${className}Token.class, $var).getReturnValue();
		}
		else {
			LOGGER.warning( "Failed to acquire an RPC proxy to a Corda Notary" );
		}
	
		return future;
    }

 
    /**
     * Handles deleting a ${className} entity as a token
     * @param		$identifierFieldName	String 
     * @param		holder					HolderEnum
     * @return		net.corda.core.concurrent.CordaFuture 
     */
    @DeleteMapping("/destroyToken")    
    public net.corda.core.concurrent.CordaFuture destroyToken( @RequestBody(required=true) String ${var}, HolderEnum holder) {                

		net.corda.core.messaging.CordaRPCOps proxy = proxy( holder );
		net.corda.core.concurrent.CordaFuture future = null;
    	Party party = null;
    	boolean exactMatch = true;
		
		if ( proxy != null ) {
			party = party( holder );

			if( party != null ) {
				LOGGER.log( Level.INFO, "Located a Corda Party for {0}", holder.toString());
				LOGGER.info( "Starting a flow for TotalPart" );

				future = proxy.startFlowDynamic(TotalPart.class, ${var}, party).getReturnValue();
			}
			else {
				LOGGER.log( Level.WARNING, "Failed to locate a Corda Party for node {0}", holder.toString() );
			}
		}
		else {
			LOGGER.log( Level.WARNING, "Failed to acquire an RPC Proxy to node {0}", holder.toString() );
		}
	
		return future;

	}        
    
    /**
     * Handles transfering a token to a given Holder
     * @param		$identifierFieldName	String 
     * @param		holder					HolderEnum
     * @return		net.corda.core.concurrent.CordaFuture 
     */
    @PostMapping("/transferToken")    
    public net.corda.core.concurrent.CordaFuture transferToken( @RequestBody(required=true) String ${var}, @RequestBody(required=true) HolderEnum holder) {                
    
		net.corda.core.messaging.CordaRPCOps proxy = proxy( holder );
		net.corda.core.concurrent.CordaFuture future = null;
    	Party party = null;
    	boolean exactMatch = true;
		
		if ( proxy != null ) {
			party = party( holder );

			if( party != null ) {
	    		
				LOGGER.log( Level.INFO, "Located a Corda Party for {0}", holder.toString());
				LOGGER.info( "Starting a flow for TransferPartToken" );

				future = proxy.startFlowDynamic(TransferPartToken.class, "${className}", ${var}, party).getReturnValue();
			}
			else {
				LOGGER.log( Level.WARNING, "Failed to locate a Corda Party for node {0}", holder.toString() );
			}
		}
		else {
			LOGGER.log( Level.WARNING, "Failed to acquire an RPC Proxy to node {0}", holder.toString() );
		}
	
		return future;
    }
	
  
//************************************************************************    
// Attributes
//************************************************************************
    private static final Logger LOGGER = Logger.getLogger(${className}TokenCommandRestController.class.getName());
    
}
