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

#end##foreach( $class in $aib.getClassesToGenerate() )


