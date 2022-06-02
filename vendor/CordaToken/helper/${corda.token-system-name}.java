#set( $identifierFieldName = $display.capitalize( $aib.getParam( "corda.identifier-field-name" ) ) )
#set( $tokenSystemName = $display.capitalize( ${aib.getParam( "corda.token-system-name" ).toLowerCase()} ) )
#header()
package ${aib.getRootPackageName(true)}.helper.corda.api;

/**
 * Helper to the different ${identifierFieldName}s for a ${tokenSystemName}
 **/
 
public class $tokenSystemName{
#foreach( $class in $aib.getClassesToGenerate() )
#set( $name = $display.uncapitalize( $class.getName() ) )
	public String ${name}${identifierFieldName} = null;
#end##foreach( $class in $aib.getClassesToGenerate() )
}
 