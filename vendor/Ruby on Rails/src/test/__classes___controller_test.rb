#set( $className = $classObject.getName() )
#set( $lowercaseClassName = ${Utils.lowercaseFirstLetter(${className})} )
require "test_helper"

class ${className}ControllerTest < ActionDispatch::IntegrationTest
  # called before every single test
  setup do
    @${lowercaseClassName} = ${lowercaseClassName}s(:one)
  end

  # called after every single test
  teardown do
    # when controller is using cache it may be a good idea to reset it afterwards
    Rails.cache.clear
  end

  test "should create ${lowercaseClassName}" do
    assert_difference("${className}.count") do
      post ${lowercaseClassName}s_url, params: { ${lowercaseClassName}: { #outputSeedDeclaration( $classObject, false ) } }
    end

    assert_redirected_to ${lowercaseClassName}s_url
  end

 
  
  test "should destroy ${lowercaseClassName}" do
    assert_difference("${className}.count", -1) do
      delete ${lowercaseClassName}_url(@${lowercaseClassName})
    end

    assert_redirected_to ${lowercaseClassName}s_url
  end
  
end


