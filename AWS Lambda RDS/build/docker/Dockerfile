#propertiesHeader()
#if( ${containerObject} )
#set( $appName = "Container-${containerObject.getName()}" )
#else
#set( $appName = $aib.getApplicationNameFormatted() )
#end
FROM adoptopenjdk/openjdk8

######################################################
# installing common packages
######################################################
RUN apt-get update --fix-missing && \
apt-get install -y dos2unix && \
apt-get install -y nano


######################################################
##  session prep
######################################################
ARG PROJECT=${appName}
ARG PROJECT_DIR=/var/www/${PROJECT}
RUN mkdir -p $PROJECT_DIR
COPY /target/${appName}*.jar $PROJECT_DIR/
COPY entrypoint.sh $PROJECT_DIR/
RUN cd $PROJECT_DIR
WORKDIR $PROJECT_DIR

RUN dos2unix /var/www/${appName}/entrypoint.sh
RUN chmod +x /var/www/${appName}/entrypoint.sh
ENTRYPOINT ["/var/www/${appName}/entrypoint.sh"]
