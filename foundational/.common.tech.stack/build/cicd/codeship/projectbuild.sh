#if( ${containerObject} )
#set( $appName = "Container-${containerObject.getName()}" )
#else
#set( $appName = $aib.getApplicationNameFormatted() )
#end
#!/bin/bash
#Codeship_Build()
cp -r -n /gitRoot/ /code/