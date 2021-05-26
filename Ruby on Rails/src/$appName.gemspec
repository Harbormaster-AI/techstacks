#set( $appName = $aib.getApplicationNameFormatted() )
require_relative 'lib/rubydemo/version'

Gem::Specification.new do |spec|
  spec.name          = '${appName}'
  spec.version       = '${aib.getParam("app.version")}'
#set( $author = ${aib.getParam("app.author")} )
#if ( $author == $null || $author == "" ) 
#set( $author = "Put author name here" )
#end
  spec.authors       = ["${aib.getParam("app.author")}"]
#set( $email = ${aib.getParam("app.email")} )
#if ( $email == $null || $email == "" ) 
#set( $email = "put_your_email_here@xyz.com" )
#end
  spec.email         = ["${email}"]
#set( $description = ${aib.getParam("app.description")} )
#if ( $description == $null || $description == "" ) 
#set( $description = "Put description name here" )
#end
  spec.summary       = %q{${description}}
#set( $homepage = ${aib.getParam("app.homepage")} )
#if ( $homepage == $null || $homepage == "" ) 
#set( $homepage = "https://put-your-home-page-here.com" )
#end
  spec.homepage      = "${homepage}"
  spec.license       = "MIT"
  spec.required_ruby_version = Gem::Requirement.new(">= 2.3.0")

  spec.metadata["allowed_push_host"] = "http://mygemserver.com"

  spec.metadata["homepage_uri"] = spec.homepage
  spec.metadata["source_code_uri"] = "https://${aib.getParam("git.host")}/${aib.getParam("git.username")}/${aib.getParam("git.repository")}.git"
  spec.metadata["changelog_uri"] = "https://${aib.getParam("git.host")}/${aib.getParam("git.username")}/${aib.getParam("git.repository")}/blob/master/CHANGELOG.md"

  # Specify which files should be added to the gem when it is released.
  # The `git ls-files -z` loads the files in the RubyGem that have been added into git.
  spec.files         = Dir.chdir(File.expand_path('..', __FILE__)) do
    `git ls-files -z`.split("\x0").reject { |f| f.match(%r{^(test|spec|features)/}) }
  end
  spec.bindir        = "bin"
  spec.executables   = spec.files.grep(%r{^exe/}) { |f| File.basename(f) }
  spec.require_paths = ["lib"]
end
