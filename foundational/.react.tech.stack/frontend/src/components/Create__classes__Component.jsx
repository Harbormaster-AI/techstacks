#set( $className = $classObject.getName() )
#set( $upperClassName = $classObject.getName().toUpperCase() )
#set( $lowercaseClassName = ${display.uncapitalize( $className )} )
#set( $includePKs = false )
#set( $includeHierarchy = true )
#set( $attributes = ${classObject.getAttributesOnly( $includeHierarchy, $includePKs )} )
#set( $numAttributes = $attributes.size() )
import React, { Component } from 'react'
import ${className}Service from '../services/${className}Service';

class Create${className}Component extends Component {
    constructor(props) {
        super(props)

        this.state = {
            // step 2
            id: this.props.match.params.id,
#foreach( $attribute in $attributes )
#set( $name = $attribute.getName() )
#set( $lowercaseName = ${display.uncapitalize( $attribute.getName() )} )
#set( $outputVarDeclaration = "${lowercaseName}: ${esc.s}${esc.s}")
#if ( $velocityCount < $numAttributes )
#set( $outputVarDeclaration = "${outputVarDeclaration},")
#end##if ( $velocityCount < $numAttributes )
                $outputVarDeclaration
#end##foreach( $attribute in $attributes )
        }
#foreach( $attribute in $attributes )
#set( $name = $attribute.getName() )
        this.change${name}Handler = this.change${name}Handler.bind(this);
#end##foreach( $attribute in $attributes )
    }

    // step 3
    componentDidMount(){

        // step 4
        if(this.state.id === '_add'){
            return
        }else{
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
    }
    saveOrUpdate${className} = (e) => {
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

        // step 5
        if(this.state.id === '_add'){
            ${lowercaseClassName}.${lowercaseClassName}Id=''
            ${className}Service.create${className}(${lowercaseClassName}).then(res =>{
                this.props.history.push('/${lowercaseClassName}s');
            });
        }else{
            ${className}Service.update${className}(${lowercaseClassName}).then( res => {
                this.props.history.push('/${lowercaseClassName}s');
            });
        }
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

    getTitle(){
        if(this.state.id === '_add'){
            return <h3 className="text-center">Add ${className}</h3>
        }else{
            return <h3 className="text-center">Update ${className}</h3>
        }
    }
    render() {
        return (
            <div>
                <br></br>
                   <div className = "container">
                        <div className = "row">
                            <div className = "card col-md-6 offset-md-3 offset-md-3">
                                {
                                    this.getTitle()
                                }
                                <div className = "card-body">
                                    <form>
                                        <div className = "form-group">
#foreach( $attribute in $attributes )
#set( $name = $attribute.getName() )
                                            <label> ${name}:&emsp; </label>
                                            #formFields( $attribute, 'create')
#end##foreach( $attribute in $attributes )
                                        </div>

                                        <button className="btn btn-outline-success" onClick={this.saveOrUpdate${className}}>Save</button>
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

export default Create${className}Component
