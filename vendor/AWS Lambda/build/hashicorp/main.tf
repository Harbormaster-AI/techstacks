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
}

#set( $aws-vpc = ${aib.getParam("terraform.aws-vpc")} )
#if ( ${aws-vpc.isEmpty()} == true ) ## use the default
#set( $aws-vpc = "${esc.dollar}{aws_vpc.default.id}" )
# Default vpc
resource "aws_vpc" "default" {
  cidr_block = "10.0.0.0/16"
}
#end

# Default security group to access
# the instances over SSH and HTTP
resource "aws_security_group" "web" {
#  name        = "${appName}-security-group-from-terrorform" #optional, when omitted, terraform creates a random name
  description = "security group for ${appName} web created from terraform"
  vpc_id      = "${aws-vpc}"

  # SSH access from anywhere
  ingress {
    from_port   = 22
    to_port     = 22
    protocol    = "tcp"
    cidr_blocks = ["0.0.0.0/0"]
  }

  # HTTP access from the VPC
  ingress {
    from_port   = 8000
    to_port     = 8000
    protocol    = "tcp"
    cidr_blocks = ["0.0.0.0/0"]
  }

  # HTTP access from the VPC
  ingress {
    from_port   = 8080
    to_port     = 8080
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

# Our default security group to for the database
resource "aws_security_group" "rds" {
  description = "security group for ${appName} RDS created from terraform"
  vpc_id      = "${aws-vpc}"

  # mysql access from anywhere
  ingress {
    from_port   = 3306
    to_port     = 3306
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

#set( $dbEngine = ${aib.getParam("terraform.db-engine")} )
#set( $dbVersion = ${aib.getParam("terraform.db-version")} )
resource "aws_db_instance" "default" {
  depends_on             = ["aws_security_group.rds"]
#  identifier             = "${appName}-rds" # Terraform will create a unique id if not assigned
  allocated_storage      = "10"
  engine                 = "${dbEngine}"
  engine_version         = "${dbVersion}"
  instance_class         = "${aib.getParam("terraform.aws-db-instance-type")}"
  name                   = "${appName}"
  username               = "${aib.getParam("hibernate.hibernate.connection.username")}"
  password               = "${aib.getParam("hibernate.hibernate.connection.password")}"
  vpc_security_group_ids = ["${aws_security_group.rds.id}"]
}

resource "aws_instance" "web" {
  # The connection block tells our provisioner how to
  # communicate with the resource (instance)
  connection {
    # The default username for our AMI
    user = "ubuntu"
 }

  instance_type = "${aib.getParam("terraform.aws-ec2-instance-type")}"
  
  tags = { Name = "${appName} instance" } 

  # standard realmethods community AMI with docker pre-installed
  ami = "ami-05033408e5e831fb0"

  # The name of the  SSH keypair you've created and downloaded
  # from the AWS console.
  #
  # https://console.aws.amazon.com/ec2/v2/home?region=us-east-1#KeyPairs:
  #
  key_name = "${aib.getParam("terraform.aws-key-pair-name")}"
  
  # Our Security group to allow HTTP and SSH access
  vpc_security_group_ids = ["${aws_security_group.web.id}"]

#set( $dockerUserName = ${aib.getParam("docker.userName")} ) 
#set( $dockerPassword = ${aib.getParam("docker.password")} )
#set( $dockerOrgName = ${aib.getParam("dockerorg-name")} )
#set( $dockerRepo = ${aib.getParam("docker.repo")} )
#set( $dockerTag = ${aib.getParam("docker.tag")} )
#set( $databaseUrl = "jdbc:${dbEngine}://${esc.dollar}{aws_db_instance.default.endpoint}/${appName}" )
  provisioner "remote-exec" {
    inline = [
      "sudo apt-get -y update",
      "sudo docker login --username ${dockerUserName} --password ${dockerPassword}",
      "sudo docker pull ${dockerOrgName}/${dockerRepo}:${dockerTag}",
      "sudo docker run -it -d -p 8000:8000 -p 8080:8080 -e DATABASE_URL=${databaseUrl} ${dockerOrgName}/${dockerRepo}:${dockerTag}"
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



