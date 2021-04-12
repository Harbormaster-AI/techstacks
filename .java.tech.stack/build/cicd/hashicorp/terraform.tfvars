#set( $provider = ${aib.getParam("terraform.provider")} )
#if( ${provider.equalsIgnoreCase("digitalocean")} == true )
token = "${aib.getParam("terraform.digitalOceantoken")}"
#end##if( ${provider.equalsIgnoreCase("digitalocean")} == true )