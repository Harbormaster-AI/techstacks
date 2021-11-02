#header()#set($className=$classObject.getName())
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
		public ${classObject.getName()}Test() {
			LOGGER.setUseParentHandlers(false);	// only want to output to the provided LogHandler
		}

		// test methods
		@Test
		/**
		 * Full Create-Read-Update-Delete of a ${classObject.getName()}, through a
		 * ${classObject.getName()}Test.
		 */
		public void testCRUD() throws Throwable {
			try {
				LOGGER.info("**********************************************************");
				LOGGER.info("Beginning full test on ${classObject.getName()}Test...");

				testCreate();
				testRead();
				testUpdate();
				testGetAll();
				testDelete();

				LOGGER.info("Successfully ran a full test on ${classObject.getName()}Test...");
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
		 * Tests creating a new ${classObject.getName()}.
		 */
		public void testCreate() throws Throwable {
			LOGGER.info( "-- Attempting to create a ${classObject.getName()}");

			StringBuilder msg = new StringBuilder( "-- Failed to create a ${classObject.getName()}" );

			try 
			{            
				${classObject.getName()}BusinessDelegate.get${classObject.getName()}Instance().create${classObject.getName()}( getNewBO() );
				LOGGER.info( "-- Successfully created a ${classObject.getName()} with primary key" + thePrimaryKey );
			}
			catch (Exception e) 
			{
				LOGGER.warning( unexpectedErrorMsg );
				LOGGER.warning( msg.toString() + " : " + e );

				throw e;
			}
		}

		/** 
		 * Tests reading a ${classObject.getName()}. 
		 */
		public ${classObject.getName()}Entity testRead() throws Throwable {
			LOGGER.info( "-- Reading a previously created ${classObject.getName()}" );

			${classObject.getName()}Entity entity = null;
			StringBuilder msg = new StringBuilder( "-- Failed to read ${classObject.getName()} with primary key" );
			msg.append( thePrimaryKey );
			
			${className}FetchOneSummary fetchOneSummary = new ${className}FetchOneSummary( thePrimaryKey.toString() );

			try {
				entity = ${classObject.getName()}BusinessDelegate.get${classObject.getName()}Instance().get${classObject.getName()}( fetchOneSummary );

				assertNotNull( entity,msg.toString() );

				LOGGER.info( "-- Successfully found ${classObject.getName()} " + entity.toString() );
			}
			catch ( Throwable e ) {
				LOGGER.warning( unexpectedErrorMsg );
				LOGGER.warning( msg.toString() + " : " + e );

				throw e;
			}
			
			return entity;
		}

		/** 
		 * Tests updating a ${classObject.getName()}.
		 */
		public void testUpdate() throws Throwable {
			LOGGER.info( "-- Attempting to update a ${classObject.getName()}." );

			StringBuilder msg = new StringBuilder( "Failed to update a ${classObject.getName()} : " );        
			${classObject.getName()}Entity entity = null;

			try {            
				entity = testRead();

				assertNotNull( entity, msg.toString() );

				LOGGER.info( "-- Now updating the created ${classObject.getName()}." );

				// for use later on...
				thePrimaryKey = entity.get${className}Id();

				${classObject.getName()}BusinessDelegate proxy = ${classObject.getName()}BusinessDelegate.get${classObject.getName()}Instance();            
				proxy.update${classObject.getName()}( entity );   

				LOGGER.info( "-- Successfully saved ${classObject.getName()} - " + entity.toString() );
			}
			catch ( Throwable e ) {
				LOGGER.warning( unexpectedErrorMsg );
				LOGGER.warning( msg.toString() + " : primarykey-" + thePrimaryKey + " : entity-" +  entity + " : " + e );

				throw e;
			}
		}

		/** 
		 * Tests deleting a ${classObject.getName()}.
		 */
		public void testDelete() throws Throwable {
			LOGGER.info( "-- Deleting a previously created ${classObject.getName()}." );

			try{
			    ${className}Entity entity = testRead(); 
				${classObject.getName()}BusinessDelegate.get${classObject.getName()}Instance().delete( entity );

				LOGGER.info( "-- Successfully deleted ${classObject.getName()} with primary key " + thePrimaryKey );            
			}
			catch ( Throwable e ) {
				LOGGER.warning( unexpectedErrorMsg );
				LOGGER.warning( "-- Failed to delete ${classObject.getName()} with primary key " + thePrimaryKey );

				throw e;
			}
		}

		/**
		 * Tests getting all ${classObject.getName()}s.
		 */
		public void testGetAll() throws Throwable {    
			LOGGER.info( "-- Retrieving Collection of ${classObject.getName()}s:" );

			StringBuilder msg = new StringBuilder( "-- Failed to get all ${classObject.getName()} : " );        
			List<${classObject.getName()}Entity> collection  = null;

			try {
				// call the static get method on the ${classObject.getName()}BusinessDelegate
				collection = ${classObject.getName()}BusinessDelegate.get${classObject.getName()}Instance().getAll${classObject.getName()}();

				if ( collection == null || collection.size() == 0 ) {
					LOGGER.warning( unexpectedErrorMsg );
					LOGGER.warning( "-- " + msg.toString() + " Empty collection returned."  );
				}
				else {
					// Now print out the values
					${classObject.getName()}Entity entity = null;            
					Iterator<${classObject.getName()}Entity> iter = collection.iterator();

					while( iter.hasNext() ) {
						// Retrieve the entity   
						entity = iter.next();

						assertNotNull( entity,"-- null entity in Collection." );
						assertNotNull( entity.get${className}Id(), "-- entit in Collection has a null primary key" );        

						LOGGER.info( " - " + entity.toString() );
					}
				}
			}
			catch ( Throwable e ) {
				LOGGER.warning( unexpectedErrorMsg );
				LOGGER.warning( msg.toString() + " : " + e );

				throw e;
			}
		}

		public ${classObject.getName()}Test setHandler(Handler handler) {
			this.handler = handler;
			LOGGER.addHandler(handler); // assign so the LOGGER can only output results to the Handler
			return this;
		}

		/**
		 * Returns a new populated ${classObject.getName()}
		 * 
		 * @return ${classObject.getName()}
		 */
		protected ${classObject.getName()}Entity getNewBO() {
			${classObject.getName()}Entity entity = null;
			
			return( entity );
		}

		// attributes 

		protected UUID thePrimaryKey = null;
		protected Properties frameworkProperties = null;
		private final Logger LOGGER 						= Logger.getLogger(${classObject.getName()}Test.class.getName());
		private Handler handler = null;
		private String unexpectedErrorMsg = ":::::::::::::: Unexpected Error :::::::::::::::::";
	}
