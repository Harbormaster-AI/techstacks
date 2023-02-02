#set( $className = $classObject.getName() )
#set( $upperClassName = $classObject.getName().toUpperCase() )
#set( $lowercaseClassName = ${display.uncapitalize( $className )} )
#set( $apiBaseUrl = "${upperClassName}_API_BASE_URL" )
import axios from 'axios';

const ${apiBaseUrl} = "${aib.getParam("react.restapi-url")}/${className}";

class ${className}Service {

    get${className}s(){
        return axios.get(${apiBaseUrl} + '/' );
    }

    create${className}(${lowercaseClassName}){
        return axios.post(${apiBaseUrl}  + '/create', ${lowercaseClassName});
    }

    get${className}ById(${lowercaseClassName}Id){
        return axios.get(${apiBaseUrl} + '/load?${lowercaseClassName}Id=' + ${lowercaseClassName}Id);
    }

    update${className}(${lowercaseClassName}){
        return axios.put(${apiBaseUrl} + '/update', ${lowercaseClassName});
    }

    delete${className}(${lowercaseClassName}Id){
        return axios.delete(${apiBaseUrl} + '/delete?${lowercaseClassName}Id=' + ${lowercaseClassName}Id);
    }
}

export default new ${className}Service()