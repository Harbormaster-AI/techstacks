#if( ${containerObject} )
#set( $appName = "Container-${containerObject.getName()}" )
#else
#set( $appName = $aib.getApplicationNameFormatted() )
#end
resource "kubernetes_replication_controller" "app-master" {
  metadata {
    name = "app-master"
  }

  spec {
    replicas = 1

    selector = {
      app  = "${appName}"
    }
    template {        
#Declare_K8_Containers()
    }
  }
}