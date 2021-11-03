#header()
package ${aib.getRootPackageName(true)}.valueobject;

import java.util.*

#set( $includeAssociations = true )
#set( $includeId = false )
#set( $forAggregate = false )
#set( $forEntity = false )

#set( $imports = [ "enumerator" ] )
#importStatements( $imports )

// --------------------------------------------
// Valueobject Definitions
// --------------------------------------------

#foreach ( $valueObject in $aib.getValueObjectsToGenerate() )
data class ${valueObject.getName()}(
#outputKotlinArgDeclarations( $valueObject $includeAssociations $includeId $forAggregate $forEntity )
)

#end##foreach ( $valueObject in $aib.getValueObjectsToGenerate() )




