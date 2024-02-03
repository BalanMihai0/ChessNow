import React, { useEffect, useContext } from "react";
import { toast } from "react-toastify";
import ChessMoveService from "../services/ChessMovesService";

const CustomInviteToast = ({ message, onAccept }) => {
  const handleAccept = () => {
    const chessMovesService = new ChessMoveService();
    chessMovesService.subscribeToChessMoves(message.matchId);
    onAccept();
    toast.dismiss();
  };

  return (
    <div>
      <div>{message}</div>
      <button className="btn btn-success" onClick={handleAccept}>
        Accept
      </button>
    </div>
  );
};

export default CustomInviteToast;
