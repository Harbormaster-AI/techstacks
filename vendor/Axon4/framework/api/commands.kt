#header()
package ${aib.getRootPackageName(true)}.api

import org.axonframework.modelling.command.TargetAggregateIdentifier

#set( $usingAggregate = true )
#outputKotlinCommands( $usingAggregate )