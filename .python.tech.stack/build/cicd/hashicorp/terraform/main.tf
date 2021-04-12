#if( ${containerObject} )
#set( $appName = "Container-${containerObject.getName()}" )
#else
#set( $appName = $aib.getApplicationNameFormatted() )
#end
# Specify the provider and access details
provider "aws" {
  region     = "${aib.getParam("terraform.region")}"
  access_key = "${var.aws-access-key}"
  secret_key = "${var.aws-secret-key}"
}

#set( $aws-vpc = ${aib.getParam("terraform.aws-vpc")} )
#if ( ${aws-vpc.isEmpty() == true} ) ## use the default
#set( $aws-vpc = "${esc.dollar}{aws_vpc.default.id}" )
# Default vpc
resource "aws_vpc" "default" {
  cidr_block = "10.0.0.0/16"
}
#end

# Default security group to access
# the instances over SSH and HTTP
resource "aws_security_group" "default" {
#  name        = "${appName}-security-group-from-terrorform" #optional, when omitted, terraform creates a random name
  description = "security group for ${appName} created from terraform"
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

  # outbound internet access
  egress {
    from_port   = 0
    to_port     = 0
    protocol    = "-1"
    cidr_blocks = ["0.0.0.0/0"]
  }
}


resource "aws_instance" "web" {
  # The connection block tells our provisioner how to
  # communicate with the resource (instance)
  connection {
    # The default username for our AMI
    user = "ubuntu"
 }

  instance_type = "t2.micro"
  
  tags = { Name = "${appName} instance" } 

  # standard realmethods community image with docker pre-installed
  ami = "ami-05033408e5e831fb0"

  # The name of the  SSH keypair you've created and downloaded
  # from the AWS console.
  #
  # https://console.aws.amazon.com/ec2/v2/home?region=us-west-2#KeyPairs:
  #
  key_name = "${aib.getParam("terraform.aws-key-pair-name")}"
  
  # Our Security group to allow HTTP and SSH access
  vpc_security_group_ids = ["${aws_security_group.default.id}"]

#set( $dockerUserName = ${aib.getParam("docker.userName")} ) 
#set( $dockerPassword = ${aib.getParam("docker.password")} )
#set( $dockerOrgName = ${aib.getParam("dockerorg-name")} )
#set( $dockerRepo = ${aib.getParam("docker.repo")} )
#set( $dockerTag = ${aib.getParam("docker.tag")} )

  provisioner "remote-exec" {
    inline = [
      "sudo apt-get -y update",
      "sudo apt-get -y install docker",
      "sudo docker login -u $DOCKER_USER -p $DOCKER_PASS",
      "sudo docker pull ${dockerUserName}/${dockerRepo}:${dockerTag}",
      "sudo docker run -it -p 3306:3306 -p 8080:8080 ${dockerUserName}/${dockerRepo}:${dockerTag}
    ]
  }
}
