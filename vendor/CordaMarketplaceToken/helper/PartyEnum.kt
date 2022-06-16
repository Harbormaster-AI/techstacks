#header()
package ${aib.getRootPackageName(true)}.helper.corda.api

/**
 * Helper to represent the different types of discovered Parties -- Holders of tokens
 **/
 
// --------------------------------------------
// enum declarations
// --------------------------------------------
## macro call to setup the cordaNodes data
#establishCordaNodes()		
#set( $totalNodes = $cordaNodes.size() )
#set( $args = "" )
enum class PartyEnum {
#establishCordaNodes()		
#foreach( $node in $cordaNodes )
#set( $node = $convert.toStrings( $node ) )
#set( $enumName = $node.get(0) )
#set( $args = "${args}$enumName" )
#if ( $velocityCount < $totalNodes )
#set( $args = "${args}, " )
#end##if ( $velocityCount < $totalAttributes )
#end##foreach( $node in cordaNodes )
    $args
}

