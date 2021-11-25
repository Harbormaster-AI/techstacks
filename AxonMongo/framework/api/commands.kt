#header()
package ${aib.getRootPackageName(true)}.api

import org.axonframework.modelling.command.TargetAggregateIdentifier

import java.util.*

#if( $aib.getParam( "axon-framework.entity-store-type") == "mongodb" )
import org.springframework.data.annotation.Id
#else
import javax.persistence.*
#end##if( $aib.getParam( "axon-framework.entity-store-type") == "mongodb" )

#set( $imports = [ "entity" ] )
#importStatements( $imports )

#set( $forEntity = false )
//-----------------------------------------------------------
// Command definitions
//-----------------------------------------------------------
#foreach( $class in $aib.getClassesToGenerate() )
#set( $className = ${class.getName()} )
#set( $lowercaseClassName = ${Utils.lowercaseFirstLetter( ${className} )} )
#set( $pk = "${lowercaseClassName}Id" )

// $className Commands
data class ${class.getCreateCommandAlias()}(
#set( $includeAssociations = false )
#set( $forAggregate = false )
#set( $includeId = true )
#outputKotlinArgDeclarations( $class $includeAssociations $includeId $forAggregate $forEntity )
)

data class ${class.getUpdateCommandAlias()}(
#set( $includeAssociations = true)
#set( $forAggregate = true )
#set( $includeId = true )
#outputKotlinArgDeclarations( $class $includeAssociations $includeId $forAggregate $forEntity )
)

data class ${class.getDeleteCommandAlias()}(@TargetAggregateIdentifier var ${pk}: UUID? = null)

// single association commands
#set( $includeComposites = false )
#foreach( $singleAssociation in $class.getSingleAssociations( ${includeComposites} ) )
#set( $roleName = $Utils.capitalizeFirstLetter( $singleAssociation.getRoleName() ) )
#set( $childType = $singleAssociation.getType() )
#set( $childClass = $aib.getClassObject( $childType ) )
data class ${singleAssociation.getAssignToCommandAlias()}(@TargetAggregateIdentifier val ${pk}: UUID, val assignment: $childType )
data class ${singleAssociation.getUnAssignFromCommandAlias()}(@TargetAggregateIdentifier val ${pk}: UUID )
#end##foreach( $singleAssociation in $classObject.getSingleAssociations( ${includeComposites} ) )

// multiple association commands
#set( $includeComposites = false )
#foreach( $multiAssociation in $class.getMultipleAssociations() )
#set( $roleName = $Utils.capitalizeFirstLetter( $multiAssociation.getRoleName() ) )
#set( $childType = $multiAssociation.getType() )
#set( $childClass = $aib.getClassObject( $childType ) )
data class ${multiAssociation.getAddToCommandAlias()}(@TargetAggregateIdentifier val ${pk}: UUID, val addTo: $childType )
data class ${multiAssociation.getRemoveFromCommandAlias()}(@TargetAggregateIdentifier val ${pk}: UUID, val removeFrom: $childType )
#end##foreach( $multiAssociation in $classObject.getMultipleAssociations() )

#end##foreach( $class in $aib.getClassesToGenerate() )


