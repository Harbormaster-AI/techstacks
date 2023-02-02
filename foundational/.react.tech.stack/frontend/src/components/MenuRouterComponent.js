#set( $appName = $aib.getApplicationNameFormatted() )
import React from "react";
import 'bootstrap/dist/css/bootstrap.css';
import {Nav, Navbar, Container, NavDropdown} from 'react-bootstrap';
const MenuRouter = props => {
  return (
     <Navbar bg="info" expand="md">
     <Container>
       <Navbar.Brand href="/">${appName}</Navbar.Brand>
       <Navbar.Toggle aria-controls="basic-navbar-nav" />
       <Navbar.Collapse id="basic-navbar-nav">
         <Nav className="me-auto">
           <NavDropdown title="Lists" id="basic-nav-dropdown">
#foreach( $class in $aib.getClassesToGenerate() )
#if( $class.isNotEmpty() == true )
#set( $className = $class.getName() )
#set( $lowercaseClassName = ${display.uncapitalize( $className )} )
            <NavDropdown.Item href="/${lowercaseClassName}s">${className} List</NavDropdown.Item>
#end##if( $class.isNotEmpty() == true )
#end##foreach( $class in $aib.getClassesToGenerate() )
           </NavDropdown>
#outputExtraMenuItems()
         </Nav>
       </Navbar.Collapse>
     </Container>
   </Navbar>
   );
};

export default MenuRouter;
