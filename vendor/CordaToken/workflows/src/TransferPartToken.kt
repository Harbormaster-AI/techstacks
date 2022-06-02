#set( $tokenSystemName = ${aib.getParam( "corda.token-system-name" ).toLowerCase()} )
#set( $identifierFieldName = $display.uncapitalize( $aib.getParam( "corda.identifier-field-name" ) ) )
package ${aib.getRootPackageName()}.${tokenSystemName}market.flows

import co.paralleluniverse.fibers.Suspendable
import com.r3.corda.lib.tokens.contracts.types.TokenPointer
import com.r3.corda.lib.tokens.workflows.flows.rpc.MoveNonFungibleTokens
import com.r3.corda.lib.tokens.workflows.flows.rpc.MoveNonFungibleTokensHandler
import com.r3.corda.lib.tokens.workflows.internal.flows.distribution.UpdateDistributionListFlow
import com.r3.corda.lib.tokens.workflows.types.PartyAndToken
import net.corda.core.flows.*
import net.corda.core.identity.Party
import net.corda.core.node.services.queryBy
import net.corda.core.utilities.ProgressTracker

#foreach( $class in $classesToGenerate )
#set( $className = ${class.getName()} )
#set( $lowercaseClassName = ${display.uncapitalize( $className )} )
import ${aib.getRootPackageName()}.${tokenSystemName}market.states.${className}TokenState
#end##foreach( $class in $classesToGenerate )

// *********
// * Flows *
// *********
@InitiatingFlow
@StartableByRPC
class TransferPartToken(val part: String,
                   val ${identifierFieldName}: String,
                   val holder: Party) : FlowLogic<String>() {
    override val progressTracker = ProgressTracker()

    @Suspendable
    override fun call():String {
#set( $classesToGenerate = $aib.getClassesToGenerate() )
#set( $numClassesToGenerate = $classesToGenerate.size() )
#set( $else = "" )
#set( $classNames = [] )
#foreach( $class in $classesToGenerate )
#set( $className = ${class.getName()} )
#set( $lowercaseClassName = ${display.uncapitalize( $className )} )
#set( $returnHolder = $classNames.add( $lowercaseClassName ) )
        ${else}if (part.equals("${lowercaseClassName}")){

            val ${lowercaseClassName}Identifier = ${identifierFieldName}
            //transfer ${lowercaseClassName} token
            val ${lowercaseClassName}StateAndRef = serviceHub.vaultService.queryBy<${className}TokenState>().states
                    .filter { it.state.data.${identifierFieldName}.equals(${lowercaseClassName}Identifier) }[0]

            //get the TokenType object
            val ${lowercaseClassName}tokentype = ${lowercaseClassName}StateAndRef.state.data

            //get the pointer pointer to the ${lowercaseClassName}
            val ${lowercaseClassName}tokenPointer: TokenPointer<*> = ${lowercaseClassName}tokentype.toPointer(${lowercaseClassName}tokentype.javaClass)
            val partyAnd${className}Token = PartyAndToken(holder,${lowercaseClassName}tokenPointer)

            val stx = subFlow(MoveNonFungibleTokens(partyAnd${className}Token))
            /* Distribution list is a list of identities that should receive updates. For this mechanism to behave correctly we call the UpdateDistributionListFlow flow */
            subFlow(UpdateDistributionListFlow(stx))
            return ("Transfer ownership of the ${lowercaseClassName} (" + this.${identifierFieldName} + ") to" + holder.name.organisation
                    + "\nTransaction ID: " + stx.id)
#if ( $velocityCount < $numClassesToGenerate )        
        }
#set( $else = "else " )
#else
#set( $else = "" )
    	} else {
            return "Please enter a part parameter for one of the following: $classNames "
        }
#end##if ( $velocityCount < $numClassesToGenerate )
#end##foreach( $class in $classesToGenerate )
    }
}

@InitiatedBy(TransferPartToken::class)
class TransferPartTokenResponder(val counterpartySession: FlowSession) : FlowLogic<Unit>() {
    @Suspendable
    override fun call() {
        // Responder flow logic goes here.
        subFlow(MoveNonFungibleTokensHandler(counterpartySession));
    }
}
