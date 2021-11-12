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
import java.util.logging.Level;
import java.util.logging.Logger;

#set( $imports = [ "api", "delegate", "entity", "exception", "handler" ] )
#importStatements( $imports )

/** 
 * Implements Struts action processing for business entity ${className}.
 *
 * @author $aib.getAuthor()
 */
##if ( $classObject.hasParent() == true )
##	#set( $parentController = "${classObject.getParentName()}RestController" )
##	#set( $parentName = $classObject.getParentName() )
##else
	#set( $parentController = "BaseSpringRestController" )
##end
@CrossOrigin
@RestController
@RequestMapping("/${className}")
public class ${className}RestController extends $parentController {

    /**
     * Handles create a ${className}.  if not key provided, calls create, otherwise calls save
     * @param		${className}	${lowercaseClassName}
     */
	@PostMapping("/create")
    public void create( @RequestBody(required=true) Create${className}Command command ) {
    	$className entity = new $className();

#set( $includeAssociations = false )
#determineArgsAsAssignment( ${classObject} "entity" "command" ${includeAssociations} )       

		try {       
        	
			${className}BusinessDelegate.get${className}Instance().create${className}( entity );
        }
        catch( Throwable exc ) {
        	LOGGER.log( Level.WARNING, exc.getMessage(), exc );        	
        }
    }

    /**
     * Handles updating a ${className}.  if not key provided, calls create, otherwise calls save
     * @param		${className} $lowercaseClassName
     */
	@PutMapping("/update")
    public void update( @RequestBody(required=true) Update${className}Command command ) {
    	$className entity = new $className();

#set( $includeAssociations = true )
#determineArgsAsAssignment( ${classObject} "entity" "command" ${includeAssociations} )       

		try {                        	        
			// create the ${className}Business Delegate            
			${className}BusinessDelegate delegate = ${className}BusinessDelegate.get${className}Instance();
	        delegate.update${className}( entity );
	        
	        if ( this.${lowercaseClassName} != null )
	            LOGGER.log( Level.WARNING, "successfully updated ${className}" );
	    }
	    catch( Throwable exc ) {
	    	LOGGER.log( Level.WARNING, "${className}Controller:update() - successfully update ${className} - " + exc.getMessage());        	
	    }
	}
 
    /**
     * Handles deleting a ${className} entity
     * @param		UUID ${lowercaseClassName}Id
     * @return		boolean
     */
    @DeleteMapping("/delete")    
    public boolean delete( @RequestParam(required=true) UUID ${lowercaseClassName}Id ) {                
        try {
        	${className}BusinessDelegate delegate = ${className}BusinessDelegate.get${className}Instance();

        	delegate.delete( load( new ${className}FetchOneSummary( ${lowercaseClassName}Id )) );
    		LOGGER.log( Level.WARNING, "Successfully deleted ${className} with key " + ${lowercaseClassName}Id );
        }
        catch( Throwable exc ) {
        	LOGGER.log( Level.WARNING, exc.getMessage() );
        	return false;        	
        }
        
        return true;
	}        
	
    /**
     * Handles loading a ${className}FetchOneSummary
     * @param		summary ${className}FetchOneSummary
     * @return		${className}FetchOneResponse
     */    
    @PostMapping("/load")
    public ${className} load( @RequestBody(required=true) ${className}FetchOneSummary summary ) {    	
    	${className} entity = null;

    	try {  
    		entity = ${className}BusinessDelegate.get${className}Instance().get${className}( summary );   
        }
        catch( Throwable exc ) {
            LOGGER.log( Level.WARNING, "failed to load ${className} using Id " + summary.get${className}Id() );
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
#set( $parentName  = $classObject.getName() )
    /**
     * save ${roleName} on ${className}
     * @param		UUID	${lowercaseClassName}Id
     * @param		${childType} entity
     * @param		${className} $lowercaseClassName
     */     
	@PutMapping("/assign${roleName}")
	public void assign${roleName}( @RequestParam(required=true) UUID ${lowercaseClassName}Id, @RequestBody ${childType} entity ) {
		try {
			${className}BusinessDelegate.get${className}Instance().assign${roleName}( ${lowercaseClassName}Id, entity );   
		}
        catch( Throwable exc ) {
        	LOGGER.log( Level.WARNING, "Failed to assign ${roleName}", exc );
        }
	}

    /**
     * unassign ${roleName} on ${className}
     * @param		UUID	${lowercaseClassName}Id
     */     
	@PutMapping("/unAssign${roleName}")
	public void unAssign${roleName}( @RequestParam(required=true) UUID ${lowercaseClassName}Id ) {
		try {
			${className}BusinessDelegate.get${className}Instance().unAssign${roleName}( ${lowercaseClassName}Id );   
		}
		catch( Exception exc ) {
			LOGGER.log( Level.WARNING, "Failed to unassign ${roleName}", exc );
		}
	}
	
#end

#foreach( $multiAssociation in $classObject.getMultipleAssociations() )
#set( $roleName = $multiAssociation.getRoleName() )
#set( $childType = $multiAssociation.getType() )
#set( $parentName  = $classObject.getName() )
    /**
     * save ${roleName} on ${className}
     * @param		UUID ${lowercaseClassName}Id
     * @param		${childType} entity 
     */     
	@PutMapping("/addTo${roleName}")
	public void addTo${roleName}( @RequestParam(required=true) UUID ${lowercaseClassName}Id, @RequestBody(required=true) ${childType} entity ) {
		try {
			${className}BusinessDelegate.get${className}Instance().addTo${roleName}( ${lowercaseClassName}Id, entity );   
		}
		catch( Exception exc ) {
			LOGGER.log( Level.WARNING, "Failed to add to Set $roleName", exc );
		}
	}

    /**
     * delete ${roleName} on ${className}
     * @param		UUID ${lowercaseClassName}Id
     * @param		UUID childId
     */     	
	@PutMapping("/removeFrom${roleName}")
	public void removeFrom${roleName}( 	@RequestParam(required=true) UUID ${lowercaseClassName}Id, @RequestParam(required=true) UUID childId)
	{		
		try {
			${className}BusinessDelegate.get${className}Instance().removeFrom${roleName}( ${lowercaseClassName}Id, childId );
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
