#set( $appName = $aib.getApplicationNameFormatted() )
#set( $roles = "" )
#foreach( $type in $types )
#set( $roles = "${roles}${type}," )
#end##foreach( $type in $types )
package test

import ( 
	"testing"
    dao "${appName}/api/dao"
	"${appName}/api/model"
	"${appName}/api/utils"
	"github.com/google/go-cmp/cmp"
	"fmt"
#declareImports()	
)

func init() {
	utils.InitializeEnvironment()
}

#set( $classes = $aib.getClassesToGenerate() )    
#foreach( $class in $classes )  
#set( $className = $class.getName() )
#set( $types = $class.getAssociationTypes() )

func Test${className}CRUD(t *testing.T) {

	//----------------------------------------------------------------------------
	// Test CRUD for $className
	//----------------------------------------------------------------------------
	${className}Obj := model.${className}#defaultTestStructOutput(${class})

	// --------------------------------------------------------------
	// Check Create
	// --------------------------------------------------------------
	create${className}RequestResult := dao.Create${className}( ${className}Obj )
	
	if create${className}RequestResult.Success == false {
		t.Errorf(create${className}RequestResult.Msg)
	} else {
		fmt.Println("Check Create ${className} success...")
	}
	
	create${className}Obj,_ := create${className}RequestResult.Data. (model.${className})

	// --------------------------------------------------------------
	// Check ${className} Obj ID
	// --------------------------------------------------------------	
	if create${className}Obj.ID == 0 {
	    t.Errorf( "The ORM failed to assign and ID for ${className}" )
	}	

	// --------------------------------------------------------------
	// Check Get
	// --------------------------------------------------------------	
	get${className}RequestResult := dao.Get${className}( uint64(create${className}Obj.ID) )
	
	if get${className}RequestResult.Success == false {
		t.Errorf(get${className}RequestResult.Msg)
	} else {
		fmt.Println("Check Get ${className} success...")
	}
	
	// --------------------------------------------------------------
	// Check returned struct from Get equals original created obj
	// --------------------------------------------------------------	
	get${className}Obj,_ := get${className}RequestResult.Data. (model.${className})
	compare${className} := cmp.Equal(create${className}Obj.ID, get${className}Obj.ID)
	
	if  compare${className} == false	{
		t.Errorf( "Created ${className} object is not equal to read object." )
	}
	
	// --------------------------------------------------------------
	// Check GetAll
	// --------------------------------------------------------------	
	getAll${className}RequestResult := dao.GetAll${className}()

	if getAll${className}RequestResult.Success == false {
			t.Errorf(getAll${className}RequestResult.Msg)
	} else {
		fmt.Println("Check GetAll ${className} success...")
	}
	
	// --------------------------------------------------------------
	// Check GetAll returns an array with zero index equal 
	// to initially created object
	// --------------------------------------------------------------		
	var getAll${className}Obj []model.${className} = getAll${className}RequestResult.Data. ([]model.${className})
		
	equal${className} := cmp.Equal(create${className}Obj.ID, getAll${className}Obj[len(getAll${className}Obj)-1].ID)
		
	if equal${className} == false {
		t.Errorf( "Created object is not equal to the last entry in ${className}[] returned by GetAll" )
    }
    
	// --------------------------------------------------------------
	// Check deletion for ${className}
	// --------------------------------------------------------------	
	delete${className}RequestResult := dao.Delete${className}(uint64(create${className}Obj.ID))

	if delete${className}RequestResult.Success == false {
			t.Errorf(delete${className}RequestResult.Msg)
	} else {
		fmt.Println("Check Deletion ${className} success...")
	}


	// --------------------------------------------------------------
	// Check deletion causes Get to fail
	// --------------------------------------------------------------		
	get${className}RequestResult = dao.Get${className}( uint64(create${className}Obj.ID) )
	
	if get${className}RequestResult.Success == true {
		t.Errorf(get${className}RequestResult.Msg)
	} else {
		fmt.Println("Validate deletion success...")
	}	
	
}

#end##foreach( $class in $classes )
