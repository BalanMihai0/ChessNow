import React, { useEffect, useState } from "react";
import { ToastContainer, toast } from "react-toastify";
import "react-toastify/dist/ReactToastify.css";
import UsersService from "../services/UsersService";
import { FaSearch, FaUser } from "react-icons/fa";
import { useNavigate } from "react-router-dom";
import TokenService from "../services/TokenService";
import { Client } from "@stomp/stompjs";
import "./MainPage.css";
import LOCALHOSTWS from "../services/URLWS";

const UserPage = () => {
  const [userData, setUserData] = useState([]);
  const [stompClient, setStompClient] = useState();
  const [decodedToken, setDecodedToken] = useState([]);
  const [username, setUsername] = useState(null);
  const [filteredUsers, setFilteredUsers] = useState([]);
  const navigate = useNavigate();
  const [ascending, setAscending] = useState(false);
  const [descending, setDescending] = useState(false);

  const handleAscendingChange = () => {
    setAscending(!ascending);
    setDescending(false);
  };

  const handleDescendingChange = () => {
    setDescending(!descending);
    setAscending(false);
  };

  useEffect(() => {
    const fetchData = async () => {
      try {
        let sortedUsers;

        if (ascending) {
          sortedUsers = await UsersService.getSortedUsers("true");
        } else if (descending) {
          sortedUsers = await UsersService.getSortedUsers("false");
        } else {
          // Handle the case where neither ascending nor descending is selected
          return;
        }

        const filteredUsers = sortedUsers.sortedUsers.filter(
          (user) => user.name !== "AI" && user.name !== "admin"
        );

        setUserData(await Promise.all(filteredUsers));
      } catch (error) {
        // Handle errors if any
        console.error("Error fetching and sorting users:", error);
      }
    };

    fetchData();
  }, [ascending, descending]);

  useEffect(() => {
    //load user list
    UsersService.getUsersSocial()
      .then(async (data) => {
        //remove users with usernames "AI" and "Admin"
        console.log(data);
        const filteredUsers = await Promise.all(
          data.users.filter(
            (user) =>
              user.name !== "AI" &&
              user.name !== "admin" &&
              user.name !== decodedToken.username
          )
        );

        console.log(filteredUsers);
        setUserData(filteredUsers);
      })
      .catch((error) => {
        console.log(error);
      });

    //deactivate the WebSocket on component unmount
    return () => {
      if (stompClient) {
        stompClient.deactivate();
      }
    };
  }, []);

  useEffect(() => {
    if (TokenService.getAccessToken()) {
      setDecodedToken(TokenService.getDecoded(TokenService.getAccessToken()));
      setUsername(decodedToken.username);
    } else {
      navigate("/loginregister");
    }
  }, []);

  useEffect(() => {
    if (username !== null) {
      setupStompClient(decodedToken.username);
    }
  }, [username]);

  const setupStompClient = (username) => {
    // stomp client over websockets
    const stompClient = new Client({
      brokerURL: `ws://${LOCALHOSTWS}/wsinvites`,
      reconnectDelay: 5000,
      heartbeatIncoming: 4000,
      heartbeatOutgoing: 4000,
    });

    stompClient.onConnect = () => {
      // subscribe to the backend public topic
      stompClient.subscribe("/invites", (data) => {
        console.log("subscribe" + data);
        onMessageReceived(data);
      });
    };

    // initiate client
    stompClient.activate();

    // maintain the client for sending and receiving
    setStompClient(stompClient);
  };

  const generateNumericId = () => {
    return Math.floor(Math.random() * 1000000); // Adjust the range as needed
  };

  const sendInvite = (playername) => {
    const payload = {
      matchId: generateNumericId(),
      from: decodedToken.username,
      to: playername.username,
      text: decodedToken.username + " invited you to play a game",
    };

    if (payload.to) {
      //start the game
      console.log(payload);
      localStorage.setItem("inGame", true);

      stompClient.publish({
        destination: `/user/${payload.to}/invites`,
        body: JSON.stringify(payload),
      });

      //just display
      toast.success(`Invitation sent to ${playername.username}`, {
        position: "top-right",
        autoClose: 3000, // Close the notification after 3 seconds
        hideProgressBar: false,
        closeOnClick: true,
        pauseOnHover: true,
        draggable: true,
      });
      navigate("/" + payload.matchId);
    }
  };

  const handleInvite = (username) => {
    sendInvite({ username });
  };

  const handleSearch = (value) => {
    setFilteredUsers(
      value ? userData.filter((u) => u.name.includes(value)) : userData
    );
  };

  return (
    <div className="container text-white">
      <div className="row">
        <ToastContainer />
        <div className="col-md-5 mb-3 mb-md-0 transparent-container">
          <p> Recommended players </p>
          <div className="form-check form-check-inline">
            <input
              className="form-check-input"
              type="checkbox"
              id="ascendingCheckbox"
              checked={ascending}
              onChange={handleAscendingChange}
            />
            <label
              className="form-check-label text-white"
              htmlFor="ascendingCheckbox"
            >
              Rating Ascending &#8593;
            </label>
          </div>
          <div className="form-check form-check-inline">
            <input
              className="form-check-input"
              type="checkbox"
              id="descendingCheckbox"
              checked={descending}
              onChange={handleDescendingChange}
            />
            <label
              className="form-check-label text-white"
              htmlFor="descendingCheckbox"
            >
              Rating Descending &#8595;
            </label>
          </div>
          <ul className="flex-wrap">
            {userData.map(
              (user, index) =>
                user.name !== username && (
                  <li
                    key={index}
                    className="p-2 border border-white rounded m-2"
                  >
                    <div className="d-flex">
                      <div>
                        <FaUser size={50} />
                        <strong>{user.name}</strong> - {user.rating} <br />
                        {user.matchesplayed ? (
                          <>
                            {JSON.stringify(user.matchesplayed)} matches played,{" "}
                            {JSON.stringify(user.matchesWon)} won
                          </>
                        ) : (
                          ""
                        )}
                      </div>
                      <div style={{ marginLeft: "20px" }}>
                        <button
                          id="inviteButton"
                          className="btn btn-success"
                          onClick={() => handleInvite(user.name)}
                        >
                          Invite
                        </button>
                      </div>
                    </div>
                  </li>
                )
            )}
          </ul>
        </div>
        <div className="col-md-5 mb-3 mb-md-0 transparent-container">
          {/* Search Bar */}
          <input
            id="searchBar"
            className="w-100 mb-3"
            type="text"
            placeholder="Search users..."
            onChange={(e) => handleSearch(e.target.value)}
          />

          {/* Display Filtered Users */}
          <ul className="flex-wrap">
            {filteredUsers.map(
              (user, index) =>
                user.name !== username && (
                  <li
                    key={index}
                    className="p-2 border border-white rounded m-2"
                  >
                    <div className="d-flex">
                      <div>
                        <FaUser size={50} />
                        <strong id="searchedUsername">
                          {user.name}
                        </strong> - {user.rating} <br />
                        {user.matchesplayed ? (
                          <>
                            {JSON.stringify(user.matchesplayed)} matches played,{" "}
                            {JSON.stringify(user.matchesWon)} won
                          </>
                        ) : (
                          ""
                        )}
                      </div>
                      <div style={{ marginLeft: "20px" }}>
                        <button
                          id="inviteButtonSearch"
                          className="btn btn-success float-right"
                          onClick={() => handleInvite(user.name)}
                        >
                          Invite
                        </button>
                      </div>
                    </div>
                  </li>
                )
            )}
          </ul>
        </div>
      </div>
    </div>
  );
};

export default UserPage;
