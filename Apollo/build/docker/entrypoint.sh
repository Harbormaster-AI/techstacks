#if( ${containerObject} )
#set( $appName = "Container-${containerObject.getName()}" )
#else
#set( $appName = $aib.getApplicationNameFormatted() )
#end
#!/bin/bash

# This allows us to dynamically inject the host location of mongoose 
# so all client calls are back to thi server and not localhost which is the default behavior
sed -i "s/localhost/$MONGOOSE_HOST_NAME/" /var/www/${appName}/src/app/services/base.service.ts

# run mongoose in background
cd /var/www/${appName}/
ls
nohup node server.js &

# run ng server
#ng serve  --host 0.0.0.0 --port 8080

# This is a developer only configuration.  
# Add the URL to the base.service.ts file directly or put it in an environment configuration
# Use the dist directory contents to execute the application in a non-dev environment
node --max_old_space_size=4098 node_modules/@angular/cli/bin/ng serve --host 0.0.0.0 --port 8080 --public-host $MONGOOSE_HOST_NAME --disable-host-check
