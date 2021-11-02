#header()
package ${aib.getRootPackageName(true)}.api;

import org.axonframework.modelling.command.TargetAggregateIdentifier

import java.util.*;

import javax.persistence.Entity
import javax.persistence.Id
import javax.persistence.NamedQueries
import javax.persistence.NamedQuery

//-----------------------------------------------------------
// Query definitions
//-----------------------------------------------------------
#foreach( $class in $aib.getClassesToGenerate() )
#set( $className = ${class.getName()} )
#set( $lowercaseClassName = ${Utils.lowercaseFirstLetter( ${className} )} )
#set( $pk = "${lowercaseClassName}Id" )

// $className Queries

data class Load${className}Filter(val ${pk} : String = "" )

class Find${className}Query(val filter: Load${className}Filter = Load${className}Filter()) {
    override fun toString(): String = "Load${className}Query"
}

class FindAll${className}Query() {
    override fun toString(): String = "LoadAll${className}Query"
}

data class ${className}FetchOneSummary(@Id var ${pk} : String) {

}

#end##foreach( $class in $aib.getClassesToGenerate() )

#*
// Query Responses for entities and individually modeled queries

@Entity
@NamedQueries(
#for( $class = $aib.getClassesToGenerate() )
#set( $className = ${class.getName()} )
        NamedQuery(
                name = "${className}.fetchOne",
                query = "SELECT c FROM ${className} c WHERE c.id = (:inputId) ORDER BY c.id"
        ),
        NamedQuery(
                name = "${className}.fetchAll",
                query = "SELECT c FROM ${className}"
        )
#end##for( $class = $aib.getClassesToGenerate() )

#foreach( $query in $aib.getQueriesToGenerate() )
#set( $affiliate = $query.getAffiliate() )
#foreach( $handler in $query.getHandlers() )
#set( $queryName = $handler.getName() )
#set( $method = $handler.getMethodObject() )
#if ( ${method.hasArguments()} )	## should only be one argument
#set( $argName = ${method.getArguments().getArgs().get(0).getName()} )
        NamedQuery(
                name = "${affiliate}.queryName",
            query = "SELECT c FROM ${affiliate} WHERE c.${argName} = (:inputId) ORDER BY c.${argName}"
        )
#end##if ( ${method.hasArguments()} )
#end##foreach( $handler in $query.getHandlers() )
#end##foreach( $query = $aib.getQueriesToGenerate() )
)
*#

