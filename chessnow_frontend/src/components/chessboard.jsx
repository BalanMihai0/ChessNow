import React, { useState, useEffect } from "react";
import { Chessboard } from "react-chessboard";
import { Chess } from "chess.js";
import "./chessboard.module.css";
import {
  sendChessMove,
  getLastPosition,
  startmatch,
  endMatch,
} from "../services/MoveService";
import "../pages/MainPage.css";
import TokenService from "../services/TokenService";
import Modal from "react-bootstrap/Modal";

const stockfishUrl = "../../node_modules/stockfish.js/stockfish.js";

const stockfish = new Worker(stockfishUrl);

const ChessboardComponent = () => {
  const [game, setGame] = useState(new Chess());
  const [isComputerTurn, setIsComputerMove] = useState(false);
  const [wasGameRead, setWasGameRead] = useState(false);
  const [gameOver, setGameOver] = useState(false);

  useEffect(() => {
    // Start the Stockfish engine
    stockfish.postMessage("uci");
    stockfish.postMessage("ucinewgame");
    stockfish.postMessage("isready");
  }, []);

  const handlePlayAgain = () => {
    const startMatchRequest = {
      player: TokenService.getDecoded(TokenService.getAccessToken()).username,
      opponent: "AI",
    };
    startmatch(startMatchRequest).then((data) =>
      localStorage.setItem("matchId", data.matchId)
    );
    //reset board
    setGame(new Chess());

    setGameOver(false);
  };

  const handleClose = () => {
    setGameOver(false);
  };

  useEffect(() => {
    if (game.isGameOver()) {
      setGameOver(true);
    } else if (isComputerTurn) {
      requestComputerMove();
    }
    setIsComputerMove(!isComputerTurn);
  }, [game]);

  useEffect(() => {
    if (game.isDraw) {
    } else if (game.isCheckmate && isComputerTurn) {
      endMatchData = {
        matchId: localStorage.getItem("matchId"),
        matchResult: "Win",
      };
      endMatch(endMatchData);
    } else if (game.isCheckmate && !isComputerTurn) {
      // player is in checkmate, computer win
      endMatchData = {
        matchId: localStorage.getItem("matchId"),
        matchResult: "Loss",
      };
      endMatch(endMatchData);
    }
  }, [gameOver]);

  useEffect(() => {
    // when page loads, pull the latest fen of the board from the backend, if there is one
    const matchId = localStorage.getItem("matchId");

    if (matchId) {
      getLastPosition(matchId)
        .then((data) => {
          if (data) {
            const temp = new Chess(data);
            setGame(temp);
            setWasGameRead(true);
            setIsComputerMove(false);
          }
        })
        .catch((error) => {
          console.error(error);
        });
    } else {
      setWasGameRead(false);
    }
  }, []);

  function makeAMove(move) {
    const accessToken = TokenService.getAccessToken();
    let result = null;
    if (accessToken && game) {
      const gameCopy = new Chess(game.fen());
      try {
        result = gameCopy.move(move, { verbose: true });
        // Check if the gameCopy object is null
        if (gameCopy) {
          // adding the extra fields
          result.isGameOver = gameCopy.isGameOver();
          result.opponent = "AI";
          result.player = TokenService.getDecoded(
            TokenService.getAccessToken()
          ).username;
          result.matchId = localStorage.getItem("matchId");

          console.log(result);
          sendChessMove(result)
            .then((response) => {
              return response.data;
            })
            .catch((error) => {
              console.error("Error sending move:", error);
            });

          // load the new board, success
          setGame(gameCopy);
        }
      } catch (Error) {
        console.log("Invalid move");
      }
    }
    return result;
  }

  function onDrop(sourceSquare, targetSquare) {
    const accessToken = TokenService.getAccessToken();
    console.log("Game over: " + gameOver);
    if (!accessToken) {
      return false; // Don't allow moves if accessToken is not valid
    }
    const move = makeAMove({
      from: sourceSquare,
      to: targetSquare,
      promotion: "q",
    });

    // Illegal move
    if (move === null) {
      if (isComputerTurn) {
        requestComputerMove();
      }
      return false;
    }
    return true;
  }

  function requestComputerMove() {
    stockfish.postMessage(`position fen ${game.fen()}`);
    stockfish.postMessage("go movetime 500"); // Adjust movetime if needed
  }

  stockfish.onmessage = (event) => {
    const response = event.data;
    if (response.startsWith("bestmove")) {
      //format: bestmove e2e4 ponder d7d5
      const moves = response.split(" ").slice(1);
      onDrop(moves[0].slice(0, 2), moves[0].slice(2, 4));
    }
  };

  return (
    <>
      <Chessboard position={game.fen()} onPieceDrop={onDrop} />
      <Modal className="round-container " show={gameOver} onHide={handleClose}>
        <Modal.Header closeButton className="bg-dark text-white">
          <Modal.Title>Game Over</Modal.Title>
        </Modal.Header>
        <Modal.Body className="bg-dark text-white">
          <p>Game over! Play again?</p>
        </Modal.Body>
        <Modal.Footer className="bg-dark text-white">
          <button className="btn btn-warning" onClick={handlePlayAgain}>
            Play Again
          </button>
          <button className="btn btn-secondary" onClick={handleClose}>
            Close
          </button>
        </Modal.Footer>
      </Modal>
    </>
    //ds
  );
};

export default ChessboardComponent;
