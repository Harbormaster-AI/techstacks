#set( $includePKs = false )
#set( $attributes = ${classObject.getAttributesOrderedInHierarchy( $includePKs )} )
#set( $declArgs = "#attributesAsKotlinArgs( $classObject $includePKs )" )
#set( $callArgs = "#argsAsFunctionCall( $attributes )" )
#set( $tokenSystemName = ${aib.getParam( "corda.token-system-name" ).toLowerCase()} )
#set( $className = $classObject.getName() )
#set( $lowercaseClassName = ${display.uncapitalize( $className )} )
#set( $identifierFieldName = "Id" )
package ${aib.getRootPackageName()}.${tokenSystemName}market.flows

import co.paralleluniverse.fibers.Suspendable
import com.r3.corda.lib.tokens.contracts.utilities.withNotary
import com.r3.corda.lib.tokens.workflows.flows.rpc.CreateEvolvableTokens
import net.corda.core.contracts.UniqueIdentifier
import net.corda.core.flows.FlowLogic
import net.corda.core.flows.StartableByRPC
import net.corda.core.identity.CordaX500Name
import net.corda.core.utilities.ProgressTracker

import java.util.UUID

import ${aib.getRootPackageName()}.${tokenSystemName}market.states.*
import ${aib.getRootPackageName()}.api.*;

// *********
// * Flows *
// *********
@StartableByRPC
class Create${className}Token(val ${lowercaseClassName}Id: UUID, ${declArgs}) : FlowLogic<String>() {
    override val progressTracker = ProgressTracker()

    @Suspendable
    override fun call():String {

        // Obtain a reference from a notary we wish to use.
        val notary = serviceHub.networkMapCache.getNotary(CordaX500Name.parse("O=Notary,L=London,C=GB"))

        //Create non-fungible ${lowercaseClassName} token
        val uuid = UniqueIdentifier( "${className}", ${lowercaseClassName}Id )
        val ${lowercaseClassName} = ${className}TokenState(ourIdentity, uuid,
        									0,
        									$callArgs
        									)

        //warp it with transaction state specifying the notary
        val transactionState = ${lowercaseClassName} withNotary notary!!

        subFlow(CreateEvolvableTokens(transactionState))

        return "\nCreated a ${lowercaseClassName} token for ${tokenSystemName} ${lowercaseClassName}. (${identifierFieldName}" + uuid + ")."
    }
}
