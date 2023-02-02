#set( $className = $classObject.getName() )
#set( $upperClassName = $classObject.getName().toUpperCase() )
#set( $lowercaseClassName = ${display.uncapitalize( $className )} )
#set( $includePKs = false )
#set( $includeHierarchy = true )
#set( $attributes = ${classObject.getAttributesOnly( $includeHierarchy, $includePKs )} )
import React, { Component } from 'react'
import ${className}Service from '../services/${className}Service'

class View${className}Component extends Component {
    constructor(props) {
        super(props)

        this.state = {
            id: this.props.match.params.id,
            ${lowercaseClassName}: {}
        }
    }

    componentDidMount(){
        ${className}Service.get${className}ById(this.state.id).then( res => {
            this.setState({${lowercaseClassName}: res.data});
        })
    }

    render() {
        return (
            <div>
                <br></br>
                <div className = "card col-md-6 offset-md-3">
                    <h3 className = "text-center"> View ${className} Details</h3>
                    <div className = "card-body">
#foreach( $attribute in $attributes )
#set( $name = $attribute.getName() )
#set( $lowercaseName = ${display.uncapitalize( $attribute.getName() )} )
                        <div className = "row">
                            <div className = "col" style={{textAlign:"right"}}><label> ${name}:&emsp; </label></div>
                            <div className = "col" style={{textAlign:"left"}}> { this.state.${lowercaseClassName}.${lowercaseName} }</div>
                        </div>
#end##foreach( $attribute in $attributes )
                    </div>
                </div>
            </div>
        )
    }
}

export default View${className}Component
