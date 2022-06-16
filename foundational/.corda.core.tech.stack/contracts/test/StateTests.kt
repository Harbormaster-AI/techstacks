#set( $tokenSystemName = ${aib.getParam( "corda.token-system-name" ).toLowerCase()} )
package ${aib.getRootPackageName()}.${tokenSystemName}market.contracts

import  ${aib.getRootPackageName()}.${tokenSystemName}market.states.*
import net.corda.testing.node.MockServices
import org.junit.Test

class StateTests {
    private val ledgerServices = MockServices()

#set( $identifierFieldName = $display.capitalize( $aib.getParam( "corda.identifier-field-name" ) ) )
    //sample State tests
    @Test
    fun has${identifierFieldName}FieldOfCorrectType() {
#set( $identifierFieldName = $display.uncapitalize( $identifierFieldName ) )
#foreach( $class in $aib.getClassesToGenerate() )
#set( $className = $class.getName() )        
        // check the $identifierFieldName field for $className
        ${className}TokenState::class.java.getDeclaredField("${identifierFieldName}")

#end##foreach( $class in $aib.getClassesToGenerate() )

    }
}