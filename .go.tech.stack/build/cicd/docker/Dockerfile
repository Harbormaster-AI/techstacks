## ------------------------------------------------------------
## Specify the base image needed for ${appName}
## ------------------------------------------------------------
FROM #Go_Lang_Image()

## ------------------------------------------------------------
## Install git to fetch dependencies
## ------------------------------------------------------------
RUN apk update && apk add --no-cache git

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
WORKDIR cd src/${appName}

## ------------------------------------------------------------
## Initialize to create the go.mod and go.sum files
## ------------------------------------------------------------
RUN go mod init ${appName}
RUN go mod tidy

## ------------------------------------------------------------
## Run go build to compile the binary executable of our Go program
## ------------------------------------------------------------
RUN go build

## ------------------------------------------------------------
## Expose port 8080 to the outside world
## ------------------------------------------------------------
EXPOSE ${aib.getParam("db.AppPort")}

## ------------------------------------------------------------
## The start command which kicks off
## a newly created binary executable
## ------------------------------------------------------------
#Command to run the executable
CMD ["./${appName}"]