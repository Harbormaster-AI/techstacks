#header()
package ${aib.getRootPackageName(true)}.api;

import java.time.Instant
import java.util.*

#if( $aib.getParam( "axon-framework.using-mongodb-as-entity-store") == "true" )
import org.springframework.data.annotation.Id
#else
import javax.persistence.*
#end##if( $aib.getParam( "axon-framework.using-mongodb-as-entity-store") == "true" )

#set( $imports = [ "entity" ] )
#importStatements( $imports )

#set( $includeId = true )
#set( $forAggregate = false )
#set( $forEntity = false )

//-----------------------------------------------------------
// Event definitions
//-----------------------------------------------------------
#foreach( $classToGenerate in $aib.getClassesToGenerate() )
#set( $className = ${classToGenerate.getName()} )
#set( $lowercaseClassName = ${Utils.lowercaseFirstLetter( ${className} )} )
#set( $pk = "${lowercaseClassName}Id" )

// $className Events

data class ${classToGenerate.getCreateEventAlias()}(
#set( $includeAssociations = false )
#outputKotlinArgDeclarations( $classToGenerate $includeAssociations $includeId $forAggregate $forEntity )
)

data class ${classToGenerate.getUpdateEventAlias()}(
#set( $includeAssociations = true )
#outputKotlinArgDeclarations( $classToGenerate $includeAssociations $includeId $forAggregate $immutable)
)

data class ${classToGenerate.getDeleteEventAlias()}(@Id val ${pk}: UUID? = null)

// single association events
#set( $includeComposites = false )
#foreach( $singleAssociation in $classToGenerate.getSingleAssociations( ${includeComposites} ) )
#set( $roleName = $Utils.capitalizeFirstLetter( $singleAssociation.getRoleName() ) )
#set( $childType = $singleAssociation.getType() )
data class ${singleAssociation.getAssignToEventAlias()}(@Id val ${pk}: UUID, val assignment: $childType )
data class ${singleAssociation.getUnAssignFromEventAlias()}(@Id val ${pk}: UUID? = null )
#end##foreach( $singleAssociation in $class.getSingleAssociations( ${includeComposites} ) )

// multiple association events
#foreach( $multiAssociation in $classToGenerate.getMultipleAssociations() )
#set( $roleName = $Utils.capitalizeFirstLetter( $multiAssociation.getRoleName() ) )
#set( $childType = $multiAssociation.getType() )
data class ${multiAssociation.getAddToEventAlias()}(@Id val ${pk}: UUID, val addTo: $childType )
data class ${multiAssociation.getRemoveFromEventAlias()}(@Id val ${pk}: UUID, val removeFrom: $childType )
#end##foreach( $multiAssociation in $class.getMultipleAssociations() )

#end##foreach( $classToGenerate in $aib.getClassesToGenerate() )


