#header()
#set( $className = $classObject.getName() )
#set( $lowercaseClassName = ${Utils.lowercaseFirstLetter( ${className} )} )
package ${aib.getRootPackageName(true)}.#getDelegatePackageName();

import java.io.IOException;
import java.util.logging.Level;
import java.util.logging.Logger;
import java.util.*;
import java.util.concurrent.*;


import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationContext;

#set( $imports = [ "api", "entity", "exception", "repository", "validator", "handler" ] )
#importStatements( $imports )

/**
 * ${classObject.getName()} business delegate class.
 * <p>
 * This class implements the Business Delegate design pattern for the purpose of:
 * <ol>
 * <li>Reducing coupling between the business tier and a client of the business tier by hiding all business-tier implementation details</li>
 * <li>Improving the available of ${classObject.getName()} related services in the case of a ${classObject.getName()} business related service failing.</li>
 * <li>Exposes a simpler, uniform ${classObject.getName()} interface to the business tier, making it easy for clients to consume a simple Java object.</li>
 * <li>Hides the communication protocol that may be required to fulfill ${classObject.getName()} business related services.</li>
 * </ol>
 * <p>
 * @author ${aib.getAuthor()}
 */
#if ( $classObject.isAbstract() == false )
public class ${classObject.getName()}BusinessDelegate 
#else
public abstract class ${classObject.getName()}BusinessDelegate 
#end##if ( $classObject.isAbstract() == false )
#if ( $classObject.hasParent() == true )
extends ${classObject.getParentName()}BusinessDelegate {
#else
extends BaseBusinessDelegate {
#end##if ( $classObject.hasParent() == true )
//************************************************************************
// Public Methods
//************************************************************************
    /** 
     * Default Constructor 
     */
    public ${classObject.getName()}BusinessDelegate()  {
    	projector = new ${className}EntityProjector( applicationContext.getBean(${className}Repository.class) );
	}

#if ( $classObject.isAbstract() == false )

   /**
	* ${className} Business Delegate Factory Method
	*
	* All methods are expected to be self-sufficient.
	*
	* @return 	${className}BusinessDelegate
	*/
	public static ${className}BusinessDelegate get${className}Instance() {
		return( new ${className}BusinessDelegate() );
	}
 
   /**
    * Creates the provided command.
    * 
    * @param		command ${class.getCreateCommandAlias()}
    * @exception    ProcessingException
    * @exception	IllegalArgumentException
    * @return		$className
    */
	public $className create${className}( ${classObject.getCreateCommandAlias()} command )
    throws ProcessingException, IllegalArgumentException {

		$className entity = new ${className}();

		try {
			// --------------------------------------
        	// validate the command
        	// --------------------------------------    	
        	${className}Validator.getInstance().validate( command );    

#set( $includeAssociations = false )
#set( $includeId = true )
#determineArgsAsAssignment( ${classObject} "entity" "command" ${includeAssociations} ${includeId} ) 
    	    
        	// ------------------------------------------
        	// persist a new one
        	// ------------------------------------------ 
    	    entity = projector.create(entity);
    	    
			LOGGER.log( Level.INFO, "done creating of ${className} {0} ", entity.toString() );
			
        }
        catch (Exception exc) {
            final String errMsg = "Unable to create ${className} - " + exc;
            LOGGER.log( Level.WARNING, errMsg, exc );
            throw new ProcessingException( errMsg, exc );
        }
        finally {
        }        
        
        return entity;
    }

   /**
    * Update the provided command.
    * @param		command ${classObject.getUpdateCommandAlias()}
    * @exception    ProcessingException
    * @exception  	IllegalArgumentException
    * @return		$className
    */
    public $className update${className}( ${classObject.getUpdateCommandAlias()} command ) 
    throws ProcessingException, IllegalArgumentException {

	    $className entity = new ${className}();

    	try {       

			// --------------------------------------
        	// validate 
        	// --------------------------------------    	
        	${className}Validator.getInstance().validate( command );    

#set( $includeAssociations = true )
#set( $includeId = true )
#determineArgsAsAssignment( ${classObject} "entity" "command" ${includeAssociations} ${includeId} )
        	
        	// ------------------------------------------
        	// persist an existing one
        	// ------------------------------------------ 
        	entity = projector.update(entity);
    	    
			LOGGER.log( Level.INFO, "done saving of ${className} {0} ", entity.toString() );
    	}
        catch (Exception exc) {
            final String errMsg = "Unable to save ${className} - " + exc;
            LOGGER.log( Level.WARNING, errMsg, exc );
            throw new ProcessingException( errMsg, exc );
        }
    	
    	return entity;
    }
   
   /**
    * Deletes the associatied value object
    * @param		command ${classObject.getDeleteCommandAlias()}
    * @exception 	ProcessingException
    */
    public void delete( ${classObject.getDeleteCommandAlias()} command ) 
    throws ProcessingException, IllegalArgumentException {	
        UUID id = null;
        
    	try {  
			// --------------------------------------
        	// validate the command
        	// --------------------------------------    	
        	${className}Validator.getInstance().validate( command );    
        
        	id = command.get${className}Id();
        	
        	// ------------------------------------------
        	// delete the entity
        	// ------------------------------------------
        	projector.delete(id);

        	LOGGER.log( Level.INFO, "done deleting of ${className} {0} ", id );

        }
        catch (Exception exc) {
            final String errMsg = "Unable to delete ${className} using Id = "  + id;
            LOGGER.log( Level.WARNING, errMsg, exc );
            throw new ProcessingException( errMsg, exc );
        }
        finally {
        }
    }

    /**
     * Method to retrieve the ${className} via ${className}FetchOneSummary
     * @param 	summary ${className}FetchOneSummary 
     * @return 	${className}FetchOneResponse
     * @exception ProcessingException - Thrown if processing any related problems
     * @exception IllegalArgumentException 
     */
    public ${className} get${className}( ${className}FetchOneSummary summary ) 
    throws ProcessingException, IllegalArgumentException {
    	
    	if( summary == null )
    		throw new IllegalArgumentException( "${className}FetchOneSummary arg cannot be null" );
    	
    	${className} entity = null;
    	UUID id = summary.get${className}Id();
    	
        try {
        	// --------------------------------------
        	// validate the fetch one summary
        	// --------------------------------------    	
        	${className}Validator.getInstance().validate( summary );    
        	
        	// --------------------------------------
        	// find a $className using the provided id
        	// --------------------------------------
        	entity = projector.find( id );
        }
        catch( Exception exc ) {
            final String errMsg = "Unable to locate ${className} with id " + id;
            LOGGER.log( Level.WARNING, errMsg, exc );
            throw new ProcessingException( errMsg, exc );
        }
        finally {
        }        
        
        return entity;
    }


    /**
     * Method to retrieve a collection of all ${className}s
     *
     * @return 	List<${className}> 
     * @exception ProcessingException Thrown if any problems
     */
    public List<${className}> getAll${className}() 
    throws ProcessingException {
        List<${className}> list = null;

        try {        	
        	list = projector.findAll( new FindAll${className}Query() );
        }
        catch( Exception exc ) {
            String errMsg = "Failed to get all ${className}";
            LOGGER.log( Level.WARNING, errMsg, exc );
            throw new ProcessingException( errMsg, exc );
        }
        finally {
        }        
        
        return list;
    }

#set( $includeComposites = false )
#foreach( $singleAssociation in $classObject.getSingleAssociations( ${includeComposites} ) )
#set( $roleName = $singleAssociation.getRoleName() )
#set( $childType = $singleAssociation.getType() )
#set( $parentName  = $classObject.getName() )
#set( $alias = ${singleAssociation.getAssignToCommandAlias()} )
    /**
     * assign ${roleName} on ${className}
     * @param		command ${alias}	
     * @exception	ProcessingException
     */     
	public void assign${roleName}( ${alias} command ) throws ProcessingException {

		try {
			// --------------------------------------
	    	// best to validate the command now
	    	// --------------------------------------    
	    	${className}Validator.getInstance().validate( command );    

			// --------------------------------------
	    	// delegate to the projector
	    	// --------------------------------------    	    	
	    	projector.assign${roleName}(command.get${className}Id(), command.getAssignment());
		    
		}
        catch( Throwable exc ) {
			final String msg = "Failed to get ${childType} using id " + command.get${className}Id();
			LOGGER.log( Level.WARNING,  msg );
			throw new ProcessingException( msg, exc );
        }
	}

#set( $alias = ${singleAssociation.getUnAssignFromCommandAlias()} )	
    /**
     * unAssign ${roleName} on ${className}
     * @param		command ${alias}
     * @exception	ProcessingException
     */     
	public void unAssign${roleName}( ${alias} command ) throws ProcessingException {

		try {
			// --------------------------------------
	    	// validate the command
	    	// --------------------------------------    
	    	${className}Validator.getInstance().validate( command );    
	
	    	// --------------------------------------
	    	// delegate to the projector
	    	// --------------------------------------    	    	
	    	projector.unAssign${roleName}(command.get${className}Id());
		}
		catch( Exception exc ) {
			final String msg = "Failed to unassign $roleName on ${className}";
			LOGGER.log( Level.WARNING, msg, exc );
			throw new ProcessingException( msg, exc );
		}
	}
	
#end##foreach( $singleAssociation in $classObject.getSingleAssociations( ${includeComposites} ) )

#foreach( $multiAssociation in $classObject.getMultipleAssociations() )
#set( $roleName = $multiAssociation.getRoleName() )
#set( $childType = $multiAssociation.getType() )
#set( $parentName  = $classObject.getName() )
#set( $alias = ${multiAssociation.getAddToCommandAlias()} )
    /**
     * add ${childType} to ${roleName} 
     * @param		command ${alias}
     * @exception	ProcessingException
     */     
	public void addTo${roleName}( ${alias} command ) throws ProcessingException {

		try {		
			// --------------------------------------
	    	// validate the command
	    	// --------------------------------------    
	    	${className}Validator.getInstance().validate( command );    

	    	// --------------------------------------
	    	// delegate to the projector
	    	// --------------------------------------    	    	
	    	projector.addTo${roleName}(command.get${className}Id(), command.getAddTo());		}
		catch( Exception exc ) {
			final String msg = "Failed to add a ${childType} as ${roleName} to ${parentName}" ; 
			LOGGER.log( Level.WARNING, msg, exc );
			throw new ProcessingException( msg, exc );
		}

	}

#set( $alias = ${multiAssociation.getRemoveFromCommandAlias()} )
    /**
     * remove ${childType} from ${roleName}
     * @param		command ${alias}
     * @exception	ProcessingException
     */     	
	public void removeFrom${roleName}( ${alias} command ) throws ProcessingException {		

		try {
			
			// --------------------------------------
	    	// validate the command
	    	// --------------------------------------    
	    	${className}Validator.getInstance().validate( command );    

	    	// --------------------------------------
	    	// delegate to the projector
	    	// --------------------------------------    	    	
	    	projector.removeFrom${roleName}(command.get${className}Id(), command.getRemoveFrom());

		}
		catch( Exception exc ) {
			final String msg = "Failed to remove child using Id " + command.get${className}Id(); 
			LOGGER.log( Level.WARNING, msg, exc );
			throw new ProcessingException( msg, exc );
		}
	}

#end##foreach( $multiAssociation in $classObject.getMultipleAssociations() )

#foreach( $query in $aib.getQueriesToGenerate(${className}) )
#foreach( $handler in $query.getHandlers() )
#set( $method = $handler.getMethodObject() )
#if ( ${method.hasArguments()} )
#set( $queryName = $Utils.capitalizeFirstLetter( $handler.getName() ) )
#set( $returnType = ${method.getArguments().getReturnType()} )
    /**
     * finder method to ${method.getName()}
     * @param 		$argType $argName
     * @return		${returnType}
     */     
	public ${returnType} ${method.getName()}( ${queryName}Query query ) {
		return projector.${method.getName()}(query);
	}
	
#end##if ( ${method.hasArguments()} )
#end##foreach( $handler in $query.getHandlers() )
#end##foreach( $query in $aib.getQueriesToGenerate($className) )


#end##if ( $classObject.isAbstract() == false )

//************************************************************************
// Attributes
//************************************************************************
	@Autowired
    private final ${className}EntityProjector projector;
	private ${className} ${lowercaseClassName} 	= null;
    private static final Logger LOGGER 			= Logger.getLogger(${className}BusinessDelegate.class.getName());
    
}
