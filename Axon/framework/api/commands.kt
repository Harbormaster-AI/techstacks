package ${aib.getRootPackageName(true)}.api;

import org.axonframework.modelling.command.TargetAggregateIdentifier
import java.util.*
import javax.persistence.*

#set( $imports = [ "entity", "enumerator", "valueobject" ] )
#importStatements( $imports )

#set( $includeAssociations = true )
#set( $includeId = true )
#set( $forAggregate = true )
//-----------------------------------------------------------
// Command definitions
//-----------------------------------------------------------
#foreach( $class in $aib.getClassesToGenerate() )
#set( $className = ${class.getName()} )
#set( $lowercaseClassName = ${Utils.lowercaseFirstLetter( ${className} )} )
#set( $pk = "${lowercaseClassName}Id" )

// $className Commands

data class Create${className}Command(
#outputKotlinArgDeclarations( $class $includeAssociations $includeId $forAggregate )
)

data class Update${className}Command(
#outputKotlinArgDeclarations( $class $includeAssociations $includeId $forAggregate )
)

data class Delete${className}Command(@TargetAggregateIdentifier val ${pk}: UUID)

#end##foreach( $class in $aib.getClassesToGenerate() )


