#if( ${containerObject} )
#set( $appName = "Container-${containerObject.getName()}" )
#else
#set( $appName = $aib.getApplicationNameFormatted() )
#end
#set( $dbEngine = ${aib.getParam("terraform.db-engine")} )
# Specify the provider and access details
provider "aws" {
  region     = "${aib.getParam("terraform.region")}"
  access_key = "${var.aws-access-key}"
  secret_key = "${var.aws-secret-key}"
  version = "~> 2.0"
}

#outputAWSKeyPairDecl()

#set( $aws-vpc = ${aib.getParam("terraform.aws-vpc")} )
#if ( ${aws-vpc.isEmpty()} == true ) ## use the default
#set( $aws-vpc = "${esc.dollar}{aws_vpc.default.id}" )

# Default vpc
resource "aws_vpc" "default" {
  cidr_block = "10.0.0.0/16"
}
#end

# Our default security group to for the database
resource "aws_security_group" "mongo" {
  description = "security group created from terraform"
  vpc_id      = "vpc-c422e2a0" # only valid for us-east-1

  # SSH access from anywhere
  ingress {
    from_port   = 22
    to_port     = 22
    protocol    = "tcp"
    cidr_blocks = ["0.0.0.0/0"]
  }
  
  # mongodb access from anywhere
  ingress {
    from_port   = 27017
    to_port     = 27017
    protocol    = "tcp"
    cidr_blocks = ["0.0.0.0/0"]
  }

  # outbound internet access
  egress {
    from_port   = 0
    to_port     = 0
    protocol    = "-1"
    cidr_blocks = ["0.0.0.0/0"]
  }
}

resource "aws_instance" "mongodb" {
  # The connection block tells our provisioner how to
  # communicate with the resource (instance)
  connection {
    # The default username for our AMI
    user = "ubuntu"
    private_key = "${tls_private_key.generated.private_key_pem}"
  }

  instance_type = "t2.micro"
  
  tags = { Name = "mongodb instance" } 

#outputMongoDBCommunityAMIDecl()

  # The name of the SSH keypair you've created and downloaded
  # from the AWS console.
  #
  # https://console.aws.amazon.com/ec2/v2/home?region=us-west-2#KeyPairs:
  #
  # key_name = "${aib.getParam("terraform.aws-key-pair-name")}"
  key_name = "${aws_key_pair.generated.key_name}"
  
  # Our Security group to allow HTTP and SSH access
  vpc_security_group_ids = ["${aws_security_group.mongo.id}"]
  
  # To ensure ssh access works
    provisioner "remote-exec" {
    inline = [
      "sudo ls",
    ]
  }
}

#set( $size = $aib.getClassesWithIdentity().size() )			
#foreach( $class in $aib.getClassesWithIdentity() )
#set( $loopCount = $velocityCount )		
#set( $className = $class.getName() )
#set( $includeComposites = false )
##handle single associations
#foreach( $singleAssociation in $class.getSingleAssociations( ${includeComposites} ) )
#set( $roleName = $singleAssociation.getRoleName() )
#set( $parentName  = $class.getName() )
#awsLambdaFuncDecl( ${class.getName()} "get${roleName}"  "get${className}${roleName}" "get the ${roleName} for ${className}", "," )
#awsLambdaFuncDecl( ${class.getName()} "save${roleName}"  "save${className}${roleName}" "save the ${roleName} for ${className}", "," )
#awsLambdaFuncDecl( ${class.getName()} "delete${roleName}"  "delete${className}${roleName}" "delete the ${roleName} for ${className}", "," )
#end##foreach( $singleAssociation in $class.getSingleAssociations( ${includeComposites} ) )
##handle multiple associations
#foreach( $multiAssociation in $class.getMultipleAssociations() )
#set( $roleName = $multiAssociation.getRoleName() )
#set( $childName = $multiAssociation.getType() )
#set( $parentName  = $class.getName() )
#awsLambdaFuncDecl( ${class.getName()} "get${roleName}"  "get${className}${roleName}" "get the ${roleName} for ${className}", "," )
#awsLambdaFuncDecl( ${class.getName()} "add${roleName}"  "add${className}${roleName}" "add a ${childName} to the ${roleName} for ${className}", "," )
#awsLambdaFuncDecl( ${class.getName()} "assign${roleName}"  "assign${className}${roleName}" "assign one or more ${childName} to the ${roleName} for ${className}", "," )
#awsLambdaFuncDecl( ${class.getName()} "delete${roleName}"  "delete${className}${roleName}" "delete a ${childName} from the ${roleName} for ${className}", "," )
#end##foreach( $multiAssociation in $classObject.getMultipleAssociations() )
##common CRUD + getAll functions
#awsLambdaFuncDecl( ${class.getName()} "create${className}" "create${className}" "creates a $className using supplied $className data", "," )
#awsLambdaFuncDecl( ${class.getName()} "get${className}" "get${className}" "find $className by its primary key", "," )
#awsLambdaFuncDecl( ${class.getName()} "getAll${className}" "getAll${className}" "find all $className entities", "," )
#awsLambdaFuncDecl( ${class.getName()} "save${className}" "save${className}" "saves a $className using supplied $className data", "," )
#if( $loopCount < $size )
#awsLambdaFuncDecl( ${class.getName()} "delete${className}" "delete${className}"  "delete a $className by its primary key", "," )
#else
#awsLambdaFuncDecl( ${class.getName()} "delete${className}" "delete${className}"  "delete a $className by its primary key", "" )
#end##if( $loopCount < $size )
#end##foreach( $class in $aib.getClassesWithIdentity() )



