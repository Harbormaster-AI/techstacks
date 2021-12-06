class $classObject.getName() < ApplicationRecord
#set( $includePrimaryKeys = false )
#set( $includeTypes = false )
#set( $includeAssociations = false )
#set( $delim = ", :" )
#set( $suffix = "" )
#foreach( $enumAsAttribute in $classObject.getEnumerators() )
#set( $enum = $aib.getEnumClassObject( $enumAsAttribute.getType() ) )
#set( $attributes = $enum.getAttributesAsString( $includePrimaryKeys, $includeTypes, $includeAssociations, $delim, $suffix ) )
  enum $enumAsAttribute.getName(): [:${attributes}]
#end##foreach( $enum in $classObject.getEnumerators() )
##declareAssociations( $classObject )
end
