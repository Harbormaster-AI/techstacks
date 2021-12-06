#header()
package ${aib.getRootPackageName(true)}.entity;

import java.util.*
import javax.persistence.*

import javax.persistence.NamedQueries
import javax.persistence.NamedQuery

#set( $imports = [ "api" ] )
#importStatements( $imports )

// --------------------------------------------
// Entity Definitions
// --------------------------------------------
#foreach ( $class in $aib.getClassesToGenerate() )
#set( $className = $class.getName() )
@Entity
#set( $queriesToGenerate = $aib.getQueriesToGenerate(  $className ) )
#set( $queriesSize = $queriesToGenerate.size() )
#if ( $queriesSize > 0 )
@NamedQueries(
#foreach( $query in $queriesToGenerate )
#set( $queryHandlers = $query.getHandlers() )
#set( $queryHandlersSize = $queryHandlers.size() )
#foreach( $handler in $queryHandlers )
#set( $queryName = $handler.getName() )
#set( $method = $handler.getMethodObject() )
#if ( ${method.hasArguments()} )## should only be one argument
#set( $methodArg = ${method.getArguments().getArgs().get(0)} )
#set( $argName = ${methodArg.getName()} )
#set( $comma = "" )
#if( $velocityCount < $queryHandlersSize )
#set( $comma = "," )
#end##if( $velocityCount < $queryHandlersSize )
        NamedQuery(
                name = "${className}.${method.getName()}",
            query = "SELECT c FROM ${className} c WHERE c.${argName} = (:${argName}) ORDER BY c.${argName}"
        )${comma}
#end##if ( ${method.hasArguments()} )
#end##foreach( $handler in $query.getHandlers() )
#end##foreach( $query in $queriesToGenerate )
)
#end##if ( $queriesToGenerate.size() > 0 )
#set( $includeAssociations = true )
#set( $includeId = true )
#set( $forAggregate = false )
#set( $forEntity = true )
data class ${class.getName()}(
#outputKotlinArgDeclarations( $class $includeAssociations $includeId $forAggregate $forEntity )
)

#end##foreach ( $class in $aib.getClassesToGenerate() )