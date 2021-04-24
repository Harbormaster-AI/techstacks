#set( $appName = $aib.getApplicationNameFormatted() )
## ------------------------------------------------------------
## Specify the base image needed for ${appName}
## ------------------------------------------------------------
FROM #Go_Lang_Image()

## ------------------------------------------------------------
## Install git to fetch dependencies
## ------------------------------------------------------------
RUN apt-get update --fix-missing && \
apt-get install -y git

## ------------------------------------------------------------
## Install dependencies
## ------------------------------------------------------------
RUN go get github.com/gorilla/mux
RUN go get -u gorm.io/gorm
RUN go get github.com/joho/godotenv
#set( $dbType = ${aib.getParam( "go.dbType" )} )     
RUN go get -u gorm.io/driver/${dbType}

## ------------------------------------------------------------
## Copy the current directory to the working Directory inside the container
## ------------------------------------------------------------ 
COPY . .

## ------------------------------------------------------------
## Change the working directory to src/${appName}
## ------------------------------------------------------------
RUN cd src/${appName}

## ------------------------------------------------------------
## Initialize to create the go.mod and go.sum files
## Already handled on the CI/CD platform
## Uncomment if required within the image
## ------------------------------------------------------------
# RUN go mod init ${appName}
# RUN go mod tidy

WORKDIR src/${appName}

## ------------------------------------------------------------
## Run go build to compile the binary executable of our Go program
## ------------------------------------------------------------
RUN go build main.go

## ------------------------------------------------------------
## Expose port 8080 to the outside world
## ------------------------------------------------------------
EXPOSE ${aib.getParam("go.appPort")}

## ------------------------------------------------------------
## The start command which kicks off
## a newly created binary executable
## ------------------------------------------------------------
#Command to run the executable
CMD ["./${appName}"]