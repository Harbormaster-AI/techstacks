const { ApolloServer } = require('apollo-server');
const typeDefs = require('./schema');
const resolvers = require('./resolvers');

const { createStore } = require('./utils');
const context = async ({ req }) => {
	  return {};
	};
#set( $classes = $aib.getClassesToGenerate() )
#foreach( $class in $classes )
#set( $className = $class.getName() )
#set( $lowercaseClassName = ${Utils.lowercaseFirstLetter(${className})} )
const ${className}API = require('./datasources/${className}DS');
#end##foreach( $class in $classes )

const store = createStore();
const internalEngineConfig = require('./engine-config');	
const dataSources = () => ({
#foreach( $class in $classes )
#set( $className = $class.getName() )
	    ${className}API: new ${className}API({ store }),
#end##foreach( $class in $classes )
});

#foreach( $class in $classes )
#set( $className = $class.getName() )
#set( $lowercaseClassName = ${Utils.lowercaseFirstLetter(${className})} )
store.${lowercaseClassName}.sync();
#end##foreach( $class in $classes )

const server = new ApolloServer({
  typeDefs,
  context,
  resolvers,
  playground: { version: '1.7.25' },
  engine: {
    apiKey: process.env.ENGINE_API_KEY,
	...internalEngineConfig,
  },
  dataSources

});

server.listen().then(({ url }) => {
  console.log(`🚀 Server ready at ${url}`);
});


module.exports = {
    dataSources,
	context,
	typeDefs,
	resolvers,
	ApolloServer,
	store,
	server,
#foreach( $class in $classes )
#set( $className = $class.getName() )
#set( $lowercaseClassName = ${Utils.lowercaseFirstLetter(${className})} )
    ${className}API,
#end##foreach( $class in $classes )
};