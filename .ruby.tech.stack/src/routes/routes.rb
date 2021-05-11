Rails.application.routes.draw do
  root '/'
#set( $classes = $aib.getClassesToGenerate() )
#foreach( $class in $classes )
#set( $className = $class.getName() )
#set( $lowercaseClassName = ${Utils.lowercaseFirstLetter(${className})} )
#set( $multis = $class.getMultipleAssociations() )
#set( $hasMultis = $multis.size() )
#if ( $hasMultis == 0 )
  resources :${className}, controller: '${lowercaseClassName}Controller'
#else##if ( $hasMutlis > 0 )
  resources :${className}, controller: '${lowercaseClassName}Controller' do
#foreach( $multi in $multis ) 
    resources :${multi.getRoleName()}
#end##foreach( $multi in $multis )
  end
#end##if ( $hasMutlis > 0 )
#end##foreach( $class in $classes )
end
