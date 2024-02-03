import React, { useEffect, useState } from "react";
import { FaUser } from "react-icons/fa";
import TokenService from "../services/TokenService";

const aiOpponentDisplayComponent = () => {
  const [playerData, setPlayerData] = useState({
    username: "AI opponent",
    rating: "???",
  });

  return (
    <div className="player-display container-small text-white ">
      <div className="row">
        <div className=" col-md-1 d-flex align-items-end">
          <FaUser size={70} />
        </div>
        <div className="col-md-11">
          <h4 className="player-username">{playerData.username}</h4>
          <h6 className="player-rating lead">Rating: {playerData.rating}</h6>
        </div>
      </div>
    </div>
  );
};

export default aiOpponentDisplayComponent;
