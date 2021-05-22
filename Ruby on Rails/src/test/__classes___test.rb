#set( $className = $classObject.getName() )
#set( $lowercaseClassName = ${Utils.lowercaseFirstLetter(${className})} )
require "test_helper"
class ${className}Test < ActiveSupport::TestCase
  # test "the truth" do
  #   assert true
  # end
end
