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
#foreach( $singleAssociation in $class.getSingleAssociations( ${includeComposites} ) )
#set( $roleName = $singleAssociation.getRoleName() )
#set( $lowercaseRoleName = ${Utils.lowercaseFirstLetter(${roleName})} )
#set( $childName = $singleAssociation.getType() )
#set( $lowercaseChildName = ${Utils.lowercaseFirstLetter(${childName})} )
#set( $parentName  = $class.getName() )
    router.HandleFunc("/api/Assign${roleName}/{parentId}/${lowercaseRoleName}Id", jsonResponseFormatter.FormatToJSON(${parentName}Controller.Assign${roleName})).Methods("PUT", "OPTIONS")
    router.HandleFunc("/api/Unassign${roleName}/{parentId}", jsonResponseFormatter.FormatToJSON(${parentName}Controller.Unassign${roleName})).Methods("DELETE", "OPTIONS")
#end##foreach( $singleAssociation in $class.getSingleAssociations( ${includeComposites} ) )

    //----------------------------------------------------------------------------
    // Multiple Association Routers
    //----------------------------------------------------------------------------    
#foreach( $multiAssociation in $class.getMultipleAssociations() )
#set( $roleName = $multiAssociation.getRoleName() )
#set( $lowercaseRoleName = ${Utils.lowercaseFirstLetter(${roleName})} )
#set( $childName = $multiAssociation.getType() )
#set( $lowercaseChildName = ${Utils.lowercaseFirstLetter(${childName})} )
#set( $parentName  = $class.getName() )
    router.HandleFunc("/api/Add${roleName}/{parentId}/${lowercaseRoleName}Id", jsonResponseFormatter.FormatToJSON(${parentName}Controller.Add${roleName})).Methods("PUT", "OPTIONS")
    router.HandleFunc("/api/Remove${roleName}/{parentId}/${lowercaseRoleName}Ids", jsonResponseFormatter.FormatToJSON(${parentName}Controller.Remove${roleName})).Methods("DELETE", "OPTIONS")
#end##foreach( $multiAssociation in $class.getMultipleAssociations() )    
#end##foreach( $class in $classes )
    return router
}