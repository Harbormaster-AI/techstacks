#header()
package ${aib.getRootPackageName(true)}.#getDelegatePackageName();

import java.util.*;
import java.io.IOException;

//import java.util.logging.Level;
//import java.util.logging.Logger;

import javax.ws.rs.core.MediaType;
import javax.ws.rs.*;

import io.swagger.annotations.*;

import com.amazonaws.services.lambda.runtime.Context;

import com.google.gson.Gson;
import com.google.gson.JsonObject;

#set( $imports = [ "#getPrimaryKeyPackageName()", "#getDAOPackageName()", "#getBOPackageName()"] )
#importStatements( $imports )

import ${aib.getRootPackageName()}.exception.CreationException;
import ${aib.getRootPackageName()}.exception.DeletionException;
import ${aib.getRootPackageName()}.exception.NotFoundException;
import ${aib.getRootPackageName()}.exception.SaveException;

/**
 * Uber AWS Lambda Proxy delegate class.
 * <p>
 * This class implements the Business Delegate design pattern for the purpose of providing a single entry point
 * into all the CRUD functions
 * <p>
 * @author ${aib.getAuthor()}
 */
@Api(value = "UberAWSLambdaDelegate", description = "Uber RESTful API to interact with all class resources.")
@Path("/UberAWSLambdaDelegate")
public abstract class UberAWSLambdaDelegate
extends BaseAWSLambdaDelegate
{
//************************************************************************
// Public Methods
//************************************************************************
    /** 
     * Default Constructor 
     */
    public UberAWSLambdaDelegate() {
	}

    //************************************************************************
    // Single Uber method to delegate to all other class specific CRUD implementations
    //************************************************************************
    @ApiOperation(value = "Creates a ${className}", notes = "Creates ${className} using the provided data" )
    @POST
    @Path("/execute")
    @Consumes(MediaType.APPLICATION_JSON)
    public static ${className} execute( 
		@ApiParam(value = "business entity name", required = true) String entityName, 
		@ApiParam(value = "name of action", required = true) String actionName,
		@ApiParam(value = "input data to pass along as JSON", required = false) JsonObject jsonObjectData,
		@ApiParam(value = "parent primary key", required = false) String parentKey, 
		@ApiParam(value = "child primary key", required = false) String childKey,
		@ApiParam(value = "List of child primary keys", required = false) List<String> childKeys,
		Context context ) {
#foreach( $class in $aib.getClassesWithIdentity() )
#outputCRUDCallsForClass( $class )
#end##foreach( $class in $aib.getClassesWithIdentity() )
    }

#set( $exposeAPI = false )
#foreach( $class in $aib.getClassesWithIdentity() )
#outputAWSLambdaCRUDs( $className $exposeAPI )
#end##foreach( $class in $aib.getClassesWithIdentity() )
    
//************************************************************************
// Attributes
//************************************************************************

//    private static final Logger LOGGER = Logger.getLogger(${fullClassName}.class.getName());
}

