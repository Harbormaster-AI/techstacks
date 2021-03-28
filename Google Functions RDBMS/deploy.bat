@echo ******************************************************************
@echo * realMethods : downloading dependent module(s)
@echo ******************************************************************
call npm install functions

#if (${aib.getParam('gcp-functions.useFirebase')} == "true" )
@echo ******************************************************************
@echo * realMethods : deploying exported functions thru GCP/Firebase
@echo ******************************************************************
call firebase login
call firebase --project ${aib.getParam('gcp-functions.projectId')} deploy --only functions
#end ##if (${aib.getParam('gcp-functions.useFirebase')} == "true" )