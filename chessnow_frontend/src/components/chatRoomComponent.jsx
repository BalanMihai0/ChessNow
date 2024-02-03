import React, { useState, useEffect } from "react";
import { Client } from "@stomp/stompjs";
import { v4 as uuidv4 } from "uuid";
import SendMessagePlaceholder from "./SendMessagePlaceholder";
import ChatMessagesPlaceholder from "./ChatMessagesPlaceHolder";
import TokenService from "../services/TokenService";
import LOCALHOSTWS from "../services/URLWS";

const chatRoomComponent = () => {
  const [decodedToken, setDecodedToken] = useState({});
  const [stompClient, setStompClient] = useState();
  const [username, setUsername] = useState(null);
  const [messagesReceived, setMessagesReceived] = useState([]);

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
    const stompClient = new Client({
      brokerURL: `ws://${LOCALHOSTWS}/ws`,
      reconnectDelay: 5000,
      heartbeatIncoming: 4000,
      heartbeatOutgoing: 4000,
    });

    stompClient.onConnect = () => {
      // subscribe to the backend public topic
      stompClient.subscribe("/topic/publicmessages", (data) => {
        onMessageReceived(data);
      });

      // subscribe to the backend "private" topic
      stompClient.subscribe(
        `/user/${decodedToken.username}/queue/inboxmessages`,
        (data) => {
          onMessageReceived(data);
        }
      );
    };

    // initiate client
    stompClient.activate();

    // maintain the client for sending and receiving
    setStompClient(stompClient);
  };

  // send the data using Stomp
  const sendMessage = (newMessage) => {
    const payload = {
      id: uuidv4(),
      from: decodedToken.username,
      to: newMessage.to,
      text: newMessage.text,
    };
    if (payload.to) {
      stompClient.publish({
        destination: `/user/${payload.to}/queue/inboxmessages`,
        body: JSON.stringify(payload),
      });
    } else {
      stompClient.publish({
        destination: "/topic/publicmessages",
        body: JSON.stringify(payload),
      });
    }
  };

  // display the received data
  const onMessageReceived = (data) => {
    const message = JSON.parse(data.body);
    setMessagesReceived((messagesReceived) => [...messagesReceived, message]);
  };
  return (
    <div className="col col-md-12 text-white bg-dark p-4 round-container">
      Chatroom
      <div className="row mb-4">
        <i className="font-weight-light"> Your messages will appear here </i>
      </div>
      <div className="row mb-4">
        <div className="col col-md-2 text-end">
          <button
            type="submit"
            className="btn btn-dark"
            style={{ height: "40px" }}
          >
            <i className="fa fa-paper-plane"></i>
          </button>
        </div>
        <div className="bg-dark">
          <div
            className="form-control bg-dark text-white"
            style={{ height: "60%" }}
          >
            <SendMessagePlaceholder
              username={username}
              onMessageSend={sendMessage}
            />
          </div>
          <br></br>
          <ChatMessagesPlaceholder
            username={username}
            messagesReceived={messagesReceived}
          />
        </div>
      </div>
    </div>
  );
};

export default chatRoomComponent;
