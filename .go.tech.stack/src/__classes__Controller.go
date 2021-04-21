#set( $className = "${classObject.getName()}" )
package controller

import (
    ${className}DAO "${appName}/api/dao"
    "${appName}/api/model"
    "${appName}/api/utils"
	"encoding/json"
	"fmt"
	"github.com/gorilla/mux"
	"net/http"
	"strconv"
)

//----------------------------------------------------------------------------
// Create controller, delegates to ${className}DAO for database creation
//----------------------------------------------------------------------------
func Create${className}(w http.ResponseWriter, r *http.Request) {
	//----------------------------------------------------------------------------
	// Initialize an empty ${className} model
	//----------------------------------------------------------------------------
	data := model.${className}{}
	
	//----------------------------------------------------------------------------
	// Parse the body into a ${className} model structure
	//----------------------------------------------------------------------------
	utils.ParseBody(r, data)

	//----------------------------------------------------------------------------
	// Delegate to the ${className} data access object to create
	//----------------------------------------------------------------------------
	requestResult := ${className}DAO.Create${className}( data )
	
	//----------------------------------------------------------------------------
	// Marshal the model into a JSON object
	//----------------------------------------------------------------------------
	res,_ := json.Marshal(requestResult)

	w.WriteHeader(http.StatusOK)
	w.Write(res)
}

//----------------------------------------------------------------------------
// Get controller, delegates to ${className}DAO to find the relevant ${className}
//----------------------------------------------------------------------------
func Get${className}(w http.ResponseWriter, r *http.Request) {
	//----------------------------------------------------------------------------
	// Retrieve the parameter from the request using hte mux
	//----------------------------------------------------------------------------
	vars := mux.Vars(r)
	
	//----------------------------------------------------------------------------
	// Locate the value for the ID key
	//----------------------------------------------------------------------------	
	id := vars["id"]
	
	//----------------------------------------------------------------------------
	// Parse the value into an integer if provided as such
	//----------------------------------------------------------------------------	
	ID, err:= strconv.ParseUint(id, 10, 64)
	if err != nil {
		fmt.Println("Error while parsing")
	}
	
	//----------------------------------------------------------------------------
	// Delegate to the ${className} data access object
	// find the one with the matching identifier
	//----------------------------------------------------------------------------
	requestResult := ${className}DAO.Get${className}(ID)
	
	//----------------------------------------------------------------------------
	// Marshal the model into a JSON object
	//----------------------------------------------------------------------------
	res,_ := json.Marshal(requestResult)

	w.WriteHeader(http.StatusOK)
	w.Write(res)
}


//----------------------------------------------------------------------------
// GetAll controller, delegates to ${className}DAO for database read of all ${className}s
//----------------------------------------------------------------------------
func GetAll${className}(w http.ResponseWriter, r *http.Request) {
	//----------------------------------------------------------------------------
	// Delegate to the ${className} data access object to get all
	//----------------------------------------------------------------------------
	requestResult := ${className}DAO.GetAll${className}()
	
	//----------------------------------------------------------------------------
	// Marshal the model into a JSON object
	//----------------------------------------------------------------------------
	res,_ := json.Marshal(requestResult)

	w.WriteHeader(http.StatusOK)
	w.Write(res)
}

//----------------------------------------------------------------------------
// Update controller, delegates to ${className}DAO for database save
//----------------------------------------------------------------------------
func Update${className}(w http.ResponseWriter, r *http.Request) {
	//----------------------------------------------------------------------------
	// Initialize an empty ${className} model
	//----------------------------------------------------------------------------
	var data = model.${className}{}
	
	//----------------------------------------------------------------------------
	// Parse the body into a ${className} model structure
	//----------------------------------------------------------------------------
	utils.ParseBody(r, data)

	//----------------------------------------------------------------------------
	// Delegate to the ${className} data access object
	// update the one with the matching identifier
	//----------------------------------------------------------------------------
	requestResult := ${className}DAO.Update${className}(data)

	//----------------------------------------------------------------------------
	// Marshal the model into a JSON object
	//----------------------------------------------------------------------------
	res, _ := json.Marshal(requestResult)
	w.WriteHeader(http.StatusOK)
	w.Write(res)
}

//----------------------------------------------------------------------------
// Delete controller, delegates to ${className}DAO for database deletion
//----------------------------------------------------------------------------
func Delete${className}(w http.ResponseWriter, r *http.Request) {
	//----------------------------------------------------------------------------
	// Retrieve the parameter from the request using hte mux
	//----------------------------------------------------------------------------
	vars := mux.Vars(r)
	
	//----------------------------------------------------------------------------
	// Locate the value for the ID key
	//----------------------------------------------------------------------------	
	id := vars["id"]

	//----------------------------------------------------------------------------
	// Parse the value into an integer if provided as such
	//----------------------------------------------------------------------------	
	ID, err:= strconv.ParseUint(id, 10, 64)
	if err != nil {
		fmt.Println("Error while parsing")
	}

	//----------------------------------------------------------------------------
	// Delegate to the ${className} data access object
	// delete the one with the matching identifier
	//----------------------------------------------------------------------------	
	requestResult := ${className}DAO.Delete${className}(ID)

	//----------------------------------------------------------------------------
	// Marshal the model into a JSON object
	//----------------------------------------------------------------------------
	res, _ := json.Marshal(requestResult)
	
	w.WriteHeader(http.StatusOK)
	w.Write(res)
}

#set( $includeComposites = false )
#foreach( $singleAssociation in $classObject.getSingleAssociations( ${includeComposites} ) )
#set( $roleName = $singleAssociation.getRoleName() )
#set( $lowercaseRoleName = ${Utils.lowercaseFirstLetter(${roleName})} )
#set( $childName = $singleAssociation.getType() )
#set( $lowercaseChildName = ${Utils.lowercaseFirstLetter(${childName})} )
#set( $parentName  = $classObject.getName() )
	//----------------------------------------------------------------------------
	// assigns a ${roleName} on a ${parentName}
	// delegates to an ORM handler
	///----------------------------------------------------------------------------
func Assign${roleName}(w http.ResponseWriter, r *http.Request) {

	vars := mux.Vars(r)

	//----------------------------------------------------------------------------		
	// Retrieve the id params
	//----------------------------------------------------------------------------		
	${lowercaseClassName}Id,_ := strconv.ParseUint( vars["id"], 10, 64)
	${lowercaseRoleName}Id,_ := strconv.ParseUint( vars["${lowercaseRoleName}Id"], 10, 64)

	//----------------------------------------------------------------------------		
	// Delegate to the ${parentName} DAO
	//----------------------------------------------------------------------------		
	requestResult := ${parentName}DAO.Assign${roleName}(${lowercaseClassName}Id, ${lowercaseRoleName}Id)

	//----------------------------------------------------------------------------
	// Marshal the model into a JSON object
	//----------------------------------------------------------------------------
	res, _ := json.Marshal(requestResult)
	w.WriteHeader(http.StatusOK)
	w.Write(res)
}

	//----------------------------------------------------------------------------
	// unassigns a ${roleName} on a ${parentName}
	// delegates to the ORM handler
	//----------------------------------------------------------------------------			
func Unassign${roleName}( w http.ResponseWriter, r *http.Request ) {

	vars := mux.Vars(r)

	//----------------------------------------------------------------------------		
	// Retrieve the id params
	//----------------------------------------------------------------------------		
	${lowercaseClassName}Id,_ := strconv.ParseUint( vars["id"], 10, 64)

	//----------------------------------------------------------------------------		
	// Delegate to the ${parentName} DAO
	//----------------------------------------------------------------------------		
	requestResult := ${parentName}DAO.Unassign${roleName}(${lowercaseClassName}Id)

	//----------------------------------------------------------------------------
	// Marshal the model into a JSON object
	//----------------------------------------------------------------------------
	res, _ := json.Marshal(requestResult)
	w.WriteHeader(http.StatusOK)
	w.Write(res)

}
	
#end##foreach( $singleAssociation in $classObject.getSingleAssociations( ${includeComposites} ) )

#foreach( $multiAssociation in $classObject.getMultipleAssociations() )
#set( $roleName = $multiAssociation.getRoleName() )
#set( $lowercaseRoleName = ${Utils.lowercaseFirstLetter(${roleName})} )
#set( $childName = $multiAssociation.getType() )
#set( $lowercaseChildName = ${Utils.lowercaseFirstLetter(${childName})} )
#set( $parentName  = $classObject.getName() )
	//----------------------------------------------------------------------------
	// adds one or more ${lowercaseRoleName}Ids as a ${roleName} to a ${parentName}
	//----------------------------------------------------------------------------
func Add${roleName}(w http.ResponseWriter, r *http.Request)  {

	vars := mux.Vars(r)

	//----------------------------------------------------------------------------		
	// Retrieve the id and child ids
	//----------------------------------------------------------------------------		
	${lowercaseClassName}Id,_ := strconv.ParseUint( vars["id"], 10, 64)
	${lowercaseRoleName}Ids,_ := vars["${lowercaseRoleName}Ids"]

	//----------------------------------------------------------------------------		
	// Delegate to the ${parentName} DAO
	//----------------------------------------------------------------------------		
	requestResult := ${parentName}DAO.Add${roleName}(${lowercaseClassName}Id, ${lowercaseRoleName}Ids)

	//----------------------------------------------------------------------------
	// Marshal the model into a JSON object
	//----------------------------------------------------------------------------
	res, _ := json.Marshal(requestResult)
	w.WriteHeader(http.StatusOK)
	w.Write(res)
}			
	
	//----------------------------------------------------------------------------
	// removes one or more ${lowercaseRoleName}Ids as a ${roleName} from a ${parentName}
	// delegates via URI to an ORM handler
	//----------------------------------------------------------------------------						
func Remove${roleName}(w http.ResponseWriter, r *http.Request)  {

	vars := mux.Vars(r)

	//----------------------------------------------------------------------------		
	// Retrieve the id and child ids
	//----------------------------------------------------------------------------		
	${lowercaseClassName}Id,_ := strconv.ParseUint( vars["id"], 10, 64)
	${lowercaseRoleName}Ids,_ := vars["${lowercaseRoleName}Ids"]

	//----------------------------------------------------------------------------		
	// Delegate to the ${parentName} DAO
	//----------------------------------------------------------------------------		
	requestResult := ${parentName}DAO.Remove${roleName}(${lowercaseClassName}Id, ${lowercaseRoleName}Ids)

	//----------------------------------------------------------------------------
	// Marshal the model into a JSON object
	//----------------------------------------------------------------------------
	res, _ := json.Marshal(requestResult)
	w.WriteHeader(http.StatusOK)
	w.Write(res)	
}
		
#end##foreach( $multiAssociation in $classObject.getMultipleAssociations() )