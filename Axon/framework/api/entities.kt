#header()
package ${aib.getRootPackageName(true)}.entity;

import java.util.*
import javax.persistence.*

#set( $imports = [ "enumerator", "valueobject" ] )
#importStatements( $imports )

// --------------------------------------------
// Entity Definitions
// --------------------------------------------
#set( $includeAssociations = true )
#set( $includeId = true )
#set( $forAggregate = false )
#foreach ( $class in $aib.getClassesToGenerate() )
@Entity
data class ${class.getName()}Entity(
#outputKotlinArgDeclarations( $class, $includeAssociations, $includeId, $forAggregate )
)

#end##foreach ( $class in $aib.getClassesToGenerate() )