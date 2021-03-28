#if( ${containerObject} )
#set( $appName = "Container-${containerObject.getName()}" )
#else
#set( $appName = $aib.getApplicationNameFormatted() )
#end
FROM tomcat:9.0

######################################################
##  final session prep
######################################################
COPY /target/*.war /usr/local/tomcat/webapps

# make the app war the root war so all default requests are directed to it
RUN mv /usr/local/tomcat/webapps/${appName}-${aib.getVersion()}.war /usr/local/tomcat/webapps/ROOT.war
RUN mv /usr/local/tomcat/webapps/ROOT /usr/local/tomcat/webapps/ROOT_OLD

# run tomcat
CMD ["/usr/local/tomcat/bin/catalina.sh", "run"]
