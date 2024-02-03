import React, { useState, useEffect } from "react";
import { Container, Navbar, Nav, Button } from "react-bootstrap";
import logo from "../assets/images/logo2-nobg.png";
import {
  FaHome,
  FaUser,
  FaEnvelope,
  FaSignInAlt,
  FaChartBar,
} from "react-icons/fa";
import { endMatch } from "../services/MoveService";
import { useNavigate } from "react-router-dom";
import TokenService from "../services/TokenService";

const SidebarNavbar = () => {
  const [collapsed, setCollapsed] = useState(false);
  const [token, setToken] = useState(localStorage.getItem("accessToken"));
  const navigate = useNavigate();

  useEffect(() => {
    // Update the token state when the component mounts
    setToken(localStorage.getItem("accessToken"));
  }, []);

  const toggleSidebar = () => {
    setCollapsed(!collapsed);
  };

  const handleLogOut = () => {
    TokenService.handleLogOut();
    //on log out, resign
    const endMatchData = {
      matchId: localStorage.getItem("matchId"),
      matchResult: "Loss",
    };
    endMatch(endMatchData);
    navigate("/");
    window.location.reload();
  };

  return (
    <Container fluid className="p-0">
      <Navbar
        id="navbar"
        variant="dark"
        expand="lg"
        className="col-md-3 col-lg-2 d-md-block bg-dark sidebar p-0 align-items-start"
        style={{ height: "100%" }}
      >
        <Navbar.Toggle aria-controls="sidebar-nav" onClick={toggleSidebar} />
        <Navbar.Collapse id="sidebar-nav" className={collapsed ? "show" : ""}>
          <div className="mb-3">
            <i className="bi bi-house fs-4"></i>
          </div>
          <Nav className="flex-column">
            <Nav.Link href="/">
              <img src={logo} height="90" width="180" alt="ChessNowBrand" />
            </Nav.Link>
            <Nav.Link href="/">
              {" "}
              <FaHome /> Home
            </Nav.Link>
            <Nav.Link href="profilepage" id="profilePage">
              {" "}
              <FaUser /> Profile
            </Nav.Link>
            <Nav.Link href="userpage" id="socialButton">
              <FaEnvelope /> Social
            </Nav.Link>

            {/* Conditionally render "Login" link based on the presence of a token */}
            {!token && (
              <Nav.Link href="loginregister" id="loginButton">
                <FaSignInAlt /> Login
              </Nav.Link>
            )}

            <Nav.Link href="#reports" disabled>
              {" "}
              <FaChartBar /> Reports{" "}
            </Nav.Link>

            {/* Conditionally render "Create an account" button based on the presence of a token */}
            {!token && (
              <Nav.Link href="loginregister" className=" text-center">
                <button className="btn btn-warning">Create an account</button>
              </Nav.Link>
            )}

            {token && (
              <Nav.Link className=" text-center">
                <button className="btn btn-warning" onClick={handleLogOut}>
                  Log Out
                </button>
              </Nav.Link>
            )}
          </Nav>
        </Navbar.Collapse>
      </Navbar>
    </Container>
  );
};

export default SidebarNavbar;
