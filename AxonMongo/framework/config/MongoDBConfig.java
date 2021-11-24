#header()
package ${aib.getRootPackageName()}.config;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.mongodb.config.AbstractMongoClientConfiguration;

import org.axonframework.eventsourcing.eventstore.EmbeddedEventStore;
import org.axonframework.eventsourcing.eventstore.EventStorageEngine;
import org.axonframework.eventsourcing.eventstore.EventStore;
import org.axonframework.extensions.mongo.MongoTemplate;
import org.axonframework.extensions.mongo.DefaultMongoTemplate;
import org.axonframework.extensions.mongo.eventsourcing.eventstore.MongoEventStorageEngine;
import org.axonframework.spring.config.AxonConfiguration;

import com.mongodb.ConnectionString;
import com.mongodb.MongoClientSettings;
import com.mongodb.client.MongoClient;
import com.mongodb.client.MongoClients;

import com.thoughtworks.xstream.XStream;

@Configuration
public class MongoDBConfig extends AbstractMongoClientConfiguration {
		 
    @Override
    protected String getDatabaseName() {
        return databaseName;
    }
 
    @Override
    @Bean
    public MongoClient mongoClient() {
    	return MongoClients.create(MongoClientSettings.builder()
                .uuidRepresentation(org.bson.UuidRepresentation.JAVA_LEGACY)
                .applyConnectionString(new ConnectionString(connectionUrl))
                .applicationName( appName )
                .build());
    	
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
	public EventStorageEngine storageEngine(com.mongodb.client.MongoClient client) {
		return MongoEventStorageEngine.builder()
				.eventSerializer(org.axonframework.serialization.xml.XStreamSerializer.builder()
						.xStream(securedXStream())
						.build() )
				.snapshotSerializer(org.axonframework.serialization.xml.XStreamSerializer.builder()
						.xStream(securedXStream())
						.build() )
				.mongoTemplate(DefaultMongoTemplate.builder()
						.mongoDatabase(client)
						.build())
				.build();
	}
	
	// secured XStream required of updated XStream implementation
	protected XStream securedXStream() {
	    XStream xStream = new XStream();
	    xStream.allowTypesByWildcard(new String[]{"com.harbormaster.**"});
	    return xStream;
	}
	
	// ------------------------------------------
    // attributes
    // ------------------------------------------
    
	@Value("${mongodb.connection.url}")
	public String connectionUrl	= null;
	@Value("${spring.application.name}")
	public String appName			= null;
	@Value("${mongodb.database.name}")
	public String databaseName		= null;
}
