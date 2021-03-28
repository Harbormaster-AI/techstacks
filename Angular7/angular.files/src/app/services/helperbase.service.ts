/** 
	Base class of all Components.  
	For convenience, contains all enums and entity lists 
**/
export class HelperBaseService {

    public ormUrl: string;

    constructor() {
        this.ormUrl = '${aib.getParam("mongodb.mongooseHost")}:${aib.getParam("mongodb.mongoosePort")}';
    }
}