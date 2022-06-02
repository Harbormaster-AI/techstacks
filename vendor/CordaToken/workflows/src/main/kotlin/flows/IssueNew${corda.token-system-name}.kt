#set( $tokenSystemName = ${aib.getParam( "corda.token-system-name" ).toLowerCase()} )
package ${aib.getRootPackageName()}.${tokenSystemName}market.flows

import co.paralleluniverse.fibers.Suspendable
import com.r3.corda.lib.tokens.contracts.types.TokenPointer
import com.r3.corda.lib.tokens.contracts.utilities.issuedBy
import com.r3.corda.lib.tokens.workflows.flows.rpc.IssueTokens
import com.r3.corda.lib.tokens.workflows.utilities.heldBy
import net.corda.core.flows.FlowLogic
import net.corda.core.flows.StartableByRPC
import net.corda.core.identity.Party
import net.corda.core.node.services.queryBy
import net.corda.core.utilities.ProgressTracker

#set( $constructorArgs = "" )
#set( $classesToGenerate = $aib.getClassesToGenerate() )
#foreach( $class in $classesToGenerate )
#set( $className = ${class.getName()} )
#set( $lowercaseClassName = ${display.uncapitalize( $className )} )
import ${aib.getRootPackageName()}.${tokenSystemName}market.states.${className}TokenState
#set( $constructorArgs = "${constructorArgs}val ${lowercaseClassName}Serial: String, " )
#end##foreach( $class in $classesToGenerate )
// *********
// * Flows *
// *********
@StartableByRPC
class IssueNew${display.capitalize( $tokenSystemName )}(${constructorArgs}
                   val holder: Party) : FlowLogic<String>() {
    override val progressTracker = ProgressTracker()

    @Suspendable
    override fun call():String {
#foreach( $class in $classesToGenerate )
#set( $className = ${class.getName()} )
#set( $lowercaseClassName = ${display.uncapitalize( $className )} )
    
        //Step ${velocityCount} : $className Token
        //get ${lowercaseClassName} states on ledger
        val ${lowercaseClassName}StateAndRef = serviceHub.vaultService.queryBy<${className}TokenState>().states
                .filter { it.state.data.serialNum.equals(${lowercaseClassName}Serial) }[0]

        //get the TokenType object
        val ${lowercaseClassName}tokentype = ${lowercaseClassName}StateAndRef.state.data

        //get the pointer pointer to the ${lowercaseClassName}
        val ${lowercaseClassName}tokenPointer = ${lowercaseClassName}tokentype.toPointer(${lowercaseClassName}tokentype.javaClass)

        //assign the issuer to the ${lowercaseClassName} type who will be issuing the tokens
        val ${lowercaseClassName}issuedTokenType = ${lowercaseClassName}tokenPointer issuedBy ourIdentity

        //mention the current holder also
        val ${lowercaseClassName}token = ${lowercaseClassName}issuedTokenType heldBy holder
#end##foreach( $class in $classesToGenerate )

        //distribute the new $tokenSystemName (two token to be exact)
        //call built in flow to issue non fungible tokens
        val stx = subFlow(IssueTokens(listOf(
        
#set( $numClassesToGenerate = $classesToGenerate.size() )
#foreach( $class in $classesToGenerate )
#set( $className = ${class.getName()} )
#set( $lowercaseClassName = ${display.uncapitalize( $className )} )
#set( $tokenVar = "${lowercaseClassName}token" )
#if ( $velocityCount < $numClassesToGenerate )				
#set( $tokenVar = "${tokenVar}," )
#end##if ( $velocityCount < $numClassesToGenerate )
        										${tokenVar}
#end##foreach( $class in $classesToGenerate )        										
        										)))

        return ("\nA new $tokenSystemName is being issued to " + holder.name.organisation + " with "
#foreach( $class in $classesToGenerate )
#set( $className = ${class.getName()} )
#set( $lowercaseClassName = ${display.uncapitalize( $className )} )
				+ ${className} serial$: " this.${lowercaseClassName}Serial
#if ( $velocityCount < $numClassesToGenerate )				
        		+ ", " 
#end##if ( $velocityCount < $numClassesToGenerate )
#end##foreach( $class in $classesToGenerate )        
                + "\nTransaction ID: " + stx.id)
    }
}