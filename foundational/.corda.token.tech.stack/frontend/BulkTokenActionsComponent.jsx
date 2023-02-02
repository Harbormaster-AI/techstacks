#set( $classes = $aib.getClassesToGenerate() )
#set( $numClasses = $classes.size() )
#set( $tokenSystemName = $display.capitalize( ${aib.getParam( "corda.token-system-name" )} ) )
import React, { Component } from 'react'
import ReactDOM from 'react-dom'
import parse from 'html-react-parser'
#foreach( $class in $aib.getClassesToGenerate() )
#if( $class.isNotEmpty() == true )
#set( $className = $class.getName() )
import ${className}Service from '../services/${className}Service';
#end##if( $class.isNotEmpty() == true )
#end##foreach( $class in $aib.getClassesToGenerate() )
import CordaService from '../services/CordaService'

class BulkTokenActionsComponent extends Component {
    constructor(props) {
        super(props)

        this.state = {
#foreach( $class in $aib.getClassesToGenerate() )
#if( $class.isNotEmpty() == true )
#set( $className = $class.getName() )
#set( $lowercaseClassName = $display.uncapitalize( $className ) )
                ${lowercaseClassName}Id: "",
                ${lowercaseClassName}s: [],
#end##if( $class.isNotEmpty() == true )
#end##foreach( $class in $aib.getClassesToGenerate() )
                party: ""
        }

         this.bulkCreate = this.bulkCreate.bind(this);
         this.bulkDestroy = this.bulkDestroy.bind(this);
         this.bulkTransfer = this.bulkTransfer.bind(this);
         this.issueNew = this.issueNew.bind(this);

#foreach( $class in $aib.getClassesToGenerate() )
#if( $class.isNotEmpty() == true )
#set( $className = $class.getName() )
         this.handle${className}Change = this.handle${className}Change.bind(this);
#end##if( $class.isNotEmpty() == true )
#end##foreach( $class in $aib.getClassesToGenerate() )
         this.handlePartyChange = this.handlePartyChange.bind(this);
    }

    componentDidMount(){
#foreach( $class in $aib.getClassesToGenerate() )
#if( $class.isNotEmpty() == true )
#set( $className = $class.getName() )
#set( $lowercaseClassName = $display.uncapitalize( $className ) )
        ${className}Service.get${className}s().then((res) => {
            this.setState({ ${lowercaseClassName}s: res.data});
        });
#end##if( $class.isNotEmpty() == true )
#end##foreach( $class in $aib.getClassesToGenerate() )
    }

    bulkCreate = (e) => {
        e.preventDefault();

        if ( this.validateParty() ) {
            let object = this.getObject();

            CordaService.bulkCreateTokens(object, this.state.party).then( res => {
                ReactDOM.render(parse('Bulk Create Tokens status: ' + JSON.stringify(res.data)), document.getElementById('bulkResultsDivId'));
            });
        }
    }

    bulkDestroy = (e) => {
        e.preventDefault();

        if ( this.validateParty() ) {
            let object = this.getObject();

            CordaService.bulkDestroyTokens(object, this.state.party).then( res => {
                ReactDOM.render(parse('Bulk Destroy Tokens status: ' + JSON.stringify(res.data).replaceAll("},","},<br/>")), document.getElementById('bulkResultsDivId'));
            });
        }
    }

    bulkTransfer = (e) => {
        e.preventDefault();

        if ( this.validateParty() ) {
            let object = this.getObject();

            CordaService.bulkTransferTokens(object, this.state.party).then( res => {
                ReactDOM.render(parse('Bulk Transfer Tokens status: ' + JSON.stringify(res.data)), document.getElementById('bulkResultsDivId'));
            });
        }
    }

    issueNew = (e) => {
        e.preventDefault();

        if ( this.validateParty() ) {
            let object = this.getObject();

            CordaService.issueNew(object, this.state.party).then( res => {
                ReactDOM.render(parse('Issue New Tokens status: ' + JSON.stringify(res.data)), document.getElementById('bulkResultsDivId'));
            });
        }
    }

    transferToken = (e) => {
        e.preventDefault();

        if ( this.validateParty() ) {
            let object = this.getObject();

            CordaService.transferToken(object, this.state.party).then( res => {
                ReactDOM.render(parse('Transfer Tokens status: ' + JSON.stringify(res.data)), document.getElementById('bulkResultsDivId'));
            });
        }
    }

    validateParty(){
        if ( this.state.party === '' ) {
            alert( "Please select a Party" );
            return false;
        }
        else
            return true;
    }

    getObject() {
        let object = {
#foreach( $class in $aib.getClassesToGenerate() )
#if( $class.isNotEmpty() == true )
#set( $className = $class.getName() )
#set( $lowercaseClassName = $display.uncapitalize( $className ) )
#set( $output = "${lowercaseClassName}Id: this.state.${lowercaseClassName}Id" )
#if ( $velocityCount < $numClasses )
#set( $output = "${output},")
#end##if ( $velocityCount < $numClasses )
            $output
#end##if( $class.isNotEmpty() == true )
#end##foreach( $class in $aib.getClassesToGenerate() )
        };

        return object;
    }

#foreach( $class in $aib.getClassesToGenerate() )
#if( $class.isNotEmpty() == true )
#set( $className = $class.getName() )
#set( $lowercaseClassName = $display.uncapitalize( $className ) )
    handle${className}Change = (e) => {
        console.log(e.target.value + " ${className} Selected!!");
        this.setState({ ${lowercaseClassName}Id: e.target.value });
    }
#end##if( $class.isNotEmpty() == true )
#end##foreach( $class in $aib.getClassesToGenerate() )

    handlePartyChange = (e) => {
        console.log(e.target.value + " Party Selected!!");
        this.setState({ party: e.target.value });
    }

    render() {
        return (
            <div>
                <p/>
#foreach( $class in $aib.getClassesToGenerate() )
#if( $class.isNotEmpty() == true )
#set( $className = $class.getName() )
#set( $displayName = $classObject.findAttributeAsBestFitName() )
#set( $lowercaseClassName = $display.uncapitalize( $className ) )
                <label> ${className}s: </label>
                <select value={this.state.${lowercaseClassName}Id} onChange={this.handle${className}Change}>
                    <option value=""> -- Select a ${lowercaseClassName} -- </option>
                           {}
                        {this.state.${lowercaseClassName}s.map((${lowercaseClassName}) => <option value={${lowercaseClassName}.${lowercaseClassName}Id}>{${lowercaseClassName}.${displayName}}</option>)}
                </select>
                <br/>
#end##if( $class.isNotEmpty() == true )
#end##foreach( $class in $aib.getClassesToGenerate() )
                <br/>
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
                            <button className="btn btn-outline-primary btn-sm" onClick={this.bulkCreate}> Bulk Create Tokens</button>
                            &nbsp;<button className="btn btn-outline-primary btn-sm" onClick={this.bulkDestroy}> Bulk Destroy Tokens</button>
                            &nbsp;<button className="btn btn-outline-primary btn-sm" onClick={this.bulkTransfer}> Bulk Transfer Tokens</button>
                            &nbsp;<button className="btn btn-outline-primary btn-sm" onClick={this.issueNew}> Issue New </button>
                            &nbsp;<button className="btn btn-outline-primary btn-sm" onClick={this.transferToken}> Transfer ${tokenSystemName} Token </button>
            </div>
        )
    }
}

export default BulkTokenActionsComponent
