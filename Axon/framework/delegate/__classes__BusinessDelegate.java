#header()
#set( $className = $classObject.getName() )
#set( $lowercaseClassName = ${Utils.lowercaseFirstLetter( ${className} )} )
package ${aib.getRootPackageName(true)}.#getDelegatePackageName();

import java.io.IOException;
import java.util.logging.Level;
import java.util.logging.Logger;
import java.util.*;
import java.util.concurrent.*;

import org.axonframework.messaging.responsetypes.ResponseTypes;
import org.axonframework.commandhandling.gateway.CommandGateway;
import org.axonframework.queryhandling.QueryGateway;
import org.axonframework.queryhandling.QueryUpdateEmitter;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationContext;

#set( $imports = [ "api", "entity", "exception", "validator" ] )
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
    	queryGateway 		= applicationContext.getBean(QueryGateway.class);
    	commandGateway 		= applicationContext.getBean(CommandGateway.class);
    	queryUpdateEmitter  = applicationContext.getBean(QueryUpdateEmitter.class);
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
    * @return		${className}
    */
	public ${className} create${className}( ${classObject.getCreateCommandAlias()} command )
    throws ProcessingException, IllegalArgumentException {

		$className entity = null;
		
		try {
			// --------------------------------------
        	// assign identity now if none
        	// -------------------------------------- 
			if ( command.get${className}Id() == null )
				command.set${className}Id( UUID.randomUUID() );
				
			// --------------------------------------
        	// validate the command
        	// --------------------------------------    	
        	${className}Validator.getInstance().validate( command );    

    		// ---------------------------------------
    		// issue the command
    		// ---------------------------------------
        	CompletableFuture<${className}> futureEntity = commandGateway.send( command );
			entity = futureEntity.get();
			
			LOGGER.log( Level.INFO, "return from Command Gateway for create of ${className} is " + entity );
			
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
    * @return		${className}
    */
    public $className update${className}( ${classObject.getUpdateCommandAlias()} command ) 
    throws ProcessingException, IllegalArgumentException {

    	$className entity = null;
    	
    	try {       

			// --------------------------------------
        	// validate 
        	// --------------------------------------    	
        	${className}Validator.getInstance().validate( command );    

        	// --------------------------------------
        	// issue the command
        	// --------------------------------------    	
        	CompletableFuture<${className}> futureEntity = commandGateway.send( command );

        	entity = futureEntity.get();
        	
			LOGGER.log( Level.INFO, "return from Command Gateway for update of ${className} is " + entity );
        	
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
    	
        try {  
			// --------------------------------------
        	// validate the command
        	// --------------------------------------    	
        	${className}Validator.getInstance().validate( command );    

        	// --------------------------------------
        	// issue the update command
        	// --------------------------------------    	
    		commandGateway.send( command );

        }
        catch (Exception exc) {
            final String errMsg = "Unable to delete ${className} using Id = "  + command.get${className}Id();
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
    	
        try {
        	// --------------------------------------
        	// validate the fetch one summary
        	// --------------------------------------    	
        	${className}Validator.getInstance().validate( summary );    
        	
        	// --------------------------------------
        	// use queryGateway to send request to Find a $className
        	// --------------------------------------
        	CompletableFuture<${className}> futureEntity = queryGateway.query(new Find${className}Query( new Load${className}Filter( summary.get${className}Id() ) ), ResponseTypes.instanceOf(${className}.class));
        	
        	entity = futureEntity.get();
        }
        catch( Exception exc ) {
            final String errMsg = "Unable to locate ${className} with id " + summary.get${className}Id();
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
     * @return 	List<${className}FetchOneSummary> 
     * @exception ProcessingException Thrown if any problems
     */
    public List<${className}> getAll${className}() 
    throws ProcessingException {
        List<${className}> list = null;

        try {
        	CompletableFuture<List<${className}>> futureList = queryGateway.query(new FindAll${className}Query(), ResponseTypes.multipleInstancesOf(${className}.class));
        	
        	list = futureList.get();
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
    /**
     * assign ${roleName} on ${className}
     * @param		Assign${roleName}To${className}Command command	
     * @exception	ProcessingException
     */     
	public void assign${roleName}( Assign${roleName}To${className}Command command ) throws ProcessingException {

		// --------------------------------------------
		// load the parent
		// --------------------------------------------
		load( command.get${className}Id() );
		
		${childType}BusinessDelegate childDelegate 	= ${childType}BusinessDelegate.get${childType}Instance();
		${className}BusinessDelegate parentDelegate = ${className}BusinessDelegate.get${className}Instance();			
		UUID childId = command.getAssignment().get${childType}Id();
		${childType} child = null;
		
		try {
	        // --------------------------------------------------------------
			// if there is a childId it means the child exists, so get it
			// to ensure we have the "most up to date" version
	        // --------------------------------------------------------------
/*			if ( childId != null )
				child = childDelegate.get${childType}( new ${childType}FetchOneSummary( childId ) );
			else // otherwise create it
				child = childDelegate.create${childType}( child );

			// --------------------------------------
	    	// update the command
	    	// --------------------------------------    
			commandAssignment( child );
*/
			// --------------------------------------
	    	// best to validate the command now
	    	// --------------------------------------    
	    	${className}Validator.getInstance().validate( command );    

	    	// --------------------------------------
        	// issue the command
        	// --------------------------------------    	
    		commandGateway.sendAndWait( command );

		}
        catch( Throwable exc ) {
			final String msg = "Failed to get ${childType} using id " + childId;
			LOGGER.log( Level.WARNING,  msg );
			throw new ProcessingException( msg, exc );
        }
	}

    /**
     * unAssign ${roleName} on ${className}
     * @param		UnAssign${roleName}From${className}Command command	
     * @exception	ProcessingException
     */     
	public void unAssign${roleName}( UnAssign${roleName}From${className}Command command ) throws ProcessingException {

		try {
			// --------------------------------------
	    	// validate the command
	    	// --------------------------------------    
	    	${className}Validator.getInstance().validate( command );    
	
	    	// --------------------------------------
	    	// issue the command
	    	// --------------------------------------    	
			commandGateway.sendAndWait( command );
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
    /**
     * add ${childType} to ${roleName} 
     * @param		Add${roleName}To${className}Command command command
     * @exception	ProcessingException
     */     
	public void addTo${roleName}( Add${roleName}To${className}Command command ) throws ProcessingException {
		
		
		// -------------------------------------------
		// load the parent
		// -------------------------------------------
		load( command.get${className}Id() );

		${childType}BusinessDelegate childDelegate 	= ${childType}BusinessDelegate.get${childType}Instance();
		${className}BusinessDelegate parentDelegate = ${className}BusinessDelegate.get${className}Instance();		
		UUID childId = command.getAddTo().get${childType}Id();
		
		// -------------------------------------------
		// if no child id or the child is not found, 
		// then create the child first, 
		// otherwise add what was found
		// -------------------------------------------
		try {		
	/*		${childType} childToAdd = childId == null ? null : childDelegate.get${childType}( new ${childType}FetchOneSummary(childId) );
				
			// -------------------------------------------
			// if not found, create it
			// -------------------------------------------
			if ( childToAdd == null ) {
				childToAdd = childDelegate.create${childType}( command );
			}

			// --------------------------------------
	    	// assign the most recent version
	    	// --------------------------------------    
			command.setAddTo( childToAdd );
	*/		
			// --------------------------------------
	    	// validate the command
	    	// --------------------------------------    
	    	${className}Validator.getInstance().validate( command );    

	    	// --------------------------------------
        	// issue the command
        	// --------------------------------------    	
    		commandGateway.sendAndWait( command );			
		}
		catch( Exception exc ) {
			final String msg = "Failed to add a ${childType} as ${roleName} to ${parentName}" ; 
			LOGGER.log( Level.WARNING, msg, exc );
			throw new ProcessingException( msg, exc );
		}

	}

    /**
     * remove ${childType} from ${roleName}
     * @param		Remove${roleName}From${className}Command command
     * @exception	ProcessingException
     */     	
	public void removeFrom${roleName}( Remove${roleName}From${className}Command command ) throws ProcessingException {		
		
		${childType}BusinessDelegate childDelegate 	= ${childType}BusinessDelegate.get${childType}Instance();
		UUID childId = command.getRemoveFrom().get${childType}Id();

		try {
			
//			$childType childToRemove = childDelegate.get${childType}(new ${childType}FetchOneSummary( childId ));
			
			// --------------------------------------
	    	// assign most recent version to the command
	    	// --------------------------------------    
//			command.setRemoveFrom( childToRemove );

			// --------------------------------------
	    	// validate the command
	    	// --------------------------------------    
	    	${className}Validator.getInstance().validate( command );    

	    	// --------------------------------------
	    	// issue the command
	    	// --------------------------------------    	
			commandGateway.sendAndWait( command );

		}
		catch( Exception exc ) {
			final String msg = "Failed to remove child using Id " + childId; 
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
#set( $argType = ${method.getArguments().getArgs().get(0).getType()} )
#set( $argName = ${method.getArguments().getArgs().get(0).getName()} )
#set( $returnType = ${method.getArguments().getReturnType()} )

    /**
     * finder method to ${method.getName()}
     * @param 		$argType $argName
     * @return		${returnType}
     */     
	public ${returnType} ${method.getName()}( ${queryName}Query query ) {
		${returnType} result = null;
        try {  
#if ( $handler.getSingleValueReturnValue() == true )
#set( $entityType = "ResponseTypes.instanceOf(${className}.class)" )
#else
#set( $entityType = "ResponseTypes.multipleInstancesOf(${className}.class)" )        	
#end##if ( $handler.getSingleValueReturnValue() == true )
#set( $queryHandlerType = $handler.getQueryHandlerType().toString() )
#if( $queryHandlerType.equalsIgnoreCase("NORMAL") == true ) 
		    CompletableFuture<${returnType}> futureResult = queryGateway.query(query, ${entityType});
#elseif( $queryHandlerType.equalsIgnoreCase("SCATTER_GATHER") == true )
		    CompletableFuture<${returnType}> futureResult = quertGateway.scatterGather(query, ${handler}.getTimeOut(), ${handler}.getTimeOutUnit())
#elseif( $queryHandlerType.equalsIgnoreCase("SUBSCRIPTION") == true ) 
		    // subscription generation not yet supported...defaulting ot normal
		    CompletableFuture<${returnType}> futureResult = queryGateway.query(query, ${entityType});
#end
        	result = futureResult.get();
        }
        catch( Throwable exc ) {
        	LOGGER.log( Level.WARNING, "Failed to execute ${method.getName()}", exc );
        }
        return result;
	}
#end##if ( ${method.hasArguments()} )
#end##foreach( $handler in $query.getHandlers() )
#end##foreach( $query in $aib.getQueriesToGenerate() )

	/**
	 * Internal helper method to load the root 
	 * 
	 * @param		id	UUID
	 * @return		${className}
	 */
	protected ${className} load( UUID id ) throws ProcessingException {
		${lowercaseClassName} = ${className}BusinessDelegate.get${className}Instance().get${className}( new ${className}FetchOneSummary(id) );	
		return ${lowercaseClassName};
	}

#end##if ( $classObject.isAbstract() == false )

//************************************************************************
// Attributes
//************************************************************************
	private final QueryGateway queryGateway;
	private final CommandGateway commandGateway;
	private final QueryUpdateEmitter queryUpdateEmitter;
	private ${className} ${lowercaseClassName} 	= null;
    private static final Logger LOGGER 			= Logger.getLogger(${className}BusinessDelegate.class.getName());
    
}
