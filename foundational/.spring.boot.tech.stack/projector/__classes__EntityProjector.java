#header()
#set( $className = ${classObject.getName()} )
#set( $lowercaseClassName = ${Utils.lowercaseFirstLetter( ${className} )} )
#set( $pk = "${className}Id" )		
package ${aib.getRootPackageName(true)}.handler;

import java.util.*;
import java.util.logging.Level;
import java.util.logging.Logger;
import java.util.stream.Collectors;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Component;


#set( $imports = [ "api", "entity", "exception", "repository" ] )
#importStatements( $imports )

/**
 * Projector for ${className} as outlined for the CQRS pattern.
 * 
 * Commands are handled by ${className}Aggregate
 * 
 * @author ${aib.getAuthor()}
 *
 */
@Component("${lowercaseClassName}-entity-projector")
public class ${className}EntityProjector {
		
	// core constructor
	public ${className}EntityProjector(${className}Repository repository ) {
        this.repository = repository;
    }	

	/*
	 * Insert a $className
	 * 
     * @param	entity $className
     */
    public ${className} create( ${className} entity) {
	    LOGGER.info("creating " + entity.toString() );
	   
    	// ------------------------------------------
    	// persist a new one
    	// ------------------------------------------ 
	    return repository.save(entity);
        
    }

	/*
	 * Update a $className
	 * 
     * @param	entity $className
     */
    public ${className} update( ${className} entity) {
	    LOGGER.info("updating " + entity.toString() );

	    // ------------------------------------------
    	// save with an existing instance
    	// ------------------------------------------ 
		return repository.save(entity);

    }
    
	/*
	 * Delete a $className
	 * 
     * @param	id		UUID
     */
    public ${className} delete( UUID id ) {
	    LOGGER.info("deleting " + id.toString() );

    	// ------------------------------------------
    	// locate the entity by the provided id
    	// ------------------------------------------
	    $className entity = repository.findById(id).get();
	    
    	// ------------------------------------------
    	// delete what is discovered 
    	// ------------------------------------------
    	repository.delete( entity );
    	
    	return entity;
    }    

#set( $includeComposites = false )
#foreach( $singleAssociation in $classObject.getSingleAssociations( ${includeComposites} ) )
#set( $roleName = $Utils.capitalizeFirstLetter( $singleAssociation.getRoleName() ) )
#set( $lowercaseRoleName = $Utils.lowercaseFirstLetter( $roleName ) )
#set( $childType = $singleAssociation.getType() )
    /*
     * Assign a ${roleName}
     * 
     * @param	parentId	UUID
     * @param	assignment 	${childType} 
     * @return	$className
     */
    public $className assign${roleName}( UUID parentId, ${childType} assignment ) {
	    LOGGER.info("assigning ${roleName} as " + assignment.toString() );

	    $className parentEntity = repository.findById( parentId ).get();
	    assignment = ${childType}Projector.find(assignment.get${childType}Id());
	    
	    // ------------------------------------------
		// assign the $roleName to the parent entity
		// ------------------------------------------ 
	    parentEntity.set${roleName}( assignment );

	    // ------------------------------------------
    	// save the parent entity
    	// ------------------------------------------ 
	    repository.save(parentEntity);
        
	    return parentEntity;
    }
    

	/*
	 * Unassign the ${roleName}
	 * 
	 * @param	parentId		UUID
	 * @return	$className
	 */
	public $className unAssign${roleName}( UUID parentId ) {
		$className parentEntity = repository.findById(parentId).get();

		LOGGER.info("unAssigning ${roleName} on " + parentEntity.toString() );
		
	    // ------------------------------------------
		// null out the ${roleName} on the parent entithy
		// ------------------------------------------     
	    parentEntity.set${roleName}(null);

	    // ------------------------------------------
		// save the parent entity
		// ------------------------------------------ 
	    repository.save(parentEntity);
    
	    return parentEntity;
	}

#end##foreach( $singleAssociation in $classObject.getSingleAssociations( ${includeComposites} ) )

#set( $includeComposites = false )
#foreach( $multiAssociation in $classObject.getMultipleAssociations() )
#set( $roleName = $Utils.capitalizeFirstLetter( $multiAssociation.getRoleName() ) )
#set( $lowercaseRoleName = $Utils.lowercaseFirstLetter( $roleName ) )    
#set( $childType = $multiAssociation.getType() )
    /*
     * Add to the ${roleName}
     * 
     * @param	parentID	UUID
     * @param	addTo		childType
     * @return	$className
     */
    public $className addTo${roleName}( UUID parentId, $childType addTo ) {
	    LOGGER.info("handling ${multiAssociation.getAddToEventAlias()} - " );
	    
	    $className parentEntity = repository.findById(parentId).get();
	    $childType child = ${childType}Projector.find(addTo.get${childType}Id());
	    
	    parentEntity.get${roleName}().add( child );

	    // ------------------------------------------
    	// save 
    	// ------------------------------------------ 
	    repository.save(parentEntity);
        
	    return parentEntity;
    }
    

    /*
     * Remove from the ${roleName}
     * 
     * @param	parentID	UUID
     * @param	removeFrom	childType
     * @return	$className
     */
	public $className removeFrom${roleName}( UUID parentId, $childType removeFrom ) {
	    LOGGER.info("handling ${multiAssociation.getRemoveFromEventAlias()} " );
	
	    $className parentEntity = repository.findById(parentId).get();
	    $childType child = ${childType}Projector.find(removeFrom.get${childType}Id());
	    
	    parentEntity.get${roleName}().remove( child );
	
	    // ------------------------------------------
		// save
		// ------------------------------------------ 
	    update(parentEntity);
	    
	    return parentEntity;
	}

#end##foreach( $multiAssociation in $classObject.getMultipleAssociations() )


    /**
     * Method to retrieve the ${className} via an Find${className}Query
     * @return 	query	Find${className}Query
     */
    @SuppressWarnings("unused")
    public ${className} find( UUID id ) {
    	LOGGER.info("handling find using " + id.toString() );
    	try {
    		return repository.findById(id).get();
    	}
    	catch( IllegalArgumentException exc ) {
    		LOGGER.log( Level.WARNING, "Failed to find a ${className} - {0}", exc.getMessage() );
    	}
    	return null;
    }
    
    /**
     * Method to retrieve a collection of all ${className}s
     *
     * @param	query	FindAll${className}Query 
     * @return 	List<${className}> 
     */
    @SuppressWarnings("unused")
    public List<${className}> findAll( FindAll${className}Query query ) {
    	LOGGER.info("handling findAll using " + query.toString() );
    	try {
    		return repository.findAll();
    	}
    	catch( IllegalArgumentException exc ) {
    		LOGGER.log( Level.WARNING, "Failed to find all ${className} - {0}", exc.getMessage() );
    	}
    	return null;
    }

#foreach( $query in $aib.getQueriesToGenerate(${className}) )
#foreach( $handler in $query.getHandlers() )
#set( $method = $handler.getMethodObject() )
#set( $queryName = $Utils.capitalizeFirstLetter( $handler.getName() ) )
#if ( ${method.hasArguments()} )##should only be one argument
#set( $argType = ${method.getArguments().getArgs().get(0).getType()} )
#set( $argName = ${method.getArguments().getArgs().get(0).getName()} )
#set( $returnType = ${method.getArguments().getReturnType()} )
    /**
     * query method to ${method.getName()}
     * @param 		$argType $argName
     * @return		$returnType
     */     
	@SuppressWarnings("unused")
	public $returnType ${method.getName()}( ${queryName}Query query ) {
		LOGGER.info("handling ${method.getName()}" );
		$returnType result = null;
		
		try {
#if ( $handler.getSingleValueReturnValue() == true )
		    result = repository.${method.getName()}( query.getFilter().get${Utils.capitalizeFirstLetter(${argName})}() );
#else##pageable
            Pageable pageable = PageRequest.of(query.getOffset(), query.getLimit());
            result = repository.${method.getName()}( query.getFilter().get${Utils.capitalizeFirstLetter(${argName})}(), pageable );
#end##if ( $handler.getSingleValueReturnValue() == false)

        }
        catch( Throwable exc ) {
        	LOGGER.log( Level.WARNING, "Failed to ${method.getName()} using " + query.getFilter(), exc );
        }
        
        return result;
	}
#end##if ( ${method.hasArguments()} )## should only be one argument
#end##foreach( $handler in $query.getHandlers() )
#end##foreach( $query in $aib.getQueriesToGenerate() )



    //--------------------------------------------------
    // attributes
    // --------------------------------------------------
	@Autowired
    protected final ${className}Repository repository;
#foreach( $associationType in $classObject.getAssociationTypes() )
    @Autowired
	@Qualifier(value = "${Utils.lowercaseFirstLetter( ${associationType} )}-entity-projector")
	com.harbormaster.handler.${associationType}EntityProjector ${associationType}Projector;
#end##for ( $associationType in $classObject.getAssociationTypes() )

    private static final Logger LOGGER 	= Logger.getLogger(${className}EntityProjector.class.getName());

}
