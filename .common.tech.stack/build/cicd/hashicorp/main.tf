#if( ${aib.getParam("terraform.provider")} == "aws" )
#outputAsAWSProvider()
#elseif( ${aib.getParam("terraform.provider")} == "google" )
#outputAsGoogleProvider()
#elseif( ${aib.getParam("terraform.provider")} == "azure" )
#outputAsAzureProvider()
#elseif( ${aib.getParam("terraform.provider")} == "nutanix" )
#outputAsNutanixProvider()
#end##if( ${aib.getParam("terraform.provider")} == "aws" )
