#header()
#set($className=$classObject.getName())
#set( $lowercaseClassName = ${Utils.lowercaseFirstLetter( ${className} )} )
package ${aib.getRootPackageName(true)}.#getTestPackageName();

import java.io.*;
import java.util.*;
import java.util.logging.*;

import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.springframework.util.Assert.state;

#set($imports=["#getDelegatePackageName()","entity", "api", "subscriber"])
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
		subscriber = new ${className}Subscriber();
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
			// jumpstart process
			// ---------------------------------------------
			jumpStart();
			
		} catch (Throwable e) {
			throw e;
		} finally {
		}
	}

	/** 
	 * jumpstart the process by instantiating2 ${className}
	 */
	protected void jumpStart() throws Throwable {
		LOGGER.info( "\n======== create instances to get the ball rolling  ======== ");

		${lowercaseClassName}Id = ${className}BusinessDelegate.get${className}Instance()
		.create${className}( generateNewCommand() )
		.get();

		// ---------------------------------------------
		// set up query subscriptions after the 1st create
		// ---------------------------------------------
		setUpQuerySubscriptions();

		${className}BusinessDelegate.get${className}Instance()
					.create${className}( generateNewCommand() );

	}

	/** 
	 * Set up query subscriptions
	 */
	protected void setUpQuerySubscriptions() throws Throwable {
		LOGGER.info( "\n======== Setting Up Query Subscriptions ======== ");
			
		try {            
			subscriber.${lowercaseClassName}Subscribe().subscribe(
					  successValue -> { 
						  LOGGER.info(successValue.toString());
						  try {
							  ${lowercaseClassName}Id = successValue.get${className}Id();
							  ${className} entity = read();
							  state( entity.get${className}Id() == successValue.get${className}Id(), "Subscribed value and associated read value are not equal");
						  } catch( Throwable exc ) {
							  LOGGER.warning( exc.getMessage() );
						  }
					  	},
					  error -> LOGGER.warning(error.getMessage()),
					  () -> LOGGER.info("Subscription on ${lowercaseClassName} consumed")
					);
			subscriber.${lowercaseClassName}Subscribe( ${lowercaseClassName}Id ).subscribe(
					  successValue -> { 
						  LOGGER.info(successValue.toString());
						  try {
							  List<${className}> all = getAll();
							  int size = all.size();
							  ${lowercaseClassName}Id = all.get(size-1).get${className}Id();
							  delete();
							  state( getAll().size() == size - 1 , "value not deleted from list");
						  } catch( Throwable exc ) {
							  LOGGER.warning( exc.getMessage() );
						  }
					  	},
					  error -> LOGGER.warning(error.getMessage()),
					  () -> LOGGER.info("Subscription on ${lowercaseClassName} for ${lowercaseClassName}Id consumed")
						);
#set( $includeComposites = false )
#foreach( $singleAssociation in $classObject.getSingleAssociations( ${includeComposites} ) )
#set( $roleName = $singleAssociation.getRoleName() )
				subscriber.${roleName}Subscribe(${lowercaseClassName}Id).subscribe(
						  successValue -> LOGGER.info(successValue.toString()),
						  error -> LOGGER.warning(error.getMessage()),
						  () -> LOGGER.info("Subscription on ${className}.${roleName} for consumed")
						);
#end##foreach( $singleAssociation in $classObject.getSingleAssociations( ${includeComposites} ) )
#foreach( $multiAssociation in $class.getMultipleAssociations() )
#set( $roleName = $multiAssociation.getRoleName() )
				subscriber.${roleName}Subscribe(${lowercaseClassName}Id).subscribe(
						  successValue -> LOGGER.info(successValue.toString()),
						  error -> LOGGER.warning(error.getMessage()),
						  () -> LOGGER.info("Subscription on ${className}.${roleName} for ${lowercaseClassName}Id consumed")
						);
#end##foreach( $multiAssociation in $class.getMultipleAssociations() )				
			}
			catch (Exception e) {
				LOGGER.warning( e.getMessage() );
				throw e;
			}
		}
		
		/** 
	 * read a ${className}. 
	 */
	protected ${className} read() throws Throwable {
		LOGGER.info( "\n======== READ ======== ");
		LOGGER.info( "-- Reading a previously created ${className}" );

		${className} entity = null;
		StringBuilder msg = new StringBuilder( "-- Failed to read ${className} with primary key" );
		msg.append( ${lowercaseClassName}Id );
		
		${className}FetchOneSummary fetchOneSummary = new ${className}FetchOneSummary( ${lowercaseClassName}Id );

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
	protected void update() throws Throwable {
		LOGGER.info( "\n======== UPDATE ======== ");
		LOGGER.info( "-- Attempting to update a ${className}." );

		StringBuilder msg = new StringBuilder( "Failed to update a ${className} : " );        
		${className} entity = read();
#set( $includeAssociations = true )
#set( $varName = "entity" )
#set( $args = "#determineArgsAsInput( $classObject, $varName, $includeAssociations )" ) 		
		${classObject.getUpdateCommandAlias()} command = new ${classObject.getUpdateCommandAlias()}(${args});

		try {            
			assertNotNull( entity, msg.toString() );

			LOGGER.info( "-- Now updating the created ${className}." );

			// for use later on...
			${lowercaseClassName}Id = entity.get${className}Id();

			${className}BusinessDelegate proxy = ${className}BusinessDelegate.get${className}Instance();  

			proxy.update${className}( command );   

			LOGGER.info( "-- Successfully saved ${className} - " + entity.toString() );
		}
		catch ( Throwable e ) {
			LOGGER.warning( unexpectedErrorMsg );
			LOGGER.warning( msg.toString() + " : primarykey = " + ${lowercaseClassName}Id + " : command -" +  command + " : " + e );

			throw e;
		}
	}

	/** 
	 * delete a ${className}.
	 */
	protected void delete() throws Throwable {
		LOGGER.info( "\n======== DELETE ======== ");
		LOGGER.info( "-- Deleting a previously created ${className}." );

		${className} entity = null;
		
		try{
		    entity = read(); 
			LOGGER.info( "-- Successfully read ${className} with id " + ${lowercaseClassName}Id );            
		}
		catch ( Throwable e ) {
			LOGGER.warning( unexpectedErrorMsg );
			LOGGER.warning( "-- Failed to read ${className} with id " + ${lowercaseClassName}Id );

			throw e;
		}

		try{
			${className}BusinessDelegate.get${className}Instance().delete( new ${classObject.getDeleteCommandAlias()}( entity.get${className}Id() ) );
			LOGGER.info( "-- Successfully deleted ${className} with id " + ${lowercaseClassName}Id );            
		}
		catch ( Throwable e ) {
			LOGGER.warning( unexpectedErrorMsg );
			LOGGER.warning( "-- Failed to delete ${className} with id " + ${lowercaseClassName}Id );

			throw e;
		}
	}

	/**
	 * get all ${className}s.
	 */
	protected List<${className}> getAll() throws Throwable {    
		LOGGER.info( "======== GETALL ======== ");
		LOGGER.info( "-- Retrieving Collection of ${className}s:" );

		StringBuilder msg = new StringBuilder( "-- Failed to get all ${className} : " );        
		List<${className}> collection  = new ArrayList<>();

		try {
			// call the static get method on the ${className}BusinessDelegate
			collection = ${className}BusinessDelegate.get${className}Instance().getAll${className}();
			assertNotNull( collection, "An Empty collection of ${className} was incorrectly returned.");
			
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
		catch ( Throwable e ) {
			LOGGER.warning( unexpectedErrorMsg );
			LOGGER.warning( msg.toString() + " : " + e );

			throw e;
		}
		
		return collection;			
	}

	/**
	 * Assigns a common log handler for each test class in the suite 
	 * in the event log output needs to go elsewhere
	 * 
	 * @param		handler	Handler
	 * @return		${className}Test
	 */
	protected ${className}Test setHandler(Handler handler) {
		if ( handler != null )
			LOGGER.addHandler(handler); 
		return this;
	}

	/**
	 * Returns a new populated ${className}
	 * 
	 * @return ${classObject.getCreateCommandAlias()} alias
	 */
		protected ${classObject.getCreateCommandAlias()} generateNewCommand() {
#set( $args = "#determineDefaultArgs()" )	
        ${classObject.getCreateCommandAlias()} command = new ${classObject.getCreateCommandAlias()}( $args );
		
		return( command );
	}

	//-----------------------------------------------------
	// attributes 
	//-----------------------------------------------------
	protected UUID ${lowercaseClassName}Id 				= null;
	protected ${className}Subscriber subscriber		= null;
	private String unexpectedErrorMsg 					= ":::::::::::::: Unexpected Error :::::::::::::::::";
	private final Logger LOGGER 						= Logger.getLogger(${className}Test.class.getName());
}
