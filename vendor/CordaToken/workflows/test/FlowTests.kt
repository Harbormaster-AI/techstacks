#set( $tokenSystemName = ${aib.getParam( "corda.token-system-name" ).toLowerCase()} )
#set( $rootPackage = "${aib.getRootPackageName()}.${tokenSystemName}market" )
#set( $classesToGenerate = $aib.getClassesToGenerate() )
#set( $numClassesToGenerate = $classesToGenerate.size() )
#set( $identifierFieldName = $display.uncapitalize( $aib.getParam( "corda.identifier-field-name" ) ) )
package ${rootPackage}

import com.google.common.collect.ImmutableList
import net.corda.core.identity.CordaX500Name
import ${rootPackage}.flows.*
import ${rootPackage}.states.*
import net.corda.testing.node.*
import org.junit.After
import org.junit.Before
import org.junit.Test
import java.util.concurrent.Future

class FlowTests {
    private var network: MockNetwork? = null
#set( $totalTestNodes = $convert.toInteger( ${aib.getParam("corda.total-test-nodes")} ) )
#foreach( $nodeCount in [1..${totalTestNodes}] )
    private var node${nodeCount}: StartedMockNode? = null
#end##foreach( $nodeCount in [1..${aib.getParam("total-test-nodes")}] )

    @Before
    fun setup() {
        network = MockNetwork(MockNetworkParameters(
                notarySpecs = listOf(MockNetworkNotarySpec(CordaX500Name("Notary","London","GB")))
        ).withCordappsForAllNodes(ImmutableList.of(
                TestCordapp.findCordapp("${rootPackage}.contracts"),
                TestCordapp.findCordapp("${rootPackage}.flows"),
                TestCordapp.findCordapp("com.r3.corda.lib.tokens.contracts"),
                TestCordapp.findCordapp("com.r3.corda.lib.tokens.workflows"))))

#foreach( $nodeCount in [1..${totalTestNodes}] )
        node${nodeCount} = network!!.createPartyNode(null)
#end##foreach( $nodeCount in [1..${aib.getParam("total-test-nodes")}] )  
                
        network!!.runNetwork()
    }

    @After
    fun tearDown() {
        network!!.stopNodes()
    }

    @Test
    fun ${tokenSystemName}TokensCreation() {
#set( $identifiers = [] )
#foreach( $class in $classesToGenerate )
#set( $className = ${class.getName()} )
#set( $lowercaseClassName = ${display.uncapitalize( $className )} )
#set( $identifier = ${math.random(8500,8799)} )
#set( $addCheck = $identifiers.add( ${identifier} ) )
        val ${lowercaseClassName}flow = Create${className}Token("${identifierFieldName}")
        val future: Future<String> = a!!.startFlow(${lowercaseClassName}flow)
        network!!.runNetwork()

#end##foreach( $class in $classesToGenerate )

#foreach( $class in $classesToGenerate )
#set( $className = ${class.getName()} )
#set( $lowercaseClassName = ${display.uncapitalize( $className )} )
#set( $index = ${velocityCount} - 1 )
#set( $identifier = $identifiers.get( $index ) )
        val (state${velocityCount}) = a!!.services.vaultService.queryBy(${className}TokenState::class.java).states.stream().filter {
            (state${velocityCount}) -> state.data.${identifierFieldName}.equals("${identifierFieldName}").findAny()
                .orElseThrow { IllegalArgumentException("lowercaseClassName identity symbol ${identifierFieldName} not found from vault") }
        val ${lowercaseClassName}IdentityStored: String = state.data.${identifierFieldName}
        assert(${lowercaseClassName}IdentifyStored == "${identifierFieldName}")      
        
#end##foreach( $class in $classesToGenerate )
    }
}