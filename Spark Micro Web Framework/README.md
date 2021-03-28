# realMethods Tech Stack Package Descriptor

## Name
Spark Micro Web Framework

## Summary Image
![alt text](http://www.realmethods.com/infopages/img/spark.micro.png)

## Intent
To create an application that uses:

- Spark Java Micro Web Framework to faciltate the view/controller of an MVC framework
- Fully functional front-end using Javascript, HTML5, JQuery, CSS, and Bootstrap
- Hibernate for object to table db mapping
- GitHub to commit application and project files
- Selected CI platform to build/test/deploy the generate application
- Optional Docker image creation with image push
- Optional deployment to cloud native infrastructure or Kubernetes cluster

## Contents
[https://github.com/realmethods-public/tech.stack.packages/tree/master/Spark%20Micro%20Web%20Framework](https://github.com/realmethods-public/tech.stack.packages/tree/master/Spark%20Micro%20Web%20Framework)


## Usage

The following will automatically build, test, and deploy for each supported CI platform (CircleCI, Cloudbees, etc..).  In the event you need to manually build the application, observe the following steps:

**Jetty**

To build and run the app issue the following from the host command line: 

`mvn clean install`

By default, Spark listens on port 4567, accessible through the browswer via: 

`http://your_host_name_or_ip_address:4567`


## External Doc Link
none

## Package Derived From
None