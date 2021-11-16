#header()
package ${aib.getRootPackageName(true)}.api;

import java.time.Instant
import java.util.*
import javax.persistence.*

#set( $imports = [ "entity" ] )
#importStatements( $imports )

#set( $includeId = true )
#set( $forAggregate = false )
#set( $forEntity = true )
//-----------------------------------------------------------
// Event definitions
//-----------------------------------------------------------
#foreach( $classToGenerate in $aib.getClassesToGenerate() )
#set( $className = ${classToGenerate.getName()} )
#set( $lowercaseClassName = ${Utils.lowercaseFirstLetter( ${className} )} )
#set( $pk = "${lowercaseClassName}Id" )

// $className Events

data class Created${className}Event(
#set( $includeAssociations = false )
#outputKotlinArgDeclarations( $classToGenerate $includeAssociations $includeId $forAggregate $forEntity )
)

data class Updated${className}Event(
#set( $includeAssociations = true )
#outputKotlinArgDeclarations( $classToGenerate $includeAssociations $includeId $forAggregate )
)

data class Deleted${className}Event(@Id val ${pk}: UUID? = null)

// single association events
#set( $includeComposites = false )
#foreach( $singleAssociation in $classToGenerate.getSingleAssociations( ${includeComposites} ) )
#set( $roleName = $Utils.capitalizeFirstLetter( $singleAssociation.getRoleName() ) )
#set( $childType = $singleAssociation.getType() )
data class Assigned${roleName}To${className}Event(@Id val ${pk}: UUID, val assignment: $childType )
data class UnAssigned${roleName}From${className}Event(@Id val ${pk}: UUID? = null )
#end##foreach( $singleAssociation in $class.getSingleAssociations( ${includeComposites} ) )

// multiple association events
#foreach( $multiAssociation in $classToGenerate.getMultipleAssociations() )
#set( $roleName = $Utils.capitalizeFirstLetter( $multiAssociation.getRoleName() ) )
#set( $childType = $multiAssociation.getType() )
data class Added${roleName}To${className}Event(@Id val ${pk}: UUID, val addTo: $childType )
data class Removed${roleName}From${className}Event(@Id val ${pk}: UUID, val removeFrom: $childType )
#end##foreach( $multiAssociation in $class.getMultipleAssociations() )

#end##foreach( $classToGenerate in $aib.getClassesToGenerate() )


