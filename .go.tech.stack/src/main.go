#set( $appName = $aib.getApplicationNameFormatted() )


package main

import (
    "fmt"
    "os"
    "${appName}/api/router"
    "log"
    "net/http"
    "github.com/joho/godotenv"
)


func main() {

    // load .env file
    err := godotenv.Load()

    if err != nil {
        log.Fatalf("Error loading .env file")
    }
  
    appRouter := router.Router()
    appPort := fmt.Sprintf(":%s", os.Getenv("APP_PORT") )
    fmt.Println("Starting server on the port ", appPort)

    log.Fatal(http.ListenAndServe(appPort, appRouter))
}
