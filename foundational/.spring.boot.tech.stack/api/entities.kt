#header()
#set( $usingMongoDBAsEntityStore = false )
#if( $aib.getParam( "spring.entity-store-type") == "mongodb" )
#set( $usingMongoDBAsEntityStore = true )
#end##if( $aib.getParam( "spring.entity-store-type") == "mongodb" )
package ${aib.getRootPackageName(true)}.entity;

import java.util.*

#if( $aib.getParam( "spring.entity-store-type") == "mongodb" )
import org.springframework.data.annotation.Id
#else
import javax.persistence.*
import javax.persistence.NamedQueries
import javax.persistence.NamedQuery
#end##if( $aib.getParam( "spring.entity-store-type") == "mongodb" )

#set( $imports = [ "api" ] )
#importStatements( $imports )

// --------------------------------------------
// Entity Definitions
// --------------------------------------------
#set( $includeAssociations = true )
#set( $includeId = true )
#set( $forAggregate = false )
#set( $forEntity = true )
#foreach ( $class in $aib.getClassesToGenerate() )
#set( $className = $class.getName() )
#if( $usingMongoDBAsEntityStore == false )
@Entity
#end##if( $usingMongoDBAsEntityStore == false )
#set( $queriesToGenerate = $aib.getQueriesToGenerate(  $className ) )
#set( $queriesSize = $queriesToGenerate.size() )
#if ( $queriesSize > 0 && $usingMongoDBAsEntityStore == false )
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
#end##if ( $queriesToGenerate.size() > 0 && $usingMongoDBAsEntityStore == false )
data class ${class.getName()}(
#outputKotlinArgDeclarations( $class $includeAssociations $includeId $forAggregate $forEntity )
)

#end##foreach ( $class in $aib.getClassesToGenerate() )