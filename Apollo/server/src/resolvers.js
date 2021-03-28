#set( $classes = $aib.getClassesToGenerate() )
#set( $size = $classes.size() )
#set( $includePrimaryKeys = false )
#set( $includeType = false )
#set( $includeAssociations = false )
#set( $delim = ", " )
#set( $suffix = "" )
#set( $includeComposites = false )
const { paginateResults } = require('./utils');

module.exports = {

  Query: {
	  
#foreach( $class in $classes )
#set( $className = $class.getName() )
#set( $lowercaseClassName = "${Utils.lowercaseFirstLetter(${className})}" )
//////////////////////////
// $className
//////////////////////////
    ${lowercaseClassName}: async (_, { id }, { dataSources }) => {
    	return await dataSources.${className}API.find( id );
	},

    ${lowercaseClassName}s: async (_, { pageSize = ${aib.getParam("apollo.pageSize")}, after }, { dataSources }) => {
      const all = await dataSources.${className}API.findAll();

      const ${lowercaseClassName}Page = paginateResults({
    		  after,
    		  pageSize,
    		  results: all,
      });

     return { ${lowercaseClassName}Page,
    		  cursor: ${lowercaseClassName}Page.length ? ${lowercaseClassName}Page[${lowercaseClassName}Page.length - 1].cursor : null,
        // if the cursor of the end of the paginated results is the same as the
        // last item in _all_ results, then there are no more results after this
    		  hasMore: ${lowercaseClassName}Page.length
    		  		? ${lowercaseClassName}Page[${lowercaseClassName}Page.length - 1].cursor !== all[all.length - 1].cursor
    		  		: false,
      };
    },
#end##foreach( $class in $classes )
  },

  Mutation: {
	  
#foreach( $class in $classes )
#set( $className = $class.getName() )
#set( $lowercaseClassName = "${Utils.lowercaseFirstLetter(${className})}" )
#set( $argsAsString = ${class.getAttributesAsString( ${includePrimaryKeys}, ${includeType}, ${includeAssociations}, ${delim}, ${suffix} )} )
//////////////////////////
// $className
//////////////////////////
	add${className}: async (_, { ${argsAsString} }, { dataSources }) => {
		return await dataSources.${className}API.add( { ${argsAsString} } );		
	},

	update${className}: async (_, { ${argsAsString}, id }, { dataSources }) => {
		return await dataSources.${className}API.update( { ${argsAsString} }, id );	
	},

	remove${className}: async (_, { id }, { dataSources }) => {
		return await dataSources.${className}API.remove( { id } );	
	},
#end##foreach( $class in $classes )
  },

#foreach( $class in $classes )
#set( $className = $class.getName() )
#set( $lowercaseClassName = "${Utils.lowercaseFirstLetter(${className})}" )
#if( $class.getAssociations().size() > 0 )
  ${className}: {
#foreach( $singleAssociation in $class.getSingleAssociations( ${includeComposites} ) )
#set( $roleName = $singleAssociation.getRoleName() )
#set( $lowercaseRoleName = ${Utils.lowercaseFirstLetter(${roleName})} )
#set( $childName = $singleAssociation.getType() )
#set( $lowercaseChildName = ${Utils.lowercaseFirstLetter(${childName})} )
#set( $parentName  = $class.getName() )
#set( $childClass = $aib.getClassObject( ${childName} ) )
#set( $argsAsString = ${childClass.getAttributesAsString( ${includePrimaryKeys}, ${includeType}, ${includeAssociations}, ${delim}, ${suffix} )} )
	
	${lowercaseRoleName}: async (${lowercaseClassName}, { id }, { dataSources }) => { 
	    return new Promise(function(resolve, reject) { 
		    dataSources.${className}API.get${roleName}( ${lowercaseClassName}.id ).then(${lowercaseChildName} => {
				resolve(${lowercaseChildName});
			})
		})
	},
	
    add${roleName}: async (${lowercaseClassName}, { ${argsAsString} }, { dataSources }) => {
    	return await dataSources.${className}API.add${roleName}( ${lowercaseClassName}.id, { ${argsAsString} } );
	},

    assign${roleName}: async (${lowercaseClassName}, { ${lowercaseRoleName}Id }, { dataSources }) => {
    	return await dataSources.${className}API.assignTo${roleName}( ${lowercaseClassName}.id, ${lowercaseRoleName}Id );	
	},
	
    unassign${roleName}: async (_, { ${lowercaseClassName}Id }, { dataSources }) => {
    	return await dataSources.${className}API.unassignFrom${roleName}( ${lowercaseClassName}Id );
	},
#end##foreach( $singleAssociation in $class.getSingleAssociations( ${includeComposites} ) )
#foreach( $multiAssociation in $class.getMultipleAssociations() )
#set( $roleName = $multiAssociation.getRoleName() )
#set( $lowercaseRoleName = ${Utils.lowercaseFirstLetter(${roleName})} )
#set( $childName = $multiAssociation.getType() )
#set( $lowercaseChildName = ${Utils.lowercaseFirstLetter(${childName})} )
#set( $parentName  = $class.getName() )
#set( $childClass = $aib.getClassObject( ${childName} ) )
#set( $argsAsString = ${childClass.getAttributesAsString( ${includePrimaryKeys}, ${includeType}, ${includeAssociations}, ${delim}, ${suffix} )} )

	$lowercaseRoleName: async (${lowercaseClassName}, {}, { dataSources }) => { 
	    return new Promise(function(resolve, reject) { 
		    dataSources.${className}API.getLeagues( ${lowercaseClassName}.id ).then(${lowercaseRoleName} => {
				resolve(${lowercaseRoleName});
			})
		})
	},
        
    addTo${roleName}: async (${lowercaseClassName}, { ${argsAsString} }, { dataSources }) => {
    	return new Promise(function(resolve, reject) {
		    dataSources.${className}API.addTo${roleName}( ${lowercaseClassName}.id, { ${argsAsString} } ).then( result => {
			    resolve ( result );
			})
		})
	},

    assignTo${roleName}: async (${lowercaseClassName}, { ${lowercaseRoleName}Ids }, { dataSources }) => {
        return new Promise(function(resolve, reject) {
            dataSources.${className}API.assignTo${roleName}( ${lowercaseClassName}.id, ${lowercaseRoleName}Ids ).then( result => {
                resolve ( result );
            })
	    })
	},
	
#end##foreach( $multiAssociation in $class.getMultipleAssociations() )
  },
#end##if( $class.getAssociations().size() > 0 )
#end##foreach( $class in $classes )
};

