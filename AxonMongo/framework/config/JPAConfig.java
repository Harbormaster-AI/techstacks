#header()
package ${aib.getRootPackageName()}.config;

import com.thoughtworks.xstream.XStream;

import java.sql.SQLException;
import java.util.Properties;
import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;

import org.axonframework.common.jdbc.PersistenceExceptionResolver;
import org.axonframework.common.jpa.EntityManagerProvider;
import org.axonframework.common.transaction.TransactionManager;
import org.axonframework.eventhandling.tokenstore.TokenStore;
import org.axonframework.eventhandling.tokenstore.jpa.JpaTokenStore;
import org.axonframework.eventsourcing.eventstore.EmbeddedEventStore;
import org.axonframework.eventsourcing.eventstore.EventStorageEngine;
import org.axonframework.eventsourcing.eventstore.EventStore;
import org.axonframework.eventsourcing.eventstore.jpa.JpaEventStorageEngine;
import org.axonframework.serialization.Serializer;
import org.axonframework.spring.config.AxonConfiguration;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.env.Environment;
import org.springframework.jdbc.datasource.DriverManagerDataSource;
import org.springframework.orm.jpa.LocalContainerEntityManagerFactoryBean;
import org.springframework.orm.jpa.vendor.HibernateJpaVendorAdapter;

import ${aib.getRootPackageName()}.provider.CustomEntityManagerProvider;

@Configuration
public class JPAConfig {

	@Bean(name = "entityStore")
	public EmbeddedEventStore eventStore(EventStorageEngine storageEngine, AxonConfiguration configuration) {
		return EmbeddedEventStore.builder().storageEngine(storageEngine)
				.messageMonitor(configuration.messageMonitor(EventStore.class, "eventStore")).build();
	}

	@Bean
	public EventStorageEngine storageEngine(Serializer defaultSerializer, Serializer eventSerializer,
			PersistenceExceptionResolver persistenceExceptionResolver, AxonConfiguration configuration,
			TransactionManager transactionManager) throws SQLException {
		return JpaEventStorageEngine.builder()
				.eventSerializer(org.axonframework.serialization.xml.XStreamSerializer.builder()
						.xStream(securedXStream()).build())
				.snapshotSerializer(org.axonframework.serialization.xml.XStreamSerializer.builder()
						.xStream(securedXStream()).build())
				.persistenceExceptionResolver(persistenceExceptionResolver)
				.entityManagerProvider(entityManagerProvider()).transactionManager(transactionManager)
				.upcasterChain(configuration.upcasterChain()).build();
	}

	@Bean(name = "entityManagerProvider")
	public EntityManagerProvider entityManagerProvider() {
		return new CustomEntityManagerProvider();
	}

	@Bean
	public javax.sql.DataSource dataSource() {
		DriverManagerDataSource dataSource = new DriverManagerDataSource();
		dataSource.setDriverClassName(env.getProperty(PROP_DB_DRIVER_CLASS));
		dataSource.setUrl(env.getProperty(PROP_DB_URL));
		dataSource.setUsername(env.getProperty(PROP_DB_USER));
		dataSource.setPassword(env.getProperty(PROP_DB_PASS));
		return dataSource;
	}

	@Bean(name = "entityManagerFactory")
	public LocalContainerEntityManagerFactoryBean entityManagerFactory() {
		LocalContainerEntityManagerFactoryBean factoryBean = new LocalContainerEntityManagerFactoryBean();
		// factoryBean.setPersistenceUnitName("entityStore");
		factoryBean.setDataSource(dataSource());

		HibernateJpaVendorAdapter hibernateAdapter = new HibernateJpaVendorAdapter();
		factoryBean.setJpaVendorAdapter(hibernateAdapter);
		factoryBean.setJpaProperties(jpaProperties());

		return factoryBean;
	}

	@Bean
	public TokenStore tokenStore( Serializer serializer ) {
		return JpaTokenStore.builder()
				.entityManagerProvider(entityManagerProvider())
				.serializer(serializer)
				.build();
	 }

	protected Properties jpaProperties() {
		final Properties props = new Properties();

		props.setProperty(HIBERNATE_DIALECT, env.getProperty("spring.jpa.hibernate.dialect"));
		props.setProperty(HIBERNATE_SHOW_SQL, env.getProperty("spring.jpq.show-sql"));
		props.setProperty(HIBERNATE_HBM2DDL_AUTO, env.getProperty("spring.jpa.hibernate.ddl-auto"));

		return props;
	}

	// secured XStream required of updated XStream implementation
	protected XStream securedXStream() {
	    XStream xStream = new XStream();
	    xStream.allowTypesByWildcard(new String[]{"${aib.getRootPackageName()}.**"});
	    return xStream;
	}

	// ------------------------------------------
	// attributes
	// ------------------------------------------
	@Autowired
	private Environment env;

	static final private String HIBERNATE_DIALECT = "hibernate.dialect";
	static final private String HIBERNATE_SHOW_SQL = "hibernate.show.sql";
	static final private String HIBERNATE_HBM2DDL_AUTO = "hibernate.hbm2ddl.auto";

	static final private String PROP_DB_DRIVER_CLASS = "spring.datasource.driver-class-name";
	static final private String PROP_DB_URL = "spring.datasource.url";
	static final private String PROP_DB_USER = "spring.datasource.username";
	static final private String PROP_DB_PASS = "spring.datasource.password";

}
	
