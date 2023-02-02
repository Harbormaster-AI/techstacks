import React, { Component } from 'react'
import ReactDOM from 'react-dom'
import CordaService from '../services/CordaService'
import parse from 'html-react-parser'

class CordaAdminComponent extends Component {
    constructor(props) {
        super(props)

        this.state = {
            party: "",
        }
         this.handlePartyChange = this.handlePartyChange.bind(this);
    }

    componentDidMount(){
    }

    validateParty(){
        if ( this.state.party == '' ) {
            alert( "Please select a Party" );
            return false;
        }
        else
            return true;
    }

    flows = (e) => {
        e.preventDefault();

        if ( this.validateParty() ) {
            CordaService.flows( this.state.party).then( res => {
                ReactDOM.render(parse(JSON.stringify(res.data).replaceAll(",","<br/>")), document.getElementById('resultsDivId'));
            });
        }
    }

    networkParameters = (e) => {
        e.preventDefault();

        CordaService.networkParameters( this.state.party).then( res => {
            ReactDOM.render(parse(JSON.stringify(res.data).replaceAll("\\n","<br/>")), document.getElementById('resultsDivId'));
        });
    }

    notaryIdentities = (e) => {
        e.preventDefault();

        CordaService.notaryIdentities( this.state.party).then( res => {
            ReactDOM.render(JSON.stringify(res.data), document.getElementById('resultsDivId'));
        });
    }

    nodeInfo = (e) => {
        e.preventDefault();

        CordaService.nodeInfo( this.state.party).then( res => {
            ReactDOM.render(JSON.stringify(res.data), document.getElementById('resultsDivId'));
        });
    }

    nodeDiagnosticInfo = (e) => {
        e.preventDefault();

        CordaService.nodeDiagnosticInfo( this.state.party).then( res => {
             ReactDOM.render(parse(JSON.stringify(res.data).replaceAll(",","<br/>")), document.getElementById('resultsDivId'));
        });
    }

    vaultQuery = (e) => {
        e.preventDefault();

        CordaService.vaultQuery( this.state.party).then( res => {
             ReactDOM.render(   parse(JSON.stringify(res.data)
                                    .replaceAll("StateAndRef","<p/>StateAndRef")
                                    .replaceAll("\\r\\n","<br/>")),
                                document.getElementById('resultsDivId'));
        });
    }

    handlePartyChange = (e) => {
        console.log(e.target.value + " Party Selected!!");
        this.setState({ party: e.target.value });
    }

    render() {
        return (
            <div>
                <p/>
                <label> Party:&emsp;  </label>
                <select value={this.state.party} onChange={this.handlePartyChange}>
                    <option value=""> -- Select a party -- </option>
#establishCordaNodes()		
#foreach( $node in $cordaNodes )
#set( $node = $convert.toStrings( $node ) )
#set( $enumName = $node.get(0) )
                    <option value="${enumName}">${enumName}</option>
#end##foreach( $node in cordaNodes )
                </select>
                <p/>
                    <button className="btn btn-outline-primary btn-sm" onClick={this.flows}> Flows</button>
                    &nbsp;<button className="btn btn-outline-primary btn-sm" onClick={this.networkParameters}>  Network Parameters</button>
                    &nbsp;<button className="btn btn-outline-primary btn-sm" onClick={this.notaryIdentities}>  Notary Identities</button>
                    &nbsp;<button className="btn btn-outline-primary btn-sm" onClick={this.nodeInfo}>  Node Info</button>
                    &nbsp;<button className="btn btn-outline-primary btn-sm" onClick={this.nodeDiagnosticInfo}>  Node Diagnostic Info</button>
                    &nbsp;<button className="btn btn-outline-primary btn-sm" onClick={this.vaultQuery}>  Vault Query </button>
                <p/>
                <hr/>
                <h6> Results </h6>
                <div id="resultsDivId" style={{
                                                height: '400px',
                                                fontSize: '0.8em',
                                                wordBreak: 'break-all',
                                                whiteSpace: 'pre-wrap',
                                                overflow: 'auto'
                                              }}></div>
            </div>
        )
    }
}

export default CordaAdminComponent
