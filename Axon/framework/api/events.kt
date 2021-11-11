#header()
package ${aib.getRootPackageName(true)}.api;

import org.axonframework.modelling.command.TargetAggregateIdentifier

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
#foreach( $class in $aib.getClassesToGenerate() )
#set( $className = ${class.getName()} )
#set( $lowercaseClassName = ${Utils.lowercaseFirstLetter( ${className} )} )
#set( $pk = "${lowercaseClassName}Id" )

// $className Events

data class Created${className}Event(
#set( $includeAssociations = false )
#outputKotlinArgDeclarations( $class $includeAssociations $includeId $forAggregate $forEntity )
)

data class Updated${className}Event(
#set( $includeAssociations = true )
#outputKotlinArgDeclarations( $class $includeAssociations $includeId $forAggregate )
)

data class Deleted${className}Event(@TargetAggregateIdentifier val ${pk}: UUID)

#end##foreach( $class in $aib.getClassesToGenerate() )


