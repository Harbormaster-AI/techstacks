#!/bin/bash

if [ $1 == 'install' ]
then
echo update apt-get
apt-get update && \

echo install wget and unzip
apt-get install -y wget unzip

echo wget terraform_0.12.20_linux_amd64.zip
wget https://releases.hashicorp.com/terraform/0.12.20/terraform_0.12.20_linux_amd64.zip

echo unzip terraform into /usr/local/bin
unzip ./terraform_0.12.20_linux_amd64.zip -d /usr/local/bin/
fi

echo test install by asking terraform for its version
terraform version

echo initialize terraform without input
terraform init --input=false 

echo create the plan
terraform plan --input=false --out=terraform.plan #providerEnvVarsForTerraform()

echo apply the plan using auto-approve
terraform apply -auto-approve --input=false "terraform.plan"