#header()
#set( $className = ${classObject.getName()} )
#set( $lowercaseClassName = ${Utils.lowercaseFirstLetter( ${className} )} )
#set( $pk = "${className}Id" )		
##set( $pkExpression = "${className}Id" ) 
##set( $pkExpression = "${lowercaseClassName}.${pk.getName()}" ) 
package ${aib.getRootPackageName(true)}.#getRestControllerPackageName();

import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.bind.annotation.RequestBody;

import java.util.*;
import java.util.logging.Level;
import java.util.logging.Logger;

#set( $imports = [ "api", "delegate", "entity", "exception", "query" ] )
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
     * Handles create a ${className}Entity.  if not key provided, calls create, otherwise calls save
     * @param		${className}Entity	${lowercaseClassName}
     */
	@RequestMapping("/create")
    public void create( @RequestBody ${className}Entity ${lowercaseClassName} ) {
        try {       
			${className}BusinessDelegate.get${className}Instance().create${className}( ${lowercaseClassName} );
        }
        catch( Throwable exc ) {
        	LOGGER.info( exc.getMessage() );        	
        }
    }

    /**
     * Handles updating a ${className}Entity.  if not key provided, calls create, otherwise calls save
     * @param		${className}Entity $lowercaseClassName
     */
	@RequestMapping("/update")
    public void update( @RequestBody ${className}Entity $lowercaseClassName ) {
	    try {                        	        
			// create the ${className}Business Delegate            
			${className}BusinessDelegate delegate = ${className}BusinessDelegate.get${className}Instance();
	        delegate.update${className}( ${lowercaseClassName} );
	        
	        if ( this.${lowercaseClassName} != null )
	            LOGGER.info( "successfully updated ${className}" );
	    }
	    catch( Throwable exc ) {
	    	LOGGER.info( "${className}Controller:update() - successfully update ${className} - " + exc.getMessage());        	
	    }
	}
 
    /**
     * Handles deleting a ${className} BO
     * @param		{className}Entity entity
     * @return		boolean
     */
    @RequestMapping("/delete")    
    public boolean delete( @RequestBody ${className}Entity $lowercaseClassName ) {                
        try {
        	${className}BusinessDelegate delegate = ${className}BusinessDelegate.get${className}Instance();

        	delegate.delete( $lowercaseClassName );
    		LOGGER.info( "Successfully deleted ${className} with key " + ${lowercaseClassName}.get${className}Id() );
        }
        catch( Throwable exc ) {
        	LOGGER.info( "${className}Controller:delete()" );
        	return false;        	
        }
        
        return true;
	}        
	
    /**
     * Handles loading a ${className}FetchOneSummary
     * @param		summary ${className}FetchOneSummary
     * @return		${className}FetchOneResponse
     */    
    @RequestMapping("/load")
    public ${className}Entity load( @RequestBody  ${className}FetchOneSummary summary ) {    	
    	${className}Entity entity = null;

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
     * @return		List<${className}>
     */
    @RequestMapping("/")
    public List<${className}Entity> loadAll() {                
        List<${className}Entity> ${lowercaseClassName}List = null;
        
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
     * @param		${childType}Entity entity
     * @param		${className} $lowercaseClassName
     */     
	@RequestMapping("/assign${roleName}")
	public void assign${roleName}( 	@RequestParam(value="${pkExpression}", required=true) UUID ${lowercaseClassName}Id, 
									@RequestBody ${childType}Entity entity ) {
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
     * @param		${childType}Entity entity
     */     
	@RequestMapping("/unAssign${roleName}")
	public void unAssign${roleName}( @RequestParam(value="${pkExpression}", required=false) UUID ${lowercaseClassName}Id ) {
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
     * @param		${childType}Entity entity 
     */     
	@RequestMapping("/addTo${roleName}")
	public void addTo${roleName}( 	@RequestParam(value="${pkExpression}", required=false) UUID ${lowercaseClassName}Id, 
									@RequestBody ${childType}Entity entity  ) {
		try {
			${className}BusinessDelegate.get${className}Instance().addTo${roleName}( ${lowercaseClassName}Id, entity );   
		}
		catch( Exception exc ) {
			LOGGER.info( "Failed to add to List $roleName" );
		}
	}

    /**
     * delete ${roleName} on ${className}
     * @param		UUID ${lowercaseClassName}Id
     * @param		${childType}Entity entity
     */     	
	@RequestMapping("/removeFrom${roleName}")
	public void removeFrom${roleName}( 	@RequestParam(value="${pkExpression}", required=true) UUID ${lowercaseClassName}Id, 
												@RequestBody ${childType}Entity entity )
	{		
		try {
			${className}BusinessDelegate.get${className}Instance().removeFrom${roleName}( ${lowercaseClassName}Id, entity );
		}
		catch( Exception exc ) {
			LOGGER.info( "Failed to remove from List ${roleName}" );
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
     * @return		List<${className}>
     */     
	@RequestMapping("/${method.getName()}")
	public ${returnType} ${method.getName()}( @RequestParam(value="arg", required=true) $argType $argName ) {
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
    protected ${className}Entity $lowercaseClassName = null;
    private static final Logger LOGGER = Logger.getLogger(${className}RestController.class.getName());
    
}
