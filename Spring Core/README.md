# realMethods Tech Stack Package Descriptor

## Name
Spring Core

## Summary Image
![alt text](http://www.realmethods.com/infopages/img/spring.rdbms.png)

## Intent
To create an application that uses:

- Spring-Boot Plugin to help facilitate a MVC framework
- Hibernate for model to table db mapping
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

`mvn spring-boot:run`

## External Doc Link
None

## Package Derived From
None