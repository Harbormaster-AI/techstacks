#header()
package ${aib.getRootPackageName(true)}.api;

import org.axonframework.modelling.command.TargetAggregateIdentifier

import java.time.Instant
import java.util.*
import javax.persistence.*

#set( $imports = [ "enumerator", "entity", "valueobject" ] )
#importStatements( $imports )

#set( $includeAssociations = true )
#set( $includeId = true )
#set( $forAggregate = false )
//-----------------------------------------------------------
// Event definitions
//-----------------------------------------------------------
#foreach( $class in $aib.getClassesToGenerate() )
#set( $className = ${class.getName()} )
#set( $lowercaseClassName = ${Utils.lowercaseFirstLetter( ${className} )} )
#set( $pk = "${lowercaseClassName}Id" )

// $className Events

data class Created${className}Event(
#outputKotlinArgDeclarations( $class $includeAssociations $includeId $forAggregate )
)

data class Updated${className}Event(
#outputKotlinArgDeclarations( $class $includeAssociations $includeId $forAggregate )
)

data class Deleted${className}Event(@TargetAggregateIdentifier val ${pk}: UUID)

#end##foreach( $class in $aib.getClassesToGenerate() )


