#header()
#set( $className = $classObject.getName() )
package ${aib.getRootPackageName(true)}.#getTestPackageName();

import java.io.*;
import java.util.*;
import java.util.logging.*;

import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.assertNotNull;

#set( $imports = [ "#getDelegatePackageName()", "entity", "api" ] )
#importStatements( $imports )

/**
 * Test ${classObject.getName()} class.
 *
 * @author    $aib.getAuthor()
 */
public class ${classObject.getName()}Test
{

// constructors

    public ${classObject.getName()}Test()
    {
    	LOGGER.setUseParentHandlers(false);	// only want to output to the provided LogHandler
    }

// test methods
    @Test
    /** 
     * Full Create-Read-Update-Delete of a ${classObject.getName()}, through a ${classObject.getName()}Test.
     */
    public void testCRUD() throws Throwable {        
        try {
        	LOGGER.info( "**********************************************************" );
            LOGGER.info( "Beginning full test on ${classObject.getName()}Test..." );
            
            testCreate();            
            testRead();        
            testUpdate();
            testGetAll();                
            testDelete();
            
            LOGGER.info( "Successfully ran a full test on ${classObject.getName()}Test..." );
            LOGGER.info( "**********************************************************" );
            LOGGER.info( "" );
        }
        catch( Throwable e ) {
            throw e;
        }
        finally  {
        	if ( handler != null ) {
        		handler.flush();
        		LOGGER.removeHandler(handler);
        	}
        }
   }

    /** 
     * Tests creating a new ${classObject.getName()}.
     *
     * @return    ${classObject.getName()}
     */
    public ${classObject.getName()} testCreate() throws Throwable {
        ${classObject.getName()} entity = null;

        LOGGER.info( "${classObject.getName()}Test:testCreate()" );
        LOGGER.info( "-- Attempting to create a ${classObject.getName()}");

        StringBuilder msg = new StringBuilder( "-- Failed to create a ${classObject.getName()}" );

        try {            
            entity = ${classObject.getName()}BusinessDelegate.get${classObject.getName()}Instance().create${classObject.getName()}( generateNewCommand() );
            assertNotNull( entity, msg.toString() );

            theId = entity.get${className}Id();
            assertNotNull( theId, msg.toString() + " Contains a null primary key" );

            LOGGER.info( "-- Successfully created a ${classObject.getName()} with primary key" + theId );
        }
        catch (Exception e)  {
            LOGGER.warning( unexpectedErrorMsg );
            LOGGER.warning( msg.toString() + entity );
            
            throw e;
        }
        return entity;
    }

    /** 
     * Tests reading a ${classObject.getName()}.
     *
     * @return    ${classObject.getName()}  
     */
    public ${classObject.getName()} testRead() throws Throwable {
        LOGGER.info( "${classObject.getName()}Test:testRead()" );
        LOGGER.info( "-- Reading a previously created ${classObject.getName()}" );

        ${classObject.getName()} entity = null;
        StringBuilder msg = new StringBuilder( "-- Failed to read ${classObject.getName()} with primary key" );
        msg.append( theId );

        try {
            entity = ${classObject.getName()}BusinessDelegate.get${classObject.getName()}Instance().get${classObject.getName()}( new ${className}FetchOneSummary(theId) );
            
            assertNotNull( entity,msg.toString() );

            // for use later
            theId = entity.get${className}Id();
            
            LOGGER.info( "-- Successfully found ${classObject.getName()} " + entity.toString() );
        }
        catch ( Throwable e ) {
            LOGGER.warning( unexpectedErrorMsg );
            LOGGER.warning( msg.toString() + " : " + e );
            
            throw e;
        }

        return( entity );
    }

    /** 
     * Tests updating a ${classObject.getName()}.
     *
     * @return    ${classObject.getName()}
     */
    public ${classObject.getName()} testUpdate() throws Throwable {
        LOGGER.info( "${classObject.getName()}Test:testUpdate()" );
        LOGGER.info( "-- Attempting to update a ${classObject.getName()}." );

        StringBuilder msg = new StringBuilder( "Failed to update a ${classObject.getName()} : " );        
        ${classObject.getName()} entity = null;
    
        try {            
        	${classObject.getUpdateCommandAlias()} updateCommand = this.generateUpdateCommand();
        	
        	// apply the current id as to update the fields of the current entity
        	updateCommand.set${className}Id( theId );
            
            assertNotNull( updateCommand, msg.toString() );

            LOGGER.info( "-- Now updating the created ${classObject.getName()}." );
            
            ${classObject.getName()}BusinessDelegate proxy = ${classObject.getName()}BusinessDelegate.get${classObject.getName()}Instance();            
            entity = proxy.update${classObject.getName()}( updateCommand );   
            
            assertNotNull( entity, msg.toString()  );

            LOGGER.info( "-- Successfully saved ${classObject.getName()} - " + entity.toString() );
        }
        catch ( Throwable e ) {
            LOGGER.warning( unexpectedErrorMsg );
            LOGGER.warning( msg.toString() + " : primarykey-" + theId + " : entity-" +  entity + " : " + e );
            
            throw e;
        }

        return( entity );
    }

    /** 
     * Tests deleting a ${classObject.getName()}.
     */
    public void testDelete() throws Throwable {
        LOGGER.info( "${classObject.getName()}Test:testDelete()" );
        LOGGER.info( "-- Deleting a previously created ${classObject.getName()}." );
        
        try {
        	${classObject.getDeleteCommandAlias()} deleteCommand = new ${classObject.getDeleteCommandAlias()}( theId );
        	
            ${classObject.getName()}BusinessDelegate.get${classObject.getName()}Instance().delete( deleteCommand );
            
            LOGGER.info( "-- Successfully deleted ${classObject.getName()} with primary key " + theId );            
        }
        catch ( Throwable e ) {
            LOGGER.warning( unexpectedErrorMsg );
            LOGGER.warning( "-- Failed to delete ${classObject.getName()} with primary key " + theId );
            
            throw e;
        }
    }

    /** 
     * Tests getting all ${classObject.getName()}s.
     *
     * @return    Collection
     */
    public List<${classObject.getName()}> testGetAll() throws Throwable {    
        LOGGER.info( "${classObject.getName()}Test:testGetAll() - Retrieving Collection of ${classObject.getName()}s:" );

        StringBuilder msg = new StringBuilder( "-- Failed to get all ${classObject.getName()} : " );        
        List<${classObject.getName()}> collection  = null;

        try {
            // call the static get method on the ${classObject.getName()}BusinessDelegate
            collection = ${classObject.getName()}BusinessDelegate.get${classObject.getName()}Instance().getAll${classObject.getName()}();

            if ( collection == null || collection.size() == 0 ) {
                LOGGER.warning( unexpectedErrorMsg );
                LOGGER.warning( "-- " + msg.toString() + " Empty collection returned."  );
            }
            else {
	            // Now print out the values
	            ${classObject.getName()} currEntity  = null;            
	            Iterator<${classObject.getName()}> iter = collection.iterator();
					
	            while( iter.hasNext() ) {
	                // Retrieve the entity   
	                currEntity = iter.next();
	                
	                assertNotNull( currEntity,"-- null value object in Collection." );
	                assertNotNull( currEntity.get${className}Id(), "-- value object in Collection has a null primary key" );        
	
	                LOGGER.info( " - " + currEntity.toString() );
	            }
            }
        }
        catch ( Throwable e ){
            LOGGER.warning( unexpectedErrorMsg );
            LOGGER.warning( msg.toString() );
            
            throw e;
        }

        return( collection );
    }
    
    public ${classObject.getName()}Test setHandler( Handler handler ) {
    	this.handler = handler;
    	LOGGER.addHandler(handler);	// assign so the LOGGER can only output results to the Handler
    	return this;
    }
    

	/**
	 * Returns a new populated ${className}
	 * 
	 * @return ${classObject.getCreateCommandAlias()} alias
	 */
	protected ${classObject.getCreateCommandAlias()} generateNewCommand() {
#set( $includeAssociations = false )
#set( $args = "#determineDefaultArgs()" )	
        ${classObject.getCreateCommandAlias()} command = new ${classObject.getCreateCommandAlias()}( $args );
		
		return( command );
	}

		/**
		 * Returns a new populated ${className}
		 * 
		 * @return ${classObject.getUpdateCommandAlias()} alias
		 */
	protected ${classObject.getUpdateCommandAlias()} generateUpdateCommand() {
#set( $includeAssociations = true )
#set( $args = "#determineDefaultArgs()" )	
	        ${classObject.getUpdateCommandAlias()} command = new ${classObject.getUpdateCommandAlias()}( $args );
			
			return( command );
		}
	//----
    
// attributes 

    protected UUID theId  = null;
	private final Logger LOGGER = Logger.getLogger(${classObject.getName()}.class.getName());
	private Handler handler = null;
	private String unexpectedErrorMsg = ":::::::::::::: Unexpected Error :::::::::::::::::";
}
