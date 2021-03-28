#set( $className = $classObject.getName() )
#set( $lowercaseClassName = "${Utils.lowercaseFirstLetter(${className})}" )
const { DataSource } = require('apollo-datasource');
#foreach( $type in $classObject.getAssociationTypes( $className ) )
const ${type}API = require('./${Utils.lowercaseFirstLetter(${type})}DS');
#end

class ${className}API extends DataSource {

	//********************************************************************
	// general holder 
	//********************************************************************

	constructor({ store }) {
    super();
    this.store = store;
  }

  /**
   * This is a function that gets called by ApolloServer when being setup.
   * This function gets called with the datasource config including things
   * like caches and context. We'll assign this.context to the request context
   * here, so we can know about the user making requests
   */
  initialize(config) {
    this.context = config.context;
  }

  #set( $includePrimaryKeys = false )
  #set( $includeType = false )
  #set( $includeAssociations = false )
  #set( $delim = ", " )
  #set( $suffix = "" )
  #set( $argsAsString = ${classObject.getAttributesAsString( ${includePrimaryKeys}, ${includeType}, ${includeAssociations}, ${delim}, ${suffix} )} )
  //********************************************************************
  // find a ${className}
  //********************************************************************
  async find( id ) {
    return await this.store.${lowercaseClassName}.findOne({ where: {id} });
  }

  //********************************************************************
  // add a ${className}
  //********************************************************************
  async add( { ${argsAsString} } ) {
    return await this.store.${lowercaseClassName}.create( { ${argsAsString} } );
  }

  //********************************************************************
  // update a ${className}
  //********************************************************************
  async update( { ${argsAsString}, id } ) {
	await this.store.${lowercaseClassName}.update( { ${argsAsString} }, { where: { id } });
	return this.find({id});
  }

  //********************************************************************
  // remove a ${className} by provided id
  //********************************************************************
  async remove( { id } ) {
	  return !!this.store.${lowercaseClassName}.destroy({ where: { id } });
  }
  
  //********************************************************************
  // get all ${className}
  //********************************************************************
  async findAll() {
    return await this.store.${lowercaseClassName}.findAll();
  }

#set( $includeComposites = false )
#foreach( $singleAssociation in $classObject.getSingleAssociations( ${includeComposites} ) )
#set( $roleName = $singleAssociation.getRoleName() )
#set( $lowercaseRoleName = ${Utils.lowercaseFirstLetter(${roleName})} )
#set( $childName = $singleAssociation.getType() )
#set( $lowercaseChildName = ${Utils.lowercaseFirstLetter(${childName})} )
#set( $parentName  = $classObject.getName() )
#set( $childClass = $aib.getClassObject( ${childName} ) )
#set( $argsAsString = ${childClass.getAttributesAsString( ${includePrimaryKeys}, ${includeType}, ${includeAssociations}, ${delim}, ${suffix} )} )

  //********************************************************************
  // adds a ${roleName} on a ${className}
  //returns this ${className}
  //********************************************************************
  getPlayer( gameId ) {
      return new Promise((resolve, reject) => {
    	  this.find({id: gameId}).then(game => {
            game.getPlayer().then(player => { 
				resolve(player);
			})
		 })
	  })
  }

  //********************************************************************
  // adds a ${roleName} on a ${className}
  //returns this ${className}
  //********************************************************************
  async add${roleName}( ${lowercaseClassName}Id, { ${argsAsString} } ) {
      return new Promise((resolve, reject) => {
    	  this.find({id: ${lowercaseClassName}Id}).then(${lowercaseClassName} => {
    		  new ${childName}API({store: this.store}).add( { ${argsAsString}  } ).then(${lowercaseChildName} => {
    			  ${lowercaseClassName}.set${roleName}(${lowercaseChildName}).then(() => {
				        resolve( ${lowercaseClassName} );
			      })
			  })
	      })
      });	  	 
  }

  //********************************************************************
  // assigns a previously created ${lowercaseRoleName} to ${roleName} 
  // on a ${className}
  //returns this ${className}
  //********************************************************************
  async assignTo${roleName}( ${lowercaseClassName}Id, ${lowercaseRoleName}Id ) {
    return new Promise((resolve, reject) => {
	    this.find(${lowercaseClassName}Id).then(${lowercaseClassName} => {
	    	new ${childName}API({store: this.store}).find( ${lowercaseRoleName}Id ).then(${lowercaseChildName} => {
    	        ${lowercaseClassName}.set${roleName}(${lowercaseChildName});
    	       resolve(${lowercaseClassName});
	    	})
        })
    })
  }

  //********************************************************************
  // unassigns a ${roleName} on a ${className} by setting it to null
  //returns this ${className}
  //********************************************************************				
  async unassign${roleName}( ${lowercaseClassName}Id ) {
    return new Promise((resolve, reject) => {
	    this.find(${lowercaseClassName}Id).then(${lowercaseClassName} => {
    	    ${lowercaseClassName}.set${roleName}(null);
    	    resolve(${lowercaseClassName});
        })
    })
  }
		
#end##foreach( $singleAssociation in $classObject.getSingleAssociations( ${includeComposites} ) )

#foreach( $multiAssociation in $classObject.getMultipleAssociations() )
#set( $roleName = $multiAssociation.getRoleName() )
#set( $lowercaseRoleName = ${Utils.lowercaseFirstLetter(${roleName})} )
#set( $childName = $multiAssociation.getType() )
#set( $lowercaseChildName = ${Utils.lowercaseFirstLetter(${childName})} )
#set( $parentName  = $classObject.getName() )
#set( $childClass = $aib.getClassObject( ${childName} ) )
#set( $argsAsString = ${childClass.getAttributesAsString( ${includePrimaryKeys}, ${includeType}, ${includeAssociations}, ${delim}, ${suffix} )} )

  //********************************************************************
  // adds a $childName as the ${roleName} by first creating it 
  //returns this ${className}
  //********************************************************************				
  async addTo${roleName}( ${lowercaseClassName}Id, { ${argsAsString} } ) {
	return new Promise((resolve, reject) => {
		this.find(${lowercaseClassName}Id).then(${lowercaseClassName} => {
		    new ${childName}API({store: this.store}).add( { ${argsAsString} } ).then(${lowercaseChildName} => {
		    	${lowercaseClassName}.addTo${roleName}(${lowercaseClassName}).then(() => {
			        resolve( ${lowercaseClassName} );
				})
		    })
		})
	});
  }			

  //********************************************************************
  // assigns one or more ${lowercaseRoleName}Ids as a ${roleName} 
  // to a ${className}
  //returns this ${className}
  //********************************************************************				
  async assignTo${roleName}( ${lowercaseClassName}Id, ${lowercaseRoleName}Ids ) {
	return new Promise((resolve, reject) => {
		this.find(id).then(${lowercaseClassName} => {
			var ${lowercaseChildName}Api = new ${childName}API({store: this.store});
			${lowercaseClassName}.set${roleName}([]).then((${lowercaseClassName}) => {			
				${lowercaseRoleName}Ids.forEach(function (${lowercaseChildName}Id, index) {
					${lowercaseChildName}Api.find({id: ${lowercaseChildName}Id}).then( found${childName} => {
						${lowercaseClassName}.addTo${roleName}(found${childName});
					})
				})
				resolve(${lowercaseClassName});
			})
		})
	})
  }			
				
#end##foreach( $multiAssociation in $classObject.getMultipleAssociations() )
#set( $argsAsString = ${classObject.getAttributesAsString( ${includePrimaryKeys}, ${includeType}, ${includeAssociations}, ${delim}, ${suffix} )} )
  //********************************************************************
  // saveHelper - internal helper to save a ${className}
  //********************************************************************
  async saveHelper( $argsAsString )  {
    return await this.update( $argsAsString );			
  }

  //********************************************************************
  // loadHelper - internal helper to load member variable ${lowercaseClassName}
  //********************************************************************	
  async loadHelper( id ) {
	  return await this.find(id);
  }
}

module.exports = ${className}API;
