package ${aib.getRootPackageName(true)}.api;

import org.axonframework.modelling.command.TargetAggregateIdentifier
import java.util.*
import javax.persistence.*

#set( $imports = [ "entity" ] )
#importStatements( $imports )

#set( $includeId = true )
#set( $forAggregate = true )
#set( $forEntity = false )
//-----------------------------------------------------------
// Command definitions
//-----------------------------------------------------------
#foreach( $class in $aib.getClassesToGenerate() )
#set( $className = ${class.getName()} )
#set( $lowercaseClassName = ${Utils.lowercaseFirstLetter( ${className} )} )
#set( $pk = "${lowercaseClassName}Id" )

// $className Commands
data class Create${className}Command(
#set( $includeAssociations = false )
#outputKotlinArgDeclarations( $class $includeAssociations $includeId $forAggregate $forEntity )
)

data class Update${className}Command(
#set( $includeAssociations = true)
#outputKotlinArgDeclarations( $class $includeAssociations $includeId $forAggregate $forEntity )
)

data class Delete${className}Command(@TargetAggregateIdentifier val ${pk}: UUID)

// single association commands
#set( $includeComposites = false )
#foreach( $singleAssociation in $class.getSingleAssociations( ${includeComposites} ) )
#set( $roleName = $Utils.capitalizeFirstLetter( $singleAssociation.getRoleName() ) )
#set( $childType = $singleAssociation.getType() )
data class Assign${roleName}To${className}Command(@TargetAggregateIdentifier val ${pk}: UUID, val assignment: $childType )
data class UnAssign${roleName}From${className}Command(@TargetAggregateIdentifier val ${pk}: UUID )
#end##foreach( $singleAssociation in $classObject.getSingleAssociations( ${includeComposites} ) )

// multiple association commands
#set( $includeComposites = false )
#foreach( $multiAssociation in $class.getMultipleAssociations() )
#set( $roleName = $Utils.capitalizeFirstLetter( $multiAssociation.getRoleName() ) )
#set( $childType = $multiAssociation.getType() )
#set( $childClass = $aib.getClassObject( $childType ) )
data class Add${roleName}To${className}Command(@TargetAggregateIdentifier val ${pk}: UUID, val addTo: $childType )
data class Remove${roleName}From${className}Command(@TargetAggregateIdentifier val ${pk}: UUID, val removeFrom: $childType )
#end##foreach( $multiAssociation in $classObject.getMultipleAssociations() )

#end##foreach( $class in $aib.getClassesToGenerate() )


