const { gql } = require('apollo-server');
const typeDefs = gql`
#set( $classes = $aib.getClassesToGenerate() )
#set( $size = $classes.size() )
#set( $includePrimaryKeys = false )
#set( $includeHierarchy = true )
#set( $includeType = false )
#set( $delim = ", " )
#set( $suffix = "" )
#set( $includeAssociations = false )

# read related functions
  type Query {
 
#foreach( $class in $classes )
#set( $className = $class.getName() )
#set( $lowercaseClassName = "${Utils.lowercaseFirstLetter(${className})}" )
"""
$className
"""
    ${lowercaseClassName}(id: ID!): ${className}
    ${lowercaseClassName}s: ${className}QueryResult

#end##foreach( $class in $classes ) 
  }

# update related functions
 type Mutation { 

#foreach( $class in $classes )
#set( $className = $class.getName() )
#set( $lowercaseClassName = "${Utils.lowercaseFirstLetter(${className})}" )
#set( $argsAsString = ${class.getAttributesAsString( ${includePrimaryKeys}, ${includeType}, ${includeAssociations}, ${delim}, ${suffix} )} )
#set( $outputTheAttributeType = false )
    add${className}(
#set( $attributes = ${class.getAttributesOnly( $includeHierarchy, $includePrimaryKeys )} )
#foreach( $attribute in $attributes ) 
      ${attribute.getName()}: #attributeTypeDeclaration(${attribute}, ${class}, ${outputTheAttributeType})
#end##foreach( $attribute in $attributes )      
    ): ${className}
  
    update${className}(
      id: ID!
#foreach( $attribute in $attributes ) 
      ${attribute.getName()}: #attributeTypeDeclaration(${attribute}, ${class}, ${outputTheAttributeType})
#end##foreach( $attribute in $attributes )      
    ): ${className}
    remove${className}(id: ID!): Boolean
#end##foreach( $class in $classes )
  }

#set( $includePrimaryKeys = false )
#set( $outputTheAttributeType = false )
#foreach( $class in $classes )
#set( $className = $class.getName() )
#set( $lowercaseClassName = "${Utils.lowercaseFirstLetter(${className})}" )
"""
$className
""" 
  type ${className} {
    id: ID!  
#foreach( $attribute in $class.getAttributesOrderedInHierarchy( ${includePrimaryKeys} ) )  
    ${Utils.lowercaseFirstLetter(${attribute.getName()})}: #attributeTypeDeclaration(${attribute}, ${classObject}, ${outputTheAttributeType}) 
#end##foreach( $attribute in $attributes )  
#set( $includeComposites = false )
#foreach( $singleAssociation in $class.getSingleAssociations( ${includeComposites} ) )
#set( $roleName = $singleAssociation.getRoleName() )
#set( $lowercaseRoleName = ${Utils.lowercaseFirstLetter(${roleName})} )
#set( $childName = $singleAssociation.getType() )
#set( $lowercaseChildName = ${Utils.lowercaseFirstLetter(${childName})} )
#set( $childClass = $aib.getClassObject( ${childName} ) )
    add${roleName}(
#set( $attributes = ${childClass.getAttributesOnly( $includeHierarchy, $includePrimaryKeys )} )    
#foreach( $attribute in $attributes ) 
      ${attribute.getName()}: #attributeTypeDeclaration(${attribute}, ${childClass}, ${outputTheAttributeType})
#end##foreach( $attribute in $attributes )           
    ): ${className}
    assign${roleName}(  ${lowercaseRoleName}Id: [ID]! ): ${className}
    unassign${roleName}( ${lowercaseClassName}Id: ID! ): ${className}
#end##foreach( $singleAssociation in $class.getSingleAssociations( ${includeComposites} ) )
#foreach( $multiAssociation in $class.getMultipleAssociations() )
#set( $roleName = $multiAssociation.getRoleName() )
#set( $lowercaseRoleName = ${Utils.lowercaseFirstLetter(${roleName})} )
#set( $childName = $multiAssociation.getType() )
#set( $lowercaseChildName = ${Utils.lowercaseFirstLetter(${childName})} )
#set( $childClass = $aib.getClassObject( ${childName} ) )
    addTo${roleName}(
#set( $attributes = ${childClass.getAttributesOnly( $includeHierarchy, $includePrimaryKeys )} )    
#foreach( $attribute in $attributes ) 
      ${attribute.getName()}: #attributeTypeDeclaration(${attribute}, ${childClass}, ${outputTheAttributeType})
#end##foreach( $attribute in $attributes )           
    ): ${className}
    assignTo${roleName}( ${lowercaseRoleName}Ids: [ID]! ): ${className}
#end##foreach( $multiAssociation in $class.getMultipleAssociations() )

  } 

  type ${className}QueryResult {
    cursor: String
    hasMore: Boolean!
    ${lowercaseClassName}Page: [${className}]
  }  
  
#end##foreach( $class in $classes )

#foreach ( $enum in $aib.getEnumClassesToGenerate() )
#set( $enumName = $enum.getName() )
"""
${enumName}
"""
  enum $enumName {
#set( $attributes = $enum.getAttributes() )
#foreach ( $attribute in $attributes )
    ${attribute.getName()}
#end##foreach ( $attribute in $attributes )
  }
  
#end##foreach ( $enum in $aib.getEnumClassesToGenerate() )
`;
module.exports = typeDefs;
