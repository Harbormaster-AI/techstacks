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
#set( $commandAlias = $classObject.getCreateCommandAlias() )
	public void validate( ${commandAlias} ${lowercaseClassName} )throws Exception {
		Assert.notNull( ${lowercaseClassName}, "${commandAlias} should not be null" );
//		Assert.isNull( ${lowercaseClassName}.get${className}Id(), "${commandAlias} identifier should be null" );
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
#set( $commandAlias = $classObject.getUpdateCommandAlias() )
	public void validate( ${commandAlias} $lowercaseClassName ) throws Exception {
		Assert.notNull( ${lowercaseClassName}, "${commandAlias} should not be null" );
		Assert.notNull( ${lowercaseClassName}.get${className}Id(), "${commandAlias} identifier should not be null" );
#foreach( $attribute in $classObject.getRequiredDirectAttributes( $includePKs ) )
#set( $attributeName = $attribute.getName() )
#set( $accessMethodName = "get${Utils.capitalizeFirstLetter(${attributeName})}" )
		Assert.notNull( ${lowercaseClassName}.${accessMethodName}(), "Field ${commandAlias}.${attributeName} should not be null" );
#end##foreach( $attribute in $classObject.getRequiredDirectAttributes( $includePKs ) )	}
    }

	/**
	 * handles delete validation for a $className
	 */
#set( $commandAlias = $classObject.getDeleteCommandAlias() )	
    public void validate( ${commandAlias} $lowercaseClassName ) throws Exception {
		Assert.notNull( ${lowercaseClassName}, "{commandAlias} should not be null" );
		Assert.notNull( ${lowercaseClassName}.get${className}Id(), "${commandAlias} identifier should not be null" );
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
#set( $alias = ${singleAssociation.getAssignToCommandAlias()} )
	/**
	 * handles assign ${roleName} validation for a $className
	 * 
	 * @param	command ${alias}
	 */	
	public void validate( ${alias} command ) throws Exception {
		Assert.notNull( command, "${alias} should not be null" );
		Assert.notNull( command.get${className}Id(), "${alias} identifier should not be null" );
		Assert.notNull( command.getAssignment(), "${alias} assignment should not be null" );
	}

#set( $alias = ${singleAssociation.getUnAssignFromCommandAlias()} )
	/**
	 * handles unassign ${roleName} validation for a $className
	 * 
	 * @param	command ${alias}
	 */	
	public void validate( ${alias} command ) throws Exception {
		Assert.notNull( command, "${alias} should not be null" );
		Assert.notNull( command.get${className}Id(), "${alias} identifier should not be null" );
	}
#end##foreach( $singleAssociation in $classObject.getSingleAssociations( ${includeComposites} ) )

#foreach( $multiAssociation in $classObject.getMultipleAssociations() )
#set( $roleName = $Utils.capitalizeFirstLetter( $multiAssociation.getRoleName() ) )
#set( $childType = $multiAssociation.getType() )
#set( $alias = ${multiAssociation.getAddToCommandAlias()} )
	/**
	 * handles add to ${roleName} validation for a $className
	 * 
	 * @param	command ${alias}
	 */	
	public void validate( ${alias} command ) throws Exception {
		Assert.notNull( command, "${alias} should not be null" );
		Assert.notNull( command.get${className}Id(), "${alias} identifier should not be null" );
		Assert.notNull( command.getAddTo(), "${alias} addTo attribute should not be null" );
	}

#set( $alias = ${multiAssociation.getRemoveFromCommandAlias()} )
	/**
	 * handles remove from ${roleName} validation for a $className
	 * 
	 * @param	command ${alias}
	 */	
	public void validate( ${alias} command ) throws Exception {
		Assert.notNull( command, "${alias} should not be null" );
		Assert.notNull( command.get${className}Id(), "${alias} identifier should not be null" );
		Assert.notNull( command.getRemoveFrom(), "${alias} removeFrom attribute should not be null" );
		Assert.notNull( command.getRemoveFrom().get${childType}Id(), "${alias} removeFrom attribubte identifier should not be null" );
	}
	
#end##foreach( $multiAssociation in $classObject.getMultipeSingleAssociations() )

}
