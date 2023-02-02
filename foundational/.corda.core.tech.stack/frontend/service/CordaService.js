#set( $tokenSystemName = $display.capitalize( ${aib.getParam( "corda.token-system-name" )} ) )
#set( $apiBaseUrl = "CORDA_API_BASE_URL" )
import axios from 'axios';

const ${apiBaseUrl} = "${aib.getParam("react.restapi-url")}/${tokenSystemName}";

class CordaService {
#outputExtraCordaServices()
    flows(party){
        return axios.get(${apiBaseUrl} + '/flows?partyEnum=' + party);
    }

    networkParameters(party){
        return axios.get(${apiBaseUrl} + '/networkParameters?partyEnum=' + party);
    }

    notaryIdentities(party){
        return axios.get(${apiBaseUrl} + '/notaryIdentities?partyEnum=' + party);
    }

    nodeInfo(party){
        return axios.get(${apiBaseUrl} + '/nodeInfo?partyEnum=' + party);
    }

    nodeDiagnosticInfo(party){
        return axios.get(${apiBaseUrl} + '/nodeDiagnosticInfo?partyEnum=' + party);
    }

    vaultQuery(party){
        return axios.get(${apiBaseUrl} + '/vaultQuery?partyEnum=' + party);
    }
}

export default new CordaService()
