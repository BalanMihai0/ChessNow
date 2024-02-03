import React, { useState } from "react";
import authService from "../services/UsersService";
import { ToastContainer, toast } from "react-toastify";
import "react-toastify/dist/ReactToastify.css";
import { useNavigate } from "react-router-dom";
import TokenService from "../services/TokenService";
import { Client } from "@stomp/stompjs";
import { startmatch } from "../services/MoveService";
import LOCALHOSTWS from "../services/URLWS";

const LoginComponent = () => {
  const [stompClient, setStompClient] = useState();
  const [username, setUsername] = useState("");
  const [password, setPassword] = useState("");

  const navigate = useNavigate();

  const cardStyle = {
    backgroundColor: "rgba(50, 50, 50, 0.7)",
  };

  const setupStompClient = (username) => {
    // stomp client over websockets
    const stompClient = new Client({
      brokerURL: `ws://${LOCALHOSTWS}/ws`,
      reconnectDelay: 5000,
      heartbeatIncoming: 4000,
      heartbeatOutgoing: 4000,
    });

    stompClient.onConnect = () => {
      // subscribe to the backend public topic
      stompClient.subscribe("/invites", (data) => {
        console.log(data);
        onMessageReceived(data);
      });

      console.log(stompClient);
    };

    // initiate client
    stompClient.activate();
    // maintain the client for sending and receiving
    setStompClient(stompClient);
  };

  const subscribeToNotifications = () => {
    setupStompClient(username);
  };

  const handleLogin = async (e) => {
    e.preventDefault();

    try {
      const loginRequest = { username, password };
      const accessToken = await authService.loginUser(loginRequest);
      // Success
      TokenService.handleLoginSuccess(accessToken, navigate);
      const decodedToken = TokenService.getDecoded(
        TokenService.getAccessToken()
      );

      if (decodedToken.isAdmin) {
        navigate("/admin");
      } else {
        const startMatchRequest = {
          player: decodedToken.username,
          opponent: "AI",
        };
        console.log("here1");
        console.log(startMatchRequest);
        startmatch(startMatchRequest).then((data) => {
          console.log(data.matchId);
          localStorage.setItem("matchId", data.matchId);
        }); //should be an id
        console.log("here2");

        navigate("/");
        subscribeToNotifications();
      }

      //window.location.reload();
    } catch (error) {
      //fail
      console.log(error);
      toast.error("Authentication failed. Please check your credentials.");
    }
  };

  return (
    <div className="container">
      <div className="row justify-content-center">
        <div className="col-md-10">
          <div className="card text-light" style={cardStyle}>
            <div className="card-header">Login</div>
            <div className="card-body">
              <form onSubmit={handleLogin} id="loginForm">
                <div className="form-group mt-2">
                  <label>Username</label>
                  <input
                    id="loginUsername"
                    type="username"
                    className="form-control"
                    value={username}
                    onChange={(e) => setUsername(e.target.value)}
                  />
                </div>
                <div className="form-group mt-2">
                  <label>Password</label>
                  <input
                    id="loginPassword"
                    type="password"
                    className="form-control"
                    value={password}
                    onChange={(e) => setPassword(e.target.value)}
                  />
                </div>
                <button type="submit" className="btn btn-warning mt-3">
                  Login
                </button>
              </form>
            </div>
          </div>
        </div>
      </div>
      <ToastContainer />
    </div>
  );
};

export default LoginComponent;
