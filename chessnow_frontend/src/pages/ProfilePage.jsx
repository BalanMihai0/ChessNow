import React, { useState, useEffect } from "react";
import { Form, Button } from "react-bootstrap";
import TokenService from "../services/TokenService";
import { useNavigate } from "react-router-dom";
import UsersService from "../services/UsersService";
import { getMatchHistory } from "../services/MoveService";
import { ToastContainer } from "react-toastify";
import { toast } from "react-toastify";

const ProfilePage = () => {
  const [userData, setUserData] = useState({
    username: "",
    email: "",
    rating: 0,
  });

  const [matchData, setMatchData] = useState([]);
  const [editMode, setEditMode] = useState(false);
  const navigate = useNavigate();

  if (!TokenService.getAccessToken()) {
    navigate("/loginregister");
  }

  useEffect(() => {
    const decodedToken = TokenService.getDecoded(TokenService.getAccessToken());

    if (decodedToken) {
      setUserData({
        username: decodedToken.username,
        email: decodedToken.email,
        rating: decodedToken.rating,
        subject: decodedToken.subject,
      });
    }
  }, []);

  useEffect(() => {
    const decodedToken = TokenService.getDecoded(TokenService.getAccessToken());
    if (decodedToken) {
      console.log(decodedToken.subject);
      getMatchHistory(decodedToken.subject).then((data) => {
        console.log(data);
        setMatchData(data);
      });
    }
  }, [userData]);

  const handleUserChange = (e) => {
    e.preventDefault();
    const { name, value } = e.target;
    setUserData((prevUserData) => ({
      ...prevUserData,
      [name]: value,
    }));
  };

  const handleEditClick = () => {
    setEditMode(true);
  };

  const handleSaveClick = () => {
    if (editMode) {
      const updatedUserData = {
        username: userData.username,
        email: userData.email,
        requesterId: TokenService.getDecoded(TokenService.getAccessToken())
          .subject,
      };
      console.log(updatedUserData);
      UsersService.updateUser(userData.subject, updatedUserData)
        .then((response) => {
          console.log("User data updated successfully:", response);
          toast.info(
            "Your data has been updated. Please log back in to see you new data"
          );
          setEditMode(false);
        })
        .catch((error) => {
          console.error("Error updating user data:", error);
        });
    } else {
      setEditMode(false);
    }
  };

  return (
    <div className="container-fluid bg-dark text-white py-5">
      <div className="container">
        <h2 className="mb-4">Your Details</h2>
        <Form>
          <Form.Group>
            <Form.Label>Username</Form.Label>
            <Form.Control
              id="profileUsername"
              type="text"
              placeholder="Enter username"
              name="username"
              value={userData.username}
              onChange={handleUserChange}
              readOnly={!editMode}
            />
          </Form.Group>

          <Form.Group controlId="formEmail">
            <Form.Label>Email</Form.Label>
            <Form.Control
              type="email"
              name="email"
              value={userData.email}
              onChange={handleUserChange}
              readOnly={!editMode}
            />
          </Form.Group>

          <Form.Group controlId="formRating">
            <Form.Label>Rating</Form.Label>
            <Form.Control
              type="number"
              className="bg-black text-white mb-2"
              value={userData.rating}
              readOnly
            />
          </Form.Group>

          {editMode ? (
            <Button
              className="mt-4"
              variant="warning"
              onClick={handleSaveClick}
            >
              Save Changes
            </Button>
          ) : (
            <Button variant="warning" onClick={handleEditClick}>
              Edit
            </Button>
          )}
        </Form>
      </div>
      <div
        className="container container-fluid"
        style={{ height: "80vh", overflowY: "auto" }}
      >
        <h2 className="mb-4 mt-6">Match History</h2>
        <div className="row">
          <ul className="flex-wrap">
            {matchData &&
              matchData.matches &&
              matchData.matches.map((match, index) => (
                <li key={index} className="p-2 border border-white rounded m-2">
                  <div className="d-flex justify-content-between flex-column flex-md-row">
                    <div className="mb-2 mb-md-0">
                      <strong>{match.player1}</strong> - {match.player2}
                    </div>
                    <div className="ml-md-3">
                      <p
                        style={{
                          color:
                            match.result === "Win"
                              ? "lime"
                              : match.result === "Loss"
                              ? "red"
                              : "yellow",
                        }}
                      >
                        Result: {match.result}
                      </p>
                    </div>
                    <div className="ml-md-auto">
                      <p>Time of Game: {match.startTime}</p>
                    </div>
                  </div>
                </li>
              ))}
          </ul>
        </div>
      </div>
    </div>
  );
};

export default ProfilePage;
