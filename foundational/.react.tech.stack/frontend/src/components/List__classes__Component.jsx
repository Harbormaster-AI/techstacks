#set( $className = $classObject.getName() )
#set( $upperClassName = $classObject.getName().toUpperCase() )
#set( $lowercaseClassName = ${display.uncapitalize( $className )} )
#set( $includePKs = false )
#set( $includeHierarchy = true )
#set( $attributes = ${classObject.getAttributesOnly( $includeHierarchy, $includePKs )} )
import React, { Component } from 'react'
import ${className}Service from '../services/${className}Service'

class List${className}Component extends Component {
    constructor(props) {
        super(props)

        this.state = {
                ${lowercaseClassName}s: []
        }
        this.add${className} = this.add${className}.bind(this);
        this.edit${className} = this.edit${className}.bind(this);
        this.delete${className} = this.delete${className}.bind(this);
    }

    delete${className}(id){
        ${className}Service.delete${className}(id).then( res => {
            this.setState({${lowercaseClassName}s: this.state.${lowercaseClassName}s.filter(${lowercaseClassName} => ${lowercaseClassName}.${lowercaseClassName}Id !== id)});
        });
    }
    view${className}(id){
        this.props.history.push(`/view-${lowercaseClassName}/${esc.dollar}{id}`);
    }
    edit${className}(id){
        this.props.history.push(`/add-${lowercaseClassName}/${esc.dollar}{id}`);
    }

    componentDidMount(){
        ${className}Service.get${className}s().then((res) => {
            this.setState({ ${lowercaseClassName}s: res.data});
        });
    }

    add${className}(){
        this.props.history.push('/add-${lowercaseClassName}/_add');
    }

    render() {
        return (
            <div>
                 <h2 className="text-center">${className} List</h2>
                 <div className = "row">
                    <button className="btn btn-primary btn-sm" onClick={this.add${className}}> Add ${className}</button>
                 </div>
                 <br></br>
                 <div className = "row">
                        <table className = "table table-striped table-bordered">

                            <thead>
                                <tr>
#foreach( $attribute in $attributes )
                                    <th> ${display.capitalize( ${attribute.getName()} )} </th>
#end##foreach( $attribute in $attributes )
                                    <th> Actions</th>
                                </tr>
                            </thead>
                            <tbody>
                                {
                                    this.state.${lowercaseClassName}s.map(
                                        ${lowercaseClassName} => 
                                        <tr key = {${lowercaseClassName}.${lowercaseClassName}Id}>
#foreach( $attribute in $attributes )
#set( $lowercaseName = ${display.uncapitalize( $attribute.getName() )} )
                                             <td> { ${lowercaseClassName}.${lowercaseName} } </td>
#end##foreach( $attribute in $attributes )
                                             <td>
                                                 <button onClick={ () => this.edit${className}(${lowercaseClassName}.${lowercaseClassName}Id)} className="btn btn-outlie-info btn-sm">Update </button>
                                                 <button style={{marginLeft: "10px"}} onClick={ () => this.delete${className}(${lowercaseClassName}.${lowercaseClassName}Id)} className="btn btn-danger btn-sm">Delete </button>
                                                 <button style={{marginLeft: "10px"}} onClick={ () => this.view${className}(${lowercaseClassName}.${lowercaseClassName}Id)} className="btn btn-outline-info btn-sm">View </button>
                                             </td>
                                        </tr>
                                    )
                                }
                            </tbody>
                        </table>

                 </div>

            </div>
        )
    }
}

export default List${className}Component
