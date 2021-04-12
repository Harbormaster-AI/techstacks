#####################################################################
# Variables
#####################################################################
variable "region" {}

#####################################################################
# Modules
#####################################################################
module "eks" {
  source   = "./eks"
  region   = var.region
}

module "k8s" {
  source   = "./k8s"
  client_certificate     = module.eks.client_certificate
  client_key             = module.eks.client_key
  cluster_ca_certificate = module.eks.cluster_ca_certificate
}