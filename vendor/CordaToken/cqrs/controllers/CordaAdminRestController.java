#header()
#set( $className = ${classObject.getName()} )
#set( $lowercaseClassName = ${Utils.lowercaseFirstLetter( ${className} )} )
#set( $tokenSystemName = $display.uncapitalize( ${aib.getParam( "corda.token-system-name" )} ) )
#set( $identifierFieldName = $display.capitalize( $aib.getParam( "corda.identifier-field-name" ) ) )
package ${aib.getRootPackageName(true)}.#getRestControllerPackageName();

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.*;
import java.util.concurrent.*;
import java.util.logging.Level;
import java.util.logging.Logger;
import java.util.stream.Collectors;


import net.corda.core.identity.CordaX500Name;
import net.corda.core.identity.Party;

#set( $imports = [ "api", "exception" ] )
#importStatements( $imports )

import ${aib.getRootPackageName(true)}.helper.corda.CordaHelper;
import ${aib.getRootPackageName(true)}.helper.corda.api.HolderEnum;

import ${aib.getRootPackageName(true)}.#getRestControllerPackageName().BaseCordaSpringRestController;

import ${aib.getRootPackageName()}.${tokenSystemName}market.flows.IssueNew${display.capitalize( $tokenSystemName )};
import ${aib.getRootPackageName()}.${tokenSystemName}market.flows.Transfer${display.capitalize( $tokenSystemName )}Token;

/** 
 * Implements Spring Controller for $display.capitalize( $tokenSystemName ) level actions
 *
 * @author $aib.getAuthor()
 */
@CrossOrigin
@RestController
@RequestMapping("/${tokenSystemName}")
public class CordaAdminRestController extends BaseCordaSpringRestController {
#set( $delim = "${identifierFieldName}, @RequestParam(required = true) String " )
#set( $declArgs = "String #identifersAsArgs( $delim )" )
#set( $delim = "${identifierFieldName}, " )
#set( $callArgs = "#identifersAsArgs( $delim )" )


    /**
     * Will transfer the provided tokens as a new ${tokenSystemName} to the specified holder
     * @param		${className}Parts ${lowercaseClassName}Parts
     * @param		HolderEnum holder
     * @return		net.corda.core.concurrent.CordaFuture
     */
	@PutMapping("/issueNew")
    public net.corda.core.concurrent.CordaFuture issueNew${tokenSystemName}( ${declArgs}, @RequestBody(required=true) HolderEnum holder ) {

		net.corda.core.messaging.CordaRPCOps proxy = proxy( holder );
		net.corda.core.concurrent.CordaFuture future = null;
    	Party party = null;
    	boolean exactMatch = true;
		
		if ( proxy != null ) {
			party = party( holder );

			if( party != null ) {
				LOGGER.log( Level.INFO, "Located a Corda Party for {0}", holder.toString());
				LOGGER.info( "Starting a flow for IssueNew${display.capitalize( $tokenSystemName )}" );

				future = proxy.startFlowDynamic(IssueNew${display.capitalize( $tokenSystemName )}.class, ${callArgs}, party).getReturnValue();
			}
			else {
				LOGGER.log( Level.WARNING, "Failed to locate a Corda Party for node {0}", holder.toString() );
			}
		}
		else {
			LOGGER.log( Level.WARNING, "Failed to acquire an RPC proxy to node {0}", holder.toString() );
		}
	
		return future;
    	
	}

    /**
     * Will sell the $tokenSystemName to the specified holder
     * @param		${className}Parts ${lowercaseClassName}Parts
     * @param		HolderEnum holder
     * @return		net.corda.core.concurrent.CordaFuture
     */
	@PutMapping("/transferToken")
    public net.corda.core.concurrent.CordaFuture transfer${display.capitalize( $tokenSystemName )}Token( ${declArgs}, @RequestBody(required=true) HolderEnum holder ) {

		net.corda.core.messaging.CordaRPCOps proxy = proxy( holder );
		net.corda.core.concurrent.CordaFuture future = null;
    	Party party = null;
    	boolean exactMatch = true;
		
		if ( proxy != null ) {
			party = party( holder );

			if( party != null ) {
				LOGGER.log( Level.INFO, "Located a Corda Party for {0}", holder.toString());
				LOGGER.info( "Starting a flow for Transfer${display.capitalize( $tokenSystemName )}Token " );

				future = proxy.startFlowDynamic(Transfer${display.capitalize( $tokenSystemName )}Token .class, ${callArgs}, party).getReturnValue();
			}
			else {
				LOGGER.log( Level.WARNING, "Failed to locate a Corda Party for node {0}", holder.toString() );
			}
		}
		else {
			LOGGER.log( Level.WARNING, "Failed to acquire an RPC proxy to node {0}", holder.toString() );
		}
	
		return future;

	}    
    
    /**
     * Returns the flows for the associated node
     * 
     * @param		holder		HolderEnum
     * @return		List<String>
     */
    @GetMapping("/flows")
    public java.util.List<java.lang.String> flows( HolderEnum holder) {
    	return proxy(holder).registeredFlows();
    }
    
    /**
     * Returns the NetworkParameters for the associated node
     * 
     * @param		holder		HolderEnum
     * @return		String
     */
    @GetMapping("/networkParameters")
    public String networkParameters( HolderEnum holder) {
    	return proxy(holder).getNetworkParameters().toString();
    }
    
    /**
     * Returns the notary identies for the associated node
     * 
     * @param		holder		HolderEnum
     * @return		java.util.List<java.lang.String>
     */
    @GetMapping("/notaryIdentities")
    public java.util.List<java.lang.String> notaryIdentities( HolderEnum holder) {
    	List<Party> parties 					= proxy(holder).notaryIdentities();
    	java.util.List<java.lang.String> list 	= new ArrayList<>();
    	
    	if ( parties != null && parties.size() > 0 ) {
    		list = parties.stream().
    				map( p -> p.getName().toString() ).
    				collect(Collectors.toList());
    	}
    	
    	return list;
    }
 
    /**
     * Returns the notary identies for the associated node
     * 
     * @param		holder		HolderEnum
     * @return		String
     */
    @GetMapping("/nodeInfo")
    public String nodeInfo( HolderEnum holder) {
    	return proxy(holder).nodeInfo().toString();
    }
    
    /**
     * Returns the notary identies for the associated node
     * 
     * @param		holder		HolderEnum
     * @return		java.util.List<net.corda.core.identity.Party>
     */
    @GetMapping("/nodeDiagnosticInfo")
    public String nodeDiagnosticInfo( HolderEnum holder) {
    	return proxy(holder).nodeDiagnosticInfo().toString();
    }
    
    //************************************************************************    
	// Attributes
	//************************************************************************
	private static final Logger LOGGER = Logger.getLogger(CordaAdminRestController.class.getName());
}
