class InitialMigration < ActiveRecord::Migration[6.1]
  def change
#set( $classes = $aib.getClassesToGenerate() )
#foreach( $class in $classes )
#set( $className = $class.getName() )
#set( $lowercaseClassName = ${Utils.lowercaseFirstLetter(${className})} )
    create_table :${lowercaseClassName}s do |t|
#migrationFields( $class )  
      t.timestamps
    end
#end##foreach( $class in $classes )
#createJoinTables()
  end
end
