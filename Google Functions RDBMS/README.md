# realMethods Tech Stack Package Descriptor

## Name
Google Functions (In Beta)

## Summary Image
![alt text](http://www.realmethods.com/infopages/img/google.functions.png)

## Intent
Depending upon the complexity of the input entity model, realMethods will created 100s to 1000s of Google functions, each representing the fundamental unit of business functionality.  Therefore the deployment process will take some time.  Once a function is deployed, it is immediately available on GCP.

To create an application that uses:

- A custom model controller framework, backed by business delegates that serve the role of Google functions
- All functions necessary to create/read/update/delete/readAll of each entity of the model as well as each model's single and multiple associations
- GitHub to push application and project files
- Leverages Firebase to handle storing and deploying the generated Google functions

**Note:** 
This package is in the process of being deployable through each of the major supported CI platforms including the execution of generated Terraform file(s).

__In Progress__
Along with a the generated Google functions, realMethods generates a separate deployable Data Access layer using the Spark Micro Web Framework.  This solves the problem whereby database connections need to be stateful yet for performance sake serverless functions should remain stateless.  By keeping the stateful db connections reusable and separate from the stateless serverless functions, the read/write performance of each Lambda function is optimized.`

When realMethods is enabled to generate the Terraform file(s) for AWS, it will create a secure server with a running MySQL database instance.  It will SSH to that server instance to pull and run the Docker image of the Data Access layer.  Also, it will point each Google function to the URL of the Data Access layer in order to communicate with it as a RESTful API.  

## Contents
[https://github.com/realmethods-public/tech.stack.packages/tree/master/Google%20Functions%20RDBMS](https://github.com/realmethods-public/tech.stack.packages/tree/master/Google%20Functions%20RDBMS)


## Usage

__In Progress__

Each Google function will automatically build and deploy for a supported CI platform (CircleCI, Cloudbees, etc..).

## Package Derived From
None