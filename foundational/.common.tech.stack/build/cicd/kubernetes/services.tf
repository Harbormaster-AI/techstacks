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
## expose the application port 
################################################################
    port {
      name        = "app-port"
      port        = #DockerComposePlatformPorts()
      target_port = #DockerComposePlatformPorts()
    }

#Expose_K8_Ports()

################################################################
## Load balancing will automatically expose the ports publicly
################################################################
#set( $serviceType = ${aib.getParam("kubernetes.serviceType")} )
    type = "${serviceType}"
  }
  
}
