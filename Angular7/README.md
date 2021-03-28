# realMethods Tech Stack Package Descriptor

## Name
Angular7 

## Summary Image
![alt text](http://www.realmethods.com/infopages/img/angular.png)

## Intent
To create an application that uses:

- Angular7 with an MVC design pattern
- Fully functional front-end with a mix of Material Design, HTML5, JQuery, Bootstrap, Javascript and Typescript
- MongoDB for document based storage using Mongoose as an ORM front to MongoDB
- A Git repository to commit all project files
- Selected CI platform to build/test/deploy the generate application
- Optional Docker image creation with image push
- Optional deployment to cloud native infrastructure or Kubernetes cluster

## Contents
[https://github.com/realmethods-public/tech.stack.packages/tree/master/Angular7](https://github.com/realmethods-public/tech.stack.packages/tree/master/Angular7)


## Usage

The following will automatically build, test, and deploy for each supported CI platform (CircleCI, Cloudbees, etc..).  In the event you need to manually build the application, observe the following steps:

**To get started**

The following instructions assume NPM is installed. If not, you can download it by installing Node.js at https://nodejs.org/en/download/.
Install the Angular CLI
`npm install -g @angular/cli`
Create a new project by the name used to generate the application. Note: It is important to use this name because the next step expects this name.

`ng new ${aib.getApplicationName()}`

**Copying generated files**

Either pull the generated files from your Git repository, or download the generated application archive file and unzip into the root directory of the application.

**Development server**

Run 

`ng serve for a dev server` 

Navigate to _http://localhost:4200/_. The app will automatically reload if you change any of the source files.

**Build**

Run 

`ng build` 

to build the project. The build artifacts will be stored in the dist/ directory. Use the --prod flag for a production build.

**MongoDB setup**

It is assumed MongoDB is running at the specified host url you provided as part of the application options. See file ./config/mongoDb.js to make changes to the database location

**Running unit tests**

Run 

`ng test` 

to execute the unit tests via Karma.

**Running end-to-end tests**

`Run ng e2e` 

to execute the end-to-end tests via Protractor.

## External Doc Link
[http://www.realmethods.com/infopages/angularjs-mongodb-info-page.html](http://www.realmethods.com/infopages/angularjs-mongodb-info-page.html)

## Package Derived From
None