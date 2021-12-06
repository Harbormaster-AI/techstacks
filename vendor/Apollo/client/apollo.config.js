module.exports = {
  client: {
    name: '${aib.getApplicationName()}',
    service: '${aib.getParam("apollo.service")}' // the name of your service created online by the Apollo Engine (service:<YOUR_SERVICE_NAME>:<THE_TOKEN>)
  },
};
