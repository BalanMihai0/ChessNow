import React, { useState, useEffect, createContext } from "react";
import {
  BrowserRouter as Router,
  Route,
  Routes,
  useNavigate,
} from "react-router-dom";
import Navbar from "./components/navbar";
import MainPage from "./pages/MainPage";
import UserPage from "./pages/UserPage";
import LoginRegisterPage from "./pages/LoginRegisterPage";
import AdminPage from "./pages/AdminPage";
import TokenService from "./services/TokenService";
import ProfilePage from "./pages/ProfilePage";
import { Client, StompConfig } from "@stomp/stompjs";
import CustomInviteToast from "./components/InviteToast";
import { ToastContainer, toast } from "react-toastify";
import "react-toastify/dist/ReactToastify.css";
import { ChessMoveStompClientContext } from "./context/Context";
import ChessMoveService from "./services/ChessMovesService";
import LOCALHOSTWS from "./services/URLWS";

function App() {
  const [decodedToken, setDecodedToken] = useState({});
  const [stompClient, setStompClient] = useState();
  const [username, setUsername] = useState(null);
  const [inviteRecieved, setInviteRecieved] = useState([]);
  const navigate = useNavigate();

  localStorage.setItem("inGame", false);

  useEffect(() => {
    if (TokenService.getAccessToken()) {
      setDecodedToken(TokenService.getDecoded(TokenService.getAccessToken()));
      setUsername(decodedToken.username);
    }
  }, []);

  useEffect(() => {
    if (username !== null) {
      setupStompClient(decodedToken.username);
    }
  }, [username]);

  const setupStompClient = (username) => {
    // stomp client over websockets
    const stompClientTemp = new Client({
      brokerURL: `ws://${LOCALHOSTWS}/wsinvites`,
      reconnectDelay: 5000,
      heartbeatIncoming: 4000,
      heartbeatOutgoing: 4000,
    });

    stompClientTemp.onConnect = () => {
      // subscribe to the backend public topic
      stompClientTemp.subscribe(`/user/${username}/invites`, (data) => {
        onInviteReceived(data);
      });

      stompClientTemp.subscribe(
        `/user/${username}/invites/accepts`,
        (data) => {}
      );
    };

    // initiate client
    stompClientTemp.activate();

    // maintain the client for sending and receiving
    setStompClient(stompClientTemp);
  };

  const handleAcceptInvite = (message) => {
    //subscribe to the chess match
    const chessMovesService = new ChessMoveService();
    chessMovesService.subscribeToChessMoves(message.matchId);
    //redirect the user to the board
    navigate("/" + message.matchId + "/" + message.from);
    //set the local app state to inGame
    localStorage.setItem("inGame", true);
    console.log("message: ");
    console.log(message);
  };

  const onInviteReceived = (data) => {
    const message = JSON.parse(data.body);
    setInviteRecieved((invitesReceived) => [...invitesReceived, message]);

    toast.info(
      <ChessMoveStompClientContext.Provider value={{}}>
        <CustomInviteToast
          message={message.text}
          onAccept={() => handleAcceptInvite(message)}
          style={{ zIndex: 9999 }}
        />
      </ChessMoveStompClientContext.Provider>,
      {
        position: "top-right",
        autoClose: false,
        hideProgressBar: true,
        closeOnClick: true,
        pauseOnHover: true,
        draggable: true,
        closeButton: true, // serves as decline for now
      }
    );
  };

  return (
    <div className="container-fluid">
      <ToastContainer />
      <div className="row">
        {decodedToken.role != "ADMIN" || !decodedToken ? (
          <>
            <div className="position-sticky col-md-3 col-lg-2 d-md-block bg-dark sidebar">
              <Navbar />
            </div>
            <main className="col-md-9 ms-sm-auto col-lg-10 px-md-4">
              <div className="d-flex justify-content-between flex-wrap flex-md-nowrap align-items-center pt-3 pb-2 mb-3 border-bottom">
                <Routes>
                  <Route
                    path="/:matchId?/:opponentName?"
                    element={<MainPage />}
                  />
                  <Route path="/userpage" element={<UserPage />} />
                  <Route
                    path="/loginregister"
                    element={<LoginRegisterPage />}
                  />
                  <Route path="/profilepage" element={<ProfilePage />} />
                </Routes>
              </div>
            </main>
          </>
        ) : (
          <Routes>
            <Route path="/" element={<AdminPage />} />
          </Routes>
        )}
      </div>
    </div>
  );
}

export default App;
