import React, { useState, useEffect } from "react";
import axios from "axios";
import UsersService from "../services/UsersService";
import { FaUser, FaCross, FaPlay } from "react-icons/fa";
import "./userListComponent.css";

function UsserListComponent() {
  const [users, setUsers] = useState([]);
  const [search, setSearch] = useState("");

  useEffect(() => {
    UsersService.getAllUsers()
      .then((data) => {
        setUsers(data.users);
      })
      .catch((error) => {
        console.log(error);
      });
  }, []);

  return (
    <div className="container-fluid    text-white w-100">
      <div className="col col-md-12">
        <p> Search </p>
        <div className="scrollable-container">
          <ul className="list-unstyled d-flex flex-wrap">
            {users.map((user, index) => (
              <li
                key={index}
                className="p-2 border border-white rounded m-2 w-100"
              >
                <div className="d-flex w-100">
                  <div>
                    <FaUser size={50} />
                    <strong>{user.username}</strong> - {user.rating}
                  </div>
                  <div className="ml-auto">
                    <button
                      className="btn btn-danger p-2"
                      onClick={() => handleBanUser(user.username)}
                    >
                      Ban User
                    </button>
                  </div>
                </div>
              </li>
            ))}
          </ul>
        </div>
      </div>
    </div>
  );
}

export default UsserListComponent;
