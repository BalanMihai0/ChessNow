import React from "react";
import { endMatch, startmatch } from "../services/MoveService";
import TokenService from "../services/TokenService";
import { TOUCH } from "three";
import { useState } from "react";
import Modal from "react-bootstrap/Modal";

const boardControlsComponent = () => {
  const [gamResignAttempt, setResignAttempt] = useState(false);
  const handleClose = () => {
    setResignAttempt(false);
  };

  const askForConfirmation = () => {
    setResignAttempt(true);
  };

  const resignMatch = () => {
    if (TokenService.getAccessToken()) {
      const endMatchData = {
        matchId: localStorage.getItem("matchId"),
        matchResult: "Loss",
      };
      endMatch(endMatchData).then(() => {
        console.log("Match ended. Starting new one");
        const startMatchRequest = {
          player: TokenService.getDecoded(TokenService.getAccessToken())
            .username,
          opponent: "AI",
        };
        startmatch(startMatchRequest).then((startData) => {
          localStorage.setItem("matchId", startData.matchId);
          console.log("New match started:", startData);
          window.location.reload();
        });
      });
    }
  };

  return (
    <div className="row mb-4 text-center ">
      <div
        className="col col-md-3 text-white bg-dark p-2 icon-large"
        data-bs-toggle="tooltip"
        data-bs-placement="top"
        title="Draw"
      >
        <i
          className="fa fa-flag"
          data-bs-toggle="tooltip"
          data-bs-placement="top"
          title="Draw"
        ></i>
      </div>
      <div
        className="col col-md-3 text-white bg-dark p-2 icon-large"
        data-bs-toggle="tooltip"
        data-bs-placement="top"
        title="Resign"
        onClick={askForConfirmation}
      >
        <i
          className="fa fa-times"
          data-bs-toggle="tooltip"
          data-bs-placement="top"
          title="Resign"
        ></i>
      </div>
      <div
        className="col col-md-3 text-white bg-dark p-2 icon-large"
        data-bs-toggle="tooltip"
        data-bs-placement="top"
        title="Next"
      >
        <i
          className="fa fa-arrow-left"
          data-bs-toggle="tooltip"
          data-bs-placement="top"
          title="Previous"
        ></i>
      </div>
      <div
        className="col col-md-3 text-white bg-dark p-2 icon-large"
        data-bs-toggle="tooltip"
        data-bs-placement="top"
        title="Previous"
      >
        <i
          className="fa fa-arrow-right"
          data-bs-toggle="tooltip"
          data-bs-placement="top"
          title="Next"
        ></i>
      </div>
      <Modal
        className="round-container "
        show={gamResignAttempt}
        onHide={handleClose}
      >
        <Modal.Header closeButton className="bg-dark text-white">
          <Modal.Title>Game Resign</Modal.Title>
        </Modal.Header>
        <Modal.Body className="bg-dark text-white">
          <p>are you sure you want to resign?</p>
        </Modal.Body>
        <Modal.Footer className="bg-dark text-white">
          <button className="btn btn-warning" onClick={resignMatch}>
            Resign
          </button>
          <button className="btn btn-secondary" onClick={handleClose}>
            Cancel
          </button>
        </Modal.Footer>
      </Modal>
    </div>
  );
};

export default boardControlsComponent;
