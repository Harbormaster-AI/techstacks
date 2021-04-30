#if ( $aib.getParam( "kubernetes.inUse" ) == "true" )
variable "kubernetes-username" {}
variable "kubernetes-password" {}
#else
variable "aws-access-key" {}
variable "aws-secret-key" {}
#end ##if ( $aib.getParam( "kubernetes.inUse" ) == "true" )