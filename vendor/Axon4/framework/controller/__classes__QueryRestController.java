#header()
#set( $className = ${classObject.getName()} )
#set( $lowercaseClassName = ${Utils.lowercaseFirstLetter( ${className} )} )
#set( $pk = "${className}Id" )		
##set( $pkExpression = "${className}Id" ) 
##set( $pkExpression = "${lowercaseClassName}.${pk.getName()}" ) 
package ${aib.getRootPackageName(true)}.#getRestControllerPackageName().query;

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
 * Implements Spring Controller query CQRS processing for entity ${className}.
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
public class ${className}QueryRestController extends $parentController {

	
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
    private static final Logger LOGGER = Logger.getLogger(${className}QueryRestController.class.getName());
    
}
