#set( $tokenSystemName = ${aib.getParam( "corda.token-system-name" ).toLowerCase()} )
#set( $classesToGenerate = $aib.getClassesToGenerate() )
#set( $numClassesToGenerate = $classesToGenerate.size() )
package ${aib.getRootPackageName()}.${tokenSystemName}market.flows

import co.paralleluniverse.fibers.Suspendable
import com.r3.corda.lib.tokens.contracts.types.TokenPointer
import com.r3.corda.lib.tokens.workflows.flows.move.addMoveNonFungibleTokens
import com.r3.corda.lib.tokens.workflows.internal.flows.distribution.UpdateDistributionListFlow
import com.r3.corda.lib.tokens.workflows.internal.flows.finality.ObserverAwareFinalityFlow
import com.r3.corda.lib.tokens.workflows.internal.flows.finality.ObserverAwareFinalityFlowHandler
import com.r3.corda.lib.tokens.workflows.utilities.getPreferredNotary
import net.corda.core.contracts.requireThat
import net.corda.core.flows.*
import net.corda.core.identity.CordaX500Name
import net.corda.core.identity.Party
import net.corda.core.node.services.queryBy
import net.corda.core.transactions.SignedTransaction
import net.corda.core.transactions.TransactionBuilder
import net.corda.core.utilities.ProgressTracker

#set( $constructorArgs = "" )
#foreach( $class in $classesToGenerate )
#set( $className = ${class.getName()} )
#set( $lowercaseClassName = ${display.uncapitalize( $className )} )
import ${aib.getRootPackageName()}.${tokenSystemName}market.states.${className}TokenState
#set( $constructorArgs = "${constructorArgs}val ${lowercaseClassName}Serial: String, " )
#end##foreach( $class in $classesToGenerate )

// *********
// * Flows *
// *********
@InitiatingFlow
@StartableByRPC

class Transfer${display.capitalize( $tokenSystemName )}Token(${constructorArgs}
                   val holder: Party) : FlowLogic<String>() {
    override val progressTracker = ProgressTracker()

    @Suspendable
    override fun call():String {
#foreach( $class in $classesToGenerate )
#set( $className = ${class.getName()} )
#set( $lowercaseClassName = ${display.uncapitalize( $className )} )
    
        //Step ${velocityCount} : $className Token
        //get $lowercaseClassName states on ledger
        val ${lowercaseClassName}StateAndRef = serviceHub.vaultService.queryBy<${className}TokenState>().states
                .filter { it.state.data.serialNum.equals(${lowercaseClassName}Serial) }[0]

        //get the TokenType object
        val ${lowercaseClassName}tokentype = ${lowercaseClassName}StateAndRef.state.data

        //get the pointer pointer to the ${lowercaseClassName}
        val ${lowercaseClassName}Pointer: TokenPointer<*> = ${lowercaseClassName}tokentype.toPointer(${lowercaseClassName}tokentype.javaClass)
#end##foreach( $class in $classesToGenerate )

        //send tokens
        val session = initiateFlow(holder)

        // Obtain a reference from a notary
#set( $nodes = $aib.getParam( "corda.node-names" ) )        
        val notary = serviceHub.networkMapCache.getNotary(CordaX500Name.parse("${nodes[0]}"))
        val txBuilder = TransactionBuilder(notary)

#foreach( $class in $classesToGenerate )
#set( $className = ${class.getName()} )
#set( $lowercaseClassName = ${display.uncapitalize( $className )} )
        addMoveNonFungibleTokens(txBuilder,serviceHub,${lowercaseClassName}tokenPointer,holder)
#end##foreach( $class in $classesToGenerate )

        val ptx = serviceHub.signInitialTransaction(txBuilder)
        val stx = subFlow(CollectSignaturesFlow(ptx, listOf(session)))
        val ftx = subFlow(ObserverAwareFinalityFlow(stx, listOf(session)))

        /* Distribution list is a list of identities that should receive updates. For this mechanism to behave correctly we call the UpdateDistributionListFlow flow */
        subFlow(UpdateDistributionListFlow(ftx))

        return ("\nTransfer ownership of a ${tokenSystemName} ( " 
#set( $numClassesToGenerate = $classesToGenerate.size() )
#foreach( $class in $classesToGenerate )
#set( $className = ${class.getName()} )
#set( $lowercaseClassName = ${display.uncapitalize( $className )} )
				+ ${className} serial$: " this.${lowercaseClassName}Serial
#if ( $velocityCount < $numClassesToGenerate )				
        		+ ", " 
#end##if ( $velocityCount < $numClassesToGenerate )
#end##foreach( $class in $classesToGenerate )
        		+ ") to "
                + holder.name.organisation + "\nTransaction IDs: "
                + ftx.id)
    }
}

@InitiatedBy(Transfer${display.capitalize(${tokenSystemName})}Token::class)
class Transfer${display.capitalize(${tokenSystemName})}TokenResponder(val flowSession: FlowSession) : FlowLogic<Unit>() {
    @Suspendable
    override fun call(): Unit {
        subFlow(ObserverAwareFinalityFlowHandler(flowSession))
    }
}

