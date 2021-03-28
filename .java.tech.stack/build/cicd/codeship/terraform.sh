#!/bin/bash

cd /code/gitRoot/terraform

terraform version
terraform init --input=false 
#if( $aib.getParam( "kubernetes.inUse" ) == "true" && $aib.getParam( "kubernetes.hostTarget") == "google" )
terraform plan --input=false --out=terraform.plan -var host=${aib.getParam("kubernetes.host")} -var username=${aib.getParam("google.userName")} -var password=${aib.getParam("google.password")} -var region=${aib.getParam("google.region")}-${aib.getParam("google.zone")} -var project=${aib.getParam("google.project")} 
#else
terraform plan --input=false --out=terraform.plan -var aws-access-key=${aib.getParam("terraform.aws-access-key")} -var aws-secret-key=${aib.getParam("terraform.aws-secret-key")} 
#end##if ( $aib.getParam( "kubernetes.inUse" ) == "true" )
terraform apply --input=false "terraform.plan"