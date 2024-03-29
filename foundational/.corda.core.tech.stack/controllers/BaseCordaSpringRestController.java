#header()
package ${aib.getRootPackageName()}.#getRestControllerPackageName();

import java.util.logging.Level;
import java.util.logging.Logger;
import javax.annotation.PreDestroy;

import org.springframework.stereotype.Component;

import net.corda.client.rpc.CordaRPCConnection;
import net.corda.core.identity.Party;

import ${aib.getRootPackageName(true)}.helper.corda.CordaHelper;
import ${aib.getRootPackageName(true)}.helper.corda.api.PartyEnum;

/** 
 * Base class of all application Spring Controller classes.
 *
 * @author $aib.getAuthor()
 */
@Component
public class BaseCordaSpringRestController
{
	/**
	 * unlike the String arg constrcutor, jumpstarting is the responsibility
	 * of the derived class.
	 */
	protected BaseCordaSpringRestController() {
		// no_op
	}
	
	protected BaseCordaSpringRestController( String nodeName ) {
		jumpstart( nodeName );
	}

	protected void jumpstart( String nodeName ) {
		this.nodeName = nodeName;
		rpcConnection = CordaHelper.getInstance().getCordaNodeConnection( nodeName );
		
		if ( rpcConnection == null )
			LOGGER.log( Level.WARNING, "Failed to acquire a Corda Connection for node {0}", nodeName);

	}

	protected CordaRPCConnection rpcConnection() {
		return rpcConnection;
	}
	
	/**
	 * proxy will jumpstart a Corda connection if non established
	 */
	protected net.corda.core.messaging.CordaRPCOps proxy(PartyEnum party ) {
		// if ( rpcConnection == null )
			jumpstart( party.toString() );
		
		return rpcConnection.getProxy();
	}
	
	/**
	 * node name connecting to
	 */
	protected String getNodeName() {
		return nodeName;
	}

	/** 
	 * Returns the corresponding party using the Party as a name to discover the correct node.
	 * From there the nameis use to search for the party based on an exact match
	 * 
	 * @param		partyEnum		PartyEnum
	 * @return		Party		the party of interest
	 */
    protected Party party(PartyEnum partyEnum) {
    	java.util.Set<Party> parties = null;
    	Party party = null;
    	boolean exactMatch = true;
    	
    	parties = proxy(partyEnum).partiesFromName( partyEnum.toString(), exactMatch );
    	
    	if( parties != null && parties.size() > 0 )
    		party = parties.iterator().next();
    	else
    		LOGGER.log( Level.WARNING, "Failed to locate a Corda Party for {0}", partyEnum.toString() );
    	
    	return party;
                
    }
	
    @PreDestroy
    public void close() {
		if ( rpcConnection != null ) {
			rpcConnection.notifyServerAndClose();
			rpcConnection = null;
		}
    }
	
	// --------------------------------------------
	// attributes 
	// --------------------------------------------
	protected CordaRPCConnection rpcConnection 	= null;
	private String nodeName						= null;
	private static final Logger LOGGER 			= Logger.getLogger(BaseCordaSpringRestController.class.getName());
}



