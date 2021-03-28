#set( $appName = ${aib.getParam("application.name" )} )
#set( $version = ${aib.getParam("application.version" )} )
#set( $userName = ${aib.getParam("nuget.userName" )} )
#set( $password = ${aib.getParam("nuget.password" )} )
#set( $repoUrl = ${aib.getParam("nuget.repoUrl" )} )
#if ( ${repoUrl.startsWith( "http" )} == false )
#set( $repoUrl = "http://${repoUrl}" )
#end##if ( $repoUrl.startsWith( "http" ) == false )
nuget sources Remove -Name Repository -Source ${repoUrl}
nuget sources Add -Name Repository -Source ${repoUrl} -username "${userName}" -password "${password}"
nuget setapikey ${userName}:${password} -Source Repository
nuget pack application.nuspec
nuget push ${appName}.${version}.nupkg -Source Repository