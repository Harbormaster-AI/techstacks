#header()
#set($className=$classObject.getName())
package ${aib.getRootPackageName(true)}.#getTestPackageName();

import java.io.*;
import java.util.*;
import java.util.logging.*;

import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.assertNotNull;

#set($imports=["#getDelegatePackageName()","entity", "api"])
#importStatements($imports)

/**
 * Test ${classObject.getName()} class.
 *
 * @author $aib.getAuthor()
 */
public class ${classObject.getName()}Test{

        // ------------------------------------
		// default constructor
	    // ------------------------------------
		public ${className}Test() {
			LOGGER.setUseParentHandlers(false);	// only want to output to the provided LogHandler
		}

		// test methods
		@Test
		/**
		 * Full Create-Read-Update-Delete of a ${className}, through a
		 * ${className}Test.
		 */
		public void testCRUD() throws Throwable {
			try {
				LOGGER.info("**********************************************************");
				LOGGER.info("Beginning full CRUD test on ${className}...");

				testCreate();
//				testRead();
//				testUpdate();
				testGetAll();
//				testDelete();

				LOGGER.info("Successfully ran a full test on ${className}Test...");
				LOGGER.info("**********************************************************");
				LOGGER.info("");
			} catch (Throwable e) {
				throw e;
			} finally {
				if (handler != null) {
					handler.flush();
					LOGGER.removeHandler(handler);
				}
			}
		}

		/** 
		 * Tests creating a new ${className}.
		 */
		public void testCreate() throws Throwable {
			LOGGER.info( "\n======== CREATE ======== ");
			LOGGER.info( "-- Attempting to create a ${className}");

			StringBuilder msg = new StringBuilder( "-- Failed to create a ${className}" );

			try {            
				${className} entity = ${className}BusinessDelegate.get${className}Instance().create${className}( generateNewEntity() );
				thePrimaryKey = entity.get${className}Id();
				LOGGER.info( "-- Successfully created a ${className} with primary key" + thePrimaryKey );
			}
			catch (Exception e) {
				LOGGER.warning( unexpectedErrorMsg );
				LOGGER.warning( msg.toString() + " : " + e );

				throw e;
			}
		}

		/** 
		 * Tests reading a ${className}. 
		 */
		public ${className} testRead() throws Throwable {
			LOGGER.info( "\n======== READ ======== ");
			LOGGER.info( "-- Reading a previously created ${className}" );

			${className} entity = null;
			StringBuilder msg = new StringBuilder( "-- Failed to read ${className} with primary key" );
			msg.append( thePrimaryKey );
			
			${className}FetchOneSummary fetchOneSummary = new ${className}FetchOneSummary( thePrimaryKey );

			try {
				entity = ${className}BusinessDelegate.get${className}Instance().get${className}( fetchOneSummary );

				assertNotNull( entity,msg.toString() );

				LOGGER.info( "-- Successfully found ${className} " + entity.toString() );
			}
			catch ( Throwable e ) {
				LOGGER.warning( unexpectedErrorMsg );
				LOGGER.warning( msg.toString() + " : " + e );

				throw e;
			}
			
			return entity;
		}

		/** 
		 * Tests updating a ${className}.
		 */
		public void testUpdate() throws Throwable {
			LOGGER.info( "\n======== UPDATE ======== ");
			LOGGER.info( "-- Attempting to update a ${className}." );

			StringBuilder msg = new StringBuilder( "Failed to update a ${className} : " );        
			${className} entity = null;

			try {            
				entity = testRead();

				assertNotNull( entity, msg.toString() );

				LOGGER.info( "-- Now updating the created ${className}." );

				// for use later on...
				thePrimaryKey = entity.get${className}Id();

				${className}BusinessDelegate proxy = ${className}BusinessDelegate.get${className}Instance();            
				proxy.update${className}( entity );   

				LOGGER.info( "-- Successfully saved ${className} - " + entity.toString() );
			}
			catch ( Throwable e ) {
				LOGGER.warning( unexpectedErrorMsg );
				LOGGER.warning( msg.toString() + " : primarykey = " + thePrimaryKey + " : entity-" +  entity + " : " + e );

				throw e;
			}
		}

		/** 
		 * Tests deleting a ${className}.
		 */
		public void testDelete() throws Throwable {
			LOGGER.info( "\n======== DELETE ======== ");
			LOGGER.info( "-- Deleting a previously created ${className}." );

			try{
			    ${className} entity = testRead(); 
				${className}BusinessDelegate.get${className}Instance().delete( entity );

				LOGGER.info( "-- Successfully deleted ${className} with primary key " + thePrimaryKey );            
			}
			catch ( Throwable e ) {
				LOGGER.warning( unexpectedErrorMsg );
				LOGGER.warning( "-- Failed to delete ${className} with primary key " + thePrimaryKey );

				throw e;
			}
		}

		/**
		 * Tests getting all ${className}s.
		 */
		public void testGetAll() throws Throwable {    
			LOGGER.info( "======== GETALL ======== ");
			LOGGER.info( "-- Retrieving Collection of ${className}s:" );

			StringBuilder msg = new StringBuilder( "-- Failed to get all ${className} : " );        
			List<${className}> collection  = null;

			try {
				// call the static get method on the ${className}BusinessDelegate
				collection = ${className}BusinessDelegate.get${className}Instance().getAll${className}();

				if ( collection == null || collection.size() == 0 ) {
					LOGGER.warning( unexpectedErrorMsg );
					LOGGER.warning( msg.toString() + " Empty collection returned."  );
				}
				else {
					// Now print out the values
					${className} entity = null;            
					Iterator<${className}> iter = collection.iterator();
					int index = 1;

					while( iter.hasNext() ) {
						// Retrieve the entity   
						entity = iter.next();

						assertNotNull( entity,"-- null entity in Collection." );
						assertNotNull( entity.get${className}Id(), "-- entity in Collection has a null primary key" );        

						LOGGER.info( " - " + String.valueOf(index) + ". " + entity.toString() );
						index++;
					}
				}
			}
			catch ( Throwable e ) {
				LOGGER.warning( unexpectedErrorMsg );
				LOGGER.warning( msg.toString() + " : " + e );

				throw e;
			}
		}

		/**
		 * Assigns a common log handler for each test class in the suite
		 * @param		handler	Handler
		 * @return		${className}Test
		 */
		public ${className}Test setHandler(Handler handler) {
			this.handler = handler;
			LOGGER.addHandler(handler); // assign so the LOGGER can only output results to the Handler
			return this;
		}

		/**
		 * Returns a new populated ${className}
		 * 
		 * @return ${className}
		 */
		protected ${className} generateNewEntity() {
#set( $args = "#determineDefaultArgs()" )	
			${className} entity = new ${className}( $args );
			
			return( entity );
		}

		//-----------------------------------------------------
		// attributes 
		//-----------------------------------------------------
		protected UUID thePrimaryKey 						= null;
		private final Logger LOGGER 						= Logger.getLogger(${className}Test.class.getName());
		private Handler handler 							= null;
		private String unexpectedErrorMsg 					= ":::::::::::::: Unexpected Error :::::::::::::::::";
	}
