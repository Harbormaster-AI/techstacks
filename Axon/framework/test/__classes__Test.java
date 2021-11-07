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
		}

		// test methods
		@Test
		/*
		 * Initiate ${className}Test.
		 */
		public void startTest() throws Throwable {
			try {
				LOGGER.info("**********************************************************");
				LOGGER.info("Beginning test on ${className}...");
				LOGGER.info("**********************************************************");
				
				// ---------------------------------------------
				// set up query subscriptions
				// ---------------------------------------------
				setUpQuerySubscriptions();

				// ---------------------------------------------
				// jumpstart process
				// ---------------------------------------------
				jumpStart();
				
			} catch (Throwable e) {
				throw e;
			} finally {
			}
		}

		/** 
		 * Set up query subscriptions for creation, update, and delete of ${className}
		 */
		public void setupSubscriptions() throws Throwable {
			LOGGER.info( "\n======== Setting Up Query Subscriptions ======== ");

			try {            
				LOGGER.info( "-- Successfully created a ${className} with primary key" + thePrimaryKey );
			}
			catch (Exception e) {
				LOGGER.warning( e );
				throw e;
			}
		}

		/** 
		 * Set up query subscriptions for creation, update, and delete of ${className}
		 */
		public void jumpStart() throws Throwable {
			LOGGER.info( "\n======== Setting Up Query Subscriptions ======== ");
			${className} entity = ${className}BusinessDelegate.get${className}Instance().create${className}( generateNewEntity() );
			thePrimaryKey = entity.get${className}Id();
		}

		
		/** 
		 * read a ${className}. 
		 */
		public ${className} read() throws Throwable {
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
		 * updating a ${className}.
		 */
		public void update() throws Throwable {
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
		 * delete a ${className}.
		 */
		public void delete() throws Throwable {
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
		 * get all ${className}s.
		 */
		public void getAll() throws Throwable {    
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
		 * in the event log output needs to go elsewhere
		 * 
		 * @param		handler	Handler
		 * @return		${className}Test
		 */
		public ${className}Test setHandler(Handler handler) {
			if ( handler != null )
				LOGGER.addHandler(handler); 
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
	}
