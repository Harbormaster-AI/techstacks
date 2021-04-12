## Specify the base image needed for ${appName}
## go application
FROM #Go_Lang_Image()


## Create an /app directory within the
## image to hold the application source
## files
RUN mkdir /app

## Copy everything in the root directory
## into the /app directory
ADD . /app

## Specify to execute 
## any further commands inside the /app
## directory
WORKDIR /app

## Run go build to compile the binary
## executable of our Go program
RUN go build -o main .

# Expose port 8080 to the outside world
EXPOSE 8080

## The start command which kicks off
## a newly created binary executable
CMD ["/app/main"]