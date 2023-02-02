#set( $className = $classObject.getName() )
#set( $upperClassName = $classObject.getName().toUpperCase() )
#set( $lowercaseClassName = ${display.uncapitalize( $className )} )
#set( $includePKs = false )
#set( $includeHierarchy = true )
#set( $attributes = ${classObject.getAttributesOnly( $includeHierarchy, $includePKs )} )
#set( $numAttributes = $attributes.size() )
import React, { Component } from 'react'
import ${className}Service from '../services/${className}Service';

class Update${className}Component extends Component {
    constructor(props) {
        super(props)

        this.state = {
            id: this.props.match.params.id,
#foreach( $attribute in $attributes )
#set( $name = $attribute.getName() )
#set( $lowercaseName = ${display.uncapitalize( $attribute.getName() )} )
#set( $outputVarDeclaration = "${lowercaseName}: ${esc.singleQuote}${esc.singleQuote}")
#if ( $velocityCount < $numAttributes )
#set( $outputVarDeclaration = "${outputVarDeclaration},")
#end##if ( $velocityCount < $numAttributes )
                $outputVarDeclaration
#end##foreach( $attribute in $attributes )
        }
        this.update${className} = this.update${className}.bind(this);

#foreach( $attribute in $attributes )
#set( $name = $attribute.getName() )
        this.change${name}Handler = this.change${name}Handler.bind(this);
#end##foreach( $attribute in $attributes )
    }

    componentDidMount(){
        ${className}Service.get${className}ById(this.state.id).then( (res) =>{
            let ${lowercaseClassName} = res.data;
            this.setState({
#foreach( $attribute in $attributes )
#set( $name = $attribute.getName() )
#set( $lowercaseName = ${display.uncapitalize( $attribute.getName() )} )
#set( $outputVarDeclaration = "${lowercaseName}: ${lowercaseClassName}.${lowercaseName}")
#if ( $velocityCount < $numAttributes )
#set( $outputVarDeclaration = "${outputVarDeclaration},")
#end##if ( $velocityCount < $numAttributes )
                $outputVarDeclaration
#end##foreach( $attribute in $attributes )
            });
        });
    }

    update${className} = (e) => {
        e.preventDefault();
        let ${lowercaseClassName} = {
            ${lowercaseClassName}Id: this.state.id,
#foreach( $attribute in $attributes )
#set( $name = $attribute.getName() )
#set( $lowercaseName = ${display.uncapitalize( $attribute.getName() )} )
#set( $outputVarDeclaration = "${lowercaseName}: this.state.${lowercaseName}")
#if ( $velocityCount < $numAttributes )
#set( $outputVarDeclaration = "${outputVarDeclaration},")
#end##if ( $velocityCount < $numAttributes )
            $outputVarDeclaration
#end##foreach( $attribute in $attributes )
        };
        console.log('${lowercaseClassName} => ' + JSON.stringify(${lowercaseClassName}));
        console.log('id => ' + JSON.stringify(this.state.id));
        ${className}Service.update${className}(${lowercaseClassName}).then( res => {
            this.props.history.push('/${lowercaseClassName}s');
        });
    }

#foreach( $attribute in $attributes )
#set( $name = $attribute.getName() )
#set( $lowercaseName = ${display.uncapitalize( $attribute.getName() )} )
    change${name}Handler= (event) => {
        this.setState({${lowercaseName}: event.target.value});
    }
#end##foreach( $attribute in $attributes )

    cancel(){
        this.props.history.push('/${lowercaseClassName}s');
    }

    render() {
        return (
            <div>
                <br></br>
                   <div className = "container">
                        <div className = "row">
                            <div className = "card col-md-6 offset-md-3 offset-md-3">
                                <h3 className="text-center">Update ${className}</h3>
                                <div className = "card-body">
                                    <form>
                                        <div className = "form-group">
#foreach( $attribute in $attributes )
#set( $name = $attribute.getName() )
                                            <label> ${name}: </label>
                                            #formFields( $attribute, 'update')
#end##foreach( $attribute in $attributes )
                                        </div>
                                        <button className="btn btn-success" onClick={this.update${className}}>Save</button>
                                        <button className="btn btn-danger" onClick={this.cancel.bind(this)} style={{marginLeft: "10px"}}>Cancel</button>
                                    </form>
                                </div>
                            </div>
                        </div>

                   </div>
            </div>
        )
    }
}

export default Update${className}Component
