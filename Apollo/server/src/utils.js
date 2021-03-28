const SQL = require('sequelize');

module.exports.paginateResults = ({
  after: cursor,
  pageSize = 20,
  results,
  // can pass in a function to calculate an item's cursor
  getCursor = () => null,
}) => {
  if (pageSize < 1) 
	  return [];

  if (!cursor) 
	  return results.slice(0, pageSize);
  
  const cursorIndex = results.findIndex(item => {
    // if an item has a `cursor` on it, use that, otherwise try to generate one
    let itemCursor = item.cursor ? item.cursor : getCursor(item);

    // if there's still not a cursor, return false by default
    return itemCursor ? cursor === itemCursor : false;
  });

  return cursorIndex >= 0
    ? cursorIndex === results.length - 1 // don't let us overflow
      ? []
      : results.slice(
          cursorIndex + 1,
          Math.min(results.length, cursorIndex + 1 + pageSize),
        )
    : results.slice(0, pageSize);
};

module.exports.createStore = () => {
  const Op = SQL.Op;
  const operatorsAliases = {
    $in: Op.in,
  };

  const db = new SQL('database', 'username', 'password', {
    dialect: 'sqlite',
    storage: './store.sqlite',
    operatorsAliases,
    logging: false,
  });

#set( $tables = "" )
#set( $classes = $aib.getClassesToGenerate() )
#set( $size = $classes.size() )
#foreach( $class in $classes )
#set( $className = $class.getName() )
#set( $lowercaseClassName = "${Utils.lowercaseFirstLetter(${className})}" )
#if( $velocityCount < $size )
#set( $tables = "${tables}${lowercaseClassName}, " )
#else
#set( $tables = "${tables}${lowercaseClassName}" )
#end##if( $velocityCount < $size )
  const $lowercaseClassName = db.define('${lowercaseClassName}', {
    id: {
        type: SQL.INTEGER,
        primaryKey: true,
        autoIncrement: true,
    },
    createdAt: SQL.DATE,
    updatedAt: SQL.DATE,
#set( $attributes = ${class.getAttributesOrderedInHierarchy( $includePrimaryKeys )} )
#foreach( $attribute in $attributes )
#if ( $attribute.isFromAssociation() == false )
#set( $sqlType = "SQL.STRING" )
#if ( $attribute.isFromEnumerator() == false )
#if( $sqlType.equalsIgnoreCase("float") )
#set( $sqlType = "SQL.FLOAT" )
#elseif( $sqlType.equalsIgnoreCase("boolean") )
#set( $sqlType = "SQL.BOOLEAN" )
#elseif( $sqlType.equalsIgnoreCase("char") )
#set( $sqlType = "SQL.CHAR" )
#elseif( $sqlType.equalsIgnoreCase("java.sql.Date") || $sqlType.equalsIgnoreCase("Date") )
#set( $sqlType = "SQL.DATE" )
#elseif( $sqlType.equalsIgnoreCase("int") || $sqlType.equalsIgnoreCase("integer") )
#set( $sqlType = "SQL.INTEGER" )
#elseif( $sqlType.equalsIgnoreCase("double") )
#set( $sqlType = "SQL.DOUBLE" )
#elseif( $sqlType.equalsIgnoreCase("short") )
#set( $sqlType = "SQL.INTEGER" )
#end##if( $sqlType.equalsIgnoreCase("float") )
#else## attribute is an enumeration, using decl form ENUM('value 1', 'value 2')
#set( $sqlType = "SQL.ENUM(" )
#set( $enum = $aib.getEnumClassObject( ${attribute.getType()} ) )
#set( $enumAttributes = $enum.getAttributes() )
#set( $attributesSize = $enumAttributes.size() )
#foreach ( $enumAttribute in $enumAttributes )
#set( $sqlType = "${sqlType}${esc.singleQuote}${enumAttribute.getName()}${esc.singleQuote}")
#if( $velocityCount < $attributesSize )
	#set( $sqlType = "${sqlType}, " )
#end##if( $velocityCount < $attributesSize )
#end##foreach ( $enumAttribute in $enumAttributes )

#set( $sqlType = "${sqlType})" )
#end##if ( $attribute.isFromEnumerator() == false )
    ${attribute.getName()}: $sqlType,
#end##if ( $attribute.isFromAssociation() == false )
#end##foreach( $attribute in $attributes )
    freezeTableName:true
  });

#end##foreach( $class in $classes )
#set( $includeComposites = false )
#foreach( $class in $classes )
#set( $className = $class.getName() )
#set( $lowercaseClassName = "${Utils.lowercaseFirstLetter(${className})}" )
#foreach( $singleAssociation in $class.getSingleAssociations( ${includeComposites} ) )
#set( $roleName = $singleAssociation.getRoleName() )
#set( $lowercaseRoleName = ${Utils.lowercaseFirstLetter(${roleName})} )
#set( $childName = $singleAssociation.getType() )
#set( $lowercaseChildName = ${Utils.lowercaseFirstLetter(${childName})} )
  ${lowercaseClassName}.hasOne(${lowercaseChildName}, {as: '${roleName}'})
#if ( $singleAssociation.hasNavigableSiblingAssociationEndObject() == true )
#if( $singleAssociation.getSiblingAssociationEndObject().isFromSingleValueAssociation() == true )															
  ${lowercaseChildName}.belongsTo(${lowercaseClassName}, { 
    as: '${singleAssociation.getSiblingAssociationEndObject().getRoleName()}' 
  }) 
#else
  ${lowercaseChildName}.belongsToMany(${lowercaseClassName}, {
	as: { 
	  singular: 'To${singleAssociation.getRoleName()}', 
	  plural: '${singleAssociation.getRoleName()}' 
	}
  })
#end##if( ${singleAssociation.getSiblingAssociationEndObject().isFromSingleValueAssociation() == true )
#end##if ( $singleAssociation.hasNavigableSiblingAssociationEndObject() == true )
#end##foreach( $singleAssociation in $class.getSingleAssociations( ${includeComposites} ) )
#foreach( $multiAssociation in $class.getMultipleAssociations() )
#set( $roleName = $multiAssociation.getRoleName() )
#set( $lowercaseRoleName = ${Utils.lowercaseFirstLetter(${roleName})} )
#set( $childName = $multiAssociation.getType() )
#if ( $multiAssociation.hasNavigableSiblingAssociationEndObject() == true )
#if( $multiAssociation.getSiblingAssociationEndObject().isFromSingleValueAssociation() == true )															
  ${lowercaseChildName}.belongsTo(${lowercaseClassName}, { 
    as: '${multiAssociation.getSiblingAssociationEndObject().getRoleName()}' 
  }) 
#else
  ${lowercaseChildName}.belongsToMany(${lowercaseClassName}, {
	as: { 
	  singular: 'To${roleName}', 
	  plural: '${roleName}' 
	}
  })
#end##if( ${multiAssociation.getSiblingAssociationEndObject().isFromSingleValueAssociation() == true )
#end##if ( $multiAssociation.hasNavigableSiblingAssociationEndObject() == true )
#set( $lowercaseChildName = ${Utils.lowercaseFirstLetter(${childName})} )
#if ( $multiAssociation.hasNavigableSiblingAssociationEndObject() == true )
  ${lowercaseChildName}.belongsToMany(${lowercaseClassName},  { 
    as: { 
	  singular: 'To${multiAssociation.getSiblingAssociationEndObject().getRoleName()}', 
	  plural: '${multiAssociation.getSiblingAssociationEndObject().getRoleName()}' 
	}
  })
#else
  ${lowercaseClassName}.hasMany(${lowercaseChildName}, {
    as: { 
	  singular: 'To${roleName}', 
	  plural: '${roleName}' 
    }
  })
#end##if( ${multiAssociation.getSiblingAssociationEndObject().isFromSingleValueAssociation() == true )
#end##foreach( $multiAssociation in $class.getMultipleAssociations() )
#end##foreach( $class in $classes )

  return { $tables };
};
