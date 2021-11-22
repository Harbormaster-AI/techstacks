#header()
#set( $className = $classObject.getName() )
#set( $lowercaseClassName = ${Utils.lowercaseFirstLetter( ${className} )} )
package ${aib.getRootPackageName(true)}.repository;

import java.util.UUID;

#if( $aib.getParam( "axon-framework.using-mongodb-as-entity-store") == "true" )
#set( $parentRepo = "MongoRepository")
import org.springframework.data.mongodb.repository.MongoRepository;
#else
#set( $parentRepo = "JpaRepository")
import org.springframework.data.jpa.repository.JpaRepository;;
#end##if( $aib.getParam( "axon-framework.using-mongodb-as-entity-store") == "true" )
import org.springframework.stereotype.Repository;

import ${aib.getRootPackageName(true)}.entity.*;


@Repository
public interface ${className}Repository extends ${parentRepo}<${className}, UUID> {

#foreach( $query in $aib.getQueriesToGenerate(${className}) )
#foreach( $handler in $query.getHandlers() )
#set( $method = $handler.getMethodObject() )
#set( $queryName = $Utils.capitalizeFirstLetter( $handler.getName() ) )
#if ( ${method.hasArguments()} )##should only be one argument
#set( $argType = ${method.getArguments().getArgs().get(0).getType()} )
#set( $argName = ${method.getArguments().getArgs().get(0).getName()} )
#set( $returnType = ${method.getArguments().getReturnType()} )
#set( $pageable = "" )
#if ( $handler.getSingleValueReturnValue() == false)
#set( $pageable = ", Pageable page" )
#end##if ( $handler.getSingleValueReturnValue() == false)
    public $returnType ${method.getName()}( $argType $argName $pageable );
    
#end##if ( ${method.hasArguments()} )## should only be one argument
#end##foreach( $handler in $query.getHandlers() )
#end##foreach( $query in $aib.getQueriesToGenerate() )
}
