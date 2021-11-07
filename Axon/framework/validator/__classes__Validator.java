#header()
#set( $className = ${classObject.getName()} )
#set( $lowercaseClassName = ${Utils.lowercaseFirstLetter( ${className} )} )
package ${aib.getRootPackageName(true)}.validator;

import org.springframework.util.Assert;

#set( $imports = [ "api" ] )
#importStatements( $imports )

public class ${className}Validator {
		
	/**
	 * default constructor
	 */
	protected ${className}Validator() {	
	}
	
	/**
	 * factory method
	 */
	static public ${className}Validator getInstance() {
		return new ${className}Validator();
	}
		
	/**
	 * handles creation validation for a $className
	 */
	public void validate( Create${className}Command $lowercaseClassName ) throws Exception {
		Assert.notNull( ${lowercaseClassName}, "Create${className}Command should not be null" );
		Assert.isNull( ${lowercaseClassName}.get${className}Id(), "Create${className}Command identifier should be null" );
#set( $includePKs = false )          
#foreach( $attribute in $classObject.getRequiredDirectAttributes( $includePKs ) )
#set( $attributeName = $attribute.getName() )
#set( $accessMethodName = "get${Utils.capitalizeFirstLetter( ${attributeName})}" )
		Assert.notNull( ${lowercaseClassName}.${accessMethodName}(), "Field Create${className}Command.${attributeName} should not be null" );
#end##foreach( $attribute in $classObject.getRequiredDirectAttributes( $includePKs ) )
	}

	/**
	 * handles update validation for a $className
	 */
	public void validate( Update${className}Command $lowercaseClassName ) throws Exception {
		Assert.notNull( ${lowercaseClassName}, "Update${className}Command should not be null" );
		Assert.notNull( ${lowercaseClassName}.get${className}Id(), "Update${className}Command identifier should not be null" );
#foreach( $attribute in $classObject.getRequiredDirectAttributes( $includePKs ) )
#set( $attributeName = $attribute.getName() )
#set( $accessMethodName = "get${Utils.capitalizeFirstLetter(${attributeName})}" )
		Assert.notNull( ${lowercaseClassName}.${accessMethodName}(), "Field Update${className}Command.${attributeName} should not be null" );
#end##foreach( $attribute in $classObject.getRequiredDirectAttributes( $includePKs ) )	}
    }

	/**
	 * handles delete validation for a $className
	 */
	public void validate( Delete${className}Command $lowercaseClassName ) throws Exception {
		Assert.notNull( ${lowercaseClassName}, "Delete${className}Command should not be null" );
		Assert.notNull( ${lowercaseClassName}.get${className}Id(), "Delete${className}Command identifier should not be null" );
	}
	
	/**
	 * handles fetchOne validation for a $className
	 */
	public void validateFetchOne( ${className}FetchOneSummary summary ) throws Exception {
		Assert.notNull( summary, "${className}FetchOneSummary should not be null" );
		Assert.notNull( summary.get${className}Id(), "${className}FetchOneSummary identifier should not be null" );
	}	
}
