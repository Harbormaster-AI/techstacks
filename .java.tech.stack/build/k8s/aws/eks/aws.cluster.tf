#if( ${containerObject} )
#set( $appName = "Container-${containerObject.getName()}" )
#else
#set( $appName = $aib.getApplicationNameFormatted() )
#end
#####################################################################
# AWS Cluster
#####################################################################
data "aws_eks_cluster" "${appName}-cluster" {
  name = "${appName}-cluster"
}

output "endpoint" {
  value = "${esc.dollar}{data.aws_eks_cluster.${appName}-cluster.endpoint}"
}

output "kubeconfig-certificate-authority-data" {
  value = "${esc.dollar}{data.aws_eks_cluster.${appName}-cluster.certificate_authority.0.data}"
}

#####################################################################
# Output for K8S
#####################################################################
