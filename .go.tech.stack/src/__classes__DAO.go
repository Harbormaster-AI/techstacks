#set( $className = "${classObject.getName()}" )
package dao

import ( 
    "${appName}/api/model"
    "${appName}/api/utils"
    "fmt"
    "strings"
)



//----------------------------------------------------------------------------
// function initialization
//----------------------------------------------------------------------------
func init() {

	//----------------------------------------------------------------------------
  	// Migrate the schema
  	//----------------------------------------------------------------------------
  	err := utils.GetDB().AutoMigrate(&model.${className}{}).Error
  	
  	if  (err == nil ) {
  		fmt.Println( strings.ToTitle( "Finished AutoMigrate on ${className}" ) )
  	} else {
  	    fmt.Println( strings.ToTitle( "Failed to AutoMigrate on ${className}" ), err )
  	}
}

//----------------------------------------------------------------------------
// Create${className} - creates a new db entry
//----------------------------------------------------------------------------
func Create${className}(obj model.${className})(utils.RequestResult){
	//----------------------------------------------------------------------------
	// variable initialization
	//----------------------------------------------------------------------------
	var requestResult utils.RequestResult	
	var createMsg string		
	var success bool	
		
	//----------------------------------------------------------------------------
	// Pass the reference to the ORM to create
	//----------------------------------------------------------------------------
	result := utils.GetDB().Create(&obj).Error 

	if result == nil {
	    createMsg = fmt.Sprintf( "Created a ${className} with ID=%v", obj.ID )
	    success = true
	} else {
		createMsg = fmt.Sprintf( "Failed trying to create a ${className}", result )
		success = false
	}
	
	requestResult = utils.RequestResult{success, createMsg, "Create${className}", obj}
	return requestResult
}


//----------------------------------------------------------------------------
// Get${className} - returns the matching the provided identifier
//----------------------------------------------------------------------------
func Get${className}(id uint64)(utils.RequestResult){
	//----------------------------------------------------------------------------
	// variable initialization
	//----------------------------------------------------------------------------
	var requestResult utils.RequestResult	
	var getMsg string		
	var success bool	

	//----------------------------------------------------------------------------
	// Pass the reference to the ORM to create
	//----------------------------------------------------------------------------
	var obj model.$className	
	
	//----------------------------------------------------------------------------
	// Retrieve the 1st occurrence from the ORM of a ${className} with a matching ID
	//----------------------------------------------------------------------------
	result := utils.GetDB().First(&obj, id).Error // find first using identifier
	
	if result == nil {
	    getMsg = fmt.Sprintf( "Retrieved a ${className} using ID=%v", id )  
	    success = true
	} else {
		getMsg = fmt.Sprintf( "Failed trying to retrieve a ${className} using ID=%v", id )
		success = false
	}

	requestResult = utils.RequestResult{success, getMsg, "Get${className}", obj}
	
	return requestResult
}

//----------------------------------------------------------------------------
// GetAll${className} - returns all 
//----------------------------------------------------------------------------
func GetAll${className}()(requestResult utils.RequestResult){	 
	//----------------------------------------------------------------------------
	// variable initialization
	//----------------------------------------------------------------------------
	var getAllMsg string		
	var success bool	
	var objs []model.${className}

	//----------------------------------------------------------------------------
	// Request the ORM to find all ${className}
	//----------------------------------------------------------------------------
	result := utils.GetDB().Find(&objs).Error // find all

	if result == nil {
	    getAllMsg = fmt.Sprintf( "Retrieved all ${className}" )  
	    success = true
	} else {
		getAllMsg = fmt.Sprintf( "Failed trying to retrieve all ${className}", result )
		success = false
	}
	
	requestResult = utils.RequestResult{success, getAllMsg, "GetAll${className}", objs}
	return requestResult
}

//----------------------------------------------------------------------------
// Update${className} - updates matching the provided identifier
//----------------------------------------------------------------------------
func Update${className}(obj model.${className})(requestResult utils.RequestResult){
	//----------------------------------------------------------------------------
	// variable initialization
	//----------------------------------------------------------------------------
	var updateMsg string		
	var success bool
		
	//----------------------------------------------------------------------------
	// Pass the reference to the ORM to save
	//----------------------------------------------------------------------------
	result := utils.GetDB().Save(&obj).Error

	if result == nil {
	    updateMsg = fmt.Sprintf( "Updated a ${className} using ID=%v", obj.ID )  
	    success = true
	} else {
		updateMsg = fmt.Sprintf( "Failed trying to update a ${className} using ID=%v", obj.ID )
		success = false
	}
	
	requestResult = utils.RequestResult{success, updateMsg, "Update${className}", obj}
	
	return requestResult
}

//----------------------------------------------------------------------------
// Delete${className} - deletes matching the provided identifier
//----------------------------------------------------------------------------
func Delete${className}(id uint64)(requestResult utils.RequestResult){
	//----------------------------------------------------------------------------
	// variable initialization
	//----------------------------------------------------------------------------
	var deleteMsg string		
	var success bool
	
	//----------------------------------------------------------------------------
	// Obtain the ${className} with the matching identifier
	//----------------------------------------------------------------------------
	requestResult = Get${className}(id)	
	
	if requestResult.Success == true {
		//----------------------------------------------------------------------------
		// Need to cast the interface to a model.${className} so the ORM can figure
		// out which table to deal with
		//----------------------------------------------------------------------------
		obj,_ := requestResult.Data. (model.${className})

		//----------------------------------------------------------------------------
		// Make call to the ORM to delete
		//----------------------------------------------------------------------------
		result := utils.GetDB().Delete(&obj).Error // pass pointer of data to Delete
		
		if result == nil {
		    deleteMsg = fmt.Sprintf( "Deleted a ${className} using ID=%v", id )  
		    success = true
		} else {
			deleteMsg = fmt.Sprintf( "Failed trying to delete a ${className} using ID=%v", id )
			success = false
		}	
		
		requestResult = utils.RequestResult{success, deleteMsg, "Delete${className}", requestResult.Data}
		
	}
	
	return requestResult
}


#set( $includeComposites = false )
#foreach( $singleAssociation in $classObject.getSingleAssociations( ${includeComposites} ) )
#set( $roleName = $singleAssociation.getRoleName() )
#set( $lowercaseRoleName = ${Utils.lowercaseFirstLetter(${roleName})} )
#set( $childName = $singleAssociation.getType() )
#set( $lowercaseChildName = ${Utils.lowercaseFirstLetter(${childName})} )
#set( $parentName  = $classObject.getName() )
//----------------------------------------------------------------------------
// assigns a ${roleName} on a ${className}
//----------------------------------------------------------------------------
func Assign${roleName}( ${lowercaseClassName}Id uint64, ${lowercaseRoleName}Id uint64 )(utils.RequestResult){

	//----------------------------------------------------------------------------
	// Obtain the ${parentName} with the matching identifier
	//----------------------------------------------------------------------------
	parentRequestResult := Get${parentName}(${lowercaseClassName}Id)	
	
	if parentRequestResult.Success == true {
		//----------------------------------------------------------------------------
		// Need to cast the interface to a model.${parentName} so the ORM can figure
		// out which table to deal with
		//----------------------------------------------------------------------------
		${parentName}Obj,_ := parentRequestResult.Data. (model.${parentName})	
		
		//----------------------------------------------------------------------------
		// Pass the reference to the ORM to get
		//----------------------------------------------------------------------------
		var ${childName}Obj model.$childName	
	
		//----------------------------------------------------------------------------
		// Retrieve the 1st occurrence from the ORM of a ${childName} with a 
		// matching ${lowercaseRoleName}Id
		//----------------------------------------------------------------------------
		childRequestResult := utils.GetDB().First(&${childName}Obj, ${lowercaseRoleName}Id).Error // find first using identifier
	
		if childRequestResult == nil {					
			//----------------------------------------------------------------------------
			// assign the $roleName	to the $parentName	
			//----------------------------------------------------------------------------					
			${parentName}Obj.${roleName} = ${childName}Obj

			//----------------------------------------------------------------------------      		
			// save the ${className}
			//----------------------------------------------------------------------------			
			return Update${parentName}(${parentName}Obj)
		} else {
			msg := fmt.Sprintf( "Failed trying to read %s using ID=%v", "${roleName}", ${lowercaseRoleName}Id )
			return utils.RequestResult{false, msg, "assign${roleName}", ${childName}Obj}
		}
	} else {
		return parentRequestResult
	}
}

//----------------------------------------------------------------------------
// unassigns a ${roleName} on a ${className}
//----------------------------------------------------------------------------
func Unassign${roleName}(${lowercaseClassName}Id uint64)(utils.RequestResult) {

	//----------------------------------------------------------------------------
	// Obtain the ${parentName} with the matching identifier
	//----------------------------------------------------------------------------
	parentRequestResult := Get${parentName}(${lowercaseClassName}Id)	
	
	if parentRequestResult.Success == true {
		//----------------------------------------------------------------------------
		// Need to cast the interface to a model.${parentName} so the ORM can figure
		// out which table to deal with
		//----------------------------------------------------------------------------
		${parentName}Obj,_ := parentRequestResult.Data. (model.${parentName})	
		
		//----------------------------------------------------------------------------      		
		// assign an empty ${childName} to the ${roleName}
		//----------------------------------------------------------------------------      				
		${parentName}Obj.$roleName = model.${childName}{}
		
		//----------------------------------------------------------------------------      		
		// save the ${className}
		//----------------------------------------------------------------------------			
		return Update${parentName}(${parentName}Obj)
	
	} else {
		return parentRequestResult
	}

}
	
#end##foreach( $singleAssociation in $classObject.getSingleAssociations( ${includeComposites} ) )

#foreach( $multiAssociation in $classObject.getMultipleAssociations() )
#set( $roleName = $multiAssociation.getRoleName() )
#set( $lowercaseRoleName = ${Utils.lowercaseFirstLetter(${roleName})} )
#set( $childName = $multiAssociation.getType() )
#set( $lowercaseChildName = ${Utils.lowercaseFirstLetter(${childName})} )
#set( $parentName  = $classObject.getName() )
//----------------------------------------------------------------------------
// adds one or more ${lowercaseRoleName}Ids as a ${roleName} to a ${className}
//----------------------------------------------------------------------------
func Add${roleName}( ${lowercaseClassName}Id uint64, ${lowercaseRoleName}Ids string )(utils.RequestResult) {

	//----------------------------------------------------------------------------
	// Obtain the ${parentName} with the matching identifier
	//----------------------------------------------------------------------------
	parentRequestResult := Get${parentName}(${lowercaseClassName}Id)	
	
	if parentRequestResult.Success == true {
		//----------------------------------------------------------------------------
		// Need to cast the interface to a model.${parentName} so the ORM can figure
		// out which table to deal with
		//----------------------------------------------------------------------------
		${parentName}Obj,_ := parentRequestResult.Data. (model.${parentName})	
				
		// slice the ids on comma with no spaces
		ids := strings.Split( ${lowercaseRoleName}Ids, ",")

		for _, ${lowercaseRoleName}Id:= range ids {
			//----------------------------------------------------------------------------
			// Pass the reference to the ORM to get
			//----------------------------------------------------------------------------
			var ${childName}Obj model.$childName	
	
			//----------------------------------------------------------------------------
			// Retrieve the 1st occurrence from the ORM of a ${childName} 
			// with a matching ${lowercaseRoleName}Id
			//----------------------------------------------------------------------------
			childRequestResult := utils.GetDB().First(&${childName}Obj , ${lowercaseRoleName}Id).Error // find first using identifier
	
			if childRequestResult == nil {
				//----------------------------------------------------------------------------
				// append to the ${roleName} using the gorm mechanism
				//----------------------------------------------------------------------------	
				utils.GetDB().Model(&${parentName}Obj).Association("${roleName}").Append( &${childName}Obj )
				
			} else {
				msg := fmt.Sprintf( "Failed trying to read %s using ID=%v", "${roleName}", ${lowercaseRoleName}Id )
				return utils.RequestResult{false, msg, "unassign${roleName}", ${childName}Obj}
			}
		}
				
		//----------------------------------------------------------------------------      		
		// retrieve the modified ${className} from the gorm
		//----------------------------------------------------------------------------			
		return Get${parentName}(${lowercaseClassName}Id)

	} else {
		return parentRequestResult
	}
}			
	
//----------------------------------------------------------------------------
// removes one or more ${lowercaseRoleName}Ids as a ${roleName} from a ${className}
//----------------------------------------------------------------------------
func Remove${roleName}( ${lowercaseClassName}Id uint64, ${lowercaseRoleName}Ids string )(utils.RequestResult) {
	//----------------------------------------------------------------------------
	// Obtain the ${parentName} with the matching identifier
	//----------------------------------------------------------------------------
	parentRequestResult := Get${parentName}(${lowercaseClassName}Id)	
	
	if parentRequestResult.Success == true {
		//----------------------------------------------------------------------------
		// Need to cast the interface to a model.${parentName} so the ORM can figure
		// out which table to deal with
		//----------------------------------------------------------------------------
		${parentName}Obj,_ := parentRequestResult.Data. (model.${parentName})	
				
		// slice the ids on comma with no spaces
		ids := strings.Split( ${lowercaseRoleName}Ids, ",")

		for _, ${lowercaseRoleName}Id:= range ids {
			//----------------------------------------------------------------------------
			// Pass the reference to the ORM to get
			//----------------------------------------------------------------------------
			var ${childName}Obj model.$childName	
	
			//----------------------------------------------------------------------------
			// Retrieve the 1st occurrence from the ORM of a ${childName} 
			// with a matching ${lowercaseRoleName}Id
			//----------------------------------------------------------------------------
			childRequestResult := utils.GetDB().First(&${childName}Obj , ${lowercaseRoleName}Id).Error // find first using identifier
		
			if childRequestResult == nil {
				//----------------------------------------------------------------------------
				// remove ${childName}Obj from the $roleName array, but wont delete it from db
				//----------------------------------------------------------------------------
				utils.GetDB().Model(&${parentName}Obj).Association("${roleName}").Delete( &${childName}Obj )
				
			} else {
				msg := fmt.Sprintf( "Failed trying to read %s using ID=%v", "${roleName}", ${lowercaseRoleName}Id )
				return utils.RequestResult{false, msg, "remove${roleName}", ${childName}Obj}
			}
		}

		//----------------------------------------------------------------------------      		
		// retrieve the modified ${className} from the gorm
		//----------------------------------------------------------------------------			
		return Get${parentName}(${lowercaseClassName}Id)

	} else {
		return parentRequestResult
	}
}
			
#end##foreach( $multiAssociation in $classObject.getMultipleAssociations() )
