#if( ${containerObject} )
#set( $appName = "Container-${containerObject.getName()}" )
#else
#set( $appName = $aib.getApplicationNameFormatted() )
#end
resource "kubernetes_service" "app-master" {
  metadata {
    name = "app-master"
  }

  spec {
    selector = {
      app  = "${appName}"
    }
################################################################
## expose the application port on 8080 by default
################################################################
    port {
      name        = "app-port"
      port        = 8080
      target_port = 8080
    }

#Expose_K8_Ports()

################################################################
## Load balancing will automatically expose the ports publicly
################################################################
    type = "LoadBalancer"
  }
  
}
