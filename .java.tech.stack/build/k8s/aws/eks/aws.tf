#####################################################################
# Google Cloud Platform
#####################################################################
provider "aws" {
  region     = "${var.region}"
  access_key = "${var.aws-access-key}"
  secret_key = "${var.aws-secret-key}"
  version = "~> 2.0"
}