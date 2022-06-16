#set( $identifierFieldName = "Id" )
#set( $tokenSystemName = $display.capitalize( ${aib.getParam( "corda.token-system-name" ).toLowerCase()} ) )
#header()
package ${aib.getRootPackageName(true)}.helper.corda.api;

import java.util.UUID;

/**
 * Helper to the different ${identifierFieldName}s for a ${tokenSystemName}
 **/
 
public class $tokenSystemName{
#foreach( $class in $aib.getClassesToGenerate() )
#set( $name = $display.uncapitalize( $class.getName() ) )
	public UUID ${name}${identifierFieldName} = null;
#end##foreach( $class in $aib.getClassesToGenerate() )
}
 