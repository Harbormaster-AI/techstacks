#set( $dbType = $aib.getParam('go.dbType') )
package utils

import (
    "github.com/joho/godotenv"
	"gorm.io/gorm"
  	"gorm.io/driver/${dbType}"
	"encoding/json"
	"io/ioutil"
	"net/http"
	"os"
	"log"
	"fmt"
	"strconv"
)

//----------------------------------------------------------------------------
// global level var declarations
//----------------------------------------------------------------------------
var db *gorm.DB

type RequestResult struct {
    Success  	bool
	Msg 		string
	Call 		string
	Data 		interface{}
}

//----------------------------------------------------------------------------
// function initialization
//----------------------------------------------------------------------------
func init() {

    // load .env file
    err := godotenv.Load()

    if err != nil {
        log.Fatalf("Error loading .env file")
    }
    
	//----------------------------------------------------------------------------
	// Open the ${dbType} database and initialize the ORM
	//----------------------------------------------------------------------------

    var dsn string
    switch dbVersion := os.Getenv("DB_VERSION"); dbVersion {
	    case "postgres":
		    // example: host=localhost user=gorm password=gorm dbname=gorm port=9920 sslmode=disable TimeZone=Asia/Shanghai
		    dsn = fmt.Sprintf( "host=%s user=%s password=%s dbname=%s port=%s %s", 
						os.Getenv("DB_HOST"), 
						os.Getenv("DB_USER_NAME"), 
						os.Getenv("DB_PASSWORD"),						
						os.Getenv("DB_NAME"), 
						os.Getenv("DB_ARGS") )
	    case "sqlite" :
		    dsn = fmt.Sprintf( "%s %s", os.Getenv("DB_NAME"), os.Getenv("DB_ARGS") )

	    case "sqlserver" :
		    // example: sqlserver://gorm:LoremIpsum86@localhost:9930?database=gorm
           dsn = fmt.Sprintf( "sqlserver://%s:%s@%s:%s?database=%s&%s", 
    						os.Getenv("DB_USER_NAME"), 
    						os.Getenv("DB_PASSWORD"), 
    						os.Getenv("DB_HOST"), 
    						os.Getenv("DB_PORT"), 
    						os.Getenv("DB_NAME"), 
    						os.Getenv("DB_ARGS") )
	    default:
		    // default to mysql
		    // example: gorm:gorm@tcp(localhost:9910)/gorm?charset=utf8&parseTime=True&loc=Local
            dsn = fmt.Sprintf( "%s:%s@tcp(%s:%s)/%s?%s", 
        						os.Getenv("DB_USER_NAME"), 
        						os.Getenv("DB_PASSWORD"), 
        						os.Getenv("DB_HOST"), 
        						os.Getenv("DB_PORT"), 
        						os.Getenv("DB_NAME"), 
        						os.Getenv("DB_ARGS") )
	}
	
	disableFKConstraint,_ := strconv.ParseBool(os.Getenv("DB_DISABLE_FK_CONSTRAINTS"));
	
	tmpDB, err := gorm.Open(${dbType}.Open(dsn),&gorm.Config{
  							DisableForeignKeyConstraintWhenMigrating: disableFKConstraint,
					})

  	if err != nil {
    	panic("failed to connect database")
  	}

    //----------------------------------------------------------------------------  	
  	// assign tmpDB for global usage
  	//----------------------------------------------------------------------------
  	
  	db = tmpDB

}

//----------------------------------------------------------------------------
// Returns the database instance
//----------------------------------------------------------------------------
func GetDB() *gorm.DB {
	return db
}

//----------------------------------------------------------------------------
// Parses the body of the HTTP Request and unmarshal it via JSON into the
// provided interface
//----------------------------------------------------------------------------
func ParseBody(r *http.Request, x interface{}) {
	if body, err := ioutil.ReadAll(r.Body); err == nil {
		if err := json.Unmarshal([]byte(body), x); err != nil {
			return
		}
	}
}