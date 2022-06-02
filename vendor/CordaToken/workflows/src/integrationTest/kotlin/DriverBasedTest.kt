#set( $tokenSystemName = ${aib.getParam( "corda.token-system-name" ).toLowerCase()} )
package ${aib.getRootPackageName()}.${tokenSystemName}market

import net.corda.core.identity.CordaX500Name
import net.corda.core.utilities.getOrThrow
import net.corda.testing.core.TestIdentity
import net.corda.testing.driver.DriverDSL
import net.corda.testing.driver.DriverParameters
import net.corda.testing.driver.NodeHandle
import net.corda.testing.driver.driver
import org.junit.Test
import java.util.concurrent.Future
import kotlin.test.assertEquals

class DriverBasedTest {
#set( $totalTestNodes = $convert.toInteger( $aib.getParam( "corda.total-test-nodes" ) ) - 1 )
#set( $testPartyType = $aib.getParam( "corda.test-party-type" ) )
#set( $lowercaseTestPartyType = $display.uncapitalize( ${testPartyType} ) )
#set( $countryCodes = ${convert.toStrings( ${aib.getParam( "corda.country-codes" )} )} )

#foreach( $nodeIndex in [0..${totalTestNodes}] ) 
    private val ${lowercaseTestPartyType}${nodeIndex} = TestIdentity(CordaX500Name("${testPartyType}${nodeIndex}", "", "${countryCodes.get(${nodeIndex})}"))
#end

    @Test
    fun `node test`() = withDriver {
#set( $varDeclaration = "val (")
#set( $startNodes = "startNodes(" )
#foreach( $nodeIndex in [0..${totalTestNodes}] ) 
#set( $varDeclaration = "${varDeclaration}party${nodeIndex}Handle" )
#set( $startNodes = "${startNodes}${lowercaseTestPartyType}${nodeIndex}" )
#if ( $velocityCount < $totalTestNodes )
#set( $varDeclaration = "${varDeclaration},  ")
#set( $startNodes = "${startNodes}, ")
#end##if ( $velocityCount < $totalTestNodes )
#end
#set( $varDeclaration = "${varDeclaration})" )
#set( $startNodes = "${startNodes})" )

        // Start a set of nodes and wait for them to be ready.
        $varDeclaration = $startNodes

        // From each node, make an RPC call to retrieve another node's name from the network map, to verify that the
        // nodes have started and can communicate.

        // This is a very basic test: in practice tests would be starting flows, and verifying the states in the vault
        // and other important metrics to ensure that your CorDapp is working as intended.
#foreach( $nodeIndex in [0..${totalTestNodes}] )
        assertEquals(${lowercaseTestPartyType}${nodeIndex}.name, party1Handle.resolveName(${lowercaseTestPartyType}${nodeIndex}.name))
#end
    }

    // Runs a test inside the Driver DSL, which provides useful functions for starting nodes, etc.
    private fun withDriver(test: DriverDSL.() -> Unit) = driver(
        DriverParameters(isDebug = true, startNodesInProcess = true)
    ) { test() }

    // Makes an RPC call to retrieve another node's name from the network map.
    private fun NodeHandle.resolveName(name: CordaX500Name) = rpc.wellKnownPartyFromX500Name(name)!!.name

    // Resolves a list of futures to a list of the promised values.
    private fun <T> List<Future<T>>.waitForAll(): List<T> = map { it.getOrThrow() }

    // Starts multiple nodes simultaneously, then waits for them all to be ready.
    private fun DriverDSL.startNodes(vararg identities: TestIdentity) = identities
        .map { startNode(providedName = it.name) }
        .waitForAll()
}