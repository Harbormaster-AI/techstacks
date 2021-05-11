#set( $className = $classObject.getName() )
#set( $lowercaseClassName = ${Utils.lowercaseFirstLetter(${className})} )
class ${className}sController < ApplicationController
  def index
    @${lowercaseClassName}s = ${className}.all
  end
 
  def show
    @${lowercaseClassName} = ${className}.find(params[:id])
  end
 
  def new
    @${lowercaseClassName} = ${className}.new
  end
 
  def edit
    @${lowercaseClassName} = ${className}.find(params[:id])
  end
 
  def create
    @${lowercaseClassName} = ${className}.new(${lowercaseClassName}_params)
 
    if @${lowercaseClassName}.save
      redirect_to @${lowercaseClassName}
    else
      render 'new'
    end
  end
 
  def update
    @${lowercaseClassName} = ${className}.find(params[:id])
 
    if @${lowercaseClassName}.update(${lowercaseClassName}_params)
      redirect_to @${lowercaseClassName}
    else
      render 'edit'
    end
  end
 
  def destroy
    @${lowercaseClassName} = ${className}.find(params[:id])
    @${lowercaseClassName}.destroy
 
    redirect_to ${lowercaseClassName}s_path
  end
 
  private
    def ${lowercaseClassName}_params
#set( $includePKs = false )
#set( $includeTypes = false )
#set( $includeAssociations = false )
#set( $delim = ", :" )
#set( $suffix = "" )    
#set( $attributesAsString = ${classObject.getAttributesAsString( ${includePKs}, ${includeTypes}, ${includeAssociations}, ${delim}, ${suffix} )} )
      params.require(:${lowercaseClassName}).permit(:${attributesAsString})
    end
end