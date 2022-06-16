#set( $tokenSystemName = ${aib.getParam( "corda.token-system-name" ).toLowerCase()} )
#header()
package ${aib.getRootPackageName()}.${tokenSystemName}market.states

// --------------------------------------------
// enum declarations
// --------------------------------------------
#foreach( $enum in $aib.getEnumClassesToGenerate() )
#set( $names = [] )
#set( $enumName = $enum.getName() )
enum class $enumName {
#set( $attributes = $enum.getAttributes() )
#foreach ( $attribute in $attributes )
#set( $void = $names.add( $attribute.getName() ) )
#end##foreach ( $attribute in $attributes )
    $display.list( $names, ", " )
}

#end##foreach ( $enum in $aib.getEnumClassesToGenerate() )

