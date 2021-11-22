#header()
package ${aib.getRootPackageName()}.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import com.mongodb.ConnectionString;
import com.mongodb.MongoClient;
import com.mongodb.MongoClientSettings;

import org.springframework.data.mongodb.config.AbstractMongoClientConfiguration;
import org.axonframework.eventsourcing.eventstore.EmbeddedEventStore;
import org.axonframework.eventsourcing.eventstore.EventStorageEngine;
import org.axonframework.eventsourcing.eventstore.EventStore;
import org.axonframework.extensions.mongo.DefaultMongoTemplate;
import org.axonframework.extensions.mongo.eventsourcing.eventstore.MongoEventStorageEngine;

@Configuration
public class MongoDBConfig extends AbstractMongoClientConfiguration {
		 
    @Override
    protected String getDatabaseName() {
        return "${aib.getParam( "mongodb.database-name"}";
    }
 
    @Override
    public MongoClient mongoClient() {
    	final String dbName 				= getDatabaseName();
    	final String host 					= "${aib.getParam( "mongod.host"}";
    	final String port 					= "${aib.getParam( "mongodb.port"}";
    	final String connectionStringFormat = "mongodb://%s:%s/%s";
    	final String appName 				= "$aib.getApplicationNameFormatted()";
        ConnectionString connectionString 	= new ConnectionString(String.format( connectionStringFormat, host, port, dbName));
        
        MongoClientSettings mongoClientSettings = MongoClientSettings.builder()
            .applyConnectionString(connectionString)
            .applyApplicationName( appName ).
            .build();
        
        return MongoClients.create(mongoClientSettings);
    }    
	    
	// The Event store `EmbeddedEventStore` delegates actual storage and retrieval of events to an `EventStorageEngine`.
	@Bean
	public EmbeddedEventStore eventStore(EventStorageEngine storageEngine, AxonConfiguration configuration) {
	    return EmbeddedEventStore.builder()
	            .storageEngine(storageEngine)
	            .messageMonitor(configuration.messageMonitor(EventStore.class, "eventStore"))
	            .build();
	}
	// The `MongoEventStorageEngine` stores each event in a separate MongoDB document
	@Bean
	public EventStorageEngine storageEngine(MongoClient client) {
	    return MongoEventStorageEngine.builder().mongoTemplate(DefaultMongoTemplate.builder().mongoDatabase(client).build()).build();
	}

    @Bean
    public MongoTemplate mongoTemplate() throws Exception {
        return new MongoTemplate(mongoClient(), getDatabaseName());
    }
}
