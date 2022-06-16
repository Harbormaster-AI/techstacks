#header()
package ${aib.getRootPackageName()}.api

import java.util.*

import net.corda.core.serialization.CordaSerializable
#set( $includePKs = false )

// --------------------------------------------
// Value Object Definitions
// --------------------------------------------

#foreach ( $valueObject in $aib.getValueObjectsToGenerate() )
@CordaSerializable
data class ${valueObject.getName()}(
    #attributesAsKotlinArgs( $valueObject $includePKs )
)
#end##foreach ( $valueObject in $aib.getValueObjectsToGenerate() )




