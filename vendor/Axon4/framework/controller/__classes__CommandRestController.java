#header()
#set( $className = ${classObject.getName()} )
#set( $lowercaseClassName = ${Utils.lowercaseFirstLetter( ${className} )} )
package ${aib.getRootPackageName(true)}.#getRestControllerPackageName().command;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.*;
import java.util.concurrent.*;
import java.util.logging.Level;
import java.util.logging.Logger;

#set( $imports = [ "api", "delegate", "entity", "exception", "projector" ] )
#importStatements( $imports )

import ${aib.getRootPackageName(true)}.#getRestControllerPackageName().*;

/** 
 * Implements Spring Controller command CQRS processing for entity ${className}.
 *
 * @author $aib.getAuthor()
 */
@CrossOrigin
@RestController
@RequestMapping("/${className}")
##if ( $classObject.hasParent() == true )
##	#set( $parentController = "${classObject.getParentName()}CommandRestController" )
##	#set( $parentName = $classObject.getParentName() )
##else
	#set( $parentController = "BaseSpringRestController" )
##end
public class ${className}CommandRestController extends $parentController {

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
    public CompletableFuture<Void> delete( @RequestParam(required=true) UUID ${lowercaseClassName}Id  ) {
    	CompletableFuture<Void> completableFuture = null;
		${classObject.getDeleteCommandAlias()} command = new ${classObject.getDeleteCommandAlias()}( ${lowercaseClassName}Id );

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

//************************************************************************    
// Attributes
//************************************************************************
    protected ${className} $lowercaseClassName = null;
    private static final Logger LOGGER = Logger.getLogger(${className}CommandRestController.class.getName());
    
}
