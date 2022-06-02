#set( $tokenSystemName = ${aib.getParam( "corda.token-system-name" ).toLowerCase()} )
#set( $className = $classObject.getName() )
#set( $lowercaseClassName = ${display.uncapitalize( $className )} )
package ${aib.getRootPackageName()}.${tokenSystemName}market.contracts

import com.r3.corda.lib.tokens.contracts.EvolvableTokenContract
import net.corda.core.contracts.Contract
import net.corda.core.contracts.Requirements
import net.corda.core.contracts.requireThat
import net.corda.core.transactions.LedgerTransaction
import ${aib.getRootPackageName()}.${tokenSystemName}market.states.${className}TokenState


class ${className}Contract : EvolvableTokenContract(), Contract {
    override fun additionalCreateChecks(tx: LedgerTransaction) {
        val newToken = tx.outputStates.single() as ${className}TokenState
        newToken.apply {
#set( $includePKs = false )
#set( $attributes = ${classObject.getAttributesOrderedInHierarchy( $includePKs )} )
#foreach( $attribute in $attributes )
#if ( $attribute.isRequired() == true )
#set( $attributeName = $display.uncapitalize( $attribute.getName() ) )
			require(${attributeName} != null) {"${attributeName} cannot be null"}
#end##if ( $attribute.isRequired() == true )
#end##foreach( $attribute in $attributes )
        
        }
    }

    override fun additionalUpdateChecks(tx: LedgerTransaction) {
        /*This additional check does not apply to this use case.
         *This sample does not allow token update */
    }

    companion object {
        const val CONTRACT_ID = "${aib.getRootPackageName()}.${tokenSystemName}market.contracts.${className}Contract"
    }
}
