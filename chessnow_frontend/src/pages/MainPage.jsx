import React from "react";
import ChessboardComponent from "../components/chessboard";
import "./MainPage.css";
import "font-awesome/css/font-awesome.min.css";
import BoardControlsComponent from "../components/boardControlsComponent";
import ChatRoomComponent from "../components/chatRoomComponent";
import PlayerDisplayComponent from "../components/playerDisplayComponent";
import AiOpponentDisplayComponent from "../components/aiOpponentDisplayComponent";
import { useParams } from "react-router-dom";
import ChessboardPVPComponent from "../components/chessboardPVP";

const MainPage = () => {
  const { matchId } = useParams();

  return (
    <div className="container">
      <div className="row">
        <div className="col col-md-6 text-danger mt-4 mb-4">
          <div className="row container-small h-5">
            {matchId ? (
              <PlayerDisplayComponent matchId={matchId} />
            ) : (
              <AiOpponentDisplayComponent />
            )}{" "}
          </div>
          <div className="row">
            {matchId ? (
              <ChessboardPVPComponent matchId={matchId} />
            ) : (
              <ChessboardComponent />
            )}
          </div>
          <div className="row container-small">
            <PlayerDisplayComponent />
          </div>
        </div>
        <div className="col col-md-6 bg- text-white p-4 round-container transparent-container">
          <BoardControlsComponent />
          <div className="row mb-10 round-container">
            <ChatRoomComponent />
          </div>
        </div>
      </div>
    </div>
  );
};

export default MainPage;
