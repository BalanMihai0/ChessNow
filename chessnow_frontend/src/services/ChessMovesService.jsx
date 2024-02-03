import { Client } from "@stomp/stompjs";
import LOCALHOST from "./URL";
import axios from "axios";

class ChessMoveService {
  constructor() {
    this.chessMoveStompClient = new Client({
      brokerURL: "ws://localhost:8080/wschessmoves",
      reconnectDelay: 5000,
      heartbeatIncoming: 4000,
      heartbeatOutgoing: 4000,
    });

    this.chessMoveStompClient.onConnect = () => {
      console.log("Connected to chess moves WebSocket");
    };

    this.chessMoveStompClient.activate();
  }

  subscribeToChessMoves(matchId) {
    this.chessMoveStompClient.onConnect = () => {
      //send match to backend
      this.chessMoveStompClient.subscribe(
        `/user/${matchId}/chessmoves`,
        (data) => {
          onChessMoveReceived(data);
        }
      );
    };

    if (this.chessMoveStompClient.connected) {
      // If already connected, subscribe immediately
      this.chessMoveStompClient.subscribe(
        `/user/${matchId}/chessmoves`,
        (data) => {
          onChessMoveReceived(data);
        }
      );
    }
  }

  startMatch(playername, opponentName, matchId) {
    axios
      .post(`${LOCALHOST}/chess/startmatch`, {
        player: playername,
        opponent: opponentName,
        matchId: matchId,
      })
      .then((response) => {
        console.log(response.data);
      })
      .catch((error) => {
        console.error("Error starting the match:", error);
      });
  }

  sendChessMove(playername, move) {
    if (this.chessMoveStompClient.connected) {
      const payload = {
        playername,
        move,
      };

      this.chessMoveStompClient.publish({
        destination: `/user/${playername}/chessmoves`,
        body: JSON.stringify(payload),
      });
    }

    //also send to backend with: from, to, matchId, move
  }
}

export default ChessMoveService;
