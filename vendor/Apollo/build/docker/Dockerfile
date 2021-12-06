#if( ${containerObject} )
#set( $appName = "Container-${containerObject.getName()}" )
#else
#set( $appName = $aib.getApplicationNameFormatted() )
#end
FROM node:#NODE_JS_Version()
# -----------------------------------------------
# install a few essentials
# -----------------------------------------------
RUN apt-get update --fix-missing && \
apt-get install -y dos2unix && \
apt-get install -y sed && \
apt-get install -y nano

# -----------------------------------------------
# copy all files 
# -----------------------------------------------
ADD server/${appName}/ .
ADD entrypoint.sh .

# -----------------------------------------------
# list for verification
# -----------------------------------------------
RUN ls 

# -----------------------------------------------
# Install NPM modules
# -----------------------------------------------
RUN npm install --${BUILD_ENV} > /dev/null

# -----------------------------------------------
# expose port
# -----------------------------------------------
EXPOSE 4000

# -----------------------------------------------
# prepare entrypoint
# -----------------------------------------------
RUN dos2unix entrypoint.sh
RUN chmod +x entrypoint.sh
ENTRYPOINT ["./entrypoint.sh"]
