package model

import (
#declareImports()
    "gorm.io/gorm"
)

#declareEnums()

#set( $classes = $aib.getClassesToGenerate() )
#foreach( $class in $classes )
#declareStructs($class)
#end##foreach( $class in $classes )
