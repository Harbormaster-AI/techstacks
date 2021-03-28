# realMethods Tech Stack Package Descriptor

## Name
AWS Lambda MongoDB 

## Summary Image
![alt text](http://www.realmethods.com/infopages/img/aws.lambda.rds.png)

## Intent
Depending upon the complexity of the input entity model, realMethods will created 100s to 1000s of AWS Lambda functions, each representing the fundamental unit of business functionality.  Therefore the deployment process will take some time.  Once a function is deployed, it is immediately available on AWS.

To create an application that uses:

- A custom model controller framework, backed by business delegates that serve the role of Lambda functions
- All functions necessary to create/read/update/delete/readAll of each entity of the model as well as each model's single and multiple associations
- GitHub to push application and project files
- Selected CI platform to build/test/deploy the generated Lambda functions to an assigned S3 bucket

**Note:** 
Along with a the generated Lambda functions, realMethods generates a separate deployable Data Access layer using the Spark Micro Web Framework.  This solves the problem whereby database connections need to be stateful yet for performance sake serverless functions should remain stateless.  By keeping the stateful db connections reusable and separate from the stateless serverless functions, the read/write performance of each Lambda function is optimized.`

If realMethods is enabled to generate the Terraform file(s) for AWS, it will create a secure server with a running MySQL database instance.  It will SSH to that server instance to pull and run the Docker image of the Data Access layer.  Also, it will point each Lambda function to the URL of the Data Access layer in order to communicate with it as a RESTful API.  

## Contents
[https://github.com/realmethods-public/tech.stack.packages/tree/master/AWS%20Lambda%20RDS](https://github.com/realmethods-public/tech.stack.packages/tree/master/AWS%20Lambda%20RDS)


## Usage

Each lambda function will automatically build and deploy for a supported CI platform (CircleCI, Cloudbees, etc..).  For any CI platform, be sure 
to assign environment variables USER_AWS_ACCESSKEY and USER_AWS_SECRETKEY to their respective values as assigned by AWS.


## External Doc Link
[http://www.realmethods.com/infopages/aws-lambda-rds-info-page.html](http://www.realmethods.com/infopages/aws-lambda-rds-info-page.html)

## Package Derived From
None