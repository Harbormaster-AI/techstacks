#header()
package ${aib.getRootPackageName(true)}.api;

import java.util.*;

#if( $aib.getParam( "spring.entity-store-type") == "mongodb" )
import org.springframework.data.annotation.Id
#else
import javax.persistence.Entity
import javax.persistence.Id
#end##if( $aib.getParam( "spring.entity-store-type") == "mongodb" )

//-----------------------------------------------------------
// Query definitions
//-----------------------------------------------------------
#foreach( $class in $aib.getClassesToGenerate() )
#set( $className = ${class.getName()} )
#set( $lowercaseClassName = ${Utils.lowercaseFirstLetter( ${className} )} )
#set( $pk = "${lowercaseClassName}Id" )

// -----------------------------------------
// $className Queries 
// -----------------------------------------

data class Load${className}Filter(val ${pk} :  UUID? = null )

class Find${className}Query(val filter: Load${className}Filter = Load${className}Filter()) {
    override fun toString(): String = "Load${className}Query"
}

class FindAll${className}Query() {
    override fun toString(): String = "LoadAll${className}Query"
}

data class ${className}FetchOneSummary(@Id var ${pk} : UUID? = null) {
}

#* 

THIS IS INTENTIONALLY BLOCKED OUT BECAUSE WE DO NOT CREATE QUERY HANDLERS FOR EACH ROLE

class FindAll${className}
data class Find${className}(val ${lowercaseClassName}Id: UUID)

#set( $includeComposites = false )
#foreach( $singleAssociation in $class.getSingleAssociations( ${includeComposites} ) )
#set( $roleName = $singleAssociation.getRoleName() )
class Find${roleName}For${className}(val ${pk} :  UUID)
#end##foreach( $singleAssociation in $class.getSingleAssociations( ${includeComposites} ) )
#foreach( $multiAssociation in $class.getMultipleAssociations() )
#set( $roleName = $multiAssociation.getRoleName() )
class Find${roleName}For${className}(val ${pk} :  UUID)
#end##foreach( $multiAssociation in $class.getMultipleAssociations() )

*#

#set( $queriesToGenerate = $aib.getQueriesToGenerate( $className ) )
#foreach( $query in $queriesToGenerate )
#set( $queryHandlers = $query.getHandlers() )
#set( $queryHandlersSize = $queryHandlers.size() )
#foreach( $handler in $queryHandlers )
#set( $queryName = $Utils.capitalizeFirstLetter( $handler.getName() ) )
#set( $method = $handler.getMethodObject() )
#set( $methodArg = ${method.getArguments().getArgs().get(0)} )
#set( $argName = ${methodArg.getName()} )
#set( $argType = ${methodArg.getType()} )
data class ${queryName}Filter(val ${argName}: ${argType}? = null)
#if ( $handler.getSingleValueReturnValue() == false)
class ${queryName}Query(val offset: Int? = 1, val limit: Int? = 100, val filter: ${queryName}Filter = ${queryName}Filter() )
#else
class ${queryName}Query(val filter: ${queryName}Filter = ${queryName}Filter())
#end##if ( $handler.getSingleValueReturnValue() == false)

#end##foreach( $handler in $query.getHandlers() )
#end##foreach( $query in $queriesToGenerate )

#end##foreach( $class in $aib.getClassesToGenerate() )


