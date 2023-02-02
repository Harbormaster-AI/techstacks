#set( $systemName = $display.capitalize( $aib.getParam( "corda.token-system-name" ) ) )
#set( $lowercaseSystemName = $display.uncapitalize( $systemName ) )
#set( $classes = $aib.getClassesToGenerate() )
#set( $numClasses = $classes.size() )
#set( $serialNumbers = [] )
#set( $firstClassName =  $display.uncapitalize( $classes.get(0).getName() ) )
#set( $classNames = [] )
#set( $identifierFieldName = $display.capitalize( $aib.getParam( "corda.identifier-field-name" ) ) )
## macro call to setup the cordaNodes data
#establishCordaNodes()
#set( $numberOfParties = $cordaNodes.size() )
#foreach( $class in $classes )
#set( $eatAddResponse = $classNames.add( $class.getName() ) )
#end##foreach( $class in $classes )

# ${systemName} Market - TokenSDK

## Introduction 
This Cordapp shows simple flows related to the token SDK. In this Cordapp, we will mimic a ${lowercaseSystemName} buying and selling market with $numberOfParties standard parties: 
#foreach( $node in $cordaNodes )
#set( $node = $convert.toStrings( $node ) )
- $node[0]
#end##foreach( $node in $cordaNodes )

From the above chart we see that Tokens are representing the ownership and status of the physical assets ( $classNames ). A key point to notice here is that **a ${lowercaseSystemName} is represented with $numClasses tokens (${classNames})**. This is designed in the way to be flexible to sell or total a specific part of your ${lowercaseSystemName}. As you can see, this ${lowercaseSystemName} buying/selling market is capable of mimicking multiple business logics. We will be demonstrating one of the possible logic here:

1. ${systemName} Company manufactures the ${lowercaseSystemName}s
2. ${systemName} Company can sell the ${lowercaseSystemName} to licensed dealer and buyers. 
3. A parts agency can get the used ${lowercaseSystemName} parts from the licensed dealer or buyers. 
4. When there is a need to destroy the physical ${lowercaseSystemName} part, the current owner of the physical part will redeem the token with the ${systemName}Co

Throughout the app, you can see how to create, transact, and redeem a token. 

## Running the app

Once you have cloned this Git repository, you are ready to run this app.

First deploy and run the Corda node network:

```
./gradlew deployNodes
./build/nodes/runnodes
```
Be patient while Corda is setting nodes. If you have any questions during setup of your Corda node netword, please go to https://docs.corda.net/getting-set-up.html for detailed setup instructions.

Next, if you wish to interact with the system via the browser, run the Spring Boot app to make available the Restful APIs:

```
./gradlew run
``` 

To access the APIs in your browser:

```
http://localhost:${aib.getParam("application.port")}/swagger-ui/
```

Once all ${numberOfParties} nodes are started up either choose a node shell and run: 

```
#foreach( $class in $classes )
#set( $name = $class.getName() )
#set( $lowercaseName = $display.uncapitalize( $name ) )
#set( $serialNum = ${math.random(1000,9999)} )
#set( $eatAddResponse = $serialNumbers.add( ${serialNum} ) )
flow start Create${name}Token ${lowercaseName}Serial: $serialNum
#end##foreach( $class in $classes )
```

OR from the browser:

```
http://localhost:8011/swagger-ui/#/corda-admin-rest-controller/bulkCreateTokensUsingPUT
```
and supply the ${serialNum}s and the target party to create the tokens on.


After this step, you have created $numClasses tokens representing the physical ${lowercaseSystemName} part with unique serial numbers(which will be unique in the manufacturing).
 
Then either from the same node shell run:

```
#set( $flow = "flow start IssueNew${systemName} ")
#foreach( $class in $classes )
#set( $name = $class.getName() )
#set( $lowercaseName = $display.uncapitalize( $name ) )
#set( $index = ${velocityCount} - 1 )
#set( $serialNum = $serialNumbers.get( $index ) )
#set( $flow = "${flow} ${lowercaseName}Serial: ${serialNum}, " )
#end##foreach( $class in $classes )
#set( $flow = "${flow} holder: LicensedDealership" )
$flow
```
Substitute the __LicensedDealership__ with the name of the node you are in.

OR from the browser:

```
http://localhost:8011/swagger-ui/#/corda-admin-rest-controller/issueNewAutomobileUsingPUT
```

and again supply the ${serialNum}s and the target party to issue the tokens to/

This will transfer the ${numClasses} tokens together to represent a single ${lowercaseSystemName}) to the targeted party. 

Now we can see we did receive the tokens at targeted node shell by either running the following within that shell: 

```
run vaultQuery contractStateType: com.r3.corda.lib.tokens.contracts.states.EvolvableTokenType
```

OR from the browser:

```
http://localhost:8011/swagger-ui/#/corda-admin-rest-controller/vaultQueryUsingGET
```
and select the targeted Party.

Continue to the business flow, the targeted Party will sell the ${lowercaseSystemName} to the another Party (call them the Buyer)  

Either run from within the Buyer Party node: 

```
#set( $flow = "flow start Transfer${systemName}Token ")
#foreach( $class in $classes )
#set( $name = $class.getName() )
#set( $lowercaseName = $display.uncapitalize( $name ) )
#set( $index = ${velocityCount} - 1 )
#set( $serialNum = $serialNumbers.get( $index ) )
#set( $flow = "${flow} ${lowercaseName}Serial: ${serialNum}, ")
#end##foreach( $class in $classes )
#set( $flow = "${flow} holder: Buyer" )
$flow
```
where Buyer here is the name of the node Party you are considering the actual buyer.

OR

from the browser:

```
http://localhost:8011/swagger-ui/#/braking-token-command-rest-controller/transferTokenUsingPOST_1
```

Substitute the name of your part for the word __braking__ in the URL. Enter the part's ${serialNum} and the destination Party.  


Now we can check at the target Party ("Buyer") node shell to see if the buyer receives the token by running the same `vaultQuery` we just ran at the source Party ("Seller") shell. 

At the Buyer side, we would assume we got a recall notice and will send the physical ${lowercaseSystemName} back to the manufacturer. The action will happen in real life, but on the ledger we will also need to "destroy"(process of redeem in Corda TokenSDK) the $firstClassName token. Run:

```
#set( $serialNum = $serialNumbers.get( 0 ) )
flow start TotalPart part: ${firstClassName}, serial: $serialNum
```

OR from the browser, for individual part destroying:

```
http://localhost:8011/swagger-ui/#/braking-token-command-rest-controller/destroyTokenUsingDELETE_1
```

Substitute the name of your part for the word __braking__ in the URL

OR for bulk destroying:

```
http://localhost:8011/swagger-ui/#/corda-admin-rest-controller/bulkDestroyTokensUsingDELETE
```

and enter values for the ${identiferFieldName} for the parts to transfer and select the targeted Party.

At the buyer's shell or from the browser, if we do the [vaultQuery](https://docs.corda.net/docs/corda-os/api-vault-query.html#api-vault-query) again, we will see we now have all but the $firstClassName tokens. With the remaining tokens, we can sell the remaining parts to the another party ("User Parts Agency"). We will achieve it by either running: 

```
#foreach( $class in $classes )
#set( $flow = "flow start TransferPartToken part ")
#if ( $velocityCount > 1 )## skip the first one
#set( $name = $class.getName() )
#set( $lowercaseName = $display.uncapitalize( $name ) )
#set( $index = ${velocityCount} - 1 )
#set( $serialNum = $serialNumbers.get( $index ) )
#set( $flow = "${flow} ${lowercaseName}Serial: ${serialNum}, ")
#set( $flow = "${flow} holder: UsedPartsAgency" )
$flow
#end##if ( $velocityCount > 1 )## skip the first one
#end##foreach( $class in $classes )
```

OR from the browser for individual part transfer:

```
http://localhost:8011/swagger-ui/#/chassis-token-command-rest-controller/transferTokenUsingPOST_2
```

OR from the browser for bulk part transfer:
```
http://localhost:8011/swagger-ui/#/corda-admin-rest-controller/bulkTransferTokensUsingPOST
```

and enter values for the ${identiferFieldName} for the parts to transfer and select the targeted Party.

At the end of the flow logic, we will find the $firstClassName token is destroyed and the used parts agency holds the remaining parts tokens. 





