#header()
package ${aib.getRootPackageName()}.config;

import com.thoughtworks.xstream.XStream;

import org.axonframework.commandhandling.distributed.RoutingStrategy;
import org.axonframework.commandhandling.distributed.AnnotationRoutingStrategy;
import org.axonframework.springboot.DistributedCommandBusProperties;
import org.axonframework.extensions.springcloud.commandhandling.SpringCloudHttpBackupCommandRouter;
import org.axonframework.serialization.xml.XStreamSerializer;

import org.springframework.cloud.client.discovery.DiscoveryClient;
import org.springframework.context.annotation.Configuration;
import org.springframework.cloud.client.serviceregistry.Registration;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Primary;
import org.springframework.web.client.RestTemplate;

/**
 * Configuration class to handle configuring Spring Cloud command routing.  It forces the use of a distributed 
 * annotation routing strategy in order to leverage the @TargetIdentifier ascribed to the relevant field of a command.
 */
@Configuration
class CommandRouterConfiguration {


	/**
	 * Required of springCloudCommandRouter bean
	 */
	@Bean
	public RestTemplate restTemplate() {
		return new RestTemplate();
	}

    @Bean
    @Primary
    public SpringCloudHttpBackupCommandRouter springCloudCommandRouter( DiscoveryClient discoveryClient,
    			Registration localServiceInstance,
	            RestTemplate restTemplate,
	            RoutingStrategy routingStrategy,
	            DistributedCommandBusProperties distributedCommandBusProperties ) {
	    return SpringCloudHttpBackupCommandRouter.builder()
	            .discoveryClient(discoveryClient)
	            .localServiceInstance(localServiceInstance)
	            .restTemplate(restTemplate)
	            .routingStrategy(new org.axonframework.commandhandling.distributed.AnnotationRoutingStrategy())
	            .messageRoutingInformationEndpoint(distributedCommandBusProperties.getSpringCloud().getFallbackUrl())
//	            .contextRootMetadataPropertyName(
//                    distributedCommandBusProperties.springCloud.contextRootMetadataPropertyName
//          		)
	            .serializer(org.axonframework.serialization.xml.XStreamSerializer.builder()
						.xStream(securedXStream())
						.build())
	            .enforceHttpDiscovery()
	            .build();
    }
    
	// secured XStream required of updated XStream implementation
	protected XStream securedXStream() {
	    XStream xStream = new XStream();
	    xStream.allowTypesByWildcard(new String[]{"${aib.getRootPackageName()}.**"});
	    return xStream;
	}
}