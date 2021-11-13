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
//		Assert.isNull( ${lowercaseClassName}.get${className}Id(), "Create${className}Command identifier should be null" );
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
	public void validate( ${className}FetchOneSummary summary ) throws Exception {
		Assert.notNull( summary, "${className}FetchOneSummary should not be null" );
		Assert.notNull( summary.get${className}Id(), "${className}FetchOneSummary identifier should not be null" );
	}

#set( $includeComposites = false )
#foreach( $singleAssociation in $classObject.getSingleAssociations( ${includeComposites} ) )
#set( $roleName = $Utils.capitalizeFirstLetter( $singleAssociation.getRoleName() ) )
	/**
	 * handles assign ${roleName} validation for a $className
	 */	
	public void validate( Assign${roleName}To${className}Command command ) throws Exception {
		Assert.notNull( command, "Assign${roleName}To${className}Command should not be null" );
		Assert.notNull( command.get${className}Id(), "Assign${roleName}To${className}Command identifier should not be null" );
		Assert.notNull( command.getAssignment(), "Assign${roleName}To${className}Command assignment should not be null" );
	}

	/**
	 * handles assign ${roleName} validation for a $className
	 */	
	public void validate( UnAssign${roleName}From${className}Command command ) throws Exception {
		Assert.notNull( command, "UnAssign${roleName}To${className}Command should not be null" );
		Assert.notNull( command.get${className}Id(), "UnAssign${roleName}To${className}Command identifier should not be null" );
	}
#end##foreach( $singleAssociation in $classObject.getSingleAssociations( ${includeComposites} ) )

#foreach( $multiAssociation in $classObject.getMultipleAssociations() )
#set( $roleName = $Utils.capitalizeFirstLetter( $multiAssociation.getRoleName() ) )
#set( $childType = $multiAssociation.getType() )
	/**
	 * handles add to ${roleName} validation for a $className
	 */	
	public void validate( Add${roleName}To${className}Command command ) throws Exception {
		Assert.notNull( command, "Add${roleName}To${className}Command should not be null" );
		Assert.notNull( command.get${className}Id(), "Add${roleName}To${className}Command identifier should not be null" );
		Assert.notNull( command.getAddTo(), "Add${roleName}To${className}Command addTo attribute should not be null" );
	}

	/**
	 * handles remove from ${roleName} validation for a $className
	 */	
	public void validate( Remove${roleName}From${className}Command command ) throws Exception {
		Assert.notNull( command, "Remove${roleName}From${className}Command should not be null" );
		Assert.notNull( command.get${className}Id(), "Remove${roleName}From${className}Command identifier should not be null" );
		Assert.notNull( command.getRemoveFrom(), "Add${roleName}To${className}Command removeFrom attribute should not be null" );
		Assert.notNull( command.getRemoveFrom().get${childType}Id(), "Add${roleName}To${className}Command removeFrom attribubte identifier should not be null" );
	}
	
#end##foreach( $multiAssociation in $classObject.getMultipeSingleAssociations() )

}
