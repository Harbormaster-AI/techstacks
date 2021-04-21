package model

import (
#declareImports()
)

#declareEnums()

#set( $classes = $aib.getClassesToGenerate() )
#foreach( $class in $classes )
#declareStructs($class)
#end##foreach( $class in $classes )
