#header()
package ${aib.getRootPackageName()}.config;

import org.axonframework.eventsourcing.*;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.beans.factory.annotation.Value;

@Configuration
public class SnapshotConfig {

	// --------------------------------------------------------
	// define a snapshot trigger for each aggregate,
	// as implicitly defined per class and explicitly defined in the model
	// --------------------------------------------------------
#foreach( $aggregate in $aib.getClassesToGenerate() )
#set( $aggregateName = $aggregate.getName() )
#set( $lowercaseName = ${Utils.lowercaseFirstLetter( ${aggregateName} )} )
#set( $aggregateSnapshotThreshold = $aib.getParam("axon-framework.default-snapshot-threshold") )
	@Bean
    public SnapshotTriggerDefinition ${lowercaseName}AggregateSnapshotTriggerDefinition(
        Snapshotter snapshotter,
        @Value("${esc.dollar}{axon.aggregate.${lowercaseName}.snapshot-threshold:${aggregateSnapshotThreshold}}") int threshold) {
        return new EventCountSnapshotTriggerDefinition(snapshotter, threshold);
    }

#end##foreach( $aggregate in $aib.getClassesToGenerate() )
#foreach( $aggregate in $aib.getAggregatesToGenerate() )
#set( $aggregateName = $aggregate.getName() )
#set( $lowercaseName = ${Utils.lowercaseFirstLetter( ${aggregateName} )} )
#set( $aggregateSnapshotThreshold = $aggregate.getSnapShotThreshold() )
		@Bean
	    public SnapshotTriggerDefinition  ${lowercaseName}AggregateSnapshotTriggerDefinition(
	      Snapshotter snapshotter,
	        @Value("${esc.dollar}{axon.aggregate.${lowercaseName}.snapshot-threshold:${aggregateSnapshotThreshold}}") int threshold) {
	        return new EventCountSnapshotTriggerDefinition(snapshotter, threshold);
	    }

#end##foreach( $aggregate in $aib.getAggregatesToGenerate() )	
	
}