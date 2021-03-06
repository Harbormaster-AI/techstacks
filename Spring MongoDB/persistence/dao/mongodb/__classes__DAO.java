#header()
#set( $className = $classObject.getName() )
#set( $lowercaseClassName = $Utils.lowercaseFirstLetter( $className ) )package ${aib.getRootPackageName(true)}.#getDAOPackageName();

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.logging.Logger;

import org.mongodb.morphia.Datastore;

import org.bson.Document;
import com.mongodb.client.MongoCollection;

import ${aib.getRootPackageName(true)}.exception.*;

#set( $imports = [ "#getPrimaryKeyPackageName()", "#getBOPackageName()", "#getDAOPackageName()" ] )
#importStatements( $imports )

import ${aib.getRootPackageName()}.#getDAOPackageName().*;
/** 
 * Implements the MongoDB NoSQL persistence processing for business entity ${className}
 * using the Morphia Java Datastore.
 *
 * @author $aib.getAuthor()
 */
#set( $parentName = "" )        
#if( $classObject.hasParent() == true )    
#set( $parentName = "${parentName}${classObject.getParentName()}DAO" )   
##set( $parentName = "${parentName}BaseDAO" )
#else ## no parent, so come directly off of the FrameworkBusinessObject
#set( $parentName = "${parentName}BaseDAO" )		
#end
public class ${classObject.getName()}DAO extends $parentName
{
    /**
     * default constructor
     */
    public ${className}DAO()
    {
    }

##if ( $aib.hasIdentity( $classObject ) == true )
	
//*****************************************************
// CRUD methods
//*****************************************************

    /**
     * Retrieves a ${className} from the persistent store, using the provided primary key. 
     * If no match is found, a null ${className} is returned.
     * <p>
     * @param       pk
     * @return      ${className}
     * @exception   ProcessingException
     */
    public ${className} find${className}( ${className}PrimaryKey pk ) 
    throws ProcessingException
    {
    	${className} businessObject = null;
    	
        if (pk == null)
        {
            throw new ProcessingException("${className}DAO.find${className} cannot have a null primary key argument");
        }
    
		Datastore dataStore = getDatastore();
#set( $key = ${classObject.getFirstPrimaryKey()} )
        try
        {
        	businessObject = dataStore.createQuery( ${className}.class )
        	       						.field( "${key.getName()}" )
        	       						.equal( (Long)pk.getFirstKey() )
        	       						.get();
		}
		catch( Throwable exc )
		{
			exc.printStackTrace();
			throw new ProcessingException( "${className}DAO.find${className} failed for primary key " + pk + " - " + exc );		
		}		
		finally
		{
		}		    
		
        return( businessObject );
    }
    
    /**
     * Inserts a new ${classObject.Name} into the persistent store.
     * @param       businessObject
     * @return      newly persisted ${className}
     * @exception   ProcessingException
     */
    public ${className} create${className}( ${className} businessObject )
    throws ProcessingException
    {
    	Datastore dataStore = getDatastore();
    	
    	try
    	{
    		// let's assign out internal unique Id
    		Long id = getNextSequence( "${classObject.getFirstPrimaryKey().getName()}" ) ;
    		businessObject.set${className}Id( id );
    		
    		dataStore.save( businessObject );
    	    LOGGER.info( "---- ${className}DAO.create${className} created a bo is " + businessObject );
    	}
		catch( Throwable exc )
		{
			exc.printStackTrace();
			throw new ProcessingException( "${className}DAO.create${className} - " + exc.getMessage() );
		}		
		finally
		{
		}		
		
        // return the businessObject
        return(  businessObject );
    }
    
    /**
     * Stores the provided ${className} to the persistent store.
     *
     * @param       businessObject
     * @return      ${className}	stored entity
     * @exception   ProcessingException 
     */
    public ${className} save${className}( ${className} businessObject )
    throws ProcessingException
    {
    	Datastore dataStore = getDatastore();    	
    	
    	try
    	{
    		dataStore.save( businessObject );
		}
		catch( Throwable exc )
		{
			exc.printStackTrace();
			throw new ProcessingException( "${className}DAO:save${className} - " + exc.getMessage() );
		}		
		finally
		{
		}		    

    	return( businessObject );
    }
    
    /**
    * Removes a ${className} from the persistent store.
    *
    * @param        pk		identity of object to remove
    * @exception    ProcessingException
    */
    public boolean delete${className}( ${className}PrimaryKey pk ) 
    throws ProcessingException 
    {
    	Datastore dataStore = getDatastore();
    	
    	try
    	{
    		dataStore.delete( find$className( pk ) );
		}
		catch( Throwable exc )
		{
			LOGGER.severe("${className}DAO:delete() - failed " + exc.getMessage() );
			
			exc.printStackTrace();			
			throw new ProcessingException( "${className}DAO.delete${className} failed - " + exc );					
		}		
		finally
		{
		}
    	
    	return( true );
    }

    /**
     * returns a Collection of all ${className}s
     * @return		ArrayList<${className}>
     * @exception   ProcessingException
     */
    public ArrayList<${className}> findAll${className}()
    throws ProcessingException
    {
		final ArrayList<${className}> list 	= new ArrayList<${className}>();
		Datastore dataStore 				= getDatastore();
		
		try
		{
			list.addAll( dataStore.createQuery( ${className}.class ).asList() );
			
		}
		catch( Throwable exc )
		{
			exc.printStackTrace();
			LOGGER.warning( "${className}DAO.findAll() errors - " + exc.getMessage() );
			throw new ProcessingException( "${className}DAO.findAll${className} failed - " + exc );		
		}		
		finally
		{
		}
		
		if ( list.size() <= 0 )
		{
			LOGGER.info( "${className}DAO:findAll${className}s() - List is empty.");
		}
        
		return( list );		        
    }		
##end ##if ( $aib.hasIdentity( $classObject ) == true )		

// abstracts and overloads from BaseDAO

	@Override
	protected String getCollectionName()
	{
		return( "${aib.getRootPackageName(true)}.${classObject.getName()}" );
	}

	@Override
	protected MongoCollection<Document> createCounterCollection( MongoCollection<Document> collection )
	{
		if ( collection != null ) 
		{
		    Document document = new Document();
		    Long initialValue = new Long(0);
		    
		    document.append( MONGO_ID_FIELD_NAME, "${classObject.getFirstPrimaryKey().getName()}" );
		    document.append( MONGO_SEQ_FIELD_NAME, initialValue );
    	    collection.insertOne(document);
		}
		
		return( collection );
	}


//*****************************************************
// Attributes
//*****************************************************
	private static final Logger LOGGER = Logger.getLogger(${classObject.getName()}.class.getName());
}
