#if( ${containerObject} )
#set( $appName = "Container-${containerObject.getName()}" )
#else
#set( $appName = $aib.getApplicationNameFormatted() )
#end
FROM node:10.18
# this allows for updates to take place without failure
RUN printf "deb http://archive.debian.org/debian/ jessie main\ndeb-src http://archive.debian.org/debian/ jessie main\ndeb http://security.debian.org jessie/updates main\ndeb-src http://security.debian.org jessie/updates main" > /etc/apt/sources.list

# install curl, dos2unix, sed, and nano
RUN apt-get update --fix-missing && \
apt-get install -y dos2unix && \
apt-get install -y sed && \
apt-get install -y nano

# copy all files to a working directory
ARG PROJECT=${appName}
ARG PROJECT_DIR=/var/www/${PROJECT}
RUN mkdir -p $PROJECT_DIR
COPY entrypoint.sh /var/www/
COPY ${CODEBUILD_SRC_DIR}/appRoot/ /var/www/

# list for verification
RUN ls /var/www
RUN ls /var/www/${appName}
RUN ls /var/www/${appName}/src

# install Angular-CLI and a core dependency
RUN cd /var/www/${appName}
RUN npm install @angular-devkit/build-angular@0.803.19
RUN npm install -g @angular/cli@8.3.21 > /dev/null

# prepare entrypoint
RUN dos2unix /var/www/entrypoint.sh
RUN chmod +x /var/www/entrypoint.sh
ENTRYPOINT ["/var/www/entrypoint.sh"]
