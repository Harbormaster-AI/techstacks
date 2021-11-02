#header()
package ${aib.getRootPackageName(true)}.enumerator;

// --------------------------------------------
// enum declarations
// --------------------------------------------
#foreach( $enum in $aib.getEnumClassesToGenerate() )
#set( $enumName = $enum.getName() )
#set( $args = "" )
enum class $enumName {
#set( $attributes = $enum.getAttributes() )
#set( $totalAttributes = $attributes.size() )
#foreach ( $attribute in $attributes )
#set( $attributeName = $attribute.getName() )
#set( $args = "${args}$attributeName" )
#if ( $velocityCount < $totalAttributes )
#set( $args = "${args}, " )
#end##if ( $velocityCount < $totalAttributes )
#end##foreach ( $attribute in $attributes )
    $args
}

#end##foreach ( $enum in $aib.getEnumClassesToGenerate() )

