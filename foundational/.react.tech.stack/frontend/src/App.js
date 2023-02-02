import React from 'react';
import './App.css';
import {BrowserRouter as Router, Route, Switch} from 'react-router-dom'
import HomePageComponent from './components/HomePageComponent';
import HeaderComponent from './components/HeaderComponent';
import FooterComponent from './components/FooterComponent';
#foreach( $class in $aib.getClassesToGenerate() )
#if( $class.isNotEmpty() == true )
#set( $className = $class.getName() )
import List${className}Component from './components/List${className}Component';
import Create${className}Component from './components/Create${className}Component';
import View${className}Component from './components/View${className}Component';
#end##if( $class.isNotEmpty() == true )
#end##foreach( $class in $aib.getClassesToGenerate() )
#outputExtraInclusionComponents()
function App() {
  return (
    <div>
        <Router>
                <HeaderComponent className="header"/>
                <div className="container">
                    <Switch>
                          <Route path = "/" exact component = {HomePageComponent}></Route>
#foreach( $class in $aib.getClassesToGenerate() )
#if( $class.isNotEmpty() == true )
#set( $className = $class.getName() )
#set( $lowercaseClassName = ${display.uncapitalize( $className )} )
                            <Route path = "/${lowercaseClassName}s" component = {List${className}Component}></Route>
                            <Route path = "/add-${lowercaseClassName}/:id" component = {Create${className}Component}></Route>
                            <Route path = "/view-${lowercaseClassName}/:id" component = {View${className}Component}></Route>
                          {/* <Route path = "/update-${lowercaseClassName}/:id" component = {Update${className}Component}></Route> */}
#end##if( $class.isNotEmpty() == true )
#end##foreach( $class in $aib.getClassesToGenerate() )
#outputExtraRoutePaths()
                    </Switch>
                </div>
              <FooterComponent />
        </Router>
    </div>
    
  );
}

export default App;
