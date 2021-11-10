package ${aib.getRootPackageName(true)}.api;

import org.axonframework.modelling.command.TargetAggregateIdentifier
import java.util.*
import javax.persistence.*

#set( $imports = [ "entity" ] )
#importStatements( $imports )

#set( $includeAssociations = false )
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
#outputKotlinArgDeclarations( $class $includeAssociations $includeId $forAggregate $forEntity )
)

#set( $includeAssociations = true)
data class Update${className}Command(
#outputKotlinArgDeclarations( $class $includeAssociations $includeId $forAggregate )
)

data class Delete${className}Command(@TargetAggregateIdentifier val ${pk}: UUID)

#end##foreach( $class in $aib.getClassesToGenerate() )


