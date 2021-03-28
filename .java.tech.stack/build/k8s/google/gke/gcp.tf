#####################################################################
# Google Cloud Platform
#####################################################################
provider "google" {
  credentials = file("./account.json")
  project = var.project
  region  = var.region
}