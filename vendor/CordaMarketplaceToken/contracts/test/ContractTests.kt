#set( $tokenSystemName = ${aib.getParam( "corda.token-system-name" ).toLowerCase()} )
package ${aib.getRootPackageName()}.${tokenSystemName}market.contracts

import net.corda.core.contracts.UniqueIdentifier
import net.corda.core.identity.CordaX500Name

#foreach( $class in $aib.getClassesToGenerate() )
#set( $className = $class.getName() )
import ${aib.getRootPackageName()}.${tokenSystemName}market.states.${className}TokenState
#end##foreach( $class in $aib.getClassesToGenerate() )

import net.corda.testing.core.TestIdentity
import net.corda.testing.node.MockServices
import net.corda.testing.node.ledger
import org.junit.Test

class ContractTests {
    private val ledgerServices = MockServices()
    val Operator = TestIdentity(CordaX500Name(organisation = "Alice", locality = "TestLand", country = "US"))

    //sample tests
#set( $identifierFieldName = $display.capitalize( $aib.getParam( "corda.identifier-field-name" ) ) )
#foreach( $class in $aib.getClassesToGenerate() )
#set( $className = $class.getName() )
#set( $lowercaseClassName = $display.uncapitalize( $className ) )
    @Test
    fun `$className $identifierFieldName Cannot Be Empty`() {
        val ${lowercaseClassName}TokenPass = ${className}TokenState(Operator.party, UniqueIdentifier(), 0, "8742")
        val ${lowercaseClassName}TokenFail = ${className}TokenState(Operator.party, UniqueIdentifier(), 0, "")
        ledgerServices.ledger {
            transaction {
                output(${className}Contract.CONTRACT_ID, ${lowercaseClassName}TokenFail)
                command(Operator.publicKey, com.r3.corda.lib.tokens.contracts.commands.Create())
                this.fails()
            }
        }
        ledgerServices.ledger {
            transaction {
                output(${className}Contract.CONTRACT_ID, ${lowercaseClassName}TokenPass)
                command(Operator.publicKey, com.r3.corda.lib.tokens.contracts.commands.Create())
                this.verifies()
            }
        }
    }
    
#end##foreach( $class in $aib.getClassesToGenerate() )
}