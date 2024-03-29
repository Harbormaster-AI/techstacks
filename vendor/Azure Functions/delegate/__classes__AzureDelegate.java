#header()
#set( $className = $classObject.getName() )
package ${aib.getRootPackageName(true)}.#getDelegatePackageName();

import java.util.*;
import java.io.IOException;

//import java.util.logging.Level;
//import java.util.logging.Logger;

import com.microsoft.azure.functions.*;
import com.microsoft.azure.functions.annotation.*;

import io.swagger.annotations.*;
 	
#set( $imports = [ "#getPrimaryKeyPackageName()", "#getDAOPackageName()", "#getBOPackageName()"] )
#importStatements( $imports )

import ${aib.getRootPackageName()}.exception.CreationException;
import ${aib.getRootPackageName()}.exception.DeletionException;
import ${aib.getRootPackageName()}.exception.NotFoundException;
import ${aib.getRootPackageName()}.exception.SaveException;

/**3
 * ${className} Azure Proxy delegate class.
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
@Api(value = "${className}", description = "RESTful API to interact with ${className} resources.")
@Path("/${className}")
#set( $fullClassName = "${className}AzureDelegate" )
#if ( $classObject.isAbstract() == false )
public class $fullClassName 
#else
public abstract class $fullClassName 
#end##if ( $classObject.isAbstract() == false )
#if ( $classObject.hasParent() == true )
extends ${classObject.getParentName()}AWSLambdaDelegate
#else
extends BaseAWSLambdaDelegate
#end##if ( $classObject.hasParent() == true )
{
//************************************************************************
// Public Methods
//************************************************************************
    /** 
     * Default Constructor 
     */
    public ${fullClassName}() {
	}
    
#* 1/12/2022 - No Support for UberMethod Needed At This Time
#if( $aib.getParam("aws-lambda.crudDeclStrategy") == "methodsPerClass" )
#set( $exposeAPI = true )
#elseif( $aib.getParam("aws-lambda.crudDeclStrategy") == "uberMethodPerClass" )
    public static ${className} execute( 
		@ApiParam(value = "name of action", required = true) String actionName,
		@ApiParam(value = "input data to pass along as JSON", required = false) JsonObject jsonObjectData,
		@ApiParam(value = "parent primary key", required = false) String parentKey, 
		@ApiParam(value = "child primary key", required = false) String childKey,
		@ApiParam(value = "List of child primary keys", required = false) List<String> childKeys,
		Context context ) {
#outputCRUDCallsForClass( $classObject )
    }
}*#

#set( $exposeAPI = false )
#end##if( ${aib.getParam("aws-lambda.crudDeclStrategy") == "methodsPerClass" )
#outputAWSLambdaCRUDs( $className $exposeAPI )

    
//************************************************************************
// Attributes
//************************************************************************

//    private static final Logger LOGGER = Logger.getLogger(${fullClassName}.class.getName());
	private String processingMsg = "Success";
}

