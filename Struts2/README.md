# realMethods Tech Stack Package Descriptor

## Name
Struts2 MVC Web Framework

## Summary Image
![alt text](http://www.realmethods.com/infopages/img/struts.rdbms.png)

## Intent
To create an application that uses:

- Struts2 MVC Framework
- Fully functional front-end using JQuery/Boostrap Javascript
- Hibernate ORM for object to relational DB mapping for persistence
- Selected CI platform to build/test/deploy the generate application
- Optional Docker image creation with image push
- Optional deployment to cloud native infrastructure or Kubernetes cluster

## Contents
[https://github.com/realmethods-public/tech.stack.packages/tree/master/Struts2](https://github.com/realmethods-public/tech.stack.packages/tree/master/Struts2)


## Usage

The following will automatically build, test, and deploy for each supported CI platform (CircleCI, Cloudbees, etc..).  In the event you need to manually build the application, observe the following steps:

**Jetty**

To quickly build and execute the application, issue the following from the app root directory- 

`mvn package jetty:run`

Running this way, the application assumes MySQL is running on the localhost @ port 3306.

## External Doc Link
None

## Package Derived From
None