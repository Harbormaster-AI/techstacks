# realMethods Tech Stack Package Descriptor

## Name
Apollo GraphQL

## Summary Image
![alt text](http://www.realmethods.com/infopages/img/apollo.png)

## Intent
To create an application that uses:

- GraphQL implementation using Apollo GraphQL Server
- Default persistence use SQLite with Sequelize for object to relational mapping
- A Git repository to commit application and project files
- Selected CI platform to build/test/deploy the generate application
- Optional Docker image creation with image push
- Optional deployment to cloud native infrastructure or Kubernetes cluster

## Contents
[https://github.com/realmethods-public/tech.stack.packages/tree/master/Apollo](https://github.com/realmethods-public/tech.stack.packages/tree/master/Apollo)


## Usage

**Create an Apollo Account**

In order to use the Apollo GraphQL Server, you first must create an account at [https://www.apollographql.com/](https://www.apollographql.com/).

**Create a New Service**

You next have to create a service within your account. When the service is created, you will be provided with an Apollo Engine API key of the form: _service:xxxxxxxxxx-9999:uygR3DDPKQ8EsexaUaPfVg_. You will need to provide this the engine-api-key input parameter to realMethods in order for it to be applied within the generated application files.

Also, apply the name of the service service as another input parameter. This is the text between the 2 colons (:) of the the engine-api-key

**Install the application**

The following instructions assume NPM is installed. If not, you can download it by installing Node.js at [https://nodejs.org/en/download/](https://nodejs.org/en/download/).

Once the application files have been pulled (or cloned) from your Git repository, cd (change directory) to the application directory. This directory will be named as the application name provided as part of the input parameters.

From this directory, install the app dependencies by running the following command: 

`npm install`

**Running the Apollo Server**

_npm start_ will start the Apollo GraphQL server, listening on default port _4000_.

**Publish the Generated Schema**

From another process and once the Apollo server has completed started startup, push the generated schema to the Apollo registry: 

`npx apollo service:push --endpoint=http://localhost:4000`

**Test Generated GraphQL API**
To run the Apollo Client from your browser, _http://localhost:4000_

To start issuing queries using GraphQL, [click here](https://www.apollographql.com/docs/apollo-server/getting-started/#step-8-execute-your-first-query)

## External Doc Link
[http://www.realmethods.com/infopages/apollo-info-page.html](http://www.realmethods.com/infopages/apollo-info-page.html)

## Package Derived From
None