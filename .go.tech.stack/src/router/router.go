#set( $classes = $aib.getClassesToGenerate() )
package router

import (
#foreach( $class in $classes )
#set( $className = $class.getName() )
    ${className}Controller "${appName}/api/controller"
#end##foreach( $class in $classes )
    jsonResponseFormatter "${appName}/api/response"
    "github.com/gorilla/mux"
    
)

// Router is exported and used in main.go
func Router() *mux.Router {

    router := mux.NewRouter()    

#foreach( $class in $classes )
#set( $className = $class.getName() )

	//----------------------------------------------------------------------------
    // $className Routes to JSON response formatter first 
    // then to the correct Controller function
	//----------------------------------------------------------------------------

	//----------------------------------------------------------------------------
	// Standard CRUD Routers
	//----------------------------------------------------------------------------    
    router.HandleFunc("/api/${className}/{id}", jsonResponseFormatter.FormatToJSON(${className}Controller.Get${className})).Methods("GET", "OPTIONS")
    router.HandleFunc("/api/${className}", jsonResponseFormatter.FormatToJSON(${className}Controller.GetAll${className})).Methods("GET", "OPTIONS")
    router.HandleFunc("/api/New${className}", jsonResponseFormatter.FormatToJSON(${className}Controller.Create${className})).Methods("POST", "OPTIONS")
    router.HandleFunc("/api/${className}/{id}", jsonResponseFormatter.FormatToJSON(${className}Controller.Update${className})).Methods("PUT", "OPTIONS")
    router.HandleFunc("/api/Delete${className}/{id}", jsonResponseFormatter.FormatToJSON(${className}Controller.Delete${className})).Methods("DELETE", "OPTIONS")

	//----------------------------------------------------------------------------
	// Single Association Routers
	//----------------------------------------------------------------------------    
#set( $includeComposites = false )
#foreach( $singleAssociation in $classObject.getSingleAssociations( ${includeComposites} ) )
#set( $roleName = $singleAssociation.getRoleName() )
#set( $lowercaseRoleName = ${Utils.lowercaseFirstLetter(${roleName})} )
#set( $childName = $singleAssociation.getType() )
#set( $lowercaseChildName = ${Utils.lowercaseFirstLetter(${childName})} )
#set( $parentName  = $classObject.getName() )
    router.HandleFunc("/api/Assign${roleName}/{parentId}/${lowercaseRoleName}Id", jsonResponseFormatter.FormatToJSON(${className}Controller.Assign${className})).Methods("DELETE", "OPTIONS")
    router.HandleFunc("/api/Unassign${className}/{parentId}", jsonResponseFormatter.FormatToJSON(${className}Controller.Delete${className})).Unassign("DELETE", "OPTIONS")
#end

	//----------------------------------------------------------------------------
	// Multiple Association Routers
	//----------------------------------------------------------------------------    
#foreach( $multiAssociation in $classObject.getMultipleAssociations() )
#set( $roleName = $multiAssociation.getRoleName() )
#set( $lowercaseRoleName = ${Utils.lowercaseFirstLetter(${roleName})} )
#set( $childName = $multiAssociation.getType() )
#set( $lowercaseChildName = ${Utils.lowercaseFirstLetter(${childName})} )
#set( $parentName  = $classObject.getName() )
    router.HandleFunc("/api/Add${roleName}/{parentId}/${lowercaseRoleName}Id", jsonResponseFormatter.FormatToJSON(${className}Controller.Add${className})).Methods("DELETE", "OPTIONS")
    router.HandleFunc("/api/Remove${className}/{parentId}/${lowercaseRoleName}Ids", jsonResponseFormatter.FormatToJSON(${className}Controller.Delete${className})).Remove("DELETE", "OPTIONS")    
#end ##foreach( $class in $classes )
    return router
}