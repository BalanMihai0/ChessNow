import React, { useEffect, useState } from "react";
import { useNavigate } from "react-router-dom";
import Login from "../components/loginComponent";
import Register from "../components/registerComponent";
import UserListComponent from "../components/userListComponent";
import RegisterComponent from "../components/registerComponent";
import TokenService from "../services/TokenService";

const AdminPage = () => {
  const navigate = useNavigate();
  useEffect(() => {
    const token = TokenService.getDecoded(TokenService.getAccessToken());
    if (token && token.isAdmin) {
    }
  }, []);

  const handleLogOut = () => {
    TokenService.handleLogOut();
    navigate("/");
    window.location.reload();
  };
  return (
    <div className="container">
      <div align="center" className="p-5">
        <button className="btn btn-warning " onClick={handleLogOut}>
          Log Out
        </button>
      </div>
      <div className="row">
        <div className="col-md-6 border-end pe-4">
          <UserListComponent />
        </div>
        <div className="col-md-6 ps-4">
          <RegisterComponent />
        </div>
      </div>
    </div>
  );
};

export default AdminPage;
