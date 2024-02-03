import { useEffect, useState } from "react";
import { Chess } from "chess.js";
import ChessMoveService from "../services/ChessMovesService";
import { Chessboard } from "react-chessboard";
import { useParams } from "react-router-dom";
import TokenService from "../services/TokenService";
import { Token } from "@mui/icons-material";

const ChessboardPVPComponent = () => {
  const [game, setGame] = useState(new Chess());
  const [matchId, setMatchId] = useState(null);

  const chessMovesService = new ChessMoveService();
  const { matchId: routeMatchId, opponent: routeOpponent } = useParams();

  useEffect(() => {
    console.log("Match id: ");
    console.log(routeMatchId);
    setMatchId(routeMatchId);
  }, [routeMatchId]);

  useEffect(() => {
    if (localStorage.getItem("inGame")) {
      const player = TokenService.getDecoded(
        TokenService.getAccessToken()
      ).username;
      chessMovesService.subscribeToChessMoves(onChessMoveRecieved, matchId);
      chessMovesService.startMatch(player, routeOpponent, matchId);
    }
  }, [localStorage.getItem("inGame")]);

  useEffect(() => {
    if (game.isGameOver()) {
      alert("Game Over");
    } else {
      // Handle the opponent's move here
    }
  }, [game]);

  function onChessMoveRecieved(chessMove) {
    // The WebSocket service will call this function when a move is received.
    // chessMove should contain the details of the move made by the opponent.

    const { from, to, promotion } = chessMove;
    makeAMove({ from, to, promotion });
  }

  function makeAMove(move) {
    const gameCopy = new Chess(game.fen());
    const result = gameCopy.move(move, { verbose: true });

    if (result !== null) {
      result.isGameOver = gameCopy.isGameOver();
      setGame(gameCopy);
    }

    chessMovesService.sendChessMove(move);

    return result;
  }

  function onDrop(sourceSquare, targetSquare) {
    const move = makeAMove({
      from: sourceSquare,
      to: targetSquare,
      promotion: "q",
    });

    if (move === null) {
      return false;
    }

    return true;
  }

  return <Chessboard position={game.fen()} onPieceDrop={onDrop} />;
};

export default ChessboardPVPComponent;
