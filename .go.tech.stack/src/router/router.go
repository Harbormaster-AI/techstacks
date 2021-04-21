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
    
    router.HandleFunc("/api/${className}/{id}", jsonResponseFormatter.FormatToJSON(${className}Controller.Get${className})).Methods("GET", "OPTIONS")
    router.HandleFunc("/api/${className}", jsonResponseFormatter.FormatToJSON(${className}Controller.GetAll${className})).Methods("GET", "OPTIONS")
    router.HandleFunc("/api/new${className}", jsonResponseFormatter.FormatToJSON(${className}Controller.Create${className})).Methods("POST", "OPTIONS")
    router.HandleFunc("/api/${className}/{id}", jsonResponseFormatter.FormatToJSON(${className}Controller.Update${className})).Methods("PUT", "OPTIONS")
    router.HandleFunc("/api/delete${className}/{id}", jsonResponseFormatter.FormatToJSON(${className}Controller.Delete${className})).Methods("DELETE", "OPTIONS")
#end ##foreach( $class in $classes )
    return router
}