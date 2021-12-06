#if( ${containerObject} )
#set( $appName = "Container-${containerObject.getName()}" )
#else
#set( $appName = $aib.getApplicationNameFormatted() )
#end

provider "kubernetes" {
  host = "$aib.getParam( "kubernetes.host" )"
  username = var.kubernetes-username
  password = var.kubernetes-password
  version = "~> 1.10"
}

#Declare_K8_Pods()
#Declare_K8_Services()