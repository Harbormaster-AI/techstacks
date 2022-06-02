#set( $tokenSystemName = ${aib.getParam( "corda.token-system-name" ).toLowerCase()} )
#set( $identifierFieldName = $display.capitalize( $aib.getParam( "corda.identifier-field-name" ) ) )
package ${aib.getRootPackageName()}.${tokenSystemName}market.flows

import co.paralleluniverse.fibers.Suspendable
import com.r3.corda.lib.tokens.contracts.types.TokenPointer
import com.r3.corda.lib.tokens.workflows.flows.rpc.MoveNonFungibleTokensHandler
import com.r3.corda.lib.tokens.workflows.flows.rpc.RedeemNonFungibleTokens
import com.r3.corda.lib.tokens.workflows.flows.rpc.RedeemNonFungibleTokensHandler
import net.corda.core.flows.*
import net.corda.core.identity.Party
import net.corda.core.node.services.queryBy
import net.corda.core.utilities.ProgressTracker

#foreach( $class in $aib.getClassesToGenerate() )
#set( $className = ${class.getName()} )
import  ${aib.getRootPackageName()}.${tokenSystemName}market.states.${className}TokenState
#end

// *********
// * Flows *
// *********
@InitiatingFlow
@StartableByRPC
class TotalPart(val part: String,
                   val $display.uncapitalize( ${identifierFieldName} ): String) : FlowLogic<String>() {
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
            val ${lowercaseClassName}${identifierFieldName} = $display.uncapitalize( $identifierFieldName )
            //transfer ${lowercaseClassName} token
            val ${lowercaseClassName}StateAndRef = serviceHub.vaultService.queryBy<${className}TokenState>().states
                    .filter { it.state.data.${display.uncapitalize( $identifierFieldName )}.equals(${lowercaseClassName}${identifierFieldName}) }[0]

            //get the TokenType object
            val ${lowercaseClassName}TokenType = ${lowercaseClassName}StateAndRef.state.data
            val issuer = ${lowercaseClassName}TokenType.maintainer

            //get the pointer pointer to the ${lowercaseClassName}
            val ${lowercaseClassName}TokenPointer: TokenPointer<*> = ${lowercaseClassName}TokenType.toPointer(${lowercaseClassName}TokenType.javaClass)

            val stx = subFlow(RedeemNonFungibleTokens(${lowercaseClassName}TokenPointer,issuer))
            return "\nThe ${lowercaseClassName} part is totaled, and the token is redeem to ${aib.getParam("application.company name")}" + "\nTransaction ID: " + stx.id
        }
#if ( $velocityCount < $numClassesToGenerate )        
#set( $else = "else " )
#else
#set( $else = "" )
    	else{
            return "Please enter a part parameter for one of the following: $classNames "
        }
#end##if ( $velocityCount < $numClassesToGenerate )
#end##foreach( $class in $classesToGenerate )
    }

}

@InitiatedBy(TotalPart::class)
class TotalPartResponder(val counterpartySession: FlowSession) : FlowLogic<Unit>() {
    @Suspendable
    override fun call() {
        // Responder flow logic goes here.
        subFlow(RedeemNonFungibleTokensHandler(counterpartySession));
    }
}

