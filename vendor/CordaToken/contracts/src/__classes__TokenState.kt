#set( $tokenSystemName = ${aib.getParam( "corda.token-system-name" ).toLowerCase()} )
#set( $className = $classObject.getName() )
#set( $lowercaseClassName = ${display.uncapitalize( $className )} )
#set( $identifierFieldName = $display.uncapitalize( $aib.getParam( "corda.identifier-field-name" ) ) )
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
					  val ${identifierFieldName}: String,
                      override val maintainers: List<Party> = listOf(maintainer)) : EvolvableTokenType()
