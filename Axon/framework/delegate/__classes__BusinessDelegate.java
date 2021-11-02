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
    	queryGateway = applicationContext.getBean(QueryGateway.class);
    	commandGateway = applicationContext.getBean(CommandGateway.class);
	}

#if ( $classObject.isAbstract() == false )

#set( $argName = "entity" )
#set($argsAsInput = "#determineArgsAsInput( ${classObject} ${argName} )" )

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
    * Creates the provided BO.
    * @param		${className}Entity entity
    * @exception    ProcessingException
    * @exception	IllegalArgumentException
    */
	public void create${className}( ${className}Entity entity )
    throws ProcessingException, IllegalArgumentException {
		final String msgPrefix = "${className}BusinessDelegate:create${className} - ";
        
        try {
        	Create${className}Command command = new Create${className}Command(${argsAsInput});
        	command.set${className}Id( UUID.randomUUID() );
        	
        	// --------------------------------------
        	// validate the create command
        	// --------------------------------------    	
    		${className}Validator.getInstance().validateCreation( command );
    		
    		// ---------------------------------------
    		// issue the create command
    		// ---------------------------------------
    		commandGateway.sendAndWait( command );
        }
        catch (Exception exc) {
            final String errMsg = "${className}BusinessDelegate:create${className}() - Unable to create ${className}" + exc;
            LOGGER.warning( errMsg );
            throw new ProcessingException( errMsg, exc );
        }
        finally {
        }        
    }

   /**
    * Saves the underlying BO.
    * @param		entity 	${className}Entity
    * @exception    ProcessingException
    * @exception  	IllegalArgumentException
    */
    public void update${className}( ${className}Entity entity ) 
    throws ProcessingException, IllegalArgumentException {
    	final String msgPrefix = "${className}BusinessDelegate:save${className} - ";

    	try {       
    		Update${className}Command command = new Update${className}Command(${argsAsInput});
    		
        	// --------------------------------------
        	// validate the update command
        	// --------------------------------------    	
        	${className}Validator.getInstance().validateUpdate( command );                

        	// --------------------------------------
        	// issue the update command
        	// --------------------------------------    	
    		commandGateway.sendAndWait( command );

    	}
        catch (Exception exc) {
            final String errMsg = "${className}BusinessDelegate:save${className}() - Unable to save ${className}" + exc;
            LOGGER.warning( errMsg );
            throw new ProcessingException( errMsg, exc );
        }
        
    }
   
   /**
    * Deletes the associatied value object
    * @param		${className}Entity	entity 
    * @exception 	ProcessingException
    */
    public void delete( ${className}Entity entity ) 
    throws ProcessingException, IllegalArgumentException {	
    	final String msgPrefix = "${className}BusinessDelegate:save${className} - ";
    	
        try {  
        	// --------------------------------------
        	// validate the deletion command
        	// --------------------------------------    	
        	Delete${className}Command command = new Delete${className}Command( entity.get${className}Id() );

        	${className}Validator.getInstance().validateDelete( command ); 

        	// --------------------------------------
        	// issue the update command
        	// --------------------------------------    	
    		commandGateway.sendAndWait( command );

        }
        catch (Exception exc) {
            final String errMsg = msgPrefix + "Unable to delete ${className} using Id = "  + entity.get${className}Id();
            LOGGER.warning( errMsg );
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
    public ${className}Entity get${className}( ${className}FetchOneSummary summary ) 
    throws ProcessingException, IllegalArgumentException {
    	final String msgPrefix = "${className}BusinessDelegate:get${className} - ";
    	
    	if( summary == null )
    		throw new IllegalArgumentException( "${className}FetchOneSummary arg cannot be null" );
    	
    	${className}Entity $lowercaseClassName = null;
    	
        try {
        	// --------------------------------------
        	// validate the fetch one summary
        	// --------------------------------------    	
        	${className}Validator.getInstance().validateFetchOne( summary );    
        	
        	// --------------------------------------
        	// use queryGateway to send request to Find a $className
        	// --------------------------------------
        	CompletableFuture<${className}Entity> futureEntity = queryGateway.query(new Find${className}Query( new Load${className}Filter( summary.get${className}Id() ) ), ResponseTypes.instanceOf(${className}Entity.class));
        	
        	$lowercaseClassName = futureEntity.get();
        }
        catch( Exception exc ) {
            final String errMsg = "Unable to locate ${className} with id " + summary.get${className}Id();
            LOGGER.warning( errMsg );
            throw new ProcessingException( errMsg, exc );
        }
        finally {
        }        
        
        return $lowercaseClassName;
    }


    /**
     * Method to retrieve a collection of all ${className}s
     *
     * @return 	List<${className}FetchOneSummary> 
     * @exception ProcessingException Thrown if any problems
     */
    public List<${className}Entity> getAll${className}() 
    throws ProcessingException {
        List<${className}Entity> list = null;

        try {
        	CompletableFuture<List<${className}Entity>> futureList = queryGateway.query(new FindAll${className}Query(), ResponseTypes.multipleInstancesOf(${className}Entity.class));
        	
        	list = futureList.get();
        }
        catch( Exception exc ) {
            String errMsg = "Failed to get all ${className}";
            LOGGER.warning( errMsg );
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
     * @param		${lowercaseClassName}Id	UUID	
     * @param		${className}Enity child
     * @exception	ProcessingException
     */     
	public void assign${roleName}( UUID ${lowercaseClassName}Id,  ${childType}Entity child ) throws ProcessingException{
		if ( ${lowercaseClassName}Id == null ) {
			throw new ProcessingException( "${lowercaseClassName}Id cannot be null" ); 
		}
		
		if ( child == null ) {
			throw new ProcessingException( "$roleName cannot be null" ); 
		}

		load( ${lowercaseClassName}Id );

		${className}Entity ${lowercaseClassName} = null; 
		
		${childType}BusinessDelegate childDelegate 	= ${childType}BusinessDelegate.get${childType}Instance();
		${className}BusinessDelegate parentDelegate = ${className}BusinessDelegate.get${className}Instance();			

		UUID childId 								= child.get${childType}Id();
		try {
			child = childDelegate.get${childType}( new ${childType}FetchOneSummary( childId.toString() ) );
		}
        catch( Throwable exc ) {
			final String msg = "Failed to get ${childType} using id " + childId.toString();
			LOGGER.info( msg );
			throw new ProcessingException( msg, exc );
        }
	
		${lowercaseClassName}.set${roleName}( child );
	
		try {
			// save it
			parentDelegate.update${className}( ${lowercaseClassName} );
		}
		catch( Exception exc ) {
			final String msg = "Failed saving parent ${className} using Id " + ${lowercaseClassName}Id.toString();
			LOGGER.info( msg );
			throw new ProcessingException( msg, exc );
		}
	}

    /**
     * unAssign ${roleName} on ${className}
     * @param		${lowercaseClassName}Id	UUID	
     * @exception	ProcessingException
     */     
	public void unAssign${roleName}( UUID ${lowercaseClassName}Id ) throws ProcessingException {

		load( ${lowercaseClassName}Id );

		if ( ${lowercaseClassName}.get${roleName}() != null ) {
			UUID childId = ${lowercaseClassName}.get${roleName}().get${childType}Id();
			
			// null out the parent first so there's no constraint during deletion
			${lowercaseClassName}.set${roleName}( null );

			try {
				${className}BusinessDelegate parentDelegate = ${className}BusinessDelegate.get${className}Instance();

				// save it
				parentDelegate.update${className}( ${lowercaseClassName} );
			}
			catch( Exception exc ) {
				final String msg = "Failed to save ${className}";
				LOGGER.info( msg );
				throw new ProcessingException( msg, exc );
			}
			
			try {
				// safe to delete the child			
				${childType}BusinessDelegate childDelegate = ${childType}BusinessDelegate.get${childType}Instance();
				childDelegate.delete( childDelegate.load(childId) );
			}
			catch( Exception exc ) {
				final String msg = "Failed to delete the child using Id " + childId.toString(); 
				LOGGER.info( msg );
				throw new ProcessingException( msg, exc );
			}
		}
	}
	
#end##foreach( $singleAssociation in $classObject.getSingleAssociations( ${includeComposites} ) )

#foreach( $multiAssociation in $classObject.getMultipleAssociations() )
#set( $roleName = $multiAssociation.getRoleName() )
#set( $childType = $multiAssociation.getType() )
#set( $parentName  = $classObject.getName() )
    /**
     * add ${childType} to ${roleName} 
     * @param		UUID ${lowercaseClassName}Id
     * @param		${childType}Entity child 
     * @exception	ProcessingException
     */     
	public void addTo${roleName}(	UUID ${lowercaseClassName}Id, 
												${childType}Entity child ) throws ProcessingException {
		if ( child == null ) {
			throw new ProcessingException( "$roleName entity arg cannot be null" ); 
		}
		
		load( ${lowercaseClassName}Id );

		${childType}BusinessDelegate childDelegate 	= ${childType}BusinessDelegate.get${childType}Instance();
		${className}BusinessDelegate parentDelegate = ${className}BusinessDelegate.get${className}Instance();		
		UUID childId = child.get${childType}Id();
		
		// if no child id, then create the child first, otherwise load it for consistency sake then use it
		if ( childId == null  ) {
			
			try {
				// create the ${childType}
				childDelegate.create${childType}( child );
			}
			catch( Exception exc ) {
				final String msg = "Failed to get child ${childType} using id " + childId.toString(); 
				LOGGER.info( msg );
				throw new ProcessingException( msg, exc );
			}
			
			// add it to the ${roleName} 
			${lowercaseClassName}.get${roleName}().add( child );				
		}
		else {
			try {
				// find the ${childType}
				child = childDelegate.get${childType}( new ${childType}FetchOneSummary(childId.toString()) );
				
				// add it to the ${roleName}
				${lowercaseClassName}.get${roleName}().add( child );
			}
			catch( Exception exc ) {
				final String msg = "Failed to add child ${childType} using id " + childId.toString(); 
				LOGGER.info( msg );
				throw new ProcessingException( msg, exc );
			}
		}

		try {
			// save the ${className}
			parentDelegate.update${className}( ${lowercaseClassName} );
		}
		catch( Exception exc ) {
			final String msg = "Failed saving parent ${className}" ; 
			LOGGER.info( msg );
			throw new ProcessingException( msg, exc );
		}
	}

    /**
     * remove ${childType} from ${roleName}
     * @param		UUID ${lowercaseClassName}Id
     * @param		${childType}Entity child
     * @exception	ProcessingException
     */     	
	public void removeFrom${roleName}( 	UUID ${lowercaseClassName}Id, ${childType}Entity child ) throws ProcessingException {		
		if ( child == null ) {
			throw new ProcessingException( "$roleName cannot be null" ); 
		}
		
		load( ${lowercaseClassName}Id );

		${childType}BusinessDelegate childDelegate 	= ${childType}BusinessDelegate.get${childType}Instance();
		${className}BusinessDelegate parentDelegate = ${className}BusinessDelegate.get${className}Instance();
		List<${childType}Entity> children = ${lowercaseClassName}.get${roleName}();

		try {
			// first remove the relevant child from the list
			// child = childDelegate.get${childType}( new ${childType}FetchOneSummary(childId.toString()));
			children.remove( child );
			
			// then safe to delete the child				
			childDelegate.delete( child );
		}
		catch( Exception exc ) {
			final String msg = "Failed to delete child using Id " + child.get${childType}Id().toString() ; 
			LOGGER.info( msg );
			throw new ProcessingException( msg, exc );
		}
			
		// assign the modified list of ${childType} back to the ${lowercaseClassName}
		${lowercaseClassName}.set${roleName}( children );			
		
		// save it 
		try {
			parentDelegate.update${className}( ${lowercaseClassName} );
		}
		catch( Throwable exc ) {
			final String msg = "Failed to save the ${className}"; 
			LOGGER.info( msg );
			throw new ProcessingException( msg, exc );
		}
	}

#end##foreach( $multiAssociation in $classObject.getMultipleAssociations() )

	/**
	 * Internal helper method to load the root 
	 * 
	 * @param		id	UUID
	 * @return		${className}Entity
	 */
	public ${className}Entity load( UUID id ) throws ProcessingException {
		${lowercaseClassName} = ${className}BusinessDelegate.get${className}Instance().get${className}( new ${className}FetchOneSummary(id.toString() ) );	
		return ${lowercaseClassName};
	}

#end##if ( $classObject.isAbstract() == false )

//************************************************************************
// Attributes
//************************************************************************
    @Autowired
    private ApplicationContext applicationContext;
	private final QueryGateway queryGateway;
	private final CommandGateway commandGateway;
	private ${className}Entity ${lowercaseClassName} 	= null;
    private static final Logger LOGGER 					= Logger.getLogger(${className}BusinessDelegate.class.getName());
    
}
