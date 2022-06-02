#set( $tokenSystemName = ${aib.getParam( "corda.token-system-name" ).toLowerCase()} )
#set( $className = $classObject.getName() )
#set( $lowercaseClassName = ${display.uncapitalize( $className )} )
package ${aib.getRootPackageName()}.${tokenSystemName}market.states

import com.r3.corda.lib.tokens.contracts.states.EvolvableTokenType
import net.corda.core.contracts.BelongsToContract
import net.corda.core.contracts.UniqueIdentifier
import net.corda.core.identity.Party
import ${aib.getRootPackageName()}.${tokenSystemName}market.contracts.${className}Contract


@BelongsToContract(${className}Contract::class)
class ${className}TokenState(val maintainer: Party,
                      override val linearId: UniqueIdentifier,
                      override val fractionDigits: Int,                     
#set( $includePKs = false )
#set( $attributes = ${classObject.getAttributesOrderedInHierarchy( $includePKs )} )
#foreach( $attribute in $attributes )
#set( $attributeName = $display.uncapitalize( $attribute.getName() ) )
                      val ${attributeName}: ${attribute.getType()},
#end##foreach( $attribute in $classObject.getAttributes() ) 
                      override val maintainers: List<Party> = listOf(maintainer)) : EvolvableTokenType()
