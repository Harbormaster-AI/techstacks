Rails.application.routes.draw do
  root "welcome#welcomeindex"
#set( $classes = $aib.getClassesToGenerate() )
#foreach( $class in $classes )
#set( $className = $class.getName() )
#set( $lowercaseClassName = ${Utils.lowercaseFirstLetter(${className})} )
#set( $multis = $class.getMultipleAssociations() )
#set( $hasMultis = $multis.size() )
#if ( $hasMultis == 0 )
  resources :${lowercaseClassName}s
#else##if ( $hasMutlis > 0 )
  resources :${lowercaseClassName}s do
#foreach( $multi in $multis )
#set( $roleName = ${multi.getRoleName()} )
    resources :${Utils.lowercaseFirstLetter(${roleName})}
#end##foreach( $multi in $multis )
  end
#end##if ( $hasMutlis > 0 )
#end##foreach( $class in $classes )
end
