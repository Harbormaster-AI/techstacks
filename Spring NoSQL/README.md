# realMethods Tech Stack Package Descriptor

## Name
Spring Core using MongoDB

## Summary Image
![alt text](http://www.realmethods.com/infopages/img/spring.rdbms.png)

## Intent
To create a headless (no UI) application that uses:

- Spring Framework for Restless Web services
- Spring-Boot for the app deployment and container
- Spy Memcached for POJO caching
- MongoDB for NoSQL Document level persistence
- JUnit for testing CRUD capabilities
- A Git repository to commit all application and project files
- Selected CI platform to build/test/deploy the generate application
- Optional Docker image creation with image push
- Optional deployment to cloud native infrastructure or Kubernetes cluster

## Contents
[https://github.com/realmethods-public/tech.stack.packages/tree/master/Spring%20Core](https://github.com/realmethods-public/tech.stack.packages/tree/master/Spring%20Core)


## Usage

The following will automatically build, test, and deploy for each supported CI platform (CircleCI, Cloudbees, etc..).  In the event you need to manually build the application, observe the following steps:

**Jetty**

To quickly execute the application, issue the following from the app root"

Uses Morphia as the Object to Document mapping framework to simplify persisting POJOs.
Run a created app in the Maven Jetty container using the following command: mvn spring-boot:run

`mvn spring-boot:run`

**Special Notes**

- Uses the MongoDB Java Driver to interact with MongoDB as a Java client.
- Uses Morphia as the Object to Document mapping framework to simplify persisting POJOs.
- Run a created app in the Maven Jetty container using the following command: mvn spring-boot:run
- To deploy a created app to a different web server, build its WAR file using the following command:
`mvn clean install`

## External Doc Link
None

## Package Derived From
None