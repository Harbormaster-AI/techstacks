#set( $tokenSystemName = ${aib.getParam( "corda.token-system-name" ).toLowerCase()} )
package ${aib.getRootPackageName()}.${tokenSystemName}market.contracts

import  ${aib.getRootPackageName()}.${tokenSystemName}market.states.*
import net.corda.testing.node.MockServices
import org.junit.Test

class StateTests {
    private val ledgerServices = MockServices()

    //sample State tests
    @Test
    fun hasSerialNumFieldOfCorrectType() {
#foreach( $class in $aib.getClassesToGenerate() )
#set( $className = $class.getName() )        
        // check fields for $className
#set( $includePKs = false )
#set( $attributes = ${class.getAttributesOrderedInHierarchy( $includePKs )} )
#foreach( $attribute in $attributes )
#set( $attributeName = $display.uncapitalize( $attribute.getName() ) )        
        ${className}TokenState::class.java.getDeclaredField("${attributeName}")
#end##foreach( $attribute in $attributes )

#end##foreach( $class in $aib.getClassesToGenerate() )
    }
}