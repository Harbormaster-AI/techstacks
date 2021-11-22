#header()
#set( $className = ${classObject.getName()} )
#set( $lowercaseClassName = ${Utils.lowercaseFirstLetter( ${className} )} )
#set( $pk = "${className}Id" )		
##set( $pkExpression = "${className}Id" ) 
##set( $pkExpression = "${lowercaseClassName}.${pk.getName()}" ) 
package ${aib.getRootPackageName(true)}.#getRestControllerPackageName();

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.*;
import java.util.concurrent.*;
import java.util.logging.Level;
import java.util.logging.Logger;

#set( $imports = [ "api", "delegate", "entity", "exception", "handler" ] )
#importStatements( $imports )

/** 
 * Implements Struts action processing for business entity ${className}.
 *
 * @author $aib.getAuthor()
 */
@CrossOrigin
@RestController
@RequestMapping("/${className}")
##if ( $classObject.hasParent() == true )
##	#set( $parentController = "${classObject.getParentName()}RestController" )
##	#set( $parentName = $classObject.getParentName() )
##else
	#set( $parentController = "BaseSpringRestController" )
##end
public class ${className}RestController extends $parentController {

    /**
     * Handles create a ${className}.  if not key provided, calls create, otherwise calls save
     * @param		${className}	${lowercaseClassName}
     * @return		CompletableFuture<UUID> 
     */
	@PostMapping("/create")
    public CompletableFuture<UUID> create( @RequestBody(required=true) ${classObject.getCreateCommandAlias()} command ) {
		CompletableFuture<UUID> completableFuture = null;
		try {       
        	
			completableFuture = ${className}BusinessDelegate.get${className}Instance().create${className}( command );
        }
        catch( Throwable exc ) {
        	LOGGER.log( Level.WARNING, exc.getMessage(), exc );        	
        }
		
		return completableFuture;
    }

    /**
     * Handles updating a ${className}.  if no key provided, calls create, otherwise calls save
     * @param		${className} $lowercaseClassName
     * @return		CompletableFuture<Void>
     */
	@PutMapping("/update")
    public CompletableFuture<Void> update( @RequestBody(required=true) ${classObject.getUpdateCommandAlias()} command ) {
		CompletableFuture<Void> completableFuture = null;
		try {                        	        
			// -----------------------------------------------
			// delegate the ${classObject.getUpdateCommandAlias()}
			// -----------------------------------------------
			completableFuture = ${className}BusinessDelegate.get${className}Instance().update${className}(command);;
	    }
	    catch( Throwable exc ) {
	    	LOGGER.log( Level.WARNING, "${className}Controller:update() - successfully update ${className} - " + exc.getMessage());        	
	    }		
		
		return completableFuture;
	}
 
    /**
     * Handles deleting a ${className} entity
     * @param		command ${class.getDeleteCommandAlias()}
     * @return		CompletableFuture<Void>
     */
    @DeleteMapping("/delete")    
    public CompletableFuture<Void> delete( @RequestBody(required=true) ${classObject.getDeleteCommandAlias()} command ) {                
    	CompletableFuture<Void> completableFuture = null;
    	try {
        	${className}BusinessDelegate delegate = ${className}BusinessDelegate.get${className}Instance();

        	completableFuture = delegate.delete( command );
    		LOGGER.log( Level.WARNING, "Successfully deleted ${className} with key " + command.get${className}Id() );
        }
        catch( Throwable exc ) {
        	LOGGER.log( Level.WARNING, exc.getMessage() );
        }
        
        return completableFuture;
	}        
	
    /**
     * Handles loading a ${className} using a UUID
     * @param		UUID uuid 
     * @return		${className}
     */    
    @GetMapping("/load")
    public ${className} load( @RequestParam(required=true) UUID uuid ) {    	
    	${className} entity = null;

    	try {  
    		entity = ${className}BusinessDelegate.get${className}Instance().get${className}( new ${className}FetchOneSummary( uuid ) );   
        }
        catch( Throwable exc ) {
            LOGGER.log( Level.WARNING, "failed to load ${className} using Id " + uuid );
            return null;
        }

        return entity;
    }

    /**
     * Handles loading all ${className} business objects
     * @return		Set<${className}>
     */
    @GetMapping("/")
    public List<${className}> loadAll() {                
    	List<${className}> ${lowercaseClassName}List = null;
        
    	try {
            // load the ${className}
            ${lowercaseClassName}List = ${className}BusinessDelegate.get${className}Instance().getAll${className}();
            
            if ( ${lowercaseClassName}List != null )
                LOGGER.log( Level.INFO,  "successfully loaded all ${className}s" );
        }
        catch( Throwable exc ) {
            LOGGER.log( Level.WARNING,  "failed to load all ${className}s ", exc );
        	return null;
        }

        return ${lowercaseClassName}List;
                            
    }

#set( $includeComposites = false )
#foreach( $singleAssociation in $classObject.getSingleAssociations( ${includeComposites} ) )
#set( $roleName = $singleAssociation.getRoleName() )
#set( $childType = $singleAssociation.getType() )
#set( $alias = ${singleAssociation.getAssignToCommandAlias()} )
    /**
     * save ${roleName} on ${className}
     * @param		command $alias
     */     
	@PutMapping("/assign${roleName}")
	public void assign${roleName}( @RequestBody ${alias} command ) {
		try {
			${className}BusinessDelegate.get${className}Instance().assign${roleName}( command );   
		}
        catch( Throwable exc ) {
        	LOGGER.log( Level.WARNING, "Failed to assign ${roleName}", exc );
        }
	}

#set( $alias = ${singleAssociation.getUnAssignFromCommandAlias()} )	
    /**
     * unassign ${roleName} on ${className}
     * @param		 command ${alias}
     */     
	@PutMapping("/unAssign${roleName}")
	public void unAssign${roleName}( @RequestBody(required=true)  ${alias} command ) {
		try {
			${className}BusinessDelegate.get${className}Instance().unAssign${roleName}( command );   
		}
		catch( Exception exc ) {
			LOGGER.log( Level.WARNING, "Failed to unassign ${roleName}", exc );
		}
	}
	
#end

#foreach( $multiAssociation in $classObject.getMultipleAssociations() )
#set( $roleName = $multiAssociation.getRoleName() )
#set( $childType = $multiAssociation.getType() )
#set( $alias = ${multiAssociation.getAddToCommandAlias()} )
    /**
     * save ${roleName} on ${className}
     * @param		command ${alias}
     */     
	@PutMapping("/addTo${roleName}")
	public void addTo${roleName}( @RequestBody(required=true) ${alias} command ) {
		try {
			${className}BusinessDelegate.get${className}Instance().addTo${roleName}( command );   
		}
		catch( Exception exc ) {
			LOGGER.log( Level.WARNING, "Failed to add to Set $roleName", exc );
		}
	}

#set( $alias = ${multiAssociation.getRemoveFromCommandAlias()} )	
    /**
     * remove ${roleName} on ${className}
     * @param		command ${alias}
     */     	
	@PutMapping("/removeFrom${roleName}")
	public void removeFrom${roleName}( 	@RequestBody(required=true) ${alias} command )
	{		
		try {
			${className}BusinessDelegate.get${className}Instance().removeFrom${roleName}( command );
		}
		catch( Exception exc ) {
			LOGGER.log( Level.WARNING, "Failed to remove from Set ${roleName}", exc );
		}
	}

#end

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
     * @return		$returnType
     */     
	@PostMapping("/${method.getName()}")
	public ${returnType} ${method.getName()}( @RequestBody(required=true) ${queryName}Query query ) {
		${returnType} result = null;
        try {  
            // call the delegate directly
        	result = new ${className}BusinessDelegate().${method.getName()}(query);
            
            if ( result != null )
                LOGGER.log( Level.WARNING,  "successfully executed ${method.getName()}" );
        }
        catch( Throwable exc ) {
        	LOGGER.log( Level.WARNING,  "failed to execute ${method.getName()}" );
        }
        return result;
	}
#end##if ( ${method.hasArguments()} )
#end##foreach( $handler in $query.getHandlers() )
#end##foreach( $query in $aib.getQueriesToGenerate() )


//************************************************************************    
// Attributes
//************************************************************************
    protected ${className} $lowercaseClassName = null;
    private static final Logger LOGGER = Logger.getLogger(${className}RestController.class.getName());
    
}
