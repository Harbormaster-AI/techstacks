#header()
#set( $className = ${classObject.getName()} )
#set( $lowercaseClassName = ${Utils.lowercaseFirstLetter( ${className} )} )
#set( $tokenSystemName = $display.capitalize( ${aib.getParam( "corda.token-system-name" )} ) )
#set( $tokenSystemName_lc = $display.uncapitalize( ${aib.getParam( "corda.token-system-name" )} ) )
#set( $identifierFieldName = $display.capitalize( $aib.getParam( "corda.identifier-field-name" ) ) )
#set( $identifierFieldName_lc = $display.uncapitalize( $aib.getParam( "corda.identifier-field-name" ) ) )
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
import ${aib.getRootPackageName(true)}.helper.corda.api.PartyEnum;
import ${aib.getRootPackageName(true)}.helper.corda.api.${display.capitalize( $tokenSystemName )};

import ${aib.getRootPackageName(true)}.#getRestControllerPackageName().BaseCordaSpringRestController;

import ${aib.getRootPackageName()}.${tokenSystemName_lc}market.flows.IssueNew${tokenSystemName};
import ${aib.getRootPackageName()}.${tokenSystemName_lc}market.flows.Transfer${tokenSystemName}Token;

import ${aib.getRootPackageName()}.${tokenSystemName_lc}market.flows.*;

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
#set( $delim = "${identifierFieldName}, app." )
#set( $callArgs = "#identifersAsArgs( $delim )" )


	/**
	 * Will transfer the provided tokens as a new ${tokenSystemName} to the specified party
	 * @param		 $tokenSystemName app
	 * @param		PartyEnum	party
	 * @return		net.corda.core.concurrent.CordaFuture
	 */
	@PutMapping("/bulkCreateTokens")
	public net.corda.core.concurrent.CordaFuture bulkCreateTokens( @RequestBody(required=true) $tokenSystemName app, PartyEnum partyEnum ) {
	
		net.corda.core.messaging.CordaRPCOps proxy = proxy( partyEnum );
		net.corda.core.concurrent.CordaFuture future = null;
		boolean exactMatch = true;
		
		if ( proxy != null ) {
			LOGGER.log( Level.INFO, "Located a Corda Party for {0}", partyEnum.toString());
				
#foreach( $class in $aib.getClassesToGenerate() )
#set( $className = $display.capitalize( $class.getName() ) )
#set( $className_lc = $display.uncapitalize( $class.getName() ) )
            LOGGER.info( "Starting a flow for CreateNew${className}" );
            future = proxy.startFlowDynamic( Create${className}Token.class, app.${className_lc}${identifierFieldName} ).getReturnValue();

#end##foreach( $class in $aib.getClassesToGenerate() )
		}
		else {
			LOGGER.log( Level.WARNING, "Failed to acquire an RPC proxy to node {0}", partyEnum.toString() );
		}
	
		return future;
		
	}


	/**
	 * Handles bulk deleting to a target party
	 * @param		$identifierFieldName	String 
	 * @param		partyEnum					PartyEnum
	 * @return		net.corda.core.concurrent.CordaFuture 
	 */
	@DeleteMapping("/bulkDestroyTokens")    
	public net.corda.core.concurrent.CordaFuture bulkDestroyTokens( @RequestBody(required=true) $tokenSystemName app, PartyEnum partyEnum) {                

		net.corda.core.messaging.CordaRPCOps proxy = proxy( partyEnum );
		net.corda.core.concurrent.CordaFuture future = null;
		boolean exactMatch = true;
		
		
		if ( proxy != null ) {
			Party party = party( partyEnum );

			if( party != null ) {
				LOGGER.log( Level.INFO, "Located a Corda Party for {0}", partyEnum.toString());
#foreach( $class in $aib.getClassesToGenerate() )
#set( $className = $display.capitalize( $class.getName() ) )
#set( $className_lc = $display.uncapitalize( $class.getName() ) )
					// only start the flow if a ${identifierFieldName_lc} has been provided
				if ( app.${className_lc}${identifierFieldName} != null ) {
					LOGGER.info( "Starting a flow for TotalPart on partyEnum.toString() for a ${className}" );
					future = proxy.startFlowDynamic( TotalPart.class, app.${className_lc}${identifierFieldName}, party ).getReturnValue();
				}
#end##foreach( $class in $aib.getClassesToGenerate() )
			}
		}
		else {
			LOGGER.log( Level.WARNING, "Failed to acquire an RPC proxy to node {0}", partyEnum.toString() );
		}
	
		return future;
	
	}   

    /**
     * Handles bulk transfering tokens to a given Party
     * @param		$identifierFieldName	String 
     * @param		partyEnum					PartyEnum
     * @return		net.corda.core.concurrent.CordaFuture 
     */
    @PostMapping("/bulkTransferTokens")    
    public net.corda.core.concurrent.CordaFuture bulkTransferTokens( @RequestBody(required=true) $tokenSystemName app, @RequestBody(required=true) PartyEnum partyEnum) {                
		net.corda.core.messaging.CordaRPCOps proxy = proxy( partyEnum );
		net.corda.core.concurrent.CordaFuture future = null;
		boolean exactMatch = true;
		
		if ( proxy != null ) {
			LOGGER.log( Level.INFO, "Located a Corda Party for {0}", partyEnum.toString());
			Party party = party( partyEnum );

			if( party != null ) {
#foreach( $class in $aib.getClassesToGenerate() )
#set( $className = $display.capitalize( $class.getName() ) )
#set( $className_lc = $display.uncapitalize( $class.getName() ) )
			// only start the flow if a ${identifierFieldName_lc} has been provided
				if ( app.${className_lc}${identifierFieldName} != null ) {
					LOGGER.info( "Starting a flow for TransferPartToken to partyEnum.toString() for a ${className}" );
					future = proxy.startFlowDynamic( TransferPartToken.class, app.${className_lc}${identifierFieldName}, party ).getReturnValue();
				}
#end##foreach( $class in $aib.getClassesToGenerate() )
			}
		}
		else {
			LOGGER.log( Level.WARNING, "Failed to acquire an RPC proxy to node {0}", partyEnum.toString() );
		}
	
		return future;
	
	}   
    
    /**
     * Will transfer the provided tokens as a new ${tokenSystemName} to the specified party
     * @param		${className}Parts ${lowercaseClassName}Parts
     * @param		PartyEnum partyEnum
     * @return		net.corda.core.concurrent.CordaFuture
     */
	@PutMapping("/issueNew")
    public net.corda.core.concurrent.CordaFuture issueNew${tokenSystemName}( @RequestBody(required=true) $tokenSystemName app, @RequestBody(required=true) PartyEnum partyEnum ) {

		net.corda.core.messaging.CordaRPCOps proxy = proxy( partyEnum );
		net.corda.core.concurrent.CordaFuture future = null;
    	Party party = null;
    	boolean exactMatch = true;
		
		if ( proxy != null ) {
			party = party( partyEnum );

			if( party != null ) {
				LOGGER.log( Level.INFO, "Located a Corda Party for {0}", partyEnum.toString());
				LOGGER.info( "Starting a flow for IssueNew${display.capitalize( $tokenSystemName )}" );

				future = proxy.startFlowDynamic(IssueNew${tokenSystemName}.class, app.${callArgs}${identifierFieldName}, party).getReturnValue();
			}
			else {
				LOGGER.log( Level.WARNING, "Failed to locate a Corda Party for node {0}", partyEnum.toString() );
			}
		}
		else {
			LOGGER.log( Level.WARNING, "Failed to acquire an RPC proxy to node {0}", partyEnum.toString() );
		}
	
		return future;
    	
	}

    /**
     * Will sell the $tokenSystemName to the specified party
     * @param		${className}Parts ${lowercaseClassName}Parts
     * @param		PartyEnum partyEnum
     * @return		net.corda.core.concurrent.CordaFuture
     */
	@PutMapping("/transferToken")
    public net.corda.core.concurrent.CordaFuture transfer${tokenSystemName}Token(@RequestBody(required=true) $tokenSystemName app, @RequestBody(required=true) PartyEnum partyEnum ) {

		net.corda.core.messaging.CordaRPCOps proxy = proxy( partyEnum );
		net.corda.core.concurrent.CordaFuture future = null;
    	Party party = null;
    	boolean exactMatch = true;
		
		if ( proxy != null ) {
			party = party( partyEnum );

			if( party != null ) {
				LOGGER.log( Level.INFO, "Located a Corda Party for {0}", partyEnum.toString());
				LOGGER.info( "Starting a flow for Transfer${tokenSystemName}Token " );

				future = proxy.startFlowDynamic(Transfer${tokenSystemName}Token.class, app.${callArgs}${identifierFieldName}, party).getReturnValue();
			}
			else {
				LOGGER.log( Level.WARNING, "Failed to locate a Corda Party for node {0}", partyEnum.toString() );
			}
		}
		else {
			LOGGER.log( Level.WARNING, "Failed to acquire an RPC proxy to node {0}", partyEnum.toString() );
		}
	
		return future;

	}    
    
    /**
     * Returns the flows for the associated node
     * 
     * @param		partyEnum		PartyEnum
     * @return		List<String>
     */
    @GetMapping("/flows")
    public java.util.List<java.lang.String> flows( PartyEnum partyEnum) {
    	return proxy(partyEnum).registeredFlows();
    }
    
    /**
     * Returns the NetworkParameters for the associated node
     * 
     * @param		partyEnum		PartyEnum
     * @return		String
     */
    @GetMapping("/networkParameters")
    public String networkParameters( PartyEnum partyEnum) {
    	return proxy(partyEnum).getNetworkParameters().toString();
    }
    
    /**
     * Returns the notary identies for the associated node
     * 
     * @param		partyEnum		PartyEnum
     * @return		java.util.List<java.lang.String>
     */
    @GetMapping("/notaryIdentities")
    public java.util.List<java.lang.String> notaryIdentities( PartyEnum partyEnum) {
    	List<Party> parties 					= proxy(partyEnum).notaryIdentities();
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
     * @param		partyEnum		PartyEnum
     * @return		String
     */
    @GetMapping("/nodeInfo")
    public String nodeInfo( PartyEnum partyEnum) {
    	return proxy(partyEnum).nodeInfo().toString();
    }
    
    /**
     * Returns the notary identies for the associated node
     * 
     * @param		partyEnum		PartyEnum
     * @return		String
     */
    @GetMapping("/nodeDiagnosticInfo")
    public String nodeDiagnosticInfo( PartyEnum partyEnum ) {
    	return proxy(partyEnum).nodeDiagnosticInfo().toString();
    }
    
    
    /**
     * Returns the notary identies for the associated node
     * @return		String
     */
    @GetMapping("/vaultQuery")
    public String vaultQuery( PartyEnum partyEnum ) {
    	return proxy(partyEnum).vaultQuery(com.r3.corda.lib.tokens.contracts.states.EvolvableTokenType.class).toString();
    }
    
    
    //************************************************************************    
	// Attributes
	//************************************************************************
	private static final Logger LOGGER = Logger.getLogger(CordaAdminRestController.class.getName());
}
