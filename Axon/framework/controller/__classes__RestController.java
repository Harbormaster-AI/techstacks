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
        try {       
        	$className entity = new $className();

#determineArgsAsAssignment( ${classObject} "entity" "command" )       
        	
			${className}BusinessDelegate.get${className}Instance().create${className}( entity );
        }
        catch( Throwable exc ) {
        	LOGGER.info( exc.getMessage() );        	
        }
    }

    /**
     * Handles updating a ${className}.  if not key provided, calls create, otherwise calls save
     * @param		${className} $lowercaseClassName
     */
	@PutMapping("/update")
    public void update( @RequestBody(required=true) Update${className}Command command ) {
	    try {                        	        
	    	$className entity = new $className();

#determineArgsAsAssignment( ${classObject} "entity" "command" )       

			// create the ${className}Business Delegate            
			${className}BusinessDelegate delegate = ${className}BusinessDelegate.get${className}Instance();
	        delegate.update${className}( entity );
	        
	        if ( this.${lowercaseClassName} != null )
	            LOGGER.info( "successfully updated ${className}" );
	    }
	    catch( Throwable exc ) {
	    	LOGGER.info( "${className}Controller:update() - successfully update ${className} - " + exc.getMessage());        	
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
    		LOGGER.info( "Successfully deleted ${className} with key " + ${lowercaseClassName}Id );
        }
        catch( Throwable exc ) {
        	LOGGER.info( exc.getMessage() );
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
            LOGGER.info( "failed to load ${className} using Id " + summary.get${className}Id() );
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
                LOGGER.info(  "successfully loaded all ${className}s" );
        }
        catch( Throwable exc ) {
            LOGGER.info(  "failed to load all ${className}s " );
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
        	LOGGER.info( "Failed to assign ${roleName}" );
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
			LOGGER.info( "Failed to unassign ${roleName}" );
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
			LOGGER.info( "Failed to add to Set $roleName" );
		}
	}

    /**
     * delete ${roleName} on ${className}
     * @param		UUID ${lowercaseClassName}Id
     * @param		${childType} entity
     */     	
	@PutMapping("/removeFrom${roleName}")
	public void removeFrom${roleName}( 	@RequestParam(required=true) UUID ${lowercaseClassName}Id, @RequestBody(required=true) ${childType} entity )
	{		
		try {
			${className}BusinessDelegate.get${className}Instance().removeFrom${roleName}( ${lowercaseClassName}Id, entity );
		}
		catch( Exception exc ) {
			LOGGER.info( "Failed to remove from Set ${roleName}" );
		}
	}

#end

#* QUERIES COMMENTED OUT
#foreach( $query in $aib.getQueriesToGenerate() )
#if ( $className.equalsIgnoreCase( $query.getAffiliate() ) )
#foreach( $handler in $query.getHandlers() )
#set( $method = $handler.getMethodObject() )
#if ( ${method.hasArguments()} )	## should only be one argument
#set( $argType = ${method.getArguments().getArgs().get(0).getType()} )
#set( $argName = ${method.getArguments().getArgs().get(0).getName()} )
#set( $returnType = ${method.getReturnType()} )
    /**
     * finder method to ${method.getName()}
     * @param 		$argType $argName
     * @return		Set<${className}>
     */     
	@GetMapping("/${method.getName()}")
	public ${returnType} ${method.getName()}( @RequestParam(required=true) $argType $argName ) {
		$returnType ${lowercaseClassName} = null;
        try {  
            // call the query directly
            ${lowercaseClassName} = new ${className}BusinessDelegate().${method.getName()}(${arg});
            
            if ( ${lowercaseClassName} != null )
                LOGGER.info(  "successfully read ${method.getName()}" );
        }
        catch( Throwable exc ) {
        	LOGGER.info(  "failed to read ${className}s for ${method.getName()}" );
        }
        return {lowercaseClassName};
	}
#end##if ( ${method.hasArguments()} )	## should only be one argument
#end##foreach( $handler in $query.getHandlers() )
#end##if ( $className.equalsIgnoreCase( $query.getAffiliate() ) )
#end##foreach( $query in $aib.getQueriesToGenerate() )
*#

//************************************************************************    
// Attributes
//************************************************************************
    protected ${className} $lowercaseClassName = null;
    private static final Logger LOGGER = Logger.getLogger(${className}RestController.class.getName());
    
}
